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

import samophis.lavalink.client.entities.AudioWrapper;
import samophis.lavalink.client.entities.TrackDataPair;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class AudioWrapperImpl implements AudioWrapper {
    private final boolean isPlaylist;
    private final TrackDataPair selected;
    private final List<TrackDataPair> tracks;
    private final String name;
    public AudioWrapperImpl(@Nullable String name, @Nullable TrackDataPair selected, @Nonnull List<TrackDataPair> tracks, boolean isPlaylist) {
        this.name = isPlaylist ? Asserter.requireNotNull(name) : null;
        this.selected = isPlaylist ? Asserter.requireNotNull(selected) : null;
        this.tracks = Collections.unmodifiableList(Asserter.requireNotNull(tracks));
        this.isPlaylist = isPlaylist;
    }
    @Override
    public boolean isPlaylist() {
        return isPlaylist;
    }
    @Nullable
    @Override
    public TrackDataPair getSelectedTrack() {
        return selected;
    }
    @Nonnull
    @Override
    public List<TrackDataPair> getLoadedTracks() {
        return tracks;
    }
    @Nullable
    @Override
    public String getPlaylistName() {
        return name;
    }
}
