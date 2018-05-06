package samophis.lavalink.client.entities.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;

public class TrackStuckEvent implements PlayerTrackEvent {
    private final LavaClient client;
    private final LavaPlayer player;
    private final AudioTrack track;
    private final long thresholdMs;
    public TrackStuckEvent(LavaPlayer player, AudioTrack track, long thresholdMs) {
        this.client = player.getClient();
        this.player = player;
        this.track = track;
        this.thresholdMs = thresholdMs;
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
    public long getThresholdMs() {
        return thresholdMs;
    }
}
