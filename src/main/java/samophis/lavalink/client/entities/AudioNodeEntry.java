package samophis.lavalink.client.entities;

/**
 * Represents an entry for an {@link AudioNode AudioNode} with configuration options.
 * This class can be constructed with an {@link samophis.lavalink.client.entities.builders.AudioNodeEntryBuilder AudioNodeEntryBuilder}.
 *
 * @since 0.1
 * @author SamOphis
 */

public interface AudioNodeEntry {
    /**
     * Fetches the {@link LavaClient LavaClient} instance associated with this entry.
     * <br><p>Note: This entry gets its default values from the client.</p>
     * @return The {@link LavaClient LavaClient} instance from which the entry gets its default values.
     */
    LavaClient getClient();

    /**
     * Fetches the server address of the associated {@link AudioNode AudioNode}.
     * @return The server address of the {@link AudioNode AudioNode} this entry represents.
     */
    String getServerAddress();

    /**
     * Fetches the password of the associated {@link AudioNode AudioNode}.
     * <br><p>The password of this entry will be replaced with the default password if one isn't specified during construction of this entry.</p>
     * @return The password of the {@link AudioNode AudioNode} this entry represents.
     */
    String getPassword();

    /**
     * Fetches the WebSocket port of the associated {@link AudioNode AudioNode}.
     * <br><p>The WebSocket port of this entry will be replaced with the default WebSocket port if one isn't specified during construction of this entry.</p>
     * @return The WebSocket port of the {@link AudioNode AudioNode} this entry represents.
     */
    int getWebSocketPort();

    /**
     * Fetches the REST API port of the associated {@link AudioNode AudioNode}.
     * <br><p>The REST API port of this entry will be replaced with the default REST API port if one isn't specified during construction of this entry.</p>
     * @return The REST API port of the {@link AudioNode AudioNode} this entry represents.
     */
    int getRestPort();
}