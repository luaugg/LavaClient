package samophis.lavalink.client.entities.messages.client;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonUnwrapper;
import com.jsoniter.output.JsonStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

public class PlayTrack {
    @JsonIgnore public final String guildId, startTime, endTime, track;
    public PlayTrack(long guildId, @Nullable String startTime, @Nullable String endTime, @Nonnull String track) {
        Objects.requireNonNull(track);
        this.guildId = String.valueOf(guildId);
        this.startTime = startTime == null ? "0" : startTime;
        this.endTime = endTime;
        this.track = track;
    }
    @JsonUnwrapper
    public void unwrapPlayTrack(JsonStream stream) throws IOException {
        stream.writeObjectField("op");
        stream.writeVal("play");
        stream.writeMore();
        stream.writeObjectField("guildId");
        stream.writeVal(guildId);
        stream.writeMore();
        stream.writeObjectField("startTime");
        stream.writeVal(startTime);
        stream.writeMore();
        if (endTime != null) {
            stream.writeObjectField("endTime");
            stream.writeVal(endTime);
            stream.writeMore();
        }
        stream.writeObjectField("track");
        stream.writeVal(track);
    }
}
