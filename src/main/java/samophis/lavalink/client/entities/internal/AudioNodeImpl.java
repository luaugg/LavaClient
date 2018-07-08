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
import samophis.lavalink.client.exceptions.SocketConnectionException;
import samophis.lavalink.client.util.Asserter;
import samophis.lavalink.client.util.LavaClientUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class AudioNodeImpl extends WebSocketAdapter implements AudioNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(AudioNodeImpl.class);
    private final LavaClient client;
    private final LoadBalancer balancer;
    private final AudioNodeEntryImpl entry;
    private final ScheduledExecutorService scheduler;
    private final WebSocketFactory factory;
    private volatile WebSocket socket;
    private volatile Statistics statistics;
    private final AtomicReference<ScheduledFuture<?>> delayTask;
    private final AtomicInteger reconnectAttempts;
    private final AtomicLong reconnectInterval;
    private final TimeUnit intervalUnit;
    private final ReconnectIntervalFunction intervalExpander;
    private final long baseInterval, maxInterval;
    private volatile boolean usingVersionThree;
    private volatile SocketConnectionHandler connectHandler, disconnectHandler;
    private volatile ScheduledFuture<?> connectHandlerChecker, disconnectHandlerChecker;
    @SuppressWarnings("WeakerAccess")
    public AudioNodeImpl(@Nonnull LavaClient client, @Nonnull AudioNodeEntryImpl entry) {
        Asserter.requireNotNull(client);
        Asserter.requireNotNull(entry);
        this.client = client;
        this.balancer = new LoadBalancerImpl(this);
        this.entry = entry;
        this.scheduler = Executors.newScheduledThreadPool(5, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("SchedulerThread");
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.setUncaughtExceptionHandler((thread1, err) -> LOGGER.error("Error in SchedulerThread!", err));
            return thread;
        });
        this.baseInterval = entry.getBaseReconnectInterval();
        this.maxInterval = entry.getMaximumReconnectInterval();
        this.factory = entry.getWebSocketFactory();
        this.intervalUnit = entry.getIntervalTimeUnit();
        this.intervalExpander = entry.getIntervalExpander();
        this.delayTask = new AtomicReference<>();
        this.reconnectInterval = new AtomicLong(baseInterval);
        this.reconnectAttempts = new AtomicInteger(0);
        this.usingVersionThree = false;
        try {
            initSocket();
        } catch (IOException exc) {
            LOGGER.error("Exception when creating WebSocket! {}", exc.getMessage());
        }
        init();
    }
    private void initSocket() throws IOException {
        WebSocket socket = factory.createSocket(entry.getWebSocketAddress() + ":" + entry.getWebSocketPort());
        SocketInitializer init = entry.getSocketInitializer();
        if (init != null)
            socket = Asserter.requireNotNull(init.initialize(socket));
        this.socket = socket.addListener(this)
                .addHeader("Authorization", entry.getPassword())
                .addHeader("Num-Shards", String.valueOf(client.getShardCount()))
                .addHeader("User-Id", String.valueOf(client.getUserId()))
                .connectAsynchronously();
    }
    private void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!scheduler.isShutdown())
                scheduler.shutdownNow();
        }));
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
    private void resetDelayConditions(WebSocket websocket) {
        ScheduledFuture<?> innerTask = delayTask.get();
        if (innerTask != null)
            return;
        long time = reconnectInterval.getAndSet(intervalExpander.expand(this, reconnectInterval.get()));
        delayTask.set(scheduler.schedule(() -> {
            try {
                websocket.recreate().connectAsynchronously();
                delayTask.set(null);
            } catch (IOException exc) {
                LOGGER.error("Attempt #{}: Error when re-creating WebSocket! Message: {}", reconnectAttempts.get(), exc.getMessage());
                throw new SocketConnectionException(exc);
            }
        }, time, intervalUnit));
    }
    @Override
    @Nonnull
    public AudioNodeEntry getEntry() {
        return entry;
    }
    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) {
        socket = websocket;
        int attempts = reconnectAttempts.get();
        LOGGER.error((attempts > 0 ? "Attempt #" + attempts + ": " : "") + "Exception when asynchronously establishing a WebSocket connection! {}\n" +
                "LavaClient will try to reconnect in {} second(s).", exception.getMessage(), reconnectInterval.get());
        resetDelayConditions(websocket);
    }
    @Override
    public void onError(WebSocket websocket, WebSocketException cause){
        socket = websocket;
        LOGGER.error("Exception thrown during a WebSocket Connection! {}", cause.getMessage());
        throw new RuntimeException(cause);
    }
    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        socket = websocket;
        if (reconnectAttempts.get() == 0)
            LOGGER.info("Connected!");
        else
            LOGGER.info("Connected after {} Reconnect Attempt(s)!", reconnectAttempts.get() - 1);
        List<String> found = headers.get("Lavalink-Major-Version");
        this.usingVersionThree = (found != null && found.get(0).equals("3"));
        client.getPlayers().forEach(player -> {
            AudioNode node = client.getBestNode();
            if (node != null) {
                ((LavaPlayerImpl) player).setState(State.NOT_CONNECTED);
                player.setNode(node);
            }
        });
        reconnectInterval.set(baseInterval);
        reconnectAttempts.set(0);
        ScheduledFuture<?> task = delayTask.get();
        if (task != null) {
            task.cancel(true);
            delayTask.set(null);
        }
        if (connectHandlerChecker != null)
            connectHandlerChecker.cancel(true);
        if (connectHandler != null)
            connectHandler.handleConnection(this, socket);
        connectHandler = null;
    }
    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
        socket = websocket;
        client.getPlayers().forEach(player -> {
            AudioNode node = client.getBestNode();
            if (node == null)
                return;
            if (equals(player.getConnectedNode())) {
                ((LavaPlayerImpl) player).setState(State.NOT_CONNECTED);
                player.setNode(node);
            }
        });
        String reason = closedByServer ? serverCloseFrame.getCloseReason() : clientCloseFrame.getCloseReason();
        int code = closedByServer ? serverCloseFrame.getCloseCode() : clientCloseFrame.getCloseCode();
        if (closedByServer) {
            if (code == 1000 || code == 1001)
                LOGGER.info("Lavalink Server - {}:{} - closed the connection gracefully with the reason: {} and close code: {}", entry.getWebSocketAddress(), entry.getWebSocketPort(), reason, code);
            else {
                LOGGER.warn("Lavalink Server - {}:{} - closed the connection unexpectedly with the reason: {} and close code: {}", entry.getWebSocketAddress(), entry.getWebSocketPort(), reason, code);
                resetDelayConditions(websocket);
            }
        }
        if (disconnectHandlerChecker != null)
            disconnectHandlerChecker.cancel(true);
        if (disconnectHandler != null)
            disconnectHandler.handleConnection(this, socket);
        disconnectHandler = null;
    }
    @Override
    public void onTextMessage(WebSocket websocket, String text) {
        socket = websocket;
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
        return socket != null && socket.isOpen();
    }
    @Override
    @Nullable
    public WebSocket getSocket() {
        return socket;
    }
    @Override
    public boolean isUsingLavalinkVersionThree() {
        return usingVersionThree;
    }
    @Override
    public void openConnection() {
        if (socket != null && socket.isOpen()) {
            LOGGER.error("Attempt to open a connection to a node using the URI: {} when one already exists!", socket.getURI().toString());
            throw new IllegalStateException("Socket == OPEN");
        }
        try {
            initSocket();
        } catch (IOException exc) {
            LOGGER.error("Attempt to open a connection to a node using the URI: {} failed! Message: {}", socket.getURI().toString(), exc.getMessage());
            throw new SocketConnectionException(exc);
        }
    }
    @Override
    public void openConnection(@Nullable SocketConnectionHandler connectHandler) {
        this.connectHandler = connectHandler;
        this.connectHandlerChecker = scheduler.schedule(() -> {
            if (this.connectHandler != null)
                LOGGER.warn("Connection attempt with handler timed out!");
            this.connectHandler = null;
        }, 2, TimeUnit.SECONDS);
        openConnection();
    }
    @Override
    public void closeConnection() {
        if (socket == null) {
            LOGGER.error("Attempt to close a non-existent WebSocket on the node: {}", entry.getWebSocketAddress() + ":" + entry.getWebSocketPort());
            throw new IllegalStateException("Socket == NULL");
        }
        if (!socket.isOpen()) {
            LOGGER.error("Attempt to close the connection to a node using the URI: {} when it's already closed.", socket.getURI().toString());
            throw new IllegalStateException("Socket == CLOSED");
        }
        socket.disconnect(1000, "LavaClient requested disconnection from this node!");
        reconnectAttempts.set(0);
        reconnectInterval.set(baseInterval);
        ScheduledFuture<?> task = delayTask.get();
        if (task != null) {
            task.cancel(true);
            delayTask.set(null);
        }
    }
    @Override
    public void closeConnection(@Nullable SocketConnectionHandler disconnectHandler) {
        this.disconnectHandler = disconnectHandler;
        this.disconnectHandlerChecker = scheduler.schedule(() -> {
            if (this.disconnectHandler != null)
                LOGGER.warn("Disconnection attempt with attached handler timed out!");
            this.disconnectHandler = null;
        }, 2, TimeUnit.SECONDS);
        closeConnection();
    }
    @Override
    public int getReconnectAttempts() {
        return reconnectAttempts.get();
    }
    @Override
    public long getReconnectInterval() {
        return reconnectInterval.get();
    }
    @Override
    public long getBaseReconnectInterval() {
        return baseInterval;
    }
    @Override
    public long getMaximumReconnectInterval() {
        return maxInterval;
    }
    @Nonnull
    @Override
    public WebSocketFactory getWebSocketFactory() {
        return factory;
    }
    @Nonnull
    @Override
    public TimeUnit getIntervalTimeUnit() {
        return intervalUnit;
    }
    @Nonnull
    @Override
    public ReconnectIntervalFunction getIntervalExpander() {
        return intervalExpander;
    }
    @Nullable
    @Override
    public SocketConnectionHandler getConnectionCallback() {
        return connectHandler;
    }
    @Nullable
    @Override
    public SocketConnectionHandler getDisconnectionCallback() {
        return disconnectHandler;
    }
}
