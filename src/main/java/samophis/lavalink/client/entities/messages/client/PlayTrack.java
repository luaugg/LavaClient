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

package samophis.lavalink.client.entities.messages.client;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonUnwrapper;
import com.jsoniter.output.JsonStream;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

public class PlayTrack {
    @SuppressWarnings("WeakerAccess")
    @JsonIgnore public final String guildId, startTime, endTime, track;
    public PlayTrack(long guildId, long startTime, long endTime, @Nonnull String track) {
        Objects.requireNonNull(track);
        if (endTime < startTime && endTime != -1)
            throw new IllegalArgumentException("Ending time cannot be smaller than starting time.");
        this.guildId = String.valueOf(guildId);
        this.startTime = startTime < 1 ? null : String.valueOf(startTime);
        this.endTime = endTime == -1 ? null : String.valueOf(endTime);
        this.track = track;
    }
    @SuppressWarnings("unused")
    @JsonUnwrapper
    public void unwrapPlayTrack(JsonStream stream) throws IOException {
        stream.writeObjectField("op");
        stream.writeVal("play");
        stream.writeMore();
        stream.writeObjectField("guildId");
        stream.writeVal(guildId);
        stream.writeMore();
        if (startTime != null) {
            stream.writeObjectField("startTime");
            stream.writeVal(startTime);
            stream.writeMore();
        }
        if (endTime != null) {
            stream.writeObjectField("endTime");
            stream.writeVal(endTime);
            stream.writeMore();
        }
        stream.writeObjectField("track");
        stream.writeVal(track);
    }
}
