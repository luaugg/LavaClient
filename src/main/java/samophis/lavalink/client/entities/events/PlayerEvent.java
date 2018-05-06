package samophis.lavalink.client.entities.events;

import samophis.lavalink.client.entities.LavaPlayer;
import samophis.lavalink.client.entities.LavaClient;

public interface PlayerEvent {
    LavaClient getClient();
    LavaPlayer getPlayer();
}