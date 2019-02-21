/*
   Copyright 2019 Sam Pritchard

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
package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.*;
import com.github.samophis.lavaclient.events.*;
import com.github.samophis.lavaclient.exceptions.RemoteTrackException;
import com.github.samophis.lavaclient.util.AudioTrackUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Accessors(fluent = true)
public class AudioNodeImpl extends AbstractVerticle implements AudioNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(AudioNodeImpl.class);
	@Getter private final LavaClient client;
	@Getter private final String baseUrl;
	@Getter private final String relativePath;
	@Getter private final String password;
	@Getter private final int port;
	@Getter private final String websocketUrl;
	@Getter private final String restUrl;
	@Getter private final LoadBalancer loadBalancer;
	@Getter private boolean available;

	private final String controlAddress;
	private final String recvAddress;
	@Getter(AccessLevel.PACKAGE) private final String sendAddress;

	@Setter @Getter private Statistics statistics;
	private HttpClient httpClient;
	private WebSocket socket;
	private List<MessageConsumer<?>> consumers;

	AudioNodeImpl(@Nonnull final LavaClient client, @Nonnull final String baseUrl,
	              @Nullable final String relativePath, @Nonnull final String password,
	              @Nonnegative final int port) {
		this.client = client;
		this.baseUrl = baseUrl;
		this.relativePath = relativePath;
		this.password = password;
		this.port = port;
		websocketUrl = String.format("ws://%s:%d%s", baseUrl, port, relativePath != null ? "/" + relativePath : "");
		restUrl = String.format("http://%s:%d", baseUrl, port);
		loadBalancer = new LoadBalancerImpl(this);
		controlAddress = controlMessageAddress();
		recvAddress = eventRecvMessageAddress();
		sendAddress = eventSendMessageAddress();
	}

	@Override
	public void openConnection() {
		openConnection0(null);
	}

	@Override
	public void openConnection(@Nonnull final Runnable callback) {
		openConnection0(callback);
	}

	private void openConnection0(@Nullable final Runnable callback) {
		if (available) {
			throw new IllegalStateException(String.format("connection to audio node: %s is already open!", baseUrl));
		}
		vertx.eventBus().send(controlAddress, new ControlMessage<>(ControlKey.CONNECT, callback));
	}

	@Override
	public void closeConnection() {
		closeConnection0(null);
	}

	@Override
	public void closeConnection(@Nonnull final Runnable callback) {
		closeConnection0(callback);
	}

	private void closeConnection0(@Nullable final Runnable callback) {
		if (!available) {
			throw new IllegalStateException(String.format("connection to audio node: %s is already closed!", baseUrl));
		}
		vertx.eventBus().send(controlAddress, new ControlMessage<>(ControlKey.DISCONNECT, callback));
	}

	@Override
	public void start() {
		final var bus = vertx.eventBus();
		consumers = new ArrayList<>(2);
		consumers.add(bus.consumer(controlAddress, this::handleControlMessage));
		consumers.add(bus.consumer(sendAddress, this::handleSentEvent));
		httpClient = vertx.createHttpClient();
	}

	@Override
	public void stop() {
		closeConnection();
		httpClient.close();
		httpClient = null;
		consumers.forEach(MessageConsumer::unregister);
	}

	private void handleControlMessage(@Nonnull final Message<?> msg) {
		var message = (ControlMessage<?>) msg.body();
		switch (message.key) {
			case CONNECT:
				final var onConnect = (Runnable) message.object;
				if (socket != null) {
					throw new IllegalStateException("socket already created!");
				}
				var headers = MultiMap.caseInsensitiveMultiMap()
				                      .add("Authorization", password)
				                      .add("User-Id", String.valueOf(client.userId()))
				                      .add("Num-Shards", String.valueOf(client.numShards()));
				httpClient.websocketAbs(websocketUrl, headers, null, null, ws -> {
					socket = ws;
					available = true;
					if (onConnect != null) {
						onConnect.run();
					}
					socket.textMessageHandler(this::handleReceivedMessage);
				}, err -> LOGGER.error("Error establishing a WebSocket connection! {}", err));
				break;
			case DISCONNECT:
				final var onDisconnect = (Runnable) message.object;
				if (socket == null) {
					throw new IllegalStateException("socket not created yet!");
				}
				try {
					final var best = client.bestNode();
					client.players()
					      .parallelStream()
					      .filter(player -> this.equals(player.connectedNode()))
					      .forEach(player -> player.connect(best));
				} catch (final IllegalStateException exc) {
					client.players()
					      .parallelStream()
					      .filter(player -> this.equals(player.connectedNode()))
					      .forEach(LavaPlayer::destroy);
				}

				socket.closeHandler(_vd -> {
					available = false;
					socket = null;
					if (onDisconnect != null) {
						onDisconnect.run();
					}
				});
				socket.close((short) 1000);
		}
	}

	private void handleSentEvent(@Nonnull final Message<JsonObject> msg) {
		if (!available) {
			throw new IllegalStateException("socket isn't available!");
		}
		socket.writeTextMessage(msg.body().toString());
	}

	@CheckReturnValue
	@Nonnull
	private LavaPlayerImpl playerFromRawData(@Nonnull final JsonObject data) {
		final var guildId = data.getString("guildId");
		var player = (LavaPlayerImpl) client.player(Long.parseUnsignedLong(guildId));
		if (player == null) {
			LOGGER.warn("unknown player for guild id: {}", guildId);
			throw new IllegalStateException("unknown player | guild id: " + guildId);
		}
		return player;
	}

	@CheckReturnValue
	@Nonnull
	private LavalinkEvent eventFromRawStats(@Nonnull final JsonObject stats) {
		// objects
		final var cpuObject = stats.getJsonObject("cpu");
		final var memObject = stats.getJsonObject("memory");
		final var frameObject = stats.getJsonObject("frameStats", null);
		final var cpuCores = cpuObject.getInteger("cores");
		// actual fields
		final var players = stats.getInteger("players");
		final var playingPlayers = stats.getInteger("playingPlayers");
		final var systemLoad = cpuObject.getDouble("systemLoad");
		final var lavalinkLoad = cpuObject.getDouble("lavalinkLoad");
		final var uptime = stats.getLong("uptime");

		final var freeMemory = memObject.getInteger("free");
		final var allocatedMemory = memObject.getInteger("allocated");
		final var usedMemory = memObject.getInteger("used");
		final var reservableMemory = memObject.getInteger("reservable");

		var sentFrames = (Long) null;
		var nulledFrames = (Long) null;
		var deficitFrames = (Long) null;
		if (frameObject != null) {
			sentFrames = frameObject.getLong("sent");
			nulledFrames = frameObject.getLong("nulled");
			deficitFrames = frameObject.getLong("deficit");
		}
		final var statistics = new StatisticsImpl()
				.players(players)
				.playingPlayers(playingPlayers)
				.uptime(uptime)
				.cpuCores(cpuCores)
				.systemLoad(systemLoad)
				.lavalinkLoad(lavalinkLoad)
				.usedMemory(usedMemory)
				.allocatedMemory(allocatedMemory)
				.freeMemory(freeMemory)
				.reservableMemory(reservableMemory)
				.sentFrames(sentFrames)
				.nulledFrames(nulledFrames)
				.deficitFrames(deficitFrames);
		return new StatsUpdateEvent(this, statistics);
	}

	@CheckReturnValue
	@Nonnull
	private LavalinkEvent eventFromRawPlayerUpdate(@Nonnull final JsonObject playerUpdate) {
		final var player = playerFromRawData(playerUpdate);
		final var state = playerUpdate.getJsonObject("state");
		final var timestamp = state.getLong("time");
		final var position = state.getLong("position");
		if (position != null) {
			player.position(position);
		}
		if (timestamp != null) {
			player.timestamp(timestamp);
		}
		return new PlayerUpdateEvent(this, player, timestamp, position);
	}

	@CheckReturnValue
	@Nonnull
	private LavalinkEvent eventFromRawLavalinkEvent(@Nonnull final JsonObject lavalinkEvent) {
		final var type = lavalinkEvent.getString("type");
		switch (type) {
			case "TrackEndEvent":
				final var player1 = playerFromRawData(lavalinkEvent);
				player1.playingTrack(null);
				final var track1 = AudioTrackUtil.fromString(lavalinkEvent.getString("track"));
				final var reason = AudioTrackEndReason.valueOf(lavalinkEvent.getString("reason"));
				return new TrackEndEvent(track1, this, player1, reason);
			case "TrackStuckEvent":
				final var player2 = playerFromRawData(lavalinkEvent);
				final var track2 = AudioTrackUtil.fromString(lavalinkEvent.getString("track"));
				return new TrackStuckEvent(track2, this, player2, lavalinkEvent.getLong("thresholdMs"));
			case "TrackExceptionEvent":
				final var player3 = playerFromRawData(lavalinkEvent);
				final var track3 = AudioTrackUtil.fromString(lavalinkEvent.getString("track"));
				return new TrackExceptionEvent(track3, this, player3, new RemoteTrackException(track3, this,
						player3, lavalinkEvent.getString("error")));
			case "WebSocketClosedEvent":
				final var player4 = playerFromRawData(lavalinkEvent);
				player4.playingTrack(null); // probably safer to do this instead of maintaining old state
				return new WebSocketClosedEvent(this, lavalinkEvent.getString("reason"), lavalinkEvent.getBoolean("byRemote"),
						lavalinkEvent.getInteger("code"));
			default:
				LOGGER.warn("Unsupported Event Type: {}", type);
				throw new UnsupportedOperationException(type);
		}
	}

	private void handleReceivedMessage(@Nonnull final String msg) {
		final var json = new JsonObject(msg);
		final var op = json.getString("op", "unknown");
		var event = (LavalinkEvent) null;
		switch (op) {
			case "stats":
				event = eventFromRawStats(json);
				break;
			case "playerUpdate":
				event = eventFromRawPlayerUpdate(json);
				break;
			case "event":
				event = eventFromRawLavalinkEvent(json);
				break;
			case "unknown":
				LOGGER.warn("json payload has no opcode! {}", json.toString());
				break;
			default:
				LOGGER.warn("unsupported lavalink opcode! {}", op);
		}
		vertx.eventBus().publish(recvAddress, event);
	}

	@CheckReturnValue
	@Nonnull
	private String controlMessageAddress() {
		return String.format("lavaclient:%s:control", baseUrl);
	}

	@CheckReturnValue
	@Nonnull
	private String eventRecvMessageAddress() {
		return String.format("lavaclient:%s:event-recv", baseUrl);
	}

	@CheckReturnValue
	@Nonnull
	private String eventSendMessageAddress() {
		return String.format("lavaclient:%s:event-send", baseUrl);
	}

	@Override
	public void on(@Nonnull final Consumer<LavalinkEvent> handler) {
		vertx.eventBus().consumer(recvAddress, msg -> {
			final var event = (LavalinkEvent) msg.body();
			handler.accept(event);
		});
	}

	class ControlMessage<V> {
		private final ControlKey key;
		private final V object;
		private ControlMessage(final ControlKey key, final V object) {
			this.key = key;
			this.object = object;
		}
	}
	private enum ControlKey {
		CONNECT,
		DISCONNECT
	}
}
