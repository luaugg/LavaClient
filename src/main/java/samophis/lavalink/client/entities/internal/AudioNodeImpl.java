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

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.neovisionaries.ws.client.*;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.lavalink.client.entities.*;
import samophis.lavalink.client.entities.builders.StatisticsBuilder;
import samophis.lavalink.client.entities.events.PlayerTrackEvent;
import samophis.lavalink.client.entities.events.TrackEndEvent;
import samophis.lavalink.client.entities.events.TrackExceptionEvent;
import samophis.lavalink.client.entities.events.TrackStuckEvent;
import samophis.lavalink.client.entities.messages.server.PlayerUpdate;
import samophis.lavalink.client.entities.messages.server.Stats;
import samophis.lavalink.client.util.Asserter;
import samophis.lavalink.client.util.LavaClientUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AudioNodeImpl extends WebSocketAdapter implements AudioNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(AudioNodeImpl.class);
    private final LavaClient client;
    private final LoadBalancer balancer;
    private final AudioNodeEntryImpl entry;
    private WebSocket socket;
    private Statistics statistics;
    private AtomicInteger reconnectInterval, reconnectAttempts;
    private volatile boolean usingVersionThree;
    @SuppressWarnings("WeakerAccess")
    public AudioNodeImpl(@Nonnull LavaClient client, @Nonnull AudioNodeEntryImpl entry) {
        Asserter.requireNotNull(client);
        Asserter.requireNotNull(entry);
        this.client = client;
        this.balancer = new LoadBalancerImpl(this);
        this.entry = entry;
        this.reconnectInterval = new AtomicInteger(1000);
        this.reconnectAttempts = new AtomicInteger(0);
        this.usingVersionThree = false;
        try {
            WebSocket socket = new WebSocketFactory().createSocket(entry.getWebSocketAddress() + ":" + entry.getWebSocketPort());
            SocketInitializer init = entry.getSocketInitializer();
            if (init != null)
                socket = Asserter.requireNotNull(init.initialize(socket));
            this.socket = socket.addListener(this)
                    .addHeader("Authorization", entry.getPassword())
                    .addHeader("Num-Shards", String.valueOf(client.getShardCount()))
                    .addHeader("User-Id", String.valueOf(client.getUserId()))
                    .connectAsynchronously();
        } catch (IOException exc) {
            LOGGER.error("Unrecoverable Exception when creating WebSocket! {}", exc.getMessage());
            throw new RuntimeException(exc);
        }
        init();
    }
    private void init() {
        Map<String, SocketHandler> handlers = entry.getInternalHandlerMap();
        SocketHandler playerUpdateHandler = SocketHandler.from("playerUpdate", (socket, data) -> {
            Any json = JsonIterator.deserialize(data);
            long guild_id = Long.parseUnsignedLong(json.get("guildId").toString());
            LavaPlayer player = client.getPlayerByGuildId(guild_id);
            if (player == null) {
                LOGGER.error("Player is null for Guild ID: {}", guild_id);
                throw new IllegalStateException("Player == null");
            }
            PlayerUpdate update = JsonIterator.deserialize(data, PlayerUpdate.class);
            ((LavaPlayerImpl) player).setTimestamp(update.state.time)
                    .setPosition(update.state.position);
        });
        SocketHandler statsHandler = SocketHandler.from("stats", (socket, data) -> {
            Stats stats = JsonIterator.deserialize(data, Stats.class);
            StatisticsBuilder builder = new StatisticsBuilder(this)
                    .setPlayers(stats.players)
                    .setPlayingPlayers(stats.playingPlayers)
                    .setUptime(stats.uptime)
                    .setFree(stats.memory.free)
                    .setUsed(stats.memory.used)
                    .setAllocated(stats.memory.allocated)
                    .setReservable(stats.memory.reservable)
                    .setCores(stats.cpu.cores)
                    .setSystemLoad(stats.cpu.systemLoad)
                    .setLavalinkLoad(stats.cpu.lavalinkLoad);
            if (stats.frameStats != null) {
                builder.setSent(stats.frameStats.sent)
                        .setNulled(stats.frameStats.nulled)
                        .setDeficit(stats.frameStats.deficit);
            }
            statistics = builder.build();
        });
        SocketHandler trackEventHandler = SocketHandler.from("event", (socket, data) -> {
            Any json = JsonIterator.deserialize(data);
            long guild_id = Long.parseUnsignedLong(json.get("guildId").toString());
            LavaPlayer player = client.getPlayerByGuildId(guild_id);
            if (player == null) {
                LOGGER.error("Player is null for Guild ID: {}", guild_id);
                throw new IllegalStateException("Player == null");
            }
            ((LavaPlayerImpl) player).setTrack(null);
            AudioTrack track = LavaClientUtil.toAudioTrack(json.get("track").toString());
            String type = json.get("type").toString();
            PlayerTrackEvent event;
            switch (type) {
                case "TrackEndEvent":
                    event = new TrackEndEvent(player, track, AudioTrackEndReason.valueOf(json.get("reason").toString()));
                    break;
                case "TrackExceptionEvent":
                    event = new TrackExceptionEvent(player, track, new RuntimeException(json.get("error").toString()));
                    break;
                case "TrackStuckEvent":
                    event = new TrackStuckEvent(player, track, json.get("thresholdMs").toLong());
                    break;
                default:
                    LOGGER.warn("Unknown/unhandled Track Event from Lavalink, Name: {}, Guild ID: {}", type, guild_id);
                    throw new UnsupportedOperationException("Unknown + unhandled event from Lavalink: " + type);
            }
            player.emitEvent(event);
        });
        handlers.put(playerUpdateHandler.getName(), playerUpdateHandler);
        handlers.put(statsHandler.getName(), statsHandler);
        handlers.put(trackEventHandler.getName(), trackEventHandler);
    }
    @Override
    @Nonnull
    public AudioNodeEntry getEntry() {
        return entry;
    }
    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) {
        LOGGER.error("Unrecoverable exception when asynchronously establishing a WebSocket connection! {}", exception.getMessage());
        throw new RuntimeException(exception);
    }
    @Override
    public void onError(WebSocket websocket, WebSocketException cause){
        LOGGER.error("Exception thrown during a WebSocket Connection! {}", cause.getMessage());
        throw new RuntimeException(cause);
    }
    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        if (reconnectAttempts.get() == 0)
            LOGGER.info("Connected!");
        else
            LOGGER.info("Connected after {} Reconnect Attempt(s)!", reconnectAttempts.get());
        List<String> found = headers.get("Lavalink-Major-Version");
        this.usingVersionThree = (found != null && found.get(0).equals("3"));
        client.getPlayers().forEach(player -> player.setNode(client.getBestNode()));
        reconnectInterval.set(1000);
        reconnectAttempts.set(0);
    }
    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        client.getPlayers().forEach(player -> {
            if (equals(player.getConnectedNode()))
                player.setNode(client.getBestNode());
        });
        String reason = closedByServer ? serverCloseFrame.getCloseReason() : clientCloseFrame.getCloseReason();
        int code = closedByServer ? serverCloseFrame.getCloseCode() : clientCloseFrame.getCloseCode();
        if (closedByServer) {
            if (code == 1000)
                LOGGER.info("Lavalink Server - {}:{} - closed the connection gracefully with the reason: {}", entry.getWebSocketAddress(), entry.getWebSocketPort(), reason);
            else {
                LOGGER.warn("Lavalink Server - {}:{} - closed the connection unexpectedly with the reason: {}", entry.getWebSocketAddress(), entry.getWebSocketPort(), reason);
                int time = reconnectInterval.getAndSet(Math.min(reconnectInterval.get() * 2, 64000));
                LOGGER.info("Reconnect Attempt #{}: Attempting to reconnect in {}ms...", reconnectAttempts.incrementAndGet(), time);
                try {
                    Thread.sleep(time);
                } catch (InterruptedException exc) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(exc);
                }
                websocket.recreate().connectAsynchronously();
            }
        }
    }
    @Override
    public void onTextMessage(WebSocket websocket, String text) {
        Any data = JsonIterator.deserialize(text);
        String op = data.get("op").toString();
        SocketHandler handler = entry.getHandlerByName(op);
        if (handler == null) {
            LOGGER.warn("Missing/Unhandled Handler for OP: {}", op);
            return;
        }
        handler.handleIncoming(websocket, text);
    }
    @Override
    @Nonnull
    public LavaClient getClient() {
        return client;
    }
    @Override
    @Nullable
    public Statistics getStatistics() {
        return statistics;
    }
    @Override
    @Nonnull
    public LoadBalancer getBalancer() {
        return balancer;
    }
    @Override
    public boolean isAvailable() {
        return socket.isOpen();
    }
    @Override
    @Nonnull
    public WebSocket getSocket() {
        return socket;
    }
    @Override
    public boolean isUsingLavalinkVersionThree() {
        return usingVersionThree;
    }
}
