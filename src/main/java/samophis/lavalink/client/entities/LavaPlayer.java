package samophis.lavalink.client.entities;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import samophis.lavalink.client.entities.events.AudioEventListener;
import samophis.lavalink.client.entities.events.PlayerEvent;
import samophis.lavalink.client.entities.nodes.AudioNode;

import javax.annotation.Nonnull;
import java.util.List;

public interface LavaPlayer {
    AudioNode getConnectedNode();
    AudioTrack getPlayingTrack();
    LavaClient getClient();
    List<AudioEventListener> getListeners();
    long getGuildId();
    long getChannelId();
    long getTimestamp();
    long getPosition();
    int getVolume();
    boolean isPaused();
    /* ---- */
    void addListener(@Nonnull AudioEventListener listener);
    void playTrack(@Nonnull AudioTrack track);
    void playTrack(@Nonnull String identifier);
    void playTrack(@Nonnull AudioTrack track, String startTime, String endTime);
    void playTrack(@Nonnull String identifier, String startTime, String endTime);
    void stopTrack();
    void setPaused(boolean isPaused);
    void destroyPlayer(); /* -- note, maintains lavalink state | player WILL keep its state allowing for future reconnects at same position -- */
    void seek(long position);
    void setVolume(int volume); /* -- bounded from 0 to 150 inclusive -- */
    void setNode(@Nonnull AudioNode node);
    void emitEvent(@Nonnull PlayerEvent event);
}