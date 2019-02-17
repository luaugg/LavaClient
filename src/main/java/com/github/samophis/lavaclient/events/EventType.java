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

@SuppressWarnings({"WeakerAccess", "unused"})
public class EventType<T extends LavalinkEvent> {
	private EventType() {}

	public static final EventType<TrackStartEvent> TRACK_START_EVENT = new EventType<>();
	public static final EventType<PlayerPauseEvent> PLAYER_PAUSE_EVENT = new EventType<>();
	public static final EventType<PlayerResumeEvent> PLAYER_RESUME_EVENT = new EventType<>();

	public static final EventType<TrackEndEvent> TRACK_END_EVENT = new EventType<>();
	public static final EventType<TrackStuckEvent> TRACK_STUCK_EVENT = new EventType<>();
	public static final EventType<TrackExceptionEvent> TRACK_EXCEPTION_EVENT = new EventType<>();
	public static final EventType<PlayerUpdateEvent> PLAYER_UPDATE_EVENT = new EventType<>();
	public static final EventType<StatsUpdateEvent> STATS_UPDATE_EVENT = new EventType<>();
	public static final EventType<WebSocketClosedEvent> WEB_SOCKET_CLOSED_EVENT = new EventType<>();
}
