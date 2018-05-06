package samophis.lavalink.client.entities.messages.client;

import com.jsoniter.annotation.JsonIgnore;

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
