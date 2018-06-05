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

import com.github.benmanes.caffeine.cache.Cache;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import samophis.lavalink.client.entities.internal.LavaClientImpl;
import samophis.lavalink.client.entities.internal.LoadBalancerImpl;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

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
    /** The default cache expire-after-write time, in milliseconds. */
    long DEFAULT_CACHE_EXPIRE_WRITE = 1200000;
    /** The default cache expire-after-access time, in milliseconds. */
    long DEFAULT_CACHE_EXPIRE_ACCESS = 900000;
    /** Whether nodes should by default be treated as using Lavalink v3. */
    boolean VERSION_THREE_ENABLED = false;

    /**
     * Used to fetch an unmodifiable list of all the {@link LavaPlayer LavaPlayers} associated with this client.
     * @return An unmodifiable view of all the {@link LavaPlayer LavaPlayers} this client contains.
     */
    @Nonnull
    List<LavaPlayer> getPlayers();

    /**
     * Used to fetch an unmodifiable list of all the {@link AudioNode AudioNodes} this client can connect to.
     * @return An unmodifiable view of all the {@link AudioNode AudioNodes} this client can connect to.
     */
    @Nonnull
    List<AudioNode> getAudioNodes();

    /**
     * Used to fetch the {@link LavaHttpManager LavaHttpManager} associated with this client, which is used to get track data from an identifier.
     * @return The {@link LavaHttpManager LavaHttpManager} used to grab track data from an identifier.
     */
    @Nonnull
    LavaHttpManager getHttpManager();

    /**
     * Fetches an {@link AudioNode AudioNode} by address and port. This operation is extremely fast as it only looks up the entry in a HashMap.
     * @param address The server address specified in the node's {@link AudioNodeEntry AudioNodeEntry}.
     * @param websocketPort The server <b>WebSocket</b> port specified in the node's {@link AudioNodeEntry AudioNodeEntry}.
     * @return A <b>possibly-null</b> {@link AudioNode AudioNode} associated with the provided server address and WebSocket port.
     */
    @Nullable
    AudioNode getNodeByIdentifier(String address, int websocketPort);

    /**
     * Fetches a {@link LavaPlayer LavaPlayer} instance by Guild ID, creating one if it doesn't already exist.
     * @param guild_id The ID of the Guild.
     * @return A <b>never-null</b> {@link LavaPlayer LavaPlayer} instance associated with the Guild ID.
     */
    @Nonnull
    LavaPlayer getPlayerByGuildId(long guild_id);

    /**
     * Fetches the default password for all {@link AudioNode AudioNodes} the client has access to.
     * <br><p>This value equates to {@value PASSWORD_DEFAULT} if it's not specified during the construction of the LavaClient instance.</p>
     * @return The default password specified for this LavaClient instance.
     */
    @Nonnull
    String getGlobalServerPassword();

    /**
     * Fetches the amount of time from which track data will be removed from the cache after being written without being accessed.
     * <br><p>When a song loads, LavaClient caches the initial loading data temporarily so that the server nor the client have to re-load it for a certain period of time.
     * <br>This value tells LavaClient how long to wait after data is written before it is removed, ignoring the access of the data.
     * <br>If the data is not removed, memory usage will keep on going up which is bad for big bots or small bots on limited hardware.
     * <br><br>This value equates to {@value DEFAULT_CACHE_EXPIRE_WRITE} milliseconds if it isn't manually specified during construction of the client.</p>
     * @return The amount of time to wait to remove cached track data after it is initially written.
     * @see LavaClient#getCacheExpireAfterAccessMs()
     */
    @Nonnegative
    long getCacheExpireAfterWriteMs();

    /**
     * Fetches the amount of time from which track data will removed from the cache after being accessed (after being written).
     * <br><p>When a song loads, LavaClient caches the initial loading data temporarily so that the server nor the client have to re-load it for a certain period of time.
     * <br>This value tells LavaClient how long to wait after data is accessed before it is removed, ignoring the writing of the data.
     * <br>If the data is not removed, memory usage will slowly creep up which is bad for big bots or small bots on limited hardware.
     * <br><br>This value equates to {@value DEFAULT_CACHE_EXPIRE_ACCESS} milliseconds if it isn't manually specified during construction of the client.</p>
     * @return The amount of time to wait to remove cached track data after it is accessed.
     * @see LavaClient#getCacheExpireAfterWriteMs()
     */
    @Nonnegative
    long getCacheExpireAfterAccessMs();
    /**
     * Fetches the default WebSocket port for all {@link AudioNode AudioNodes} the client has access to.
     * <br><p>This value equates to {@value WS_PORT_DEFAULT} if it's not specified during the construction of the LavaClient instance.</p>
     * @return The default WebSocket port for this LavaClient instance.
     */
    @Nonnegative
    int getGlobalWebSocketPort();

    /**
     * Fetches the default REST API port for all {@link AudioNode AudioNodes} the client has access to.
     * <br><p>This value equates to {@value REST_PORT_DEFAULT} if it's not specified during the construction of the LavaClient instance.</p>
     * @return The default REST API port for this LavaClient instance.
     */
    @Nonnegative
    int getGlobalRestPort();

    /**
     * Fetches whether LavaClient should by-default treat all {@link AudioNode AudioNodes} it has access to as using Lavalink Server v3.
     * <br><p>This value equates to {@value VERSION_THREE_ENABLED} if it's not specified during the construction of the LavaClient instance.
     * <br>Deprecated since v1.2 as the newest versions of Lavalink v3 report their version server-side.</p>
     * @return Whether LavaClient should treat all nodes as using Lavalink Server v3.
     */
    @Deprecated
    boolean isGloballyUsingLavalinkVersionThree();

    /**
     * Fetches the amount of shards specified during the construction of the LavaClient instance.
     * @return The amount of shards LavaClient is aware of and passes to {@link AudioNode AudioNodes} when connecting.
     */
    @Nonnegative
    int getShardCount();

    /**
     * Fetches the User ID of the Bot User specified during the construction of the LavaClient instance.
     * @return The User ID of the Bot User which LavaClient passes to {@link AudioNode AudioNodes} when connecting.
     */
    @Nonnegative
    long getUserId();

    /**
     * Adds a <b>not-null</b> {@link AudioNode AudioNode} and opens a connection to it <b>(if it isn't already open)</b>.
     * <br><p>Note: This will overwrite and disconnect the client from the node if it pre-exists with the same information.</p>
     * @param node The <b>not-null</b> {@link AudioNode AudioNode} to add.
     * @throws NullPointerException If the provided {@link AudioNode node} was {@code null}.
     */
    void addNode(@Nonnull AudioNode node);

    /**
     * Attempts to remove a <b>not-null</b> {@link AudioNode AudioNode}.
     * <br><p>If no node was found with the attached information, no action will be performed. Additionally, if one was found the client will disconnect from it
     * and then remove it from the global map.</p>
     * @param node The <b>not-null</b> {@link AudioNode AudioNode} to remove.
     * @throws NullPointerException If the provided {@link AudioNode AudioNode} was {@code null}.
     */
    void removeNode(@Nonnull AudioNode node);

    /**
     * Adds a <b>not-null</b> {@link AudioNode AudioNode} and opens a connection to it based on the information provided by the {@link AudioNodeEntry AudioNodeEntry}.
     * @param entry The {@link AudioNodeEntry AudioNodeEntry} containing the address, the port, etc. of the {@link AudioNode AudioNode}.
     * @throws NullPointerException If the provided {@link AudioNodeEntry entry} was {@code null}.
     */
    void addEntry(@Nonnull AudioNodeEntry entry);

    /**
     * Attempts to remove the {@link AudioNode AudioNode} associated with the provided, <b>not-null</b> {@link AudioNodeEntry AudioNodeEntry}.
     * <br><p>This will do nothing if no node was found, and it'll disconnect the client from the node and remove it if it was found.</p>
     * @param entry The <b>not-null</b> {@link AudioNodeEntry AudioNodeEntry} used to identify and remove the associated {@link AudioNode AudioNode}.
     * @throws NullPointerException If the provided {@link AudioNodeEntry AudioNodeEntry} was {@code null}.
     */
    void removeEntry(@Nonnull AudioNodeEntry entry);

    /**
     * Attempts to remove the {@link AudioNode AudioNode} associated with a provided server address and port.
     * <br><p>This will do nothing if no node was found, and it'll disconnect the client from the node and remove it if it was found.</p>
     * @param serverAddress The <b>not-null</b> raw address of the server without any scheme behind it.
     * @param websocketPort The <b>not-negative</b> WebSocket port of the Lavalink Server.
     * @throws NullPointerException If the provided server address was {@code null}.
     * @throws IllegalArgumentException If the provided WebSocket port was negative.
     */
    void removeEntry(@Nonnull String serverAddress, @Nonnegative int websocketPort);

    /**
     * Fetches the internal identifier cache used by LavaClient to cut down the impact of loading the same sources in quick succession.
     * <br><p>Note: Editing this cache might cause some delay and bugs. Modifying it is your choice.</p>
     * @return the internal cache LavaClient uses to cut down repeated song loading impact.
     */
    @Nonnull
    Cache<String, TrackDataPair> getIdentifierCache();

    /**
     * Fetches an <b>unmodifiable</b> view of the internal player map.
     * <br><p>Note: This returns an <b>unmodifiable</b> map, meaning any attempts to modify it will throw exceptions.</p>
     * @return An <b>unmodifiable</b> view of the internal player map.
     */
    @Nonnull
    Long2ObjectMap<LavaPlayer> getPlayerMap();

    /**
     * Fetches the {@link AudioNode AudioNode} with the least amount of load on it, used to balance the load of {@link LavaPlayer LavaPlayers} on different nodes.
     * @return The best {@link AudioNode AudioNode} to connect to.
     * @throws IllegalStateException If no Lavalink node exists or is available.
     */
    @Nonnull
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
