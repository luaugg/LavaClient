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

package samophis.lavalink.client.entities.messages.client;

@SuppressWarnings("WeakerAccess")
public class SeekTrack {
    public final String op, guildId;
    public final long position;
    public SeekTrack(long guildId, long position) {
        this.op = "seek";
        this.guildId = String.valueOf(guildId);
        this.position = position;
    }
}
