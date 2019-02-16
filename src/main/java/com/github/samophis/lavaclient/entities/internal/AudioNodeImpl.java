package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.*;
import com.github.samophis.lavaclient.events.*;
import com.github.samophis.lavaclient.exceptions.RemoteTrackException;
import com.github.samophis.lavaclient.util.AudioTrackUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Accessors(fluent = true)
public class AudioNodeImpl extends AbstractVerticle implements AudioNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(AudioNodeImpl.class);
	@Getter private final LavaClient client;
	@Getter private final String baseUrl;
	@Getter private final String password;
	@Getter private final int port;
	@Getter private final String websocketUrl;
	@Getter private final String restUrl;
	@Getter private final LoadBalancer loadBalancer;
	@Getter private boolean available;

	private final String controlAddress;
	private final String recvAddress;
	private final String sendAddress;

	@Setter @Getter private Statistics statistics;
	private HttpClient httpClient;
	private WebSocket socket;

	public AudioNodeImpl(@Nonnull final LavaClient client, @Nonnull final String baseUrl,
	                     @Nonnull final String password, @Nonnegative final int port) {
		this.client = client;
		this.baseUrl = baseUrl;
		this.password = password;
		this.port = port;
		websocketUrl = String.format("ws://%s:%d", baseUrl, port);
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
		bus.consumer(controlAddress, this::handleControlMessage);
		bus.consumer(sendAddress, this::handleSentEvent);
		httpClient = vertx.createHttpClient();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
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
			case DISCONNECT:
				final var onDisconnect = (Runnable) message.object;
				if (socket == null) {
					throw new IllegalStateException("socket not created yet!");
				}
				client.players()
				      .parallelStream()
				      .filter(player -> this.equals(player.connectedNode()))
				      .forEach(player -> {
				      	client.removePlayer(player.guildId());
				      	// todo load balancing!!!!!!
				      });


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

	private void handleReceivedMessage(@Nonnull final String msg) {
		final var json = new JsonObject(msg);
		final var guildId = json.getString("guildId", null);
		var player = (LavaPlayerImpl) null;
		if (guildId != null) {
			player = (LavaPlayerImpl) client.player(Long.parseUnsignedLong(guildId));
			if (player == null) {
				LOGGER.warn("unknown player for guild id: {}", guildId);
				throw new IllegalStateException("unknown player | guild id: " + guildId);
			}
		}

		var event = (LavalinkEvent) null;
		final var op = json.getString("op");
		switch (op) {
			case "playerUpdate":
				if (player == null) {
					throw new IllegalStateException("report this bug to the developer(s), player null for update");
				}
				final var state = json.getJsonObject("state");
				final var timestamp = state.getLong("timestamp");
				final var position = state.getLong("position");
				player.position(position);
				player.timestamp(timestamp);
				event = new PlayerUpdateEvent(this, player, timestamp, position);
				break;
			case "stats":
				// objects
				final var cpuObject = json.getJsonObject("cpu");
				final var memObject = json.getJsonObject("memory");
				final var frameObject = json.getJsonObject("frameStats", null);
				final var cpuCores = cpuObject.getInteger("cores");
				// actual fields
				final var players = json.getInteger("players");
				final var playingPlayers = json.getInteger("playingPlayers");
				final var systemLoad = cpuObject.getDouble("systemLoad");
				final var lavalinkLoad = cpuObject.getDouble("lavalinkLoad");
				final var uptime = json.getLong("uptime");

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
				final var stats = new StatisticsImpl()
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
				statistics = stats;
				event = new StatsUpdateEvent(this, stats);
				break;
			case "event":
				final var type = json.getString("type");
				switch (type) {
					case "TrackEndEvent":
						if (player == null) {
							throw new IllegalStateException("report to developers | player null for track end");
						}
						player.playingTrack(null);
						final var track1 = AudioTrackUtil.fromString(json.getString("track"));
						final var reason = AudioTrackEndReason.valueOf(json.getString("reason"));
						event = new TrackEndEvent(track1, this, player, reason);
					case "TrackStuckEvent":
						if (player == null) {
							throw new IllegalStateException("report to developers | player null for track end");
						}
						final var track2 = AudioTrackUtil.fromString(json.getString("track"));
						event = new TrackStuckEvent(track2, this, player, json.getLong("thresholdMs"));
					case "TrackExceptionEvent":
						if (player == null) {
							throw new IllegalStateException("report to developers | player null for track end");
						}
						final var track3 = AudioTrackUtil.fromString(json.getString("track"));
						event = new TrackExceptionEvent(track3, this, player, new RemoteTrackException(track3, this,
								player, json.getString("error")));
					case "WebSocketClosedEvent":
						if (player == null) {
							throw new IllegalStateException("report to developers | player null for track end");
						}
						player.playingTrack(null); // probably safer to do this instead of maintaining old state
						event = new WebSocketClosedEvent(this, json.getString("reason"), json.getBoolean("byRemote"),
								json.getInteger("code"));
					default:
						LOGGER.warn("Unsupported Event Type: {}", type);
				}
				break;
			default:
				LOGGER.warn("Unsupported Incoming Message Type: {}", op);
		}
		if (event != null) {
			vertx.eventBus().publish(recvAddress, event);
		}
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

	private class ControlMessage<V> {
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
