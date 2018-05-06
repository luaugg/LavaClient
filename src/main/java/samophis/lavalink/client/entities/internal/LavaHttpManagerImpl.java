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
import samophis.lavalink.client.entities.AudioNodeEntry;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaHttpManager;
import samophis.lavalink.client.util.LavaClientUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.CancellationException;
import java.util.function.BiConsumer;

public class LavaHttpManagerImpl implements LavaHttpManager {
    private final CloseableHttpAsyncClient http;
    private final LavaClient client;
    public LavaHttpManagerImpl(LavaClient client) {
        this.http = HttpAsyncClients.createDefault();
        this.http.start();
        this.client = client;
    }
    @Override
    public LavaClient getClient() {
        return client;
    }
    @Override
    public void resolveTrack(String identifier, BiConsumer<String, AudioTrack> callback) {
        try {
            identifier = URLEncoder.encode(identifier, "UTF-8");
        } catch (UnsupportedEncodingException exc) {
            throw new RuntimeException(exc);
        }
        AudioNodeEntry node = LavaClient.getBestNode().getEntry();
        HttpGet request = new HttpGet(node.getServerAddress() + ":" + node.getRestPort() + "/loadtracks?identifier=" + identifier);
        request.addHeader("Authorization", node.getPassword());
        http.execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                HttpEntity entity = result.getEntity();
                if (entity == null)
                    throw new UnsupportedOperationException("Lavalink Server returned no data!");
                String content;
                try {
                    content = EntityUtils.toString(entity);
                } catch (IOException exc) {
                    EntityUtils.consumeQuietly(entity);
                    throw new RuntimeException(exc);
                }
                if (content == null) {
                    EntityUtils.consumeQuietly(entity);
                    throw new UnsupportedOperationException("Lavalink Server returned no data!");
                }
                System.out.println(request.getURI().toString() + " | " + content);
                String _track = JsonIterator.deserialize(content).get(0).get("track").toString();
                System.out.println(_track);
                AudioTrack track = LavaClientUtil.toAudioTrack(_track);
                EntityUtils.consumeQuietly(entity);
                callback.accept(_track, track);
            }
            @Override
            public void failed(Exception ex) {
                throw new RuntimeException(ex);
            }
            @Override
            public void cancelled() {
                throw new CancellationException("HTTP Request cancelled???");
            }
        });
    }
}
