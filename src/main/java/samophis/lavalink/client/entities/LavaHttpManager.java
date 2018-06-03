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

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * This class is the manager which allows LavaClient to send requests to get information from a track for use in both the client and the server.
 *
 * @author SamOphis
 * @since 0.1
 */

public interface LavaHttpManager {
    /**
     * Fetches the {@link LavaClient LavaClient} associated with this manager.
     * @return The {@link LavaClient LavaClient} attached to this manager.
     */
    @Nonnull
    LavaClient getClient();

    /**
     * Sends a request to the {@link AudioNode AudioNode} with the least load on it to resolve track(s) based on the provided identifier.
     * @param identifier The <b>not-null</b> identifier from which the tracks are derived.
     * @param callback The <b>not-null</b> callback to use, accepting a <b>not-null</b> {@link AudioWrapper AudioWrapper} object.
     * @throws NullPointerException If the provided identifier or callback are {@code null}.
     */
    void resolveTracks(@Nonnull String identifier, @Nonnull Consumer<AudioWrapper> callback);
}
