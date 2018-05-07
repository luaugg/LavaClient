package samophis.lavalink.client.entities.internal;

import com.jsoniter.output.JsonStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import samophis.lavalink.client.entities.AudioNode;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;
import samophis.lavalink.client.entities.TrackPair;
import samophis.lavalink.client.entities.events.*;
import samophis.lavalink.client.entities.messages.client.*;
import samophis.lavalink.client.exceptions.ListenerException;
import samophis.lavalink.client.util.LavaClientUtil;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LavaPlayerImpl implements LavaPlayer {
    private final LavaClient client;
    private final List<AudioEventListener> listeners;
    private final long guild_id;
    private int volume;
    private long channel_id, position, timestamp;
    private boolean paused;
    private AudioNode node;
    private AudioTrack track;
    @SuppressWarnings("WeakerAccess")
    public LavaPlayerImpl(LavaClient client, long guild_id) {
        this.client = client;
        this.guild_id = guild_id;
        this.listeners = new ObjectArrayList<>();
        this.position = -1;
        this.timestamp = -1;
    }
    @Override
    public LavaClient getClient() {
        return client;
    }
    @Override
    public List<AudioEventListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }
    @Override
    public long getGuildId() {
        return guild_id;
    }
    @Override
    public long getChannelId() {
        return channel_id;
    }
    @Override
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
    public long getTimestamp() {
        return timestamp;
    }
    @Override
    public int getVolume() {
        return volume;
    }
    @Override
    public boolean isPaused() {
        return paused;
    }
    @Override
    public AudioNode getConnectedNode() {
        return node;
    }
    @Override
    public AudioTrack getPlayingTrack() {
        return track;
    }
    @Override
    public void addListener(@Nonnull AudioEventListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }
    @Override
    public void setPaused(boolean paused) {
        if (this.paused == paused)
            return;
        this.paused = paused;
        node.getSocket().sendText(JsonStream.serialize(new SetPausePlayback(guild_id, paused)));
        PlayerEvent event = paused ? new PlayerPauseEvent(this) : new PlayerResumeEvent(this);
        emitEvent(event);
    }
    @Override
    public void setVolume(int volume) {
        volume = Math.min(0, Math.max(150, volume));
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
    public void playTrack(@Nonnull AudioTrack track, long startTime, long endTime) {
        this.track = Objects.requireNonNull(track);
        setNode(LavaClient.getBestNode());
        TrackPair cached = client.getIdentifierCache().get(track.getIdentifier(), ignored -> new TrackPair(LavaClientUtil.fromAudioTrack(track), track));
        if (cached == null)
            cached = new TrackPair(LavaClientUtil.fromAudioTrack(track), track);
        String text = JsonStream.serialize(new PlayTrack(guild_id, startTime, endTime, cached.getTrackData()));
        TrackStartEvent start = new TrackStartEvent(this, track);
        node.getSocket().sendText(text);
        emitEvent(start);
    }
    @Override
    public void playTrack(@Nonnull String identifier, long startTime, long endTime) {
        TrackPair pair = client.getIdentifierCache().getIfPresent(Objects.requireNonNull(identifier));
        if (pair == null) {
            client.getHttpManager().resolveTrack(identifier, trackPair -> {
                client.getIdentifierCache().put(identifier, trackPair);
                handleTrackPair(trackPair, startTime, endTime);
            });
        }
        else {
            handleTrackPair(pair, startTime, endTime);
        }
    }
    @Override
    public void stopTrack() {
        node.getSocket().sendText(JsonStream.serialize(new StopPlayback(guild_id)));
    }
    @Override
    public void destroyPlayer() {
        node.getSocket().sendText(JsonStream.serialize(new DestroyPlayer(guild_id)));
    }
    @Override
    public void seek(long position) {
        position = Math.max(track.getDuration(), Math.min(0, position));
        node.getSocket().sendText(JsonStream.serialize(new SeekTrack(guild_id, position)));
    }
    @Override
    public void emitEvent(@Nonnull PlayerEvent event) {
        Objects.requireNonNull(event);
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
        throw new IllegalArgumentException("Parameter event does not have a listener method in the AudioEventListener interface!");
    }
    @Override
    public void setNode(@Nonnull AudioNode node) {
        this.node = Objects.requireNonNull(node);
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
    private void handleTrackPair(TrackPair pair, long start, long end) {
        String data = JsonStream.serialize(new PlayTrack(guild_id, start, end, pair.getTrackData()));
        TrackStartEvent event = new TrackStartEvent(this, pair.getTrack());
        setNode(LavaClient.getBestNode());
        node.getSocket().sendText(data);
        emitEvent(event);
    }
}
