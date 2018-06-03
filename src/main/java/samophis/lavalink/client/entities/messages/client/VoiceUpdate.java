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

import com.jsoniter.annotation.JsonIgnore;

@SuppressWarnings({"WeakerAccess", "unused"})
public class VoiceUpdate {
    public final String op, guildId, sessionId;
    public final VoiceUpdateData event;
    @JsonIgnore public final String token;
    @JsonIgnore public final String endpoint;
    public VoiceUpdate(long guildId, String sessionId, String token, String endpoint) {
        this.op = "voiceUpdate";
        this.guildId = String.valueOf(guildId);
        this.sessionId = sessionId;
        this.token = token;
        this.endpoint = endpoint;
        this.event = new VoiceUpdateData();
    }
    public class VoiceUpdateData {
        public final String token, guild_id, endpoint;
        public VoiceUpdateData() {
            this.token = VoiceUpdate.this.token;
            this.guild_id = VoiceUpdate.this.guildId;
            this.endpoint = VoiceUpdate.this.endpoint;
        }
    }
}
