package samophis.lavalink.client.entities.builders;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.internal.LavaClientImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class LavaClientBuilder {
    private final List<AudioNodeEntry> entries;
    private final long expireWriteMs, expireAccessMs;
    private String password;
    private int restPort, wsPort, shards;
    private long userId;
    public LavaClientBuilder(boolean overrideJson, long expireWriteMs, long expireAccessMs) {
        if (overrideJson) {
            JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
            JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
        }
        this.expireWriteMs = expireWriteMs;
        this.expireAccessMs = expireAccessMs;
        this.entries = new ObjectArrayList<>();
        this.password = LavaClient.PASSWORD_DEFAULT;
        this.restPort = LavaClient.REST_PORT_DEFAULT;
        this.wsPort = LavaClient.WS_PORT_DEFAULT;
    }
    public LavaClientBuilder(long expireWriteMs, long expireAccessMs) {
        this(true, expireWriteMs, expireAccessMs);
    }
    public LavaClientBuilder(boolean overrideJson) {
        this(overrideJson, LavaClient.DEFAULT_CACHE_EXPIRE_WRITE, LavaClient.DEFAULT_CACHE_EXPIRE_ACCESS);
    }
    public LavaClientBuilder() {
        this(true);
    }

    public LavaClientBuilder addEntry(@Nonnull AudioNodeEntry entry) {
        Objects.requireNonNull(entry);
        if (entry.getServerAddress() == null || entry.getServerAddress().isEmpty())
            throw new IllegalArgumentException("Your Lavalink Node Address must be non-null and not empty (and valid!)");
        entries.add(entry);
        return this;
    }
    public LavaClientBuilder addEntry(@Nonnull LavaClient client, @Nonnull String address, int restPort, int wsPort, @Nullable String password) {
        return addEntry(new AudioNodeEntryBuilder(client)
                .setAddress(address)
                .setRestPort(restPort)
                .setWsPort(wsPort)
                .setPassword(password)
                .build()
        );
    }
    public LavaClientBuilder setGlobalServerPassword(String password) {
        this.password = password;
        return this;
    }
    public LavaClientBuilder setGlobalRestPort(int restPort) {
        this.restPort = restPort;
        return this;
    }
    public LavaClientBuilder setGlobalWebSocketPort(int wsPort) {
        this.wsPort = wsPort;
        return this;
    }
    public LavaClientBuilder setShardCount(int shards) {
        this.shards = shards;
        return this;
    }
    public LavaClientBuilder setUserId(long userId) {
        this.userId = userId;
        return this;
    }
    public LavaClient build() {
        return new LavaClientImpl(password, restPort, wsPort, shards, expireWriteMs, expireAccessMs, userId, entries);
    }
}
