package samophis.lavalink.client.entities;

import com.neovisionaries.ws.client.WebSocket;

/**
 * Represents a remote, independent audio-sending node running Lavalink-Server.
 *
 * @since 0.1
 * @author SamOphis
 */

public interface AudioNode {
    LavaClient getClient();
    WebSocket getSocket();
    LoadBalancer getBalancer();
    Statistics getStatistics();
    AudioNodeEntry getEntry();
    @SuppressWarnings("all")
    boolean isAvailable();
}