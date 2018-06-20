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

package samophis.lavalink.client.entities;

import com.jsoniter.any.Any;
import com.neovisionaries.ws.client.WebSocket;
import samophis.lavalink.client.entities.internal.SocketHandlerImpl;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

/**
 * A handler which takes incoming messages from Lavalink Nodes and responds in some way.
 * <br><p><b>Note: LavaClient will override any handlers with names like {@code voiceUpdate} as they have predefined behaviours.</b></p>
 *
 * @since 2.2.0
 * @author Sam Pritchard
 */

public interface SocketHandler {
    /**
     * Returns the <b>not-null</b> name of this handler (the name of the OP it responds to).
     * @return The <b>not-null</b> name of this handler.
     */
    @Nonnull String getName();

    /**
     * Responds to an incoming OP from Lavalink in some pre-defined manner.
     * @param socket The <b>not-null</b> WebSocket connection between LavaClient and a Lavalink Node.
     * @param data The <b>not-null</b> data from the event.
     */
    void handleIncoming(@Nonnull WebSocket socket, @Nonnull Any data);

    /**
     * Creates a new SocketHandler instance with a provided name and event handler.
     * @param name The <b>not-null</b> name of the SocketHandler.
     * @param rawHandler The <b>not-null</b> handler callback used to respond to incoming events.
     * @return A <b>not-null</b> SocketHandler instance.
     */
    @Nonnull
    static SocketHandler from(@Nonnull String name, @Nonnull BiConsumer<WebSocket, Any> rawHandler) {
        return new SocketHandlerImpl(name, rawHandler);
    }
}