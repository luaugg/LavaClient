package com.github.samophis.lavaclient.entities;

import io.vertx.core.VertxOptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Accessors(fluent = true, chain = true)
@NoArgsConstructor
@SuppressWarnings("WeakerAccess")
public class LavaClientOptions {
	@Getter(onMethod_ = {@CheckReturnValue, @Nonnegative})
	@Setter(onMethod_ = {@CheckReturnValue, @Nonnull}, onParam_= {@Nonnegative})
	private int shardCount;

	@Getter(onMethod_ = {@CheckReturnValue, @Nonnegative})
	@Setter(onMethod_ = {@CheckReturnValue, @Nonnull}, onParam_= {@Nonnegative})
	private long userId;

	@Getter(onMethod_ = {@CheckReturnValue, @Nonnull})
	@Setter(onMethod_ = {@CheckReturnValue, @Nonnull}, onParam_= {@Nullable})
	private VertxOptions vertxOptions;
}
