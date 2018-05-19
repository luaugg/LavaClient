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
     * <br><p>Deprecated as of 19th May 2018 -- this defaults to the address returned by {@link AudioNodeEntry#getRawAddress()} now.
     * <b>Due to be removed upon the next minor version update.</b>
     * <br>Use {@link AudioNodeEntry#getRawAddress()} now to avoid compile errors upon the next update.</p>
     * @return The server address of the {@link AudioNode AudioNode} this entry represents.
     * @see #getRawAddress()
     */
    @Deprecated String getServerAddress();

    /**
     * Fetches the server address of the {@link AudioNode AudioNode} this entry represents,
     * as provided by the user through an {@link samophis.lavalink.client.entities.builders.AudioNodeEntryBuilder AudioNodeEntryBuilder}.
     * @return The raw server address of the {@link AudioNode AudioNode} this entry represents.
     */
    String getRawAddress();

    /**
     * Fetches the HTTP address of the {@link AudioNode AudioNode} this entry represents, as used by the {@link LavaHttpManager LavaHttpManager} class.
     * @return The HTTP address of the {@link AudioNode AudioNode} this entry represents, with a default {@code 'http://'} scheme if not already provided.
     */
    String getHttpAddress();

    /**
     * Fetches the WebSocket address (with the {@code 'ws://'} scheme) of the {@link AudioNode AudioNode} this entry represents.
     * <br><p>This value cannot be specified manually -- it is constructed from the original server address passed to LavaClient.</p>
     * @return The WebSocket address of the {@link AudioNode AudioNode} this entry represents.
     */
    String getWebSocketAddress();

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