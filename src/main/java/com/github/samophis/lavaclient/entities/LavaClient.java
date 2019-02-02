package com.github.samophis.lavaclient.entities;

import io.vertx.core.Vertx;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
}
