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

package samophis.lavalink.client.entities.internal;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import samophis.lavalink.client.entities.*;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LavaClientImpl extends LavaClient {
    private final LavaHttpManager manager;
    private final String password;
    private final int restPort, wsPort, shards;
    private final long expireWriteMs, expireAccessMs, userId;
    private final Cache<String, TrackDataPair> identifierCache;
    private boolean isShutdown;
    public LavaClientImpl(String password, int restPort, int wsPort, int shards, long expireWriteMs, long expireAccessMs, long userId, List<AudioNodeEntry> entries) {
        super();
        this.manager = new LavaHttpManagerImpl(this);
        this.password = Asserter.requireNotNull(password);
        this.restPort = Asserter.requireNotNegative(restPort);
        this.wsPort = Asserter.requireNotNegative(wsPort);
        this.shards = Asserter.requireNotNegative(shards);
        this.expireWriteMs = Asserter.requireNotNegative(expireWriteMs);
        this.expireAccessMs = Asserter.requireNotNegative(expireAccessMs);
        this.userId = Asserter.requireNotNegative(userId);
        this.identifierCache = Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(5000)
                .expireAfterWrite(expireWriteMs, TimeUnit.MILLISECONDS)
                .expireAfterAccess(expireAccessMs, TimeUnit.MILLISECONDS)
                .build();
        entries.forEach(entry -> {
            AudioNodeEntryImpl impl = (AudioNodeEntryImpl) entry;
            if (impl.isFileBased())
                impl.setClient(this);
            nodes.put(entry.getRawAddress() + entry.getWebSocketPort(), new AudioNodeImpl(this, impl));
        });
        this.isShutdown = false;
    }
    @Nonnull
    @Override
    public LavaHttpManager getHttpManager() {
        return manager;
    }
    @Nonnull
    @Override
    public String getGlobalServerPassword() {
        return password;
    }
    @Nonnegative
    @Override
    public long getCacheExpireAfterWriteMs() {
        return expireWriteMs;
    }
    @Nonnegative
    @Override
    public long getCacheExpireAfterAccessMs() {
        return expireAccessMs;
    }
    @Nonnegative
    @Override
    public int getGlobalRestPort() {
        return restPort;
    }
    @Nonnegative
    @Override
    public int getGlobalWebSocketPort() {
        return wsPort;
    }
    @Nonnegative
    @Override
    public int getShardCount() {
        return shards;
    }
    @Nonnegative
    @Override
    public long getUserId() {
        return userId;
    }
    @Override
    public void addNode(@Nonnull AudioNode node) {
        AudioNodeEntry entry = Asserter.requireNotNull(node).getEntry();
        nodes.put(entry.getRawAddress() + entry.getWebSocketPort(), node);
    }
    @Override
    public void addEntry(@Nonnull AudioNodeEntry entry) {
        Asserter.requireNotNull(entry);
        nodes.put(entry.getRawAddress() + entry.getWebSocketPort(), new AudioNodeImpl(this, (AudioNodeEntryImpl) entry));
    }
    @Override
    public void removeEntry(@Nonnull AudioNodeEntry entry) {
        Asserter.requireNotNull(entry);
        AudioNode node = nodes.remove(entry.getRawAddress() + entry.getWebSocketPort());
        if (node != null)
            node.getSocket().disconnect(1000, "Client requested removal/disconnection of entry!");
    }
    @Override
    public void removeEntry(@Nonnull String serverAddress, int websocketPort) {
        AudioNode node = nodes.remove(Asserter.requireNotNull(serverAddress) + Asserter.requireNotNegative(websocketPort));
        if (node != null)
            node.getSocket().disconnect(1000, "Client requested removal/disconnection of entry!");
    }
    @Override
    public void removeNode(@Nonnull AudioNode node) {
        AudioNodeEntry entry = Asserter.requireNotNull(node).getEntry();
        AudioNode nd = nodes.remove(entry.getRawAddress() + entry.getWebSocketPort());
        if (nd != null)
            nd.getSocket().disconnect(1000, "Client requested removal/disconnection of node!");
    }
    @Nonnull
    @Override
    public List<AudioNode> getAudioNodes() {
        return Collections.unmodifiableList((List<AudioNode>) nodes.values());
    }
    @Nonnull
    @Override
    public List<LavaPlayer> getPlayers() {
        return ObjectLists.unmodifiable((ObjectList<LavaPlayer>) players.values());
    }
    @Nullable
    @Override
    public LavaPlayer getPlayerByGuildId(@Nonnegative long guild_id) {
        return players.get(Asserter.requireNotNegative(guild_id));
    }
    @Nonnull
    @Override
    public LavaPlayer newPlayer(@Nullable AudioNode node, long guild_id) {
        return players.computeIfAbsent(Asserter.requireNotNegative(guild_id), id -> {
            LavaPlayer player = new LavaPlayerImpl(this, id);
            player.setNode(node == null ? getBestNode() : node);
            return player;
        });
    }
    @Nonnull
    @Override
    public LavaPlayer newPlayer(long guild_id) {
        return newPlayer(null, guild_id);
    }
    @Nullable
    @Override
    public AudioNode getNodeByIdentifier(@Nonnull String address, @Nonnegative int websocketPort) {
        return nodes.get(Asserter.requireNotNull(address) + Asserter.requireNotNegative(websocketPort));
    }
    @Nonnull
    @Override
    public Cache<String, TrackDataPair> getIdentifierCache() {
        return identifierCache;
    }
    @Nonnull
    @Override
    public Long2ObjectMap<LavaPlayer> getPlayerMap() {
        return Long2ObjectMaps.unmodifiable(players);
    }
    @Nullable
    @Override
    public LavaPlayer removePlayer(long guild_id) {
        return removePlayer(guild_id, false);
    }
    @Nullable
    @Override
    public LavaPlayer removePlayer(long guild_id, boolean shouldDestroy) {
        LavaPlayer player = players.get(Asserter.requireNotNegative(guild_id));
        if (player == null)
            return null;
        if (shouldDestroy)
            player.destroyPlayer();
        return players.remove(guild_id);
    }
    @Override
    public void shutdown() {
        if (isShutdown)
            throw new IllegalStateException("This LavaClient instance is already shutdown!");
        isShutdown = true;
        players.values().forEach(player -> removePlayer(player.getGuildId(), true));
        nodes.values().forEach(this::removeNode);
        manager.shutdown();
    }
    @Nonnull
    @Override
    public AudioNode getBestNode() {
        AudioNode node = null;
        int record = Integer.MAX_VALUE;
        for (AudioNode nd : nodes.values()) {
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
