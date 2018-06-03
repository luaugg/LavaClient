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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Represents an event with a {@link samophis.lavalink.client.entities.LavaClient LavaClient}, a {@link samophis.lavalink.client.entities.LavaPlayer LavaPlayer} and an AudioTrack.
 *
 * @author SamOphis
 * @since 0.1
 */

public interface PlayerTrackEvent extends PlayerEvent {
    /**
     * Fetches The AudioTrack associated with this event.
     * @return The AudioTrack attached to this event.
     */
    AudioTrack getTrack();
}