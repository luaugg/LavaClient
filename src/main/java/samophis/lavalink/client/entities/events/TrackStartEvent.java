package samophis.lavalink.client.entities.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;

public class TrackStartEvent implements PlayerTrackEvent {
    private final LavaClient client;
    private final LavaPlayer player;
    private final AudioTrack track;
    public TrackStartEvent(LavaPlayer player, AudioTrack track) {
        this.client = player.getClient();
        this.player = player;
        this.track = track;
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
}
