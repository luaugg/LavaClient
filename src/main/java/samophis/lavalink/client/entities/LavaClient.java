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
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

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
public abstract class LavaClient {
    /** The default port to use for the Lavalink-Server REST API. */
    public static final int REST_PORT_DEFAULT = 2333;
    /** The default port to use for the Lavalink-Server WebSocket. */
    public static final int WS_PORT_DEFAULT = 80;
    /** The default password to use for the Lavalink-Server. */
    public static final String PASSWORD_DEFAULT = "youshallnotpass";
    /** The default cache expire-after-write time, in milliseconds. */
    public static final long DEFAULT_CACHE_EXPIRE_WRITE = 1200000;
    /** The default cache expire-after-access time, in milliseconds. */
    public static final long DEFAULT_CACHE_EXPIRE_ACCESS = 900000;

    protected final Map<String, AudioNode> nodes;
    protected final Long2ObjectMap<LavaPlayer> players;
    protected LavaClient() {
        this.nodes = new Object2ObjectOpenHashMap<>();
        this.players = new Long2ObjectOpenHashMap<>();
    }
    /**
     * Used to fetch an unmodifiable list of all the {@link LavaPlayer LavaPlayers} associated with this client.
     * @return An unmodifiable view of all the {@link LavaPlayer LavaPlayers} this client contains.
     */
    @Nonnull
    public abstract List<LavaPlayer> getPlayers();

    /**
     * Used to fetch an unmodifiable list of all the {@link AudioNode AudioNodes} this client can connect to.
     * @return An unmodifiable view of all the {@link AudioNode AudioNodes} this client can connect to.
     */
    @Nonnull
    public abstract List<AudioNode> getAudioNodes();

    /**
     * Used to fetch the {@link LavaHttpManager LavaHttpManager} associated with this client, which is used to get track data from an identifier.
     * @return The {@link LavaHttpManager LavaHttpManager} used to grab track data from an identifier.
     */
    @Nonnull
    public abstract LavaHttpManager getHttpManager();

    /**
     * Fetches an {@link AudioNode AudioNode} by address and port. This operation is extremely fast as it only looks up the entry in a HashMap.
     * @param address The <b>not-null</b> server address specified in the node's {@link AudioNodeEntry AudioNodeEntry}.
     * @param websocketPort The <b>positive</b> server <b>WebSocket</b> port specified in the node's {@link AudioNodeEntry AudioNodeEntry}.
     * @return A <b>possibly-null</b> {@link AudioNode AudioNode} associated with the provided server address and WebSocket port.
     * @throws NullPointerException If the provided address was {@code null}.
     * @throws IllegalArgumentException If the provided WebSocket Port was negative.
     */
    @Nullable
    public abstract AudioNode getNodeByIdentifier(@Nonnull String address, @Nonnegative int websocketPort);

    /**
     * Fetches a {@link LavaPlayer LavaPlayer} instance by Guild ID.
     * <br><p>This method <b>will return {@code null} if a player for the provided Guild ID doesn't exist, unlike previous versions of LavaClient.</b></p>
     * @param guild_id The <b>positive</b> ID of the Guild.
     * @return A <b>possibly-null</b> {@link LavaPlayer LavaPlayer} instance associated with the Guild ID.
     */
    @Nullable
    public abstract LavaPlayer getPlayerByGuildId(@Nonnegative long guild_id);

    /**
     * Fetches the default password for all {@link AudioNode AudioNodes} the client has access to.
     * <br><p>This value equates to {@value PASSWORD_DEFAULT} if it's not specified during the construction of the LavaClient instance.</p>
     * @return The default password specified for this LavaClient instance.
     */
    @Nonnull
    public abstract String getGlobalServerPassword();

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
    public abstract long getCacheExpireAfterWriteMs();

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
    public abstract long getCacheExpireAfterAccessMs();

    /**
     * Fetches the default WebSocket port for all {@link AudioNode AudioNodes} the client has access to.
     * <br><p>This value equates to {@value WS_PORT_DEFAULT} if it's not specified during the construction of the LavaClient instance.</p>
     * @return The default WebSocket port for this LavaClient instance.
     */
    @Nonnegative
    public abstract int getGlobalWebSocketPort();

    /**
     * Fetches the default REST API port for all {@link AudioNode AudioNodes} the client has access to.
     * <br><p>This value equates to {@value REST_PORT_DEFAULT} if it's not specified during the construction of the LavaClient instance.</p>
     * @return The default REST API port for this LavaClient instance.
     */
    @Nonnegative
    public abstract int getGlobalRestPort();

