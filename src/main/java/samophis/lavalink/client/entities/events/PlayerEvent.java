package samophis.lavalink.client.entities.events;

import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;

/**
 * Represents an event which has both a {@link LavaClient LavaClient} and a {@link LavaPlayer LavaPlayer} attached to it.
 *
 * @author SamOphis
 * @since 0.1
 */

public interface PlayerEvent {
    /**
     * Fetches the {@link LavaClient LavaClient} instance attached to this event.
     * @return The {@link LavaClient instance} attached to this event.
     */
    LavaClient getClient();

    /**
     * Fetches the {@link LavaPlayer LavaPlayer} instance which emitted this event.
     * @return The {@link LavaPlayer LavaPlayer} instance which emitted this event.
     */
    LavaPlayer getPlayer();
}