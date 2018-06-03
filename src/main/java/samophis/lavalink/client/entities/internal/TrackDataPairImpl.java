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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import samophis.lavalink.client.entities.TrackDataPair;
import samophis.lavalink.client.util.Asserter;
import samophis.lavalink.client.util.LavaClientUtil;

import javax.annotation.Nonnull;

@SuppressWarnings({"WeakerAccess", "unused"})
public class TrackDataPairImpl implements TrackDataPair {
    private final String encoded;
    private final AudioTrack track;
    public TrackDataPairImpl(@Nonnull String encoded) {
        this.encoded = Asserter.requireNotNull(encoded);
        this.track = LavaClientUtil.toAudioTrack(encoded);
    }
    public TrackDataPairImpl(@Nonnull String encoded, @Nonnull AudioTrack track) {
        this.encoded = Asserter.requireNotNull(encoded);
        this.track = Asserter.requireNotNull(track);
    }
    public TrackDataPairImpl(@Nonnull AudioTrack track) {
        this.track = Asserter.requireNotNull(track);
        this.encoded = LavaClientUtil.fromAudioTrack(track);
    }
    @Nonnull
    @Override
    public String getTrackData() {
        return encoded;
    }
    @Nonnull
    @Override
    public AudioTrack getTrack() {
        return track;
    }
}
