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

package samophis.lavalink.client.entities.events;

import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;

/**
 * Represents an event which has both a {@link LavaClient LavaClient} and a {@link LavaPlayer LavaPlayer} attached to it.
 *
 * @author SamOphis
 * @since 0.1
 */

public interface PlayerEvent {
    /**
     * Fetches the {@link LavaClient LavaClient} instance attached to this event.
     * @return The {@link LavaClient instance} attached to this event.
     */
    LavaClient getClient();

    /**
     * Fetches the {@link LavaPlayer LavaPlayer} instance which emitted this event.
     * @return The {@link LavaPlayer LavaPlayer} instance which emitted this event.
     */
    LavaPlayer getPlayer();
}