package com.github.samophis.lavaclient.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AudioNode {
	@Nonnull
	@CheckReturnValue
	LavaClient client();

	@Nonnull
	@CheckReturnValue
	String baseUrl();

	@Nonnull
	@CheckReturnValue
	String websocketUrl();

	@Nonnull
	@CheckReturnValue
	String restUrl();

	@Nullable
	@CheckReturnValue
	Runnable onConnectCallback();

	@Nullable
	@CheckReturnValue
	Runnable onDisconnectCallback();

	@Nonnegative
	@CheckReturnValue
	int port();

	@CheckReturnValue
	boolean available();

	void openConnection();

	void openConnection(@Nonnull final Runnable callback);

	void closeConnection();

	void closeConnection(@Nonnull final Runnable callback);
}
