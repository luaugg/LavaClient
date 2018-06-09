/*
   Copyright 2018 Samuel Pritchard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package samophis.lavalink.client.entities.builders;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.internal.LavaClientImpl;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class LavaClientBuilder {
    private final List<AudioNodeEntry> entries;
    private final long expireWriteMs, expireAccessMs;
    private String password;
    private int restPort, wsPort, shards;
    private long userId;
    public LavaClientBuilder(boolean overrideJson, @Nonnegative long expireWriteMs, @Nonnegative long expireAccessMs) {
        if (overrideJson) {
            JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
            JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
        }
        this.expireWriteMs = expireWriteMs;
        this.expireAccessMs = expireAccessMs;
        this.entries = new ObjectArrayList<>();
        this.password = LavaClient.PASSWORD_DEFAULT;
        this.restPort = LavaClient.REST_PORT_DEFAULT;
        this.wsPort = LavaClient.WS_PORT_DEFAULT;
    }
    public LavaClientBuilder(@Nonnegative long expireWriteMs, @Nonnegative long expireAccessMs) {
        this(true, expireWriteMs, expireAccessMs);
    }
    public LavaClientBuilder(boolean overrideJson) {
        this(overrideJson, LavaClient.DEFAULT_CACHE_EXPIRE_WRITE, LavaClient.DEFAULT_CACHE_EXPIRE_ACCESS);
    }
    public LavaClientBuilder() {
        this(true);
    }

    public LavaClientBuilder addEntry(@Nonnull AudioNodeEntry entry) {
        entries.add(Asserter.requireNotNull(entry));
        return this;
    }
    public LavaClientBuilder addEntry(@Nonnull LavaClient client, @Nonnull String address, @Nonnegative int restPort, @Nonnegative int wsPort, @Nonnull String password) {
        return addEntry(new AudioNodeEntryBuilder(client)
                .setAddress(address)
                .setRestPort(restPort)
                .setWebSocketPort(wsPort)
                .setPassword(password)
                .build()
        );
    }
    public LavaClientBuilder setGlobalServerPassword(String password) {
        this.password = password;
        return this;
    }
    public LavaClientBuilder setGlobalRestPort(int restPort) {
        this.restPort = restPort;
        return this;
    }
    public LavaClientBuilder setGlobalWebSocketPort(int wsPort) {
        this.wsPort = wsPort;
        return this;
    }
    public LavaClientBuilder setShardCount(int shards) {
        this.shards = shards;
        return this;
    }
    public LavaClientBuilder setUserId(long userId) {
        this.userId = userId;
        return this;
    }
    public LavaClient build() {
        return new LavaClientImpl(password, restPort, wsPort, shards, expireWriteMs, expireAccessMs, userId, entries);
    }
}
