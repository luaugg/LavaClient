package samophis.lavalink.client.entities.events;

import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;

public class PlayerResumeEvent implements PlayerEvent {
    private final LavaClient client;
    private final LavaPlayer player;
    public PlayerResumeEvent(LavaPlayer player) {
        this.client = player.getClient();
        this.player = player;
    }
    @Override
    public LavaClient getClient() {
        return client;
    }
    @Override
    public LavaPlayer getPlayer() {
        return player;
    }
}
