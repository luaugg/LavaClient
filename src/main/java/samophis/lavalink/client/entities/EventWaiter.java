/*
   Copyright 2018 Samuel Pritchard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package samophis.lavalink.client.entities;

import samophis.lavalink.client.entities.internal.EventWaiterImpl;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

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

@SuppressWarnings("unused")
public interface EventWaiter {
    /**
     * Fetches the {@link LavaClient LavaClient} instance specified during construction of this event waiter.
     * @return The {@link LavaClient LavaClient} instance attached to this event waiter.
     */
    @Nonnull
    LavaClient getClient();

    /**
     * Fetches the Session ID as set by the end-user. This may be {@code 'null'} at any given time.
     * @return The <b>possibly-null-or-invalid</b> Session ID as set by the end-user.
     */
    @Nullable
    String getSessionId();

    /**
     * Fetches the Voice Token (<b>NOT the same as an Account Token</b>) as set by the end-user. This may be {@code 'null'} at any given time.
     * @return The <b>possibly-null-or-invalid</b> Voice Token as set by the end-user.
     */
    @Nullable
    String getToken();

    /**
     * Fetches the Voice Server Endpoint as set by the end-user. This may be {@code 'null'} at any given time.
     * @return The <b>possibly-null-or-invalid</b> Voice Server Endpoint as set by the end-user.
     */
    @Nullable
    String getEndpoint();

    /** Fetches the <b>possibly-null</b> callback used to execute code directly after a connection attempt while preserving order.
     * @return The provided, <b>possibly-null</b> connection callback.
     */
    @Nullable
    Consumer<AudioNode> getCallback();

    /**
     * Fetches the <b>not-null</b> {@link AudioNode AudioNode} this EventWaiter will send an update to.
     * <br><p>LavaClient will automatically set this to the {@link AudioNode AudioNode} with the least load on it if the user does not provide a node to connect to.</p>
     * @return The <b>not-null</b> {@link AudioNode AudioNode} this EventWaiter will send an update to.
     */
    @Nonnull
    AudioNode getNode();

    /**
     * Fetches the ID of the Guild to open an Audio Stream to, as specified by the end-user.
     * @return The <b>possibly-invalid</b> ID of the Guild to open an Audio Stream to, as specified by the end-user.
     */
    @Nonnegative
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
     * <br><p>LavaClient will replace the node with the "best node" that has the least load on it if the node is {@code null}.
     * <br>This additionally allows a callback to be used once LavaClient attempts to send a Voice Update to Lavalink.</p>
     * @param client The <b>not-null</b> {@link LavaClient LavaClient} instance used to get the best node.
     * @param node The <b>possibly-null</b> AudioNode to connect to.
     * @param callback The <b>possibly-null</b> callback to use right after LavaClient attempts to connect.
     * @param guild_id The <b>positive</b> ID of the Guild to open an Audio Stream to.
     * @return A new EventWaiter object with the provided {@link AudioNode AudioNode} and Guild ID.
     * @throws NullPointerException If the provided client was {@code null}.
     * @throws IllegalArgumentException If the provided Guild ID was negative.
     * @see EventWaiter#from(LavaClient, long)
     * @see EventWaiter#from(LavaClient, AudioNode, long)
     * @see EventWaiter#from(LavaClient, Consumer, long)
     */

    @Nonnull
    static EventWaiter from(@Nonnull LavaClient client, @Nullable AudioNode node, @Nullable Consumer<AudioNode> callback, @Nonnegative long guild_id) {
        return new EventWaiterImpl(client, node == null ? Asserter.requireNotNull(client).getBestNode() : node, callback, guild_id);
    }

    /**
     * Creates a new EventWaiter object for an associated {@link AudioNode AudioNode} and Guild ID.
     * <br><p>LavaClient will replace the node with the "best node" that has the least load on it if the node is {@code null}.</p>
     * @param client The <b>not-null</b> {@link LavaClient LavaClient} instance used to get the best node.
     * @param node The <b>possibly-null</b> AudioNode to connect to.
     * @param guild_id The <b>positive</b> ID of the Guild to open an Audio Stream to.
     * @return A new EventWaiter object with the provided {@link AudioNode AudioNode} and Guild ID.
     * @throws NullPointerException If the provided client was {@code null}.
     * @throws IllegalArgumentException If the provided Guild ID was negative.
     * @see EventWaiter#from(LavaClient, long)
     * @see EventWaiter#from(LavaClient, AudioNode, Consumer, long)
     * @see EventWaiter#from(LavaClient, Consumer, long)
     */

    @Nonnull
    static EventWaiter from(@Nonnull LavaClient client, @Nullable AudioNode node, @Nonnegative long guild_id) {
        return from(client, node, null, guild_id);
    }

    /**
     * Creates a new EventWaiter using the "best node" that has the least load on it and an associated Guild ID.
     * @param client The <b>not-null</b> {@link LavaClient LavaClient} instance used to get the best node.
     * @param guild_id The <b>positive</b> ID of the Guild to open an Audio Stream to.
     * @return A new EventWaiter object with the {@link AudioNode AudioNode} that has the least load on it and the associated Guild ID.
     * @throws NullPointerException If the provided client was {@code null}.
     * @throws IllegalArgumentException If the provided Guild ID was negative.
     * @see EventWaiter#from(LavaClient, AudioNode, Consumer, long)
     * @see EventWaiter#from(LavaClient, AudioNode, long)
     * @see EventWaiter#from(LavaClient, Consumer, long)
     */
    @Nonnull
    static EventWaiter from(@Nonnull LavaClient client, @Nonnegative long guild_id) {
        return from(client, null, null, guild_id);
    }

    /**
     * Creates a new EventWaiter object using the best {@link AudioNode AudioNode} and provied Guild ID.
     * <br><p>LavaClient will replace the node with the "best node" that has the least load on it if the node is {@code null}.
     * <br>This additionally allows a callback to be used once LavaClient attempts to send a Voice Update to Lavalink.</p>
     * @param client The <b>not-null</b> {@link LavaClient LavaClient} instance used to fetch the best node.
     * @param callback The <b>possibly-null</b> callback to use right after LavaClient attempts to connect.
     * @param guild_id The <b>positive</b> ID of the Guild to open an Audio Stream to.
     * @return A new EventWaiter object with the provided {@link AudioNode AudioNode} and Guild ID.
     * @throws NullPointerException If the provided client was {@code null}.
     * @throws IllegalArgumentException If the provided Guild ID was negative.
     * @see EventWaiter#from(LavaClient, long)
     * @see EventWaiter#from(LavaClient, AudioNode, long)
     * @see EventWaiter#from(LavaClient, AudioNode, Consumer, long)
     */

    @Nonnull
    static EventWaiter from(@Nonnull LavaClient client, @Nullable Consumer<AudioNode> callback, @Nonnegative long guild_id) {
        return from(client, null, callback, guild_id);
    }
}