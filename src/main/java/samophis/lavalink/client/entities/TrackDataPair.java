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

import javax.annotation.Nonnull;

/**
 * Represents a pair between a Lavaplayer AudioTrack and its encoded data.
 * <br><p>This is used to give access to both the encoded data (used to play the track) and the track itself for convenience.</p>
 *
 * @author SamOphis
 * @since 1.0.0
 */

@SuppressWarnings("unused")
public interface TrackDataPair {
    /**
     * Returns the encoded track data as provided by the Lavalink Server.
     * @return The data the AudioTrack encodes into.
     */
    @Nonnull
    String getTrackData();

    /**
     * Returns the actual AudioTrack object itself, constructed from the provided, encoded track data.
     * @return The AudioTrack object constructed from the track data.
     */
    @Nonnull
    AudioTrack getTrack();
}