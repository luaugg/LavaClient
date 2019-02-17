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
