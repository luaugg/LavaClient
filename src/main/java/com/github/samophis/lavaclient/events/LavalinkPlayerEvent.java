package com.github.samophis.lavaclient.events;

import com.github.samophis.lavaclient.entities.LavaPlayer;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface LavalinkPlayerEvent extends LavalinkEvent {
	@CheckReturnValue
	@Nonnull
	LavaPlayer player();

	@Nonnegative
	@CheckReturnValue
	default long guildId() {
		return player().guildId();
	}
}
