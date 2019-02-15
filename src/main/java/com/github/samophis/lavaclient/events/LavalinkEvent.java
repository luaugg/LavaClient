package com.github.samophis.lavaclient.events;

import com.github.samophis.lavaclient.entities.AudioNode;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface LavalinkEvent {
	@Nonnull
	@CheckReturnValue
	AudioNode node();

	@Nonnull
	@CheckReturnValue
	EventType type();
}