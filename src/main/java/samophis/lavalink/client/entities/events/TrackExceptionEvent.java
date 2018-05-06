package samophis.lavalink.client.entities.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;

public class TrackExceptionEvent implements PlayerTrackEvent {
    private final LavaClient client;
    private final LavaPlayer player;
    private final AudioTrack track;
    private final Exception exception;
    public TrackExceptionEvent(LavaPlayer player, AudioTrack track, Exception exception) {
        this.client = player.getClient();
        this.player = player;
        this.track = track;
        this.exception = exception;
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
    public Exception getException() {
        return exception;
    }
}
