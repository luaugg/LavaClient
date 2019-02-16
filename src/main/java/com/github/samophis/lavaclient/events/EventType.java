package com.github.samophis.lavaclient.events;

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
	WEBSOCKET_CLOSED_EVENT
}
