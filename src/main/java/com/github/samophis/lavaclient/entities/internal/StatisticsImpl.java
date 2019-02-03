package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.Statistics;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Accessors(fluent = true)
public class StatisticsImpl implements Statistics {
	private int players, playingPlayers, cpuCores;
	private long uptime, freeMemory, allocatedMemory, usedMemory, reservableMemory;
	private double systemLoad, lavalinkLoad;
	private Long sentFrames, nulledFrames, deficitFrames;
}
