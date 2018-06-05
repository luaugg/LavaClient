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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.lavalink.client.entities.*;
import samophis.lavalink.client.entities.messages.server.TrackLoadResult;
import samophis.lavalink.client.exceptions.HttpRequestException;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.function.Consumer;

import samophis.lavalink.client.entities.messages.server.TrackLoadResult.TrackLoadResultObject;

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
    public void resolveTracks(@Nonnull String identifier, @Nonnull Consumer<AudioWrapper> callback) {
        Asserter.requireNotNull(identifier);
        Asserter.requireNotNull(callback);
        try {
            identifier = URLEncoder.encode(identifier, "UTF-8");
        } catch (UnsupportedEncodingException exc) {
            throw new HttpRequestException(exc);
        }
        AudioNode node = LavaClient.getBestNode();
        AudioNodeEntry entry = node.getEntry();
        HttpGet request = new HttpGet(entry.getHttpAddress() + ":" + entry.getRestPort() + "/loadtracks?identifier=" + identifier);
        request.addHeader("Authorization", entry.getPassword());
        http.execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                HttpEntity entity = result.getEntity();
                if (entity == null)
                    throw new HttpRequestException("Lavalink Server returned no data!");
                String content;
                try {
                    content = EntityUtils.toString(entity);
                    EntityUtils.consumeQuietly(entity);
                } catch (IOException exc) {
                    throw new HttpRequestException(exc);
                }
                if (content == null)
                    throw new HttpRequestException("Lavalink Server returned no data!");
                AudioWrapper wrapper;
                if (node.isUsingLavalinkVersionThree()) {
                    TrackLoadResult res = JsonIterator.deserialize(content, TrackLoadResult.class);
                    List<TrackDataPair> pairs = new ObjectArrayList<>(res.tracks.length);
                    for (TrackLoadResultObject obj : res.tracks)
                        pairs.add(new TrackDataPairImpl(obj.track));
                    Integer selected = res.selectedTrack;
                    TrackDataPair selectedPair = null;
                    if (selected != null && selected < pairs.size())
                        selectedPair = pairs.get(selected);
                    wrapper = new AudioWrapperImpl(res.playlistName, selectedPair, pairs, res.isPlaylist);
                }
                else {
                    TrackLoadResultObject[] objects = JsonIterator.deserialize(content, TrackLoadResultObject[].class);
                    List<TrackDataPair> pairs = new ObjectArrayList<>(objects.length);
                    for (TrackLoadResultObject obj : objects)
                        pairs.add(new TrackDataPairImpl(obj.track));
                    wrapper = new AudioWrapperImpl(null, null, pairs, false);
                }
                callback.accept(wrapper);
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