    /**
     * Fetches the amount of shards specified during the construction of the LavaClient instance.
     * @return The amount of shards LavaClient is aware of and passes to {@link AudioNode AudioNodes} when connecting.
     */
    @Nonnegative
    public abstract int getShardCount();

    /**
     * Fetches the User ID of the Bot User specified during the construction of the LavaClient instance.
     * @return The User ID of the Bot User which LavaClient passes to {@link AudioNode AudioNodes} when connecting.
     */
    @Nonnegative
    public abstract long getUserId();

    /**
     * Adds a <b>not-null</b> {@link AudioNode AudioNode} and opens a connection to it <b>(if it isn't already open)</b>.
     * <br><p>Note: This will overwrite and disconnect the client from the node if it pre-exists with the same information.</p>
     * @param node The <b>not-null</b> {@link AudioNode AudioNode} to add.
     * @throws NullPointerException If the provided {@link AudioNode node} was {@code null}.
     */
    public abstract void addNode(@Nonnull AudioNode node);

    /**
     * Attempts to remove a <b>not-null</b> {@link AudioNode AudioNode}.
     * <br><p>If no node was found with the attached information, no action will be performed. Additionally, if one was found the client will disconnect from it
     * and then remove it from the global map.</p>
     * @param node The <b>not-null</b> {@link AudioNode AudioNode} to remove.
     * @throws NullPointerException If the provided {@link AudioNode AudioNode} was {@code null}.
     */
    public abstract void removeNode(@Nonnull AudioNode node);

    /**
     * Adds a <b>not-null</b> {@link AudioNode AudioNode} and opens a connection to it based on the information provided by the {@link AudioNodeEntry AudioNodeEntry}.
     * @param entry The {@link AudioNodeEntry AudioNodeEntry} containing the address, the port, etc. of the {@link AudioNode AudioNode}.
     * @throws NullPointerException If the provided {@link AudioNodeEntry entry} was {@code null}.
     */
    public abstract void addEntry(@Nonnull AudioNodeEntry entry);

    /**
     * Attempts to remove the {@link AudioNode AudioNode} associated with the provided, <b>not-null</b> {@link AudioNodeEntry AudioNodeEntry}.
     * <br><p>This will do nothing if no node was found, and it'll disconnect the client from the node and remove it if it was found.</p>
     * @param entry The <b>not-null</b> {@link AudioNodeEntry AudioNodeEntry} used to identify and remove the associated {@link AudioNode AudioNode}.
     * @throws NullPointerException If the provided {@link AudioNodeEntry AudioNodeEntry} was {@code null}.
     */
    public abstract void removeEntry(@Nonnull AudioNodeEntry entry);

    /**
     * Attempts to remove the {@link AudioNode AudioNode} associated with a provided server address and port.
     * <br><p>This will do nothing if no node was found, and it'll disconnect the client from the node and remove it if it was found.</p>
     * @param serverAddress The <b>not-null</b> raw address of the server without any scheme behind it.
     * @param websocketPort The <b>not-negative</b> WebSocket port of the Lavalink Server.
     * @throws NullPointerException If the provided server address was {@code null}.
     * @throws IllegalArgumentException If the provided WebSocket port was negative.
     */
    public abstract void removeEntry(@Nonnull String serverAddress, @Nonnegative int websocketPort);

    /**
     * Fetches the internal identifier cache used by LavaClient to cut down the impact of loading the same sources in quick succession.
     * <br><p>Note: Editing this cache might cause some delay and bugs. Modifying it is your choice.</p>
     * @return the internal cache LavaClient uses to cut down repeated song loading impact.
     */
    @Nonnull
    public abstract Cache<String, TrackDataPair> getIdentifierCache();

    /**
     * Fetches an <b>unmodifiable</b> view of the internal player map.
     * <br><p>Note: This returns an <b>unmodifiable</b> map, meaning any attempts to modify it will throw exceptions.</p>
     * @return An <b>unmodifiable</b> view of the internal player map.
     */
    @Nonnull
    public abstract Long2ObjectMap<LavaPlayer> getPlayerMap();

    /**
     * Shuts down LavaClient (also resetting the state).
     * <br><p>This method removes and disconnects from all {@link AudioNode AudioNodes} and additionally shuts down the attached {@link LavaHttpManager LavaHttpManager}.</p>
     * @throws IllegalStateException If this client has already been shutdown.
     */
    public abstract void shutdown();

    /**
     * Fetches the {@link AudioNode AudioNode} with the least amount of load on it, used to balance the load of {@link LavaPlayer LavaPlayers} on different nodes.
     * @return The best {@link AudioNode AudioNode} to connect to.
     * @throws IllegalStateException If no Lavalink node exists or is available.
     */
    @Nonnull
    public abstract AudioNode getBestNode();
}
