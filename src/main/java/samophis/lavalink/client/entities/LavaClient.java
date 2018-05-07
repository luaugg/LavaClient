package samophis.lavalink.client.entities;

import samophis.lavalink.client.entities.internal.LavaClientImpl;
import samophis.lavalink.client.entities.internal.LoadBalancerImpl;

import java.util.List;

/**
 * Represents the LavaClient implementation of Lavalink, with default constants matching those of the Lavalink-Server, collections of nodes, players, etc.
 * <br>This is the gateway into every other LavaClient action and can be constructed with a {@link samophis.lavalink.client.entities.builders.LavaClientBuilder LavaClientBuilder}.
 *
 * @since 0.1
 * @author SamOphis
 */

@SuppressWarnings("unused")
public interface LavaClient {
    /** The default port to use for the Lavalink-Server REST API. */
    int REST_PORT_DEFAULT = 2333;
    /** The default port to use for the Lavalink-Server WebSocket. */
    int WS_PORT_DEFAULT = 80;
    /** The default password to use for the Lavalink-Server. */
    String PASSWORD_DEFAULT = "youshallnotpass";

    /**
     * Used to fetch an unmodifiable list of all the {@link LavaPlayer LavaPlayers} associated with this client.
     * @return An unmodifiable view of all the {@link LavaPlayer LavaPlayers} this client contains.
     */
    List<LavaPlayer> getPlayers();

    /**
     * Used to fetch an unmodifiable list of all the {@link AudioNode AudioNodes} this client can connect to.
     * @return An unmodifiable view of all the {@link AudioNode AudioNodes} this client can connect to.
     */
    List<AudioNode> getAudioNodes();

    /**
     * Used to fetch the {@link LavaHttpManager LavaHttpManager} associated with this client, which is used to get track data from an identifier.
     * @return The {@link LavaHttpManager LavaHttpManager} used to grab track data from an identifier.
     */
    LavaHttpManager getHttpManager();

    /**
     * Fetches an {@link AudioNode AudioNode} by address and port. This operation is extremely fast as it only looks up the entry in a HashMap.
     * @param address The server address specified in the node's {@link AudioNodeEntry AudioNodeEntry}.
     * @param websocketPort The server <b>WebSocket</b> port specified in the node's {@link AudioNodeEntry AudioNodeEntry}.
     * @return A <b>possibly-null</b> {@link AudioNode AudioNode} associated with the provided server address and WebSocket port.
     */
    AudioNode getNodeByIdentifier(String address, int websocketPort);

    /**
     * Fetches a {@link LavaPlayer LavaPlayer} instance by Guild ID, creating one if it doesn't already exist.
     * @param guild_id The ID of the Guild.
     * @return A <b>never-null</b> {@link LavaPlayer LavaPlayer} instance associated with the Guild ID.
     */
    LavaPlayer getPlayerByGuildId(long guild_id);

    /**
     * Fetches the default password for all {@link AudioNode AudioNodes} the client has access to.
     * <br><p>This value equates to {@value PASSWORD_DEFAULT} if it's not specified during the construction of the LavaClient instance.</p>
     * @return The default password specified for this LavaClient instance.
     */
    String getGlobalServerPassword();

    /**
     * Fetches the default WebSocket port for all {@link AudioNode AudioNodes} the client has access to.
     * <br><p>This value equates to {@value WS_PORT_DEFAULT} if it's not specified during the construction of the LavaClient instance.</p>
     * @return The default WebSocket port for this LavaClient instance.
     */
    int getGlobalWebSocketPort();

    /**
     * Fetches the default REST API port for all {@link AudioNode AudioNodes} the client has access to.
     * <br><p>This value equates to {@value REST_PORT_DEFAULT} if it's not specified during the construction of the LavaClient instance.</p>
     * @return The default REST API port for this LavaClient instance.
     */
    int getGlobalRestPort();

    /**
     * Fetches the amount of shards specified during the construction of the LavaClient instance.
     * @return The amount of shards LavaClient is aware of and passes to {@link AudioNode AudioNodes} when connecting.
     */
    int getShardCount();

    /**
     * Fetches the User ID of the Bot User specified during the construction of the LavaClient instance.
     * @return The User ID of the Bot User which LavaClient passes to {@link AudioNode AudioNodes} when connecting.
     */
    long getUserId();

    /**
     * Adds an {@link AudioNode AudioNode} and opens a connection to it based on the information provided by the {@link AudioNodeEntry AudioNodeEntry}.
     * @param entry The {@link AudioNodeEntry AudioNodeEntry} containing the address, the port, etc. of the {@link AudioNode AudioNode}.
     */
    void addEntry(AudioNodeEntry entry);

    /**
     * Fetches the {@link AudioNode AudioNode} with the least amount of load on it, used to balance the load of {@link LavaPlayer LavaPlayers} on different nodes.
     * @return The best {@link AudioNode AudioNode} to connect to.
     * @throws IllegalStateException If no Lavalink node exists or is available.
     */
    static AudioNode getBestNode() {
        AudioNode node = null;
        int record = Integer.MAX_VALUE;
        for (AudioNode nd : LavaClientImpl.NODES.values()) {
            int penalty = ((LoadBalancerImpl) nd.getBalancer()).initWithNode().getTotalPenalty();
            if (penalty < record) {
                node = nd;
                record = penalty;
            }
        }
        if (node == null)
            throw new IllegalStateException("No available Lavalink nodes!");
        if (!node.isAvailable())
            throw new IllegalStateException("Lavalink node wasn't available!");
        return node;
    }
}
