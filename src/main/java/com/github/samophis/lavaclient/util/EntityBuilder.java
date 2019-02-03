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
		if (position < 0) {
			throw new IllegalArgumentException("position is negative!");
		}
		return new JsonObject()
				.put("guildId", guildId)
				.put("position", position)
				.put("op", "seek");
	}

	public static JsonObject createVolumePayload(@Nonnull final String guildId, @Nonnegative final int volume) {
		if (volume < 0 || volume > 1000) {
			throw new IllegalArgumentException("volume is out of bounds! (0 min, 1000 max)");
		}
		return new JsonObject()
				.put("guildId", guildId)
				.put("volume", volume)
				.put("op", "volume");
	}

	public static JsonObject createVoiceUpdatePayload(@Nonnull final String guildId, @Nonnull final String sessionId,
	                                                  @Nonnull final String endpoint,
	                                                  @Nonnull final String voiceToken) {
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
