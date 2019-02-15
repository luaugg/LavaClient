package com.github.samophis.lavaclient.events;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LavaPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@Getter(onMethod_ = {@CheckReturnValue, @Nonnull})
@Accessors(fluent = true)
@RequiredArgsConstructor
public class PlayerResumeEvent implements LavalinkEvent {
	private final AudioNode node;
	private final LavaPlayer player;
	private final EventType type = EventType.PLAYER_RESUME_EVENT;
}
