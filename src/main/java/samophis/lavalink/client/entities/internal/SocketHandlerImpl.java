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

import com.jsoniter.any.Any;
import com.neovisionaries.ws.client.WebSocket;
import samophis.lavalink.client.entities.SocketHandler;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class SocketHandlerImpl implements SocketHandler {
    private final String name;
    private final BiConsumer<WebSocket, Any> rawHandler;
    public SocketHandlerImpl(@Nonnull String name, @Nonnull BiConsumer<WebSocket, Any> rawHandler) {
        this.name = Asserter.requireNotNull(name);
        this.rawHandler = Asserter.requireNotNull(rawHandler);
    }
    @Nonnull
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void handleIncoming(@Nonnull WebSocket socket, @Nonnull Any data) {
        rawHandler.accept(Asserter.requireNotNull(socket), Asserter.requireNotNull(data));
    }
}
