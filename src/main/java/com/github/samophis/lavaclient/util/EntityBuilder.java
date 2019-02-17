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
package com.github.samophis.lavaclient.util;

import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityBuilder {
	private EntityBuilder() {}

	public static JsonObject createDestroyPayload(@Nonnull final String guildId) {
		return new JsonObject()
				.put("guildId", guildId)
				.put("op", "destroy");
	}

	public static JsonObject createStopPayload(@Nonnull final String guildId) {
		return new JsonObject()
				.put("guildId", guildId)
				.put("op", "stop");
	}

	public static JsonObject createPausePayload(@Nonnull final String guildId, final boolean paused) {
		return new JsonObject()
				.put("guildId", guildId)
				.put("pause", paused)
				.put("op", "pause");
	}

	public static JsonObject createSeekPayload(@Nonnull final String guildId, @Nonnegative final long position) {
		return new JsonObject()
				.put("guildId", guildId)
				.put("position", position)
				.put("op", "seek");
	}

	public static JsonObject createVolumePayload(@Nonnull final String guildId, @Nonnegative final int volume) {
		return new JsonObject()
				.put("guildId", guildId)
				.put("volume", volume)
				.put("op", "volume");
	}

	public static JsonObject createVoiceUpdatePayload(@Nonnull final String guildId, @Nonnull final String sessionId,
	                                                  @Nonnull final String voiceToken,
	                                                  @Nonnull final String endpoint) {
		return new JsonObject()
				.put("guildId", guildId)
				.put("sessionId", sessionId)
				.put("op", "voiceUpdate")
				.put("event", new JsonObject()
					.put("endpoint", endpoint)
					.put("token", voiceToken)
					.put("guild_id", guildId));
	}

	public static JsonObject createPlayPayload(@Nonnull final String guildId, @Nonnull final String track,
	                                           @Nullable final String startTime, @Nullable final String endTime,
	                                           final boolean noReplace) {
		var object = new JsonObject()
				.put("guildId", guildId)
				.put("track", track)
				.put("op", "play")
				.put("noReplace", noReplace);
		if (startTime != null) {
			object.put("startTime", startTime);
		}
		if (endTime != null) {
			object.put("endTime", endTime);
		}
		return object;
	}

	// Ignoring Equalizer support for now.
}
