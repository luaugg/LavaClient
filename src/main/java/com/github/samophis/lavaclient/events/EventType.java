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
