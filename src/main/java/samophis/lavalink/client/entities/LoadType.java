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

import samophis.lavalink.client.util.Asserter;
import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Represents the type of the loaded source from the {@link LavaHttpManager#resolveTracks(String, Consumer)} method.
 * <br><p><b>Note: In the earlier versions of Lavalink, {@link #LOAD_FAILED} or {@link #NO_MATCHES} will never be returned.</b>
 * This is because these versions only have an {@code isPlaylist} field, and some (v2) don't even have that.
 *
 * <br>Only the very latest versions of v3 have support for load types.</p>
 *
 * @author Sam Pritchard
 * @since 2.3.0
 */
public enum LoadType {
    /** If a single AudioTrack was loaded. */
    TRACK_LOADED,
    /** If a playlist was loaded. */
    PLAYLIST_LOADED,
    /** If a search result was loaded. */
    SEARCH_RESULT,
    /** If no matches for the provided identifier could be found. */
    NO_MATCHES,
    /** If Lavaplayer failed to load the audio source. */
    LOAD_FAILED,
    /** If Lavalink didn't work properly or if an unrecognized load type was returned. */
    UNKNOWN;

    /**
     * Returns the LoadType associated with a provided name, returning {@link #UNKNOWN} otherwise.
     * @param name The <b>not-null</b> name to search for.
     * @return A <b>not-null</b> LoadType.
     */
    @Nonnull
    public static LoadType from(@Nonnull String name) {
        Asserter.requireNotNull(name);
        for (LoadType type : values())
            if (type.name().equals(name))
                return type;
        return UNKNOWN;
    }
}