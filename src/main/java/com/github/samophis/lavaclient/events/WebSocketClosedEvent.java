package com.github.samophis.lavaclient.events;

import com.github.samophis.lavaclient.entities.AudioNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

@Getter(onMethod_ = {@CheckReturnValue, @Nonnull})
@Accessors(fluent = true)
@RequiredArgsConstructor
public class WebSocketClosedEvent implements LavalinkEvent {
	private final AudioNode node;
	private final EventType type = EventType.WEBSOCKET_CLOSED_EVENT;
	private final String reason;

	@Getter(onMethod_ = @CheckReturnValue)
	private final boolean byRemote;
	@Getter(onMethod_ = {@CheckReturnValue, @Nonnegative})
	private final int closeCode;
}
