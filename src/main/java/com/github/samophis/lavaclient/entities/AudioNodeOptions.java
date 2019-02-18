package com.github.samophis.lavaclient.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Setter(onMethod_ = {@CheckReturnValue, @Nonnull}, onParam_ = @Nullable)
@Getter(onMethod_ = {@CheckReturnValue, @Nullable})
@Accessors(fluent = true, chain = true)
@NoArgsConstructor
@SuppressWarnings("unused")
public class AudioNodeOptions {
	private String password;
	private String host;

	@Setter(onMethod_ = {@CheckReturnValue, @Nonnull}, onParam_ = @Nonnegative)
	private int port;
}
