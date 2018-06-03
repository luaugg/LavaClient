/*
   Copyright 2018 Samuel Pritchard

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

package samophis.lavalink.client.entities.events;

@SuppressWarnings("unused")
public interface AudioEventListener {
    void onTrackStart(TrackStartEvent event);
    void onTrackStuck(TrackStuckEvent event);
    void onTrackEnd(TrackEndEvent event);
    void onTrackException(TrackExceptionEvent event);
    void onPlayerPause(PlayerPauseEvent event);
    void onPlayerResume(PlayerResumeEvent event);
}
