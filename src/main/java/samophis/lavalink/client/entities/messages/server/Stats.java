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
