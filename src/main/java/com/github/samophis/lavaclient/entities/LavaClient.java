package com.github.samophis.lavaclient.entities;

import io.vertx.core.Vertx;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public interface LavaClient {
	@Nonnull
	@CheckReturnValue
	Vertx vertx();

    @Nonnull
    @CheckReturnValue
    List<AudioNode> nodes();

    @Nonnull
	@CheckReturnValue
	List<LavaPlayer> players();

    @Nullable
    @CheckReturnValue
    AudioNode node(@Nonnull final String baseUrl);

    @Nullable
	@CheckReturnValue
	LavaPlayer player(@Nonnegative final long guildId);

    @Nonnull
	@CheckReturnValue
	LavaPlayer newPlayer(@Nonnegative final long guildId);

    @Nonnull
	LavaPlayer removePlayer(@Nonnegative final long guildId, final boolean shouldDestroy);

	@Nonnull
	default LavaPlayer removePlayer(@Nonnegative final long guildId) {
		return removePlayer(guildId, true);
	}

	@Nonnegative
	@CheckReturnValue
	long userId();

	@Nonnegative
	@CheckReturnValue
	int numShards();
}
