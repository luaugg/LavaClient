/*
   Copyright 2018 Samuel Pritchard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package samophis.lavalink.client.entities.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;
import com.neovisionaries.ws.client.WebSocketFactory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.ReconnectIntervalFunction;
import samophis.lavalink.client.entities.internal.AudioNodeEntryImpl;
import samophis.lavalink.client.entities.internal.LavaClientImpl;
import samophis.lavalink.client.exceptions.ConfigurationFormatException;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"WeakerAccess", "unused"})
public class LavaClientBuilder {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private static final Logger LOGGER = LoggerFactory.getLogger(LavaClientBuilder.class);
    private final List<AudioNodeEntry> entries;
    private String password;
    private int restPort, wsPort, shards;
    private long expireWriteMs, expireAccessMs, userId, maxInterval, baseInterval;
    private TimeUnit unit;
    private ReconnectIntervalFunction expander;
    @Deprecated
    public LavaClientBuilder(@Nonnegative long expireWriteMs, @Nonnegative long expireAccessMs) {
        this();
        init();
        this.expireWriteMs = expireWriteMs;
        this.expireAccessMs = expireAccessMs;
    }
    public LavaClientBuilder(byte[] content) {
        this();
        try {
            init(MAPPER.readTree(Asserter.requireNotNull(content)));
        } catch (IOException exc) {
            LOGGER.error("Error when configuring LavaClient!", exc);
        }
    }
    public LavaClientBuilder(@Nonnull String content) {
        this();
        try {
            init(MAPPER.readTree(Asserter.requireNotNull(content)));
        } catch (IOException exc) {
            LOGGER.error("Error when configuring LavaClient!", exc);
        }
    }
    public LavaClientBuilder(@Nonnull File file) {
        this();
        try {
            init(MAPPER.readTree(Asserter.requireNotNull(file)));
        } catch (IOException exc) {
            LOGGER.error("Error when configuring LavaClient!", exc);
        }
    }
    public LavaClientBuilder(@Nonnull URL source) {
        this();
        try {
            init(MAPPER.readTree(Asserter.requireNotNull(source)));
        } catch (IOException exc) {
            LOGGER.error("Error when configuring LavaClient!", exc);
        }
    }
    public LavaClientBuilder() {
        init();
        this.entries = new ObjectArrayList<>();
        this.maxInterval = LavaClient.DEFAULT_MAX_INTERVAL;
        this.baseInterval = LavaClient.DEFAULT_BASE_INTERVAL;
        this.expireAccessMs = LavaClient.DEFAULT_CACHE_EXPIRE_ACCESS;
        this.expireWriteMs = LavaClient.DEFAULT_CACHE_EXPIRE_WRITE;
        this.password = LavaClient.DEFAULT_PASSWORD;
        this.restPort = LavaClient.DEFAULT_REST_PORT;
        this.wsPort = LavaClient.DEFAULT_WS_PORT;
        this.unit = LavaClient.DEFAULT_INTERVAL_UNIT;
        this.expander = LavaClient.DEFAULT_INTERVAL_EXPANDER;
    }
    public LavaClientBuilder addEntry(@Nonnull AudioNodeEntry entry) {
        entries.add(Asserter.requireNotNull(entry));
        return this;
    }
    public LavaClientBuilder addEntry(@Nonnull LavaClient client, @Nonnull String address, @Nonnegative int restPort, @Nonnegative int wsPort, @Nonnull String password) {
        return addEntry(new AudioNodeEntryBuilder(client)
                .setAddress(address)
                .setRestPort(restPort)
                .setWebSocketPort(wsPort)
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
    public LavaClientBuilder setReconnectIntervalUnit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }
    public LavaClientBuilder setReconnectIntervalExpander(ReconnectIntervalFunction expander) {
        this.expander = expander;
        return this;
    }
    public LavaClientBuilder setCacheExpireAccessMs(long expireAccessMs) {
        this.expireAccessMs = expireAccessMs;
        return this;
    }
    public LavaClientBuilder setCacheExpireWriteMs(long expireWriteMs) {
        this.expireWriteMs = expireWriteMs;
        return this;
    }
    public LavaClientBuilder setReconnectBaseInterval(long baseInterval) {
        this.baseInterval = baseInterval;
        return this;
    }
    public LavaClientBuilder setReconnectMaximumInterval(long maxInterval) {
        this.maxInterval = maxInterval;
        return this;
    }
    public LavaClient build() {
        return new LavaClientImpl(password, restPort, wsPort, shards, expireWriteMs, expireAccessMs, userId,
                baseInterval, maxInterval, expander, unit, entries);
    }
    private void init() {
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }
    private void init(JsonNode node) {
        init();
        JsonNode global_data = node.get("global");
        if (global_data != null && !global_data.isNull()) {
            expireWriteMs = getLongOrElse(global_data, "expireWriteMs", LavaClient.DEFAULT_CACHE_EXPIRE_WRITE);
            expireAccessMs = getLongOrElse(global_data, "expireAccessMs", LavaClient.DEFAULT_CACHE_EXPIRE_ACCESS);
            restPort = getIntOrElse(global_data, "restPort", LavaClient.DEFAULT_REST_PORT);
            wsPort = getIntOrElse(global_data, "wsPort", LavaClient.DEFAULT_WS_PORT);
            shards = getOrThrowAndLog(global_data, "shardCount").intValue();
            userId = getOrThrowAndLog(global_data, "userId").longValue();
            password = getStringOrElse(global_data, "password", LavaClient.DEFAULT_PASSWORD);
            baseInterval = getLongOrElse(global_data, "baseInterval", LavaClient.DEFAULT_BASE_INTERVAL);
            maxInterval = getLongOrElse(global_data, "maxInterval", LavaClient.DEFAULT_MAX_INTERVAL);
        }
        JsonNode nodes = node.get("nodes");
        if (nodes != null && nodes.isObject()) {
            for (JsonNode next : nodes) {
                String address = getOrThrowAndLog(next, "serverAddress").textValue();
                String password = getStringOrElse(next, "password", this.password);
                int restPort = getIntOrElse(next, "restPort", this.restPort);
                int wsPort = getIntOrElse(next, "wsPort", this.wsPort);
                long baseInterval = getLongOrElse(next, "baseInterval", this.baseInterval);
                long maxInterval = getLongOrElse(next, "maxInterval", this.maxInterval);
                AudioNodeEntry entry = new AudioNodeEntryImpl()
                        .setAddress(address)
                        .setPassword(password)
                        .setRest(Asserter.requireNotNegative(restPort))
                        .setWs(Asserter.requireNotNegative(wsPort))
                        .setBaseInterval(baseInterval)
                        .setMaxInterval(Asserter.requireNotNegative(maxInterval))
                        .setUnit(unit)
                        .setExpander(expander)
                        .setFactory(new WebSocketFactory());
                entries.add(entry);
            }
        }
    }
    private JsonNode getOrThrowAndLog(JsonNode base, String name) {
        JsonNode found = base.get(name);
        if (found == null || found.isNull()) {
            LOGGER.error("Missing field/object in configuration with name: {}", name);
            throw new ConfigurationFormatException(name);
        }
        return found;
    }
    @SuppressWarnings("all")
    private String getStringOrElse(JsonNode base, String name, String defaultValue) {
        JsonNode found = base.get(name);
        return (found == null || found.isNull()) ? defaultValue : found.textValue();
    }
    private int getIntOrElse(JsonNode base, String name, int defaultValue) {
        JsonNode found = base.get(name);
        return (found == null || found.isNull()) ? defaultValue : found.intValue();
    }
    private long getLongOrElse(JsonNode base, String name, long defaultValue) {
        JsonNode found = base.get(name);
        return (found == null || found.isNull()) ? defaultValue : found.longValue();
    }
    @SuppressWarnings("all")
    private boolean getBooleanOrElse(JsonNode base, String name, boolean defaultValue) {
        JsonNode found = base.get(name);
        return (found == null || found.isNull()) ? defaultValue : found.booleanValue();
    }
}
