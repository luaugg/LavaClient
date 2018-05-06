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
    public AudioTrackEndReason getReason() {
        return reason;
    }
}
