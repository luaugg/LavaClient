package com.github.samophis.lavaclient.events;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LavaPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class PlayerUpdateEvent implements LavalinkEvent {
	@Getter(onMethod_ = {@CheckReturnValue, @Nonnull})
	private final AudioNode node;
	@Getter(onMethod_ = {@CheckReturnValue, @Nonnull})
	private final LavaPlayer player;

	@Getter(onMethod_ = {@CheckReturnValue, @Nonnegative})
	private final long timestamp;
	@Getter(onMethod_ = {@CheckReturnValue, @Nonnegative})
	private final long position;

	@Getter
	private final EventType type = EventType.PLAYER_UPDATE_EVENT;
}
