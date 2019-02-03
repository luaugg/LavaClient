package com.github.samophis.lavaclient.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface AudioNode {
	@Nullable
	@CheckReturnValue
	Statistics statistics();

	@Nonnull
	@CheckReturnValue
	LoadBalancer loadBalancer();

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

	@Nonnull
	@CheckReturnValue
	String password();

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
