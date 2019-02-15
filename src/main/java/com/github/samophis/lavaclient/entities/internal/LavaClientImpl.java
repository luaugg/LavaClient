package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LavaClient;
import com.github.samophis.lavaclient.entities.LavaPlayer;
import com.github.samophis.lavaclient.events.*;
import com.github.samophis.lavaclient.util.JsonPojoCodec;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
public class LavaClientImpl implements LavaClient {
	private final Vertx vertx;
	private final List<AudioNode> nodes;
	private final TLongObjectHashMap<LavaPlayer> players;
	private final long userId;
	private final int numShards;

	public LavaClientImpl(final Vertx vertx, final List<AudioNode> nodes,
	                      final TLongObjectHashMap<LavaPlayer> players, final long userId, final int numShards) {
		this.vertx = vertx;
		this.nodes = nodes;
		this.players = players;
		this.userId = userId;
		this.numShards = numShards;
		register(JsonObject.class);
		register(TrackStartEvent.class);
		register(TrackEndEvent.class);
		register(TrackExceptionEvent.class);
		register(TrackStuckEvent.class);
		register(PlayerPauseEvent.class);
		register(PlayerResumeEvent.class);
		register(PlayerUpdateEvent.class);
		register(StatsUpdateEvent.class);
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
}
