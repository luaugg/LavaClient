package samophis.lavalink.client.entities;

public interface AudioNodeEntry {
    LavaClient getClient();
    String getServerAddress();
    String getPassword();
    int getWebSocketPort();
    int getRestPort();
}