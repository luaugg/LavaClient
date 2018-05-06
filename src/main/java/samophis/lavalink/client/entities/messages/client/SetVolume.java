package samophis.lavalink.client.entities.messages.client;

public class SetVolume {
    public final String op, guildId;
    public final int volume;
    public SetVolume(long guildId, int volume) {
        this.op = "volume";
        this.guildId = String.valueOf(guildId);
        this.volume = volume;
    }
}
