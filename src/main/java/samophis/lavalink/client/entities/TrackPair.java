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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Represents a pair between a track's encoded Base64 data and the track itself.
 * <br><p>The data can be retrieved with LavaClientUtil#fromAudioTrack.</p>
 *
 * @author SamOphis
 * @since 0.2
 */

public class TrackPair {
    private final String track_data;
    private final AudioTrack track;
    public TrackPair(String track_data, AudioTrack track) {
        this.track_data = track_data;
        this.track = track;
    }
    public String getTrackData() {
        return track_data;
    }
    public AudioTrack getTrack() {
        return track;
    }
}
