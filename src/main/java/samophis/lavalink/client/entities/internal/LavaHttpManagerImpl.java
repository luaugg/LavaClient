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

import com.jsoniter.JsonIterator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaHttpManager;
import samophis.lavalink.client.entities.TrackPair;
import samophis.lavalink.client.exceptions.HttpRequestException;
import samophis.lavalink.client.util.Asserter;
import samophis.lavalink.client.util.LavaClientUtil;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Consumer;

public class LavaHttpManagerImpl implements LavaHttpManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LavaHttpManagerImpl.class);
    private final CloseableHttpAsyncClient http;
    private final LavaClient client;
    @SuppressWarnings("WeakerAccess")
    public LavaHttpManagerImpl(@Nonnull LavaClient client) {
        this.http = HttpAsyncClients.createDefault();
        this.http.start();
        this.client = Asserter.requireNotNull(client);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                http.close();
            } catch (IOException exc) {
                LOGGER.error("Error when closing HTTP Client!", exc);
                // no need to throw it again, application already exiting
            }
        }));
    }
    @Override
    @Nonnull
    public LavaClient getClient() {
        return client;
    }
    @Override
    public void resolveTrack(@Nonnull String identifier, @Nonnull Consumer<TrackPair> callback) {
        Asserter.requireNotNull(identifier);
        Asserter.requireNotNull(callback);
        try {
            identifier = URLEncoder.encode(identifier, "UTF-8");
        } catch (UnsupportedEncodingException exc) {
            throw new HttpRequestException(exc);
        }
        AudioNodeEntry node = LavaClient.getBestNode().getEntry();
        HttpGet request = new HttpGet(node.getHttpAddress() + ":" + node.getRestPort() + "/loadtracks?identifier=" + identifier);
        request.addHeader("Authorization", node.getPassword());
        http.execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                HttpEntity entity = result.getEntity();
                if (entity == null)
                    throw new HttpRequestException("Lavalink Server returned no data!");
                String content;
                try {
                    content = EntityUtils.toString(entity);
                } catch (IOException exc) {
                    EntityUtils.consumeQuietly(entity);
                    throw new HttpRequestException(exc);
                }
                if (content == null) {
                    EntityUtils.consumeQuietly(entity);
                    throw new HttpRequestException("Lavalink Server returned no data!");
                }
                String _track = JsonIterator.deserialize(content).get(0).get("track").toString();
                AudioTrack track = LavaClientUtil.toAudioTrack(_track);
                EntityUtils.consumeQuietly(entity);
                callback.accept(new TrackPair(_track, track));
            }
            @Override
            public void failed(Exception exc) {
                LOGGER.error("HTTP Request to Lavalink Server in order to resolve a track failed! {}", exc.getMessage());
                throw new HttpRequestException(exc);
            }
            @Override
            public void cancelled() {
                LOGGER.warn("HTTP Request to Lavalink Server in order to resolve a track was cancelled!");
                throw new HttpRequestException("HTTP Request cancelled while waiting for a response!");
            }
        });
    }
}
