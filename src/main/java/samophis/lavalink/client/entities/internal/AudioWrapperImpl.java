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
import samophis.lavalink.client.entities.LoadType;
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
    private final LoadType type;
    public AudioWrapperImpl(@Nullable String name, @Nullable TrackDataPair selected, @Nonnull List<TrackDataPair> tracks, LoadType type) {
        Asserter.requireNotNull(tracks);
        Asserter.requireNotNull(type);
        this.name = type == LoadType.PLAYLIST_LOADED ? Asserter.requireNotNull(name) : null;
        this.selected = type == LoadType.PLAYLIST_LOADED ? Asserter.requireNotNull(selected) : null;
        this.tracks = Collections.unmodifiableList(tracks);
        this.isPlaylist = type == LoadType.PLAYLIST_LOADED;
        this.type = type;
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
    @Nonnull
    @Override
    public LoadType getLoadType() {
        return type;
    }
}
