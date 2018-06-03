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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a remote, independent audio-sending node running Lavalink Server that the client connects to.
 * <br><p>Upon creation, LavaClient attempts to open a new connection to the node.
 * Unlike the JDA Client, LavaClient will keep trying to reconnect to nodes unless the server closes the connection with close-code 1000, or until the client
 * manually closes the connection.
 *
 * @since 0.1
 * @author SamOphis
 */

public interface AudioNode {
    /**
     * Returns the <b>not-null</b> {@link LavaClient LavaClient} associated with this node object.
     * @return The associated {@link LavaClient LavaClient}.
     */
    @Nonnull
    LavaClient getClient();

    /**
     * Returns the raw, <b>not-null</b>, third-party WebSocket connection to the node.
     * <br><p>It's advised that you seriously don't mess with this unless you know <b>exactly</b> what you are doing!</p>
     * @return The <b>not-null</b> WebSocket connection.
     */
    @Nonnull
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
}