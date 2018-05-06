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