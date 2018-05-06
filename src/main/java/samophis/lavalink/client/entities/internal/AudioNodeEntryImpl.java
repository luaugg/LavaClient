package samophis.lavalink.client.entities.internal;

import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;

import java.util.Objects;

public class AudioNodeEntryImpl implements AudioNodeEntry {
    public final LavaClient client;
    private final String address, password;
    private final int rest, ws;
    public AudioNodeEntryImpl(LavaClient client, String address, String password, int rest, int ws) {
        this.client = client;
        this.address = Objects.requireNonNull(address);
        this.password = password == null ? client.getGlobalServerPassword() : password;
        this.rest = rest == 0 ? client.getGlobalRestPort() : rest;
        this.ws = ws == 0 ? client.getGlobalWebSocketPort() : ws;
    }
    @Override
    public LavaClient getClient() {
        return client;
    }
    @Override
    public String getServerAddress() {
        return address;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public int getRestPort() {
        return rest;
    }
    @Override
    public int getWebSocketPort() {
        return ws;
    }
}
