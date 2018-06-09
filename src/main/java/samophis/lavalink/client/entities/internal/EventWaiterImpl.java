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

package samophis.lavalink.client.entities.internal;

import com.jsoniter.output.JsonStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.lavalink.client.entities.*;
import samophis.lavalink.client.entities.messages.client.VoiceUpdate;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EventWaiterImpl implements EventWaiter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventWaiterImpl.class);
    private final LavaClient client;
    private final AudioNode node;
    private final Consumer<AudioNode> callback;
    private final long guild_id;
    private String session_id, token, endpoint;
    private LavaPlayer player;
    public EventWaiterImpl(@Nonnull LavaClient client, @Nonnull AudioNode node, @Nullable Consumer<AudioNode> callback, @Nonnegative long guild_id) {
        this.client = Asserter.requireNotNull(client);
        this.node = Asserter.requireNotNull(node);
        this.callback = callback;
        this.guild_id = Asserter.requireNotNegative(guild_id);
    }
    @Nonnull
    @Override
    public LavaClient getClient() {
        return client;
    }
    @Nullable
    @Override
    public String getSessionId() {
        return session_id;
    }
    @Nullable
    @Override
    public String getToken() {
        return token;
    }
    @Nullable
    @Override
    public String getEndpoint() {
        return endpoint;
    }
    @Nullable
    @Override
    public Consumer<AudioNode> getCallback() {
        return callback;
    }
    @Nonnull
    @Override
    public AudioNode getNode() {
        return node;
    }
    @Nonnegative
    @Override
    public long getGuildId() {
        return guild_id;
    }
    @Override
    public void setSessionIdAndTryConnect(@Nonnull String session_id) {
        this.session_id = Asserter.requireNotNull(session_id);
        if (token != null && endpoint != null) {
            this.player = client.newPlayer(node, guild_id);
            tryConnect();
        }
    }
    @Override
    public void setServerAndTryConnect(@Nonnull String token, @Nonnull String endpoint) {
        this.token = Asserter.requireNotNull(token);
        this.endpoint = Asserter.requireNotNull(endpoint);
        if (session_id != null) {
            this.player = client.newPlayer(node, guild_id);
            tryConnect();
        }
    }
    @Override
    public void tryConnect() {
        if (player == null) {
            LOGGER.warn("LavaPlayer instance == null (incorrect state)!");
            throw new IllegalStateException("LavaPlayer instance hasn't been created yet (incorrect state!)");
        }
        if (player.getState() == State.CONNECTED) {
            LOGGER.warn("LavaPlayer instance is already connected!");
            throw new IllegalStateException("LavaPlayer instance is already connected!");
        }
        node.getSocket().sendText(JsonStream.serialize(new VoiceUpdate(guild_id, session_id, token, endpoint)));
        ((LavaPlayerImpl) player).setState(State.CONNECTED);
        if (callback != null)
            callback.accept(node);
    }
}
