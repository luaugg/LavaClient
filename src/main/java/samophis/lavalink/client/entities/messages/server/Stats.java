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

package samophis.lavalink.client.entities.messages.server;

/* -- Short name to prevent collisions with /entities/Statistics -- */

import com.jsoniter.annotation.JsonProperty;

public class Stats {
    public int players, playingPlayers;
    public long uptime;
    public StatsMemory memory;
    public StatsCpu cpu;
    @JsonProperty public StatsFrames frameStats;
    public class StatsMemory {
        public int free, used, allocated, reservable;
    }
    public class StatsCpu {
        public int cores;
        public double systemLoad, lavalinkLoad;
    }
    public class StatsFrames {
        public int sent, nulled, deficit;
    }
}
