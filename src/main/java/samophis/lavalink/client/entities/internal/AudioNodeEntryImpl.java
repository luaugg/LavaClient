package samophis.lavalink.client.entities.internal;

import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;

import java.util.Objects;
import java.util.regex.Pattern;

public class AudioNodeEntryImpl implements AudioNodeEntry {
    private static final Pattern HTTP_PATTERN = Pattern.compile("^https?://");
    private static final Pattern WS_PATTERN = Pattern.compile("^wss?://");
    public final LavaClient client;
    private final String address, password, httpAddress, wsAddress;
    private final int rest, ws;
    public AudioNodeEntryImpl(LavaClient client, String address, String password, int rest, int ws) {
        this.client = client;
        this.address = Objects.requireNonNull(address);
        this.httpAddress = !HTTP_PATTERN.matcher(address).find() ? "http://" + address : address;
        this.wsAddress = !WS_PATTERN.matcher(address).find() ? "ws://" + address : address;
        this.password = password == null ? client.getGlobalServerPassword() : password;
        this.rest = rest == 0 ? client.getGlobalRestPort() : rest;
        this.ws = ws == 0 ? client.getGlobalWebSocketPort() : ws;
    }
    @Override
    public LavaClient getClient() {
        return client;
    }
    @Deprecated
    @Override
    public String getServerAddress() {
        return address;
    }
    @Override
    public String getRawAddress() {
        return address;
    }
    @Override
    public String getHttpAddress() {
        return httpAddress;
    }
    @Override
    public String getWebSocketAddress() {
        return wsAddress;
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
