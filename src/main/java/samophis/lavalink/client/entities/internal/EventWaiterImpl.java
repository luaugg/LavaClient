package samophis.lavalink.client.entities.internal;

import com.jsoniter.output.JsonStream;
import samophis.lavalink.client.entities.AudioNode;
import samophis.lavalink.client.entities.EventWaiter;
import samophis.lavalink.client.entities.messages.client.VoiceUpdate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class EventWaiterImpl implements EventWaiter {
    private final AudioNode node;
    private final long guild_id;
    private String session_id, token, endpoint;
    public EventWaiterImpl(@Nonnull AudioNode node, long guild_id) {
        this.node = Objects.requireNonNull(node);
        this.guild_id = guild_id;
    }
    @Nullable
    @Override
    public String getSessionId() {
        return session_id;
    }
    @Nullable
    @Override
    public String getToken() {
        return token;
    }
    @Nullable
    @Override
    public String getEndpoint() {
        return endpoint;
    }
    @Override
    public AudioNode getNode() {
        return node;
    }
    @Override
    public long getGuildId() {
        return guild_id;
    }
    @Override
    public void setSessionIdAndTryConnect(@Nonnull String session_id) {
        this.session_id = Objects.requireNonNull(session_id);
        if (token != null && endpoint != null)
            tryConnect();
    }
    @Override
    public void setServerAndTryConnect(@Nonnull String token, @Nonnull String endpoint) {
        this.token = Objects.requireNonNull(token);
        this.endpoint = Objects.requireNonNull(endpoint);
        if (session_id != null)
            tryConnect();
    }
    @Override
    public void tryConnect() {
        node.getSocket().sendText(JsonStream.serialize(new VoiceUpdate(guild_id, session_id, token, endpoint)));
    }
}
