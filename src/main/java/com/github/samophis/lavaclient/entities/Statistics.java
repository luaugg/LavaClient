package com.github.samophis.lavaclient.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface Statistics {
	@CheckReturnValue
	@Nonnegative
	int players();

	@CheckReturnValue
	@Nonnegative
	int playingPlayers();

	@CheckReturnValue
	@Nonnegative
	long uptime();

	@CheckReturnValue
	@Nonnegative
	long freeMemory();

	@CheckReturnValue
	@Nonnegative
	long allocatedMemory();

	@CheckReturnValue
	@Nonnegative
	long usedMemory();

	@CheckReturnValue
	@Nonnegative
	long reservableMemory();

	@CheckReturnValue
	@Nonnegative
	int cpuCores();

	@CheckReturnValue
	@Nonnegative
	double systemLoad();

	@CheckReturnValue
	@Nonnegative
	double lavalinkLoad();

	@CheckReturnValue
	@Nullable
	@Nonnegative
	Long sentFrames();

	@CheckReturnValue
	@Nullable
	@Nonnegative
	Long nulledFrames();

	@CheckReturnValue
	@Nullable
	@Nonnegative
	Long deficitFrames();
}
