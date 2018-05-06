package samophis.lavalink.client.entities.builders;

import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.internal.AudioNodeEntryImpl;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AudioNodeEntryBuilder {
    private final LavaClient client;
    private String address, password;
    private int restPort, wsPort;
    public AudioNodeEntryBuilder(@Nonnull LavaClient client) {
        this.client = Objects.requireNonNull(client);
    }
    public AudioNodeEntryBuilder setAddress(String address) {
        this.address = address;
        return this;
    }
    public AudioNodeEntryBuilder setPassword(String password) {
        this.password = password;
        return this;
    }
    public AudioNodeEntryBuilder setRestPort(int restPort) {
        this.restPort = restPort;
        return this;
    }
    public AudioNodeEntryBuilder setWsPort(int wsPort) {
        this.wsPort = wsPort;
        return this;
    }
    public AudioNodeEntry build() {
        return new AudioNodeEntryImpl(client, address, password, restPort, wsPort);
    }
}
