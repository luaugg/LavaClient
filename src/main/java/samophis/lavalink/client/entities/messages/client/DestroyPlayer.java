package samophis.lavalink.client.entities.messages.client;

public class DestroyPlayer {
    public final String op, guildId;
    public DestroyPlayer(long guildId) {
        this.op = "destroy";
        this.guildId = String.valueOf(guildId);
    }
}
