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
import javax.annotation.Nullable;
import java.util.List;

/**
 * Wraps a playlist of loaded tracks or a singular track into one object.
 * <br><p>This is used (and is different from v0.3) simply to be compatible with Lavalink Server v3 and to contain the loaded track(s) in one object.</p>
 *
 * @since 1.0.0
 * @author SamOphis
 */

public interface AudioWrapper {
    /**
     * Determines whether or not the loaded tracks are the contents of a playlist or simply a search result.
     * @return Whether the result was from a playlist or a search.
     */
    boolean isPlaylist();

    /**
     * Returns the <b>possibly-null</b> name of the playlist this object wraps.
     * <br><p>Note: This may return {@code null} if this object does <b>NOT</b> wrap a playlist.</p>
     * @return The <b>possibly-null</b> name of the playlist this object represents.
     */
    @Nullable
    String getPlaylistName();

    /**
     * Returns the <b>possibly-null</b> {@link TrackDataPair TrackDataPair} resembling the selected track of the playlist this object wraps.
     * <br><p>Note: This may return {@code null} if this object does <b>NOT</b> wrap a playlist.</p>
     * @return The <b>possibly-null</b> {@link TrackDataPair TrackDataPair} with the information of the selected track.
     */
    @Nullable
    TrackDataPair getSelectedTrack();

    /**
     * Returns an unmodifiable, <b>NOT-null</b> list containing all the {@link TrackDataPair TrackDataPairs}.
     * <br><p>The {@link TrackDataPair TrackDataPair} class just contains the AudioTrack itself and its encoded data.
     * <br>Additionally, if no tracks were returned (errors, no matches, empty, etc.) then an empty list will be returned, <b>NOT a {@code null} one.</b></p>
     * @return An <b>not-null</b> unmodifiable list containing all the {@link TrackDataPair TrackDataPairs}.
     */
    @Nonnull
    List<TrackDataPair> getLoadedTracks();
}