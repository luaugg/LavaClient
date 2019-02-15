package com.github.samophis.lavaclient.events;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public enum EventType {
	/* -- Client-side actions -- */
	TRACK_START_EVENT,
	PLAYER_PAUSE_EVENT,
	PLAYER_RESUME_EVENT,
	/* -- Server-side actions -- */
	TRACK_END_EVENT,
	TRACK_STUCK_EVENT,
	TRACK_EXCEPTION_EVENT,
	PLAYER_UPDATE_EVENT,
	STATS_UPDATE_EVENT,
	/* -- Unknown -- */
	UNKNOWN;

	@Nonnull
	@CheckReturnValue
	public static EventType from(@Nonnull final String name) {
		switch (name) {
			case "TrackEndEvent":
				return TRACK_END_EVENT;
			case "TrackStuckEvent":
				return TRACK_STUCK_EVENT;
			case "TrackExceptionEvent":
				return TRACK_EXCEPTION_EVENT;
			default:
				return UNKNOWN;
		}
	}
}
