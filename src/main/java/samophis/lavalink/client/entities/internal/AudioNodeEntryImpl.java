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

import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class AudioNodeEntryImpl implements AudioNodeEntry {
    private static final Pattern HTTP_PATTERN = Pattern.compile("^https?://");
    private static final Pattern WS_PATTERN = Pattern.compile("^wss?://");
    public final LavaClient client;
    private final String address, password, httpAddress, wsAddress;
    private final int rest, ws;
    public AudioNodeEntryImpl(LavaClient client, String address, String password, int rest, int ws) {
        this.client = Asserter.requireNotNull(client);
        this.address = Asserter.requireNotNull(address);
        this.httpAddress = !HTTP_PATTERN.matcher(address).find() ? "http://" + address : address;
        this.wsAddress = !WS_PATTERN.matcher(address).find() ? "ws://" + address : address;
        this.password = password == null ? client.getGlobalServerPassword() : password;
        this.rest = rest == 0 ? client.getGlobalRestPort() : rest;
        this.ws = ws == 0 ? client.getGlobalWebSocketPort() : ws;
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
}
