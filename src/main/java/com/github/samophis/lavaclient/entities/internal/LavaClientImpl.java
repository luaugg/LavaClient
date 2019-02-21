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

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.AudioNodeOptions;
import com.github.samophis.lavaclient.entities.LavaClient;
import com.github.samophis.lavaclient.entities.LavaPlayer;
import com.github.samophis.lavaclient.events.*;
import com.github.samophis.lavaclient.util.JsonPojoCodec;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
public class LavaClientImpl implements LavaClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(LavaClientImpl.class);
	private final Vertx vertx;
	private final List<AudioNode> nodes;
	private final TLongObjectHashMap<LavaPlayer> players;
	private final long userId;
	private final int numShards;
	private final WebClient client;

	public LavaClientImpl(final Vertx vertx, final List<AudioNode> nodes,
	                      final TLongObjectHashMap<LavaPlayer> players, final long userId, final int numShards) {
		this.vertx = vertx;
		this.nodes = nodes;
		this.players = players;
		this.userId = userId;
		this.numShards = numShards;
		client = WebClient.create(vertx);
		register(JsonObject.class);
		register(TrackStartEvent.class);
		register(TrackEndEvent.class);
		register(TrackExceptionEvent.class);
		register(TrackStuckEvent.class);
		register(PlayerPauseEvent.class);
		register(PlayerResumeEvent.class);
		register(PlayerUpdateEvent.class);
		register(StatsUpdateEvent.class);
		register(WebSocketClosedEvent.class);
		register(AudioNodeImpl.ControlMessage.class);
	}

	private <T> void register(@Nonnull final Class<T> cls) {
		vertx.eventBus().registerDefaultCodec(cls, new JsonPojoCodec<>(cls));
	}

	@Nonnull
	@Override
	public List<AudioNode> nodes() {
		return Collections.unmodifiableList(nodes);
	}

	@Nonnull
	@Override
	public List<LavaPlayer> players() {
		return List.copyOf(players.valueCollection());
	}

	@Nonnull
	@Override
	public LavaPlayer newPlayer(@Nonnegative final long guildId) {
		if (guildId < 0) {
			throw new IllegalArgumentException("negative guild id!");
		}
		LavaPlayer player = players.get(guildId);
		if (player == null) {
			player = new LavaPlayerImpl(this, guildId);
			players.put(guildId, player);
		}
		return player;
	}

	@Nonnull
	@Override
	public LavaPlayer removePlayer(@Nonnegative final long guildId, final boolean shouldDestroy) {
		if (guildId < 0) {
			throw new IllegalArgumentException("negative guild id!");
		}
		final LavaPlayer player = players.remove(guildId);
		if (player == null) {
			throw new IllegalArgumentException("no player associated with guild id: " + guildId);
		}
		// should destroy logic
		return player;
	}

	@Nullable
	@Override
	public AudioNode node(@Nonnull final String baseUrl) {
		for (final AudioNode node : nodes) {
			if (node.baseUrl().equals(baseUrl)) {
				return node;
			}
		}
		return null;
	}

	@Nullable
	@Override
	public LavaPlayer player(@Nonnegative final long guildId) {
		if (guildId < 0) {
			throw new IllegalArgumentException("negative guild id!");
		}
		return players.get(guildId);
	}

	@Nonnull
	@Override
	public AudioNode bestNode() {
		// https://github.com/natanbc/andesite-node/issues/1
		var currentPenalty = Integer.MAX_VALUE; // in the odd case that no stats updates have been received.
		var node = (AudioNode) null;
		for (final var nd : nodes) {
			if (!nd.available()) {
				continue;
			}
			final var penalty = nd.loadBalancer().updatePenalties().totalPenalty();
			if (penalty < currentPenalty) {
				currentPenalty = penalty;
				node = nd;
			}
		}
		if (node == null) {
			LOGGER.warn("no nodes could be found!");
			throw new IllegalStateException("no nodes could be found!");
		}
		if (!node.available()) {
			LOGGER.warn("no available nodes could be found!");
			throw new IllegalStateException("no available nodes could be found!");
		}
		return node;
	}

	@Override
	public void addNode(@Nonnull final AudioNode node) {
		nodes.add(node);
		vertx.deployVerticle((AudioNodeImpl) node, _result -> node.openConnection());
	}

	@Override
	public void removeNode(@Nonnull final String baseUrl) {
		final var node = (AudioNodeImpl) node(baseUrl);
		if (node == null) {
			return;
		}
		vertx.undeploy(node.deploymentID());
		nodes.remove(node);
	}

	@Override
	public void shutdown() {
		nodes.forEach(node -> {
			final var impl = (AudioNodeImpl) node;
			vertx.undeploy(impl.deploymentID());
			nodes.remove(node);
		});
		client.close();
		vertx.close();
	}

	@Nonnull
	@Override
	public AudioNode nodeFrom(@Nonnull final AudioNodeOptions options) {
		final var host = options.host();
		if (host == null) {
			throw new IllegalArgumentException("host/address is null!");
		}
		var password = options.host();
		if (password == null) {
			password = "";
		}
		final var port = options.port();
		if (port <= 0) {
			throw new IllegalArgumentException("port is smaller or equal to 0");
		}
		return new AudioNodeImpl(this, host, options.relativePath(), password, port);
	}
}
