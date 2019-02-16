package com.github.samophis.lavaclient.events;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.Statistics;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@RequiredArgsConstructor
@Getter(onMethod_ = {@Nonnull, @CheckReturnValue})
@Accessors(fluent = true)
public class StatsUpdateEvent implements LavalinkEvent {
	private final AudioNode node;
	private final Statistics statistics;
	private final EventType type = EventType.STATS_UPDATE_EVENT;
}
