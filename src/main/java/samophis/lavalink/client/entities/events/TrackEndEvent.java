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
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;

public class TrackEndEvent implements PlayerTrackEvent {
    private final LavaClient client;
    private final LavaPlayer player;
    private final AudioTrack track;
    private final AudioTrackEndReason reason;
    public TrackEndEvent(LavaPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        this.client = player.getClient();
        this.player = player;
        this.track = track;
        this.reason = reason;
    }
    @Override
    public LavaClient getClient() {
        return client;
    }
    @Override
    public LavaPlayer getPlayer() {
        return player;
    }
    @Override
    public AudioTrack getTrack() {
        return track;
    }
    @SuppressWarnings("unused")
    public AudioTrackEndReason getReason() {
        return reason;
    }
}
