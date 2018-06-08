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
import samophis.lavalink.client.entities.AudioNode;
import samophis.lavalink.client.entities.EventWaiter;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.messages.client.VoiceUpdate;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EventWaiterImpl implements EventWaiter {
    private final LavaClient client;
    private final AudioNode node;
    private final Consumer<AudioNode> callback;
    private final long guild_id;
    private String session_id, token, endpoint;
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
        if (token != null && endpoint != null)
            tryConnect();
    }
    @Override
    public void setServerAndTryConnect(@Nonnull String token, @Nonnull String endpoint) {
        this.token = Asserter.requireNotNull(token);
        this.endpoint = Asserter.requireNotNull(endpoint);
        if (session_id != null)
            tryConnect();
    }
    @Override
    public void tryConnect() {
        node.getSocket().sendText(JsonStream.serialize(new VoiceUpdate(guild_id, session_id, token, endpoint)));
        if (callback != null)
            callback.accept(node);
    }
}
