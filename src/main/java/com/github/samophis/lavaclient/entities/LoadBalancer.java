package com.github.samophis.lavaclient.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface LoadBalancer {
	@CheckReturnValue
	@Nonnull
	AudioNode node();

	@CheckReturnValue
	@Nonnegative
	int playerPenalty();

	@CheckReturnValue
	@Nonnegative
	int cpuPenalty();

	@CheckReturnValue
	@Nonnegative
	int deficitFramePenalty();

	@CheckReturnValue
	@Nonnegative
	int nullFramePenalty();

	@CheckReturnValue
	@Nonnegative
	default int totalPenalty() {
		return node().statistics() == null || !node().available()
				? Integer.MAX_VALUE - 1
		       : playerPenalty() + cpuPenalty() + deficitFramePenalty() + nullFramePenalty();
	}

	@CheckReturnValue
	@Nonnull
	LoadBalancer updatePenalties();
}
