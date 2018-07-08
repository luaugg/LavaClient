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

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * Represents a remote, independent audio-sending node running Lavalink Server that the client connects to.
 * <br><p>Upon creation, LavaClient attempts to open a new connection to the node.
 * Unlike the JDA Client, LavaClient will keep trying to reconnect to nodes unless the server closes the connection with close-code 1000, or until the client
 * manually closes the connection.
 *
 * <br>When trying to open a new WebSocket connection to the node, initially LavaClient won't throw exceptions (it'll just log),
 * however when trying to connect through {@link #openConnection()}, there's a chance exceptions can be thrown.</p>
 *
 * @since 0.1
 * @author SamOphis
 */

@SuppressWarnings("unused")
public interface AudioNode {
    /**
     * Returns the <b>not-null</b> {@link LavaClient LavaClient} associated with this node object.
     * @return The associated {@link LavaClient LavaClient}.
     */
    @Nonnull
    LavaClient getClient();

    /**
     * Returns the raw, <b>possibly-null</b>, third-party WebSocket connection to the node.
     * <br><p>It's advised that you seriously don't mess with this unless you know <b>exactly</b> what you are doing!
     * <br><b>Note: As of v2.5.0, this object can be {@code null} if LavaClient fails to connect to the node.</b></p>
     * @return The <b>possibly-null</b> WebSocket connection.
     */
    @Nullable
    WebSocket getSocket();

    /**
     * Fetches the <b>not-null</b> {@link LoadBalancer LoadBalancer} that is responsible for determining the penalty imposed on the node.
     * @return The <b>not-null</b> {@link LoadBalancer LoadBalancer}.
     */
    @Nonnull
    LoadBalancer getBalancer();

    /**
     * Fetches the <b>possibly-null</b> {@link Statistics Statistics} data sent from this specific node, updated every minute.
     * <br><p>When initially connecting and for a short time afterwards this object may be {@code null}.</p>
     * @return The <b>possibly-null</b> {@link Statistics Statistics} data updated every minute.
     */
    @Nullable
    Statistics getStatistics();

    /**
     * Returns the actual, <b>not-null</b> {@link AudioNodeEntry entry} used to create this node object.
     * @return The {@link AudioNodeEntry entry} data used to create the node.
     */
    @Nonnull
    AudioNodeEntry getEntry();

    /**
     * Returns whether or not the WebSocket connection is open (available).
     * @return Whether or not the WebSocket connection is open.
     */
    @SuppressWarnings("all")
    boolean isAvailable();

    /**
     * Returns whether or not this node is running Lavalink v3 based on response headers from the initial connection.
     * @return Whether or not this node is running Lavalink v3.
     */
    boolean isUsingLavalinkVersionThree();

    /**
     * Returns the interval last used to try to reconnect to this node, set to the base reconnect interval if a reconnect hasn't
     * been attempted, or just after a reconnect has succeeded.
     * @return The reconnect interval last used to reconnect to this node.
     */
    @Nonnegative long getReconnectInterval();

    /**
     * Returns the current amount of reconnects that have been attempted by LavaClient, set to zero if either none
     * have been attempted or right after a reconnect succeeds.
     * @return The current amount of reconnects to this node performed by LavaClient.
     */
    @Nonnegative int getReconnectAttempts();

    /**
     * Returns the "default" interval, which is used to set the actual interval before and after successful reconnects.
     * @return The "default" reconnect interval.
     */
    @Nonnegative long getBaseReconnectInterval();

    /**
     * Returns the maximum interval LavaClient will wait before attempting to reconnect.
     * <br><p>If the value returned by {@link #getReconnectInterval()} is smaller, that will be used, otherwise this will.</p>
     * @return The maximum interval LavaClient will wait before attempting to reconnect.
     */
    @Nonnegative long getMaximumReconnectInterval();

    /**
     * Returns the time unit LavaClient will attempt to wait in before attempting to reconnect.
     * @return The time unit LavaClient will wait in for reconnect attempts.
     */
    @Nonnull TimeUnit getIntervalTimeUnit();

    /**
     * Returns a user-specified {@link ReconnectIntervalFunction ReconnectIntervalFunction} or the default one if it wasn't manually
     * specified.
     * <br><p>This function is used to get the next interval for the next reconnect attempt.</p>
     * @return The <b>not-null</b> {@link ReconnectIntervalFunction ReconnectIntervalFunction} used by this node for reconnecting purposes.
     */
    @Nonnull ReconnectIntervalFunction getIntervalExpander();

    /**
     * Returns a user-specified WebSocketFactory or the default LavaClient-provided one used to create WebSocket instances.
     * <br><p>Note: This configuration approach can be combined with {@link SocketInitializer SocketInitializers} in order
     * to configure everything that can be configured socket-wise.</p>
     * @return The <b>not-null</b> WebSocketFactory used to create the WebSocket instance that connects to this node.
     */
    @Nonnull WebSocketFactory getWebSocketFactory();

    /**
     * Returns a <b>possibly-null</b> {@link SocketConnectionHandler SocketConnectionHandler} specified by the user after
     * a WebSocket Connection to this node successfully opens.
     * <br><p>This callback is set via the {@link #openConnection(SocketConnectionHandler)} method, which opens a connection
     * <b>AND</b> sets the callback.
     *
     * <br>LavaClient automatically sets the provided callback to {@code null} either after 2 seconds due to a timeout
     * or right after connecting, mainly so that old callbacks aren't re-called.</p>
     * @return The <b>possibly-null</b> {@link SocketConnectionHandler SocketConnectionHandler}
     */
    @Nullable SocketConnectionHandler getConnectionCallback();

    /**
     * Returns a <b>possibly-null</b> {@link SocketConnectionHandler SocketConnectionHandler} specified by the user after
     * the WebSocket Connection to this node closes.
     * <br><p>This callback is set via the {@link #closeConnection(SocketConnectionHandler)} method, which closes the connection
     * <b>AND</b> sets the callback.
     *
     * <br>LavaClient automatically sets the provided callback to {@code null} either after 2 seconds due to a timeout
     * or right after disconnecting, mainly so that old callbacks aren't re-called.</p>
     * @return The <b>possibly-null</b> {@link SocketConnectionHandler SocketConnectionHandler}
     */
    @Nullable SocketConnectionHandler getDisconnectionCallback();

    /**
     * Attempts to open a connection to the matching Lavalink Node.
     * <br><p>This can be used to reconnect (open a new WebSocket connection), usually in the case of disconnection or if LavaClient failed to connect normally.</p>
     * @throws IllegalStateException If a connection to the node is already open and available to use.
     * @throws samophis.lavalink.client.exceptions.SocketConnectionException If LavaClient failed to open a connection to this node.
     * @see #openConnection(SocketConnectionHandler)
     */
    void openConnection();

    /**
     * Attempts to open a connection to this node, assigning a callback to be called upon the socket actually opening.
     * <br><p>The provided callback <b>can be {@code null}</b>, in which case no callback will be executed.
     * To make it a little bit easier, you can avoid manually specifying null by using the {@link #openConnection()}} method.</p>
     * @param connectHandler The <b>possibly-null</b> {@link SocketConnectionHandler SocketConnectionHandler} callback.
     * @throws IllegalStateException If the connection to the node is already open and available to use.
     * @throws samophis.lavalink.client.exceptions.SocketConnectionException If LavaClient failed to open a connection to this node.
     */
    void openConnection(@Nullable SocketConnectionHandler connectHandler);

    /**
     * Attempts to close the current connection to the matching Lavalink Node.
     * <br><p>This can be used to completely disconnect LavaClient from a node (and by extension, all {@link LavaPlayer LavaPlayers}).</p>
     * @throws IllegalStateException If the connection to the node is {@code null} or already closed/unavailable.
     * @see #closeConnection(SocketConnectionHandler)
     */
    void closeConnection();

    /**
     * Attempts to close the current connection, assigning a callback to be called upon the socket closing.
     * <br><p>The provided callback <b>can be {@code null}</b>, in which case no callback will be executed.
     * To make it a little bit easier, you can avoid manually specifying null by using the {@link #closeConnection()}} method.</p>
     * @param disconnectHandler The <b>possibly-null</b> {@link SocketConnectionHandler SocketConnectionHandler} callback.
     * @throws IllegalStateException If the connection to the node is {@code null} or already closed/unavailable.
     */
    void closeConnection(@Nullable SocketConnectionHandler disconnectHandler);
}