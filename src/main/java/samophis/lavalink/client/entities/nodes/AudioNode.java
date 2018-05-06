package samophis.lavalink.client.entities.nodes;

import com.neovisionaries.ws.client.WebSocket;
import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.Statistics;

public interface AudioNode {
    LavaClient getClient();
    WebSocket getSocket();
    LoadBalancer getBalancer();
    Statistics getStatistics();
    AudioNodeEntry getEntry();
    boolean isAvailable();
}