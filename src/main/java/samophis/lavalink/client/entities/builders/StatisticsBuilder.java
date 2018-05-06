package samophis.lavalink.client.entities.builders;

import samophis.lavalink.client.entities.AudioNode;
import samophis.lavalink.client.entities.Statistics;
import samophis.lavalink.client.entities.internal.StatisticsImpl;

import javax.annotation.Nonnull;
import java.util.Objects;

public class StatisticsBuilder {
    private final AudioNode node;
    private int players, playingPlayers, free, used, allocated, reservable, cores, sent, nulled, deficit;
    private long uptime;
    private double systemLoad, lavalinkLoad;
    public StatisticsBuilder(@Nonnull AudioNode node) {
        this.node = Objects.requireNonNull(node);
    }
    public StatisticsBuilder setPlayers(int players) {
        this.players = players;
        return this;
    }
    public StatisticsBuilder setPlayingPlayers(int playingPlayers) {
        this.playingPlayers = playingPlayers;
        return this;
    }
    public StatisticsBuilder setFree(int free) {
        this.free = free;
        return this;
    }
    public StatisticsBuilder setUsed(int used) {
        this.used = used;
        return this;
    }
    public StatisticsBuilder setAllocated(int allocated) {
        this.allocated = allocated;
        return this;
    }
    public StatisticsBuilder setReservable(int reservable) {
        this.reservable = reservable;
        return this;
    }
    public StatisticsBuilder setCores(int cores) {
        this.cores = cores;
        return this;
    }
    public StatisticsBuilder setSent(int sent) {
        this.sent = sent;
        return this;
    }
    public StatisticsBuilder setNulled(int nulled) {
        this.nulled = nulled;
        return this;
    }
    public StatisticsBuilder setDeficit(int deficit) {
        this.deficit = deficit;
        return this;
    }
    public StatisticsBuilder setUptime(long uptime) {
        this.uptime = uptime;
        return this;
    }
    public StatisticsBuilder setLavalinkLoad(double lavalinkLoad) {
        this.lavalinkLoad = lavalinkLoad;
        return this;
    }
    public StatisticsBuilder setSystemLoad(double systemLoad) {
        this.systemLoad = systemLoad;
        return this;
    }
    public Statistics build() {
        return new StatisticsImpl(node, players, playingPlayers, free, used, allocated, reservable, cores, sent, nulled, deficit, uptime, systemLoad, lavalinkLoad);
    }
}
