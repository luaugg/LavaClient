package samophis.lavalink.client.entities.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface PlayerTrackEvent extends PlayerEvent {
    AudioTrack getTrack();
}