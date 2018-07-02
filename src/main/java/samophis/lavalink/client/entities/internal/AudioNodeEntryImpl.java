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

package samophis.lavalink.client.entities.internal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.SocketHandler;
import samophis.lavalink.client.entities.SocketInitializer;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AudioNodeEntryImpl implements AudioNodeEntry {
    private static final Pattern HTTP_PATTERN = Pattern.compile("^https?://");
    private static final Pattern WS_PATTERN = Pattern.compile("^wss?://");
    public LavaClient client;
    private String address, password, httpAddress, wsAddress;
    private int rest, ws;
    private final boolean fileBased;
    private SocketInitializer initializer;
    private Map<String, SocketHandler> handlers;
    public AudioNodeEntryImpl(LavaClient client, String address, String password, int rest, int ws,
                              SocketInitializer initializer, Map<String, SocketHandler> handlers) {
        this.client = Asserter.requireNotNull(client);
        this.address = Asserter.requireNotNull(address);
        this.httpAddress = !HTTP_PATTERN.matcher(address).find() ? "http://" + address : address;
        this.wsAddress = !WS_PATTERN.matcher(address).find() ? "ws://" + address : address;
        this.password = password == null ? client.getGlobalServerPassword() : password;
        this.rest = rest == 0 ? client.getGlobalRestPort() : rest;
        this.ws = ws == 0 ? client.getGlobalWebSocketPort() : ws;
        this.initializer = initializer;
        this.handlers = Asserter.requireNotNull(handlers);
        this.fileBased = false;
    }
    public AudioNodeEntryImpl() {
        this.fileBased = true;
    }
    @Override
    @Nonnull
    public LavaClient getClient() {
        return client;
    }
    @Override
    @Nonnull
    public String getRawAddress() {
        return address;
    }
    @Override
    @Nonnull
    public String getHttpAddress() {
        return httpAddress;
    }
    @Override
    @Nonnull
    public String getWebSocketAddress() {
        return wsAddress;
    }
    @Override
    @Nonnull
    public String getPassword() {
        return password;
    }
    @Override
    @Nonnegative
    public int getRestPort() {
        return rest;
    }
    @Override
    @Nonnegative
    public int getWebSocketPort() {
        return ws;
    }
    @Nullable
    @Override
    public SocketInitializer getSocketInitializer() {
        return initializer;
    }
    @Nonnull
    @Override
    public Map<String, SocketHandler> getHandlerMap() {
        return Collections.unmodifiableMap(handlers);
    }
    @Nonnull
    @Override
    public List<SocketHandler> getHandlers() {
        return ObjectLists.unmodifiable(new ObjectArrayList<>(handlers.values()));
    }
    @Nullable
    @Override
    public SocketHandler getHandlerByName(@Nonnull String name) {
        return handlers.get(Asserter.requireNotNull(name));
    }
    @Nonnull
    public Map<String, SocketHandler> getInternalHandlerMap() {
        return handlers;
    }
    public boolean isFileBased() {
        return fileBased;
    }
    public AudioNodeEntryImpl setAddress(String address) {
        this.address = address;
        this.httpAddress = !HTTP_PATTERN.matcher(address).find() ? "http://" + address : address;
        this.wsAddress = !WS_PATTERN.matcher(address).find() ? "ws://" + address : address;
        return this;
    }
    public AudioNodeEntryImpl setPassword(String password) {
        this.password = password;
        return this;
    }
    public AudioNodeEntryImpl setRest(int rest) {
        this.rest = rest;
        return this;
    }
    public AudioNodeEntryImpl setWs(int ws) {
        this.ws = ws;
        return this;
    }
    public AudioNodeEntryImpl setInitializer(SocketInitializer initializer) {
        this.initializer = initializer;
        return this;
    }
    public AudioNodeEntryImpl setHandlers(Map<String, SocketHandler> handlers) {
        this.handlers = handlers;
        return this;
    }
    public AudioNodeEntryImpl setClient(LavaClient client) {
        this.client = client;
        return this;
    }
}
