package samophis.lavalink.client.entities;

import samophis.lavalink.client.entities.internal.EventWaiterImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a "logical" waiter used to send a full Voice Update to a possibly-provided {@link AudioNode AudioNode} upon all the information being available.
 * <br><p>This uses no sleep/wait logic, and so if Discord fails to send any information nothing bad will happen besides some angry developers.
 * Additionally, this waiter <b>CANNOT</b> be used again, so you must recreate it for every voice connection that utilizes it.
 *
 * <br>Further note that this waiter will only send Lavalink Voice Updates -- the actual Discord Voice State Update and the rate limiting for the events is still
 * something the end-user <b>MUST</b> implement to work properly.</p>
 *
 * @since 0.2.2
 * @author SamOphis
 */

public interface EventWaiter {
    /**
     * Fetches the Session ID as set by the end-user. This may be {@code 'null'} at any given time.
     * @return The <b>possibly-null-or-invalid</b> Session ID as set by the end-user.
     */
    @Nullable String getSessionId();

    /**
     * Fetches the Voice Token (<b>NOT the same as an Account Token</b>) as set by the end-user. This may be {@code 'null'} at any given time.
     * @return The <b>possibly-null-or-invalid</b> Voice Token as set by the end-user.
     */
    @Nullable String getToken();

    /**
     * Fetches the Voice Server Endpoint as set by the end-user. This may be {@code 'null'} at any given time.
     * @return The <b>possibly-null-or-invalid</b> Voice Server Endpoint as set by the end-user.
     */
    @Nullable String getEndpoint();

    /**
     * Fetches the <b>not-null</b> {@link AudioNode AudioNode} this EventWaiter will send an update to.
     * <br><p>LavaClient will automatically set this to the {@link AudioNode AudioNode} with the least load on it if the user does not provide a node to connect to.</p>
     * @return The <b>not-null</b> {@link AudioNode AudioNode} this EventWaiter will send an update to.
     */
    AudioNode getNode();

    /**
     * Fetches the ID of the Guild to open an Audio Stream to, as specified by the end-user.
     * @return The <b>possibly-invalid</b> ID of the Guild to open an Audio Stream to, as specified by the end-user.
     */
    long getGuildId();

    /**
     * Sets the Session ID and tries to connect if both the Voice Token and Endpoint have also been set.
     * @param session_id The <b>not-null</b> Session ID to set.
     * @throws NullPointerException If {@code 'session_id'} is {@code null}.
     */
    void setSessionIdAndTryConnect(@Nonnull String session_id);

    /**
     * Sets the Voice Token and Endpoint, attempting to connect if the Session ID has also been set.
     * @param token The <b>not-null</b> Voice Token to set.
     * @param endpoint The <b>not-null</b> Voice Server Endpoint to set.
     * @throws NullPointerException If either {@code 'token'} or {@code 'endpoint'} are {@code null}.
     */
    void setServerAndTryConnect(@Nonnull String token, @Nonnull String endpoint);

    /**
     * Attempts to connect to the provided {@link AudioNode AudioNode} regardless of current state.
     * <br><p>The user should have absolutely no need to call this method at all. It's pretty dangerous.</p>
     */
    void tryConnect();

    /**
     * Creates a new EventWaiter object for an associated {@link AudioNode AudioNode} and Guild ID.
     * <br><p>LavaClient will replace the node with the "best node" that has the least load on it if the node is {@code null}.</p>
     * @param node The <b>possibly-null</b> AudioNode to connect to.
     * @param guild_id The ID of the Guild to open an Audio Stream to.
     * @return A new EventWaiter object with the provided {@link AudioNode AudioNode} and Guild ID.
     * @see EventWaiter#from(long)
     */
    static EventWaiter from(@Nullable AudioNode node, long guild_id) {
        return new EventWaiterImpl(node == null ? LavaClient.getBestNode() : node, guild_id);
    }

    /**
     * Creates a new EventWaiter using the "best node" that has the least load on it and an associated Guild ID.
     * @param guild_id The ID of the Guild to open an Audio Stream to.
     * @return A new EventWaiter object with the {@link AudioNode AudioNode} that has the least load on it and the associated Guild ID.
     * @see EventWaiter#from(AudioNode, long)
     */
    static EventWaiter from(long guild_id) {
        return from(null, guild_id);
    }
}