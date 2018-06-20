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

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.SocketHandler;
import samophis.lavalink.client.entities.SocketInitializer;
import samophis.lavalink.client.entities.internal.AudioNodeEntryImpl;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnull;
import java.util.Map;

@SuppressWarnings("unused")
public class AudioNodeEntryBuilder {
    private final LavaClient client;
    private final Map<String, SocketHandler> handlers;
    private String address, password;
    private int restPort, wsPort;
    private SocketInitializer initializer;
    @SuppressWarnings({"deprecation", "WeakerAccess"})
    public AudioNodeEntryBuilder(@Nonnull LavaClient client) {
        this.client = Asserter.requireNotNull(client);
        this.handlers = new Object2ObjectOpenHashMap<>();
    }
    public AudioNodeEntryBuilder setAddress(String address) {
        this.address = address;
        return this;
    }
    public AudioNodeEntryBuilder setPassword(String password) {
        this.password = password;
        return this;
    }
    public AudioNodeEntryBuilder setRestPort(int restPort) {
        this.restPort = restPort;
        return this;
    }
    public AudioNodeEntryBuilder setWebSocketPort(int wsPort) {
        this.wsPort = wsPort;
        return this;
    }
    public AudioNodeEntryBuilder setSocketInitializer(SocketInitializer initializer) {
        this.initializer = initializer;
        return this;
    }
    public AudioNodeEntryBuilder addSocketHandler(SocketHandler handler) {
        this.handlers.put(Asserter.requireNotNull(handler).getName(), handler);
        return this;
    }
    public AudioNodeEntry build() {
        return new AudioNodeEntryImpl(client, address, password, restPort, wsPort, initializer, handlers);
    }
}
