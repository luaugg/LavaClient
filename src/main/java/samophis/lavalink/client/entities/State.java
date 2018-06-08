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

/**
 * Represents the state of a {@link LavaPlayer LavaPlayer} instance.
 *
 * @author SamOphis
 * @since 2.0.0
 */

public enum State {
    /** The initial state of all {@link samophis.lavalink.client.entities.LavaPlayer LavaPlayer} instances. */
    NOT_CONNECTED,
    /** The state {@link samophis.lavalink.client.entities.LavaPlayer LavaPlayer} instances get when they send a Voice Update. */
    CONNECTED,
    /** The state {@link samophis.lavalink.client.entities.LavaPlayer LavaPlayer} instances get when they are destroyed. */
    DESTROYED
}

