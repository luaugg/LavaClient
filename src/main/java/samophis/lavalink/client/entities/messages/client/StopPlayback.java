package samophis.lavalink.client.entities.messages.client;

public class StopPlayback {
    public final String op, guildId;
    public StopPlayback(long guildId) {
        this.op = "stop";
        this.guildId = String.valueOf(guildId);
    }
}
