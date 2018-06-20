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

import com.jsoniter.output.JsonStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.lavalink.client.entities.*;
import samophis.lavalink.client.entities.events.*;
import samophis.lavalink.client.entities.messages.client.*;
import samophis.lavalink.client.exceptions.ListenerException;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LavaPlayerImpl implements LavaPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LavaPlayerImpl.class);
    private final LavaClient client;
    private final List<AudioEventListener> listeners;
    private final long guild_id;
    private int volume;
    private long channel_id, position, timestamp;
    private boolean paused;
    private AudioNode node;
    private AudioTrack track;
    private State state;
    @SuppressWarnings("WeakerAccess")
    public LavaPlayerImpl(@Nonnull LavaClient client, @Nonnegative long guild_id) {
        this.client = Asserter.requireNotNull(client);
        this.guild_id = Asserter.requireNotNegative(guild_id);
        this.listeners = new ObjectArrayList<>();
        this.position = 0;
        this.timestamp = 0;
        this.state = State.NOT_CONNECTED;
    }
    @Override
    @Nonnull
    public LavaClient getClient() {
        return client;
    }
    @Override
    @Nonnull
    public List<AudioEventListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }
    @Override
    @Nonnegative
    public long getGuildId() {
        return guild_id;
    }
    @Override
    @Nonnegative
    public long getChannelId() {
        return channel_id;
    }
    @Override
    @Nonnegative
    public long getPosition() {
        /* Note, this logic is taken from Frederikam's default JDA Lavalink Client.
           All credit for this section goes entirely to him, which can be found here:
                https://github.com/Frederikam/Lavalink/blob/master/LavalinkClient/src/main/java/lavalink/client/player/LavalinkPlayer.java#L145
         */
        if (track == null)
            throw new IllegalStateException("This Lavalink Player is currently not playing anything!");
        return paused
                ? Math.min(position, track.getDuration())
                : Math.min(position + (System.currentTimeMillis() - timestamp), track.getDuration());
    }
    @Override
    @Nonnegative
    public long getTimestamp() {
        return timestamp;
    }
    @Override
    @Nonnegative
    public int getVolume() {
        return volume;
    }
    @Override
    public boolean isPaused() {
        return paused;
    }
    @Override
    @Nonnull
    public AudioNode getConnectedNode() {
        return node;
    }
    @Override
    @Nonnull
    public AudioTrack getPlayingTrack() {
        return track;
    }
    @Nonnull
    @Override
    public State getState() {
        return state;
    }
    @Override
    public void addListener(@Nonnull AudioEventListener listener) {
        listeners.add(Asserter.requireNotNull(listener));
    }
    @Override
    public void setPaused(boolean paused) {
        if (this.state != State.CONNECTED) {
            LOGGER.warn("Attempted to set paused to {} for Guild ID: {} while in the {} state!", paused, guild_id, state.name());
            throw new IllegalStateException("State != CONNECTED");
        }
        if (this.paused == paused)
            return;
        this.paused = paused;
        node.getSocket().sendText(JsonStream.serialize(new SetPausePlayback(guild_id, paused)));
        PlayerEvent event = paused ? new PlayerPauseEvent(this) : new PlayerResumeEvent(this);
        emitEvent(event);
    }
    @Override
    public void setVolume(@Nonnegative int volume) {
        if (this.state != State.CONNECTED) {
            LOGGER.warn("Attempted to set volume to {} for Guild ID: {} while in the {} state!", volume, guild_id, state.name());
            throw new IllegalStateException("State != CONNECTED");
        }
        if (Asserter.requireNotNegative(volume) > 150)
            return;
        node.getSocket().sendText(JsonStream.serialize(new SetVolume(guild_id, volume)));
        this.volume = volume;
    }
    @Override
    public void playTrack(@Nonnull String identifier) {
        playTrack(identifier, 0, -1);
    }
    @Override
    public void playTrack(@Nonnull AudioTrack track) {
        playTrack(track, 0, -1);
    }
    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void playTrack(@Nonnull AudioTrack track, @Nonnegative long startTime, long endTime) {
        if (this.state != State.CONNECTED) {
            LOGGER.warn("Attempt to play track for Guild ID: {} while in the {} state!", guild_id, state.name());
            throw new IllegalStateException("State != CONNECTED");
        }
        this.track = Asserter.requireNotNull(track);
        Asserter.requireNotNegative(startTime);
        setNode(client.getBestNode());
        TrackDataPair cached = client.getIdentifierCache().get(track.getIdentifier(), ignored -> new TrackDataPairImpl(track));
        if (cached == null)
            cached = new TrackDataPairImpl(track);
        handleTrackPair(cached, startTime, endTime);
    }
    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void playTrack(@Nonnull String identifier, @Nonnegative long startTime, long endTime) {
        if (this.state != State.CONNECTED) {
            LOGGER.warn("Attempt to play track for Guild ID: {} while in the {} state!", guild_id, state.name());
            throw new IllegalStateException("State != CONNECTED");
        }
        Asserter.requireNotNegative(startTime);
        TrackDataPair pair = client.getIdentifierCache().getIfPresent(identifier);
        setNode(client.getBestNode());
        if (pair == null) {
            client.getHttpManager().resolveTracks(identifier, wrapper -> {
                List<TrackDataPair> tracks = wrapper.getLoadedTracks();
                if (tracks.size() > 0) {
                    TrackDataPair pr = tracks.get(0);
                    this.track = pr.getTrack();
                    client.getIdentifierCache().put(identifier, pr);
                    handleTrackPair(pr, startTime, endTime);
                }
            });
        }
        else {
            this.track = pair.getTrack();
            handleTrackPair(pair, startTime, endTime);
        }
    }
    @Nonnull
    @Override
    public AudioWrapper loadTracks(@Nonnull String identifier) {
        if (this.state != State.CONNECTED) {
            LOGGER.warn("Attempt to load tracks for Guild ID: {} while in the {} state!", guild_id, state.name());
            throw new IllegalStateException("State != CONNECTED");
        }
        TrackDataPair pair = client.getIdentifierCache().getIfPresent(identifier);
        if (pair != null) {
            List<TrackDataPair> pairs = new ObjectArrayList<>(1);
            pairs.add(pair);
            return new AudioWrapperImpl(null, null, pairs, LoadType.TRACK_LOADED);
        }
        AtomicReference<AudioWrapper> reference = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        try {
            try {
                client.getHttpManager().resolveTracks(identifier, ref -> {
                    reference.set(ref);
                    latch.countDown();
                });
            } catch (Throwable thr) {
                latch.countDown(); // Just to ensure a thread isn't blocked forever due to an error.
            }
            latch.await();
            return reference.get();
        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exc);
        }
    }
    @Override
    public void loadTracksAsync(@Nonnull String identifier, @Nonnull Consumer<AudioWrapper> callback) {
        if (this.state != State.CONNECTED) {
            LOGGER.warn("Attempt to load tracks asynchronously for Guild ID: {} while in the {} state!", guild_id, state.name());
            throw new IllegalStateException("State != CONNECTED");
        }
        Asserter.requireNotNull(callback);
        TrackDataPair pair = client.getIdentifierCache().getIfPresent(identifier);
        if (pair != null) {
            List<TrackDataPair> pairs = new ObjectArrayList<>(1);
            pairs.add(pair);
            callback.accept(new AudioWrapperImpl(null, null, pairs, LoadType.TRACK_LOADED));
            return;
        }
        client.getHttpManager().resolveTracks(identifier, callback);
    }
    @Override
    public void stopTrack() {
        if (this.state != State.CONNECTED) {
            LOGGER.warn("Attempt to stop track for Guild ID: {} while in the {} state!", guild_id, state.name());
            throw new IllegalStateException("State != CONNECTED");
        }
        node.getSocket().sendText(JsonStream.serialize(new StopPlayback(guild_id)));
    }
    @Override
    public void destroyPlayer() {
        if (this.state != State.CONNECTED) {
            LOGGER.warn("Attempt to destroy player for Guild ID: {} while in the {} state!", guild_id, state.name());
            throw new IllegalStateException("State != CONNECTED");
        }
        client.removePlayer(guild_id);
        state = State.DESTROYED;
        node.getSocket().sendText(JsonStream.serialize(new DestroyPlayer(guild_id)));
    }
    @Override
    public void seek(@Nonnegative long position) {
        if (this.state != State.CONNECTED) {
            LOGGER.warn("Attempt to seek track for Guild ID: {} while in the {} state!", guild_id, state.name());
            throw new IllegalStateException("State != CONNECTED");
        }
        if (Asserter.requireNotNegative(position) > Asserter.requireNotNull(track).getDuration())
            return;
        node.getSocket().sendText(JsonStream.serialize(new SeekTrack(guild_id, position)));
        this.position = position;
    }
    @Override
    public void emitEvent(@Nonnull PlayerEvent event) {
        Asserter.requireNotNull(event);
        Class<? extends AudioEventListener> cls = AudioEventListener.class;
        Class<? extends PlayerEvent> ev_cls = event.getClass();
        for (Method meth : cls.getDeclaredMethods()) {
            Parameter[] params = meth.getParameters();
            if (params.length == 0)
                continue;
            if (params[0].getType().isAssignableFrom(ev_cls)) {
                listeners.forEach(listener -> {
                    try {
                        meth.invoke(listener, event);
                    } catch (IllegalAccessException | InvocationTargetException exc) {
                        throw new ListenerException(exc);
                    }
                });
                return;
            }
        }
        LOGGER.warn("Event does not have a listener method! Event Name: {}", ev_cls.getSimpleName());
        throw new IllegalArgumentException("Parameter event does not have a listener method in the AudioEventListener interface!");
    }
    @Override
    public void setNode(@Nonnull AudioNode node) {
        this.node = Asserter.requireNotNull(node);
    }

    @Override
    public void connect(@Nonnull String session_id, @Nonnull String token, @Nonnull String endpoint) {
        if (this.state == State.CONNECTED) {
            LOGGER.warn("Player (with Guild ID: {}) is already connected!", guild_id);
            throw new IllegalStateException("State == CONNECTED");
        }
        VoiceUpdate update = new VoiceUpdate(guild_id, Asserter.requireNotNull(session_id), Asserter.requireNotNull(token), Asserter.requireNotNull(endpoint));
        node.getSocket().sendText(JsonStream.serialize(update));
        this.state = State.CONNECTED;
    }

    public LavaPlayerImpl setPosition(long position) {
        this.position = position;
        return this;
    }
    @SuppressWarnings("all")
    public LavaPlayerImpl setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    public LavaPlayerImpl setTrack(AudioTrack track) {
        this.track = track;
        return this;
    }
    @SuppressWarnings("unused")
    public LavaPlayerImpl setChannelId(long channel_id) {
        this.channel_id = channel_id;
        return this;
    }
    @SuppressWarnings("all")
    public LavaPlayerImpl setState(State state) {
        this.state = state;
        return this;
    }
    private void handleTrackPair(TrackDataPair pair, long start, long end) {
        String data = JsonStream.serialize(new PlayTrack(guild_id, start, end, pair.getTrackData()));
        TrackStartEvent event = new TrackStartEvent(this, pair.getTrack());
        setNode(client.getBestNode());
        node.getSocket().sendText(data);
        emitEvent(event);
    }
}
