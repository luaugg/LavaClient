package samophis.lavalink.client.entities.messages.client;

public class SeekTrack {
    public final String op, guildId;
    public final long position;
    public SeekTrack(long guildId, long position) {
        this.op = "seek";
        this.guildId = String.valueOf(guildId);
        this.position = position;
    }
}
