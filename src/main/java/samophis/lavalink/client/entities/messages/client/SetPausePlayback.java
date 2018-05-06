package samophis.lavalink.client.entities.messages.client;

public class SetPausePlayback {
    public final String op, guildId;
    public final boolean pause;
    public SetPausePlayback(long guildId, boolean pause) {
        this.op = "pause";
        this.guildId = String.valueOf(guildId);
        this.pause = pause;
    }
}
