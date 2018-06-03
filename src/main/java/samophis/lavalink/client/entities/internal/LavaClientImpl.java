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
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import samophis.lavalink.client.entities.*;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LavaClientImpl implements LavaClient {
    public static final Map<String, AudioNode> NODES = new Object2ObjectOpenHashMap<>();
    @SuppressWarnings("WeakerAccess")
    public static final Long2ObjectMap<LavaPlayer> PLAYERS = new Long2ObjectOpenHashMap<>();
    private final LavaHttpManager manager;
    private final String password;
    private final int restPort, wsPort, shards;
    private final long expireWriteMs, expireAccessMs, userId;
    private final Cache<String, TrackPair> identifierCache;
    public LavaClientImpl(String password, int restPort, int wsPort, int shards, long expireWriteMs, long expireAccessMs, long userId, List<AudioNodeEntry> entries) {
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
        entries.forEach(entry -> NODES.put(entry.getRawAddress() + entry.getWebSocketPort(), new AudioNodeImpl(this, entry)));
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
        NODES.put(entry.getRawAddress() + entry.getWebSocketPort(), node);
    }
    @Override
    public void addEntry(@Nonnull AudioNodeEntry entry) {
        Asserter.requireNotNull(entry);
        NODES.put(entry.getRawAddress() + entry.getWebSocketPort(), new AudioNodeImpl(this, entry));
    }
    @Override
    public void removeEntry(@Nonnull AudioNodeEntry entry) {
        Asserter.requireNotNull(entry);
        AudioNode node = NODES.remove(entry.getRawAddress() + entry.getWebSocketPort());
        if (node != null)
            node.getSocket().disconnect(1000, "Client requested removal of entry!");
    }
    @Override
    public void removeEntry(@Nonnull String serverAddress, int websocketPort) {
        AudioNode node = NODES.remove(Asserter.requireNotNull(serverAddress) + Asserter.requireNotNegative(websocketPort));
        if (node != null)
            node.getSocket().disconnect(1000, "Client requested removal of entry!");
    }
    @Override
    public void removeNode(@Nonnull AudioNode node) {
        AudioNodeEntry entry = Asserter.requireNotNull(node).getEntry();
        AudioNode nd = NODES.remove(entry.getRawAddress() + entry.getWebSocketPort());
        if (nd != null)
            nd.getSocket().disconnect(1000, "Client requested removal of node!");
    }
    @Nonnull
    @Override
    public List<AudioNode> getAudioNodes() {
        return Collections.unmodifiableList((List<AudioNode>) NODES.values());
    }
    @Nonnull
    @Override
    public List<LavaPlayer> getPlayers() {
        return ObjectLists.unmodifiable((ObjectList<LavaPlayer>) PLAYERS.values());
    }
    @Nonnull
    @Override
    public LavaPlayer getPlayerByGuildId(@Nonnegative long guild_id) {
        return PLAYERS.computeIfAbsent(Asserter.requireNotNegative(guild_id), ignored -> new LavaPlayerImpl(this, ignored));
    }
    @Nullable
    @Override
    public AudioNode getNodeByIdentifier(@Nonnull String address, @Nonnegative int websocketPort) {
        return NODES.get(Asserter.requireNotNull(address) + Asserter.requireNotNegative(websocketPort));
    }
    @Nonnull
    @Override
    public Cache<String, TrackPair> getIdentifierCache() {
        return identifierCache;
    }
}
