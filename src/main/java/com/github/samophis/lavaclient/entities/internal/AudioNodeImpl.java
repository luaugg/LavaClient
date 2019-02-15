package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LavaClient;
import com.github.samophis.lavaclient.entities.LoadBalancer;
import com.github.samophis.lavaclient.entities.Statistics;
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
		// bus.consumer(recvAddress, this::handleReceivedEvent);
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

	private void handleReceivedEvent(@Nonnull final String msg) {

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
