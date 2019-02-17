/*
   Copyright 2019 Sam Pritchard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LoadBalancer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(fluent = true)
public class LoadBalancerImpl implements LoadBalancer {
	private final AudioNode node;
	private int playerPenalty, cpuPenalty, nullFramePenalty, deficitFramePenalty;

	@CheckReturnValue
	@Nonnull
	@Override
	public LoadBalancer updatePenalties() {
		final var stats = node.statistics();
		if (stats == null) {
			return this;
		}
		playerPenalty = stats.playingPlayers();
		cpuPenalty = (int) Math.pow(1.05d, 100 * stats.systemLoad()) * 10 - 10;
		if (stats.deficitFrames() != null) {
			// we know that the following variables are definitely not-null here.
			final var defFrames = stats.deficitFrames();
			final var nullFrames = stats.nulledFrames();
			//noinspection ConstantConditions
			deficitFramePenalty = (int) (Math.pow(1.03d, 500f * ((float) defFrames / 3000f)) * 600 - 600);
			//noinspection ConstantConditions
			nullFramePenalty = ((int) (Math.pow(1.03d, 500f * ((float) nullFrames / 3000f)) * 300 - 300)) * 2;
		}
		return this;
	}
}
