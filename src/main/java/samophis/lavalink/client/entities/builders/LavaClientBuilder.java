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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.internal.AudioNodeEntryImpl;
import samophis.lavalink.client.entities.internal.LavaClientImpl;
import samophis.lavalink.client.exceptions.ConfigurationFormatException;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class LavaClientBuilder {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private static final Logger LOGGER = LoggerFactory.getLogger(LavaClientBuilder.class);
    private final List<AudioNodeEntry> entries;
    private String password;
    private int restPort, wsPort, shards;
    private long expireWriteMs, expireAccessMs, userId;
    public LavaClientBuilder(boolean overrideJson, @Nonnegative long expireWriteMs, @Nonnegative long expireAccessMs) {
        this();
        init(overrideJson, expireWriteMs, expireAccessMs);
    }
    public LavaClientBuilder(@Nonnegative long expireWriteMs, @Nonnegative long expireAccessMs) {
        this(true, expireWriteMs, expireAccessMs);
    }
    public LavaClientBuilder(boolean overrideJson) {
        this(overrideJson, LavaClient.DEFAULT_CACHE_EXPIRE_WRITE, LavaClient.DEFAULT_CACHE_EXPIRE_ACCESS);
    }
    public LavaClientBuilder(byte[] content) {
        this();
        try {
            init(MAPPER.readTree(content));
        } catch (IOException exc) {
            LOGGER.error("Error when configuring LavaClient!", exc);
        }
    }
    public LavaClientBuilder(@Nonnull String content) {
        this();
        try {
            init(MAPPER.readTree(content));
        } catch (IOException exc) {
            LOGGER.error("Error when configuring LavaClient!", exc);
        }
    }
    public LavaClientBuilder(@Nonnull File file) {
        this();
        try {
            init(MAPPER.readTree(file));
        } catch (IOException exc) {
            LOGGER.error("Error when configuring LavaClient!", exc);
        }
    }
    public LavaClientBuilder(@Nonnull URL source) {
        this();
        try {
            init(MAPPER.readTree(source));
        } catch (IOException exc) {
            LOGGER.error("Error when configuring LavaClient!", exc);
        }
    }
    public LavaClientBuilder() {
        init(true, LavaClient.DEFAULT_CACHE_EXPIRE_WRITE, LavaClient.DEFAULT_CACHE_EXPIRE_ACCESS);
        this.entries = new ObjectArrayList<>();
        this.password = LavaClient.PASSWORD_DEFAULT;
        this.restPort = LavaClient.REST_PORT_DEFAULT;
        this.wsPort = LavaClient.WS_PORT_DEFAULT;
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
    private void init(boolean overrideJson, @Nonnegative long expireWriteMs, @Nonnegative long expireAccessMs) {
        if (overrideJson) {
            JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
            JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
        }
        this.expireWriteMs = Asserter.requireNotNegative(expireWriteMs);
        this.expireAccessMs = Asserter.requireNotNegative(expireAccessMs);
    }
    private void init(JsonNode node) {
        JsonNode init_data = node.get("init");
        if (init_data != null && !init_data.isNull())
            init(getBooleanOrElse(init_data, "overrideJson", true), getLongOrElse(init_data, "expireWriteMs", LavaClient.DEFAULT_CACHE_EXPIRE_WRITE),
                    getLongOrElse(init_data, "expireAccessMs", LavaClient.DEFAULT_CACHE_EXPIRE_ACCESS));

        JsonNode global_data = node.get("global");
        if (global_data != null && !global_data.isNull()) {
            restPort = getIntOrElse(global_data, "restPort", LavaClient.REST_PORT_DEFAULT);
            wsPort = getIntOrElse(global_data, "wsPort", LavaClient.WS_PORT_DEFAULT);
            shards = getOrThrowAndLog(global_data, "shardCount").intValue();
            userId = getOrThrowAndLog(global_data, "userId").longValue();
            password = getStringOrElse(global_data, "password", LavaClient.PASSWORD_DEFAULT);
        }

        JsonNode nodes = node.get("nodes");
        if (nodes != null && nodes.isObject()) {
            for (JsonNode next : nodes) {
                String address = getOrThrowAndLog(next, "serverAddress").textValue();
                String password = getStringOrElse(next, "password", this.password);
                int restPort = getIntOrElse(next, "restPort", this.restPort);
                int wsPort = getIntOrElse(next, "wsPort", this.wsPort);
                AudioNodeEntry entry = new AudioNodeEntryImpl()
                        .setAddress(address)
                        .setPassword(password)
                        .setRest(Asserter.requireNotNegative(restPort))
                        .setWs(Asserter.requireNotNegative(wsPort));
                entries.add(entry);
            }
        }
    }
    private JsonNode getOrThrowAndLog(JsonNode base, String name) {
        JsonNode found = base.get(name);
        if (found == null || found.isNull()) {
            LOGGER.error("Missing field/object in configuration with name: {}", name);
            throw new ConfigurationFormatException(name);
        }
        return found;
    }
    @SuppressWarnings("all")
    private String getStringOrElse(JsonNode base, String name, String defaultValue) {
        JsonNode found = base.get(name);
        return (found == null || found.isNull()) ? defaultValue : found.textValue();
    }
    private int getIntOrElse(JsonNode base, String name, int defaultValue) {
        JsonNode found = base.get(name);
        return (found == null || found.isNull()) ? defaultValue : found.intValue();
    }
    private long getLongOrElse(JsonNode base, String name, long defaultValue) {
        JsonNode found = base.get(name);
        return (found == null || found.isNull()) ? defaultValue : found.longValue();
    }
    @SuppressWarnings("all")
    private boolean getBooleanOrElse(JsonNode base, String name, boolean defaultValue) {
        JsonNode found = base.get(name);
        return (found == null || found.isNull()) ? defaultValue : found.booleanValue();
    }
}
