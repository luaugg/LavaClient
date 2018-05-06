package samophis.lavalink.client.entities.messages.server;

public class PlayerUpdate {
    public String op, guildId;
    public PlayerUpdateData state;
    public class PlayerUpdateData {
        public long time, position;
    }
}
