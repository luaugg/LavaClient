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

import com.neovisionaries.ws.client.WebSocket;
import javax.annotation.Nonnull;

/**
 * A handler/callback which accepts an {@link AudioNode AudioNode} and an {@link WebSocket WebSocket}, both of which
 * <b>shouldn't</b> be {@code null} if called from LavaClient and not manually.
 *
 * <br><p>This class is a functional interface, meaning you can treat it as a special BiConsumer and use the lambda syntax.</p>
 *
 * @author Sam Pritchard
 * @since 2.6.0
 */

@FunctionalInterface
public interface SocketConnectionHandler {
    /**
     * Called by LavaClient upon a {@link AudioNode AudioNode's} WebSocket connection to Lavalink closing/opening.
     * <br><p>Both parameters should <b>NOT be {@code null}</b> if this method isn't called manually.</p>
     * @param node The {@link AudioNode AudioNode} which opened/closed.
     * @param socket The raw WebSocket connection.
     */
    void handleConnection(@Nonnull AudioNode node, @Nonnull WebSocket socket);
}
