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

	@Getter(onMethod_ = {@CheckReturnValue, @Nonnull, @Nonnegative})
	private final Long timestamp;
	@Getter(onMethod_ = {@CheckReturnValue, @Nonnull, @Nonnegative})
	private final Long position;

	@Getter
	private final EventType<PlayerUpdateEvent> type = EventType.PLAYER_UPDATE_EVENT;
}
