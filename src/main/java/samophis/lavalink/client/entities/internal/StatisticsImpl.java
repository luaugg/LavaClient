package samophis.lavalink.client.entities.internal;

import samophis.lavalink.client.entities.AudioNode;
import samophis.lavalink.client.entities.Statistics;

public class StatisticsImpl implements Statistics {
    private final AudioNode node;
    private final int players, playingPlayers, free, used, allocated, reservable, cores;
    private final long uptime;
    private final double systemLoad, lavalinkLoad;
    private final Frames frames;
    public StatisticsImpl(AudioNode node, int players, int playingPlayers, int free, int used, int allocated, int reservable, int cores, int sent, int nulled, int deficit,
                          long uptime, double systemLoad, double lavalinkLoad) {
        this.node = node;
        this.players = players;
        this.playingPlayers = playingPlayers;
        this.free = free;
        this.used = used;
        this.reservable = reservable;
        this.allocated = allocated;
        this.cores = cores;
        this.frames = new FramesImpl(sent, nulled, deficit);
        this.uptime = uptime;
        this.systemLoad = systemLoad;
        this.lavalinkLoad = lavalinkLoad;
    }
    @Override
    public AudioNode getNode() {
        return node;
    }
    @Override
    public int getPlayers() {
        return players;
    }
    @Override
    public int getPlayingPlayers() {
        return playingPlayers;
    }
    @Override
    public int getFreeMemory() {
        return free;
    }
    @Override
    public int getUsedMemory() {
        return used;
    }
    @Override
    public int getAllocatedMemory() {
        return allocated;
    }
    @Override
    public int getReservableMemory() {
        return reservable;
    }
    @Override
    public int getCpuCores() {
        return cores;
    }
    @Override
    public double getSystemLoad() {
        return systemLoad;
    }
    @Override
    public double getLavalinkLoad() {
        return lavalinkLoad;
    }
    @Override
    public long getUptime() {
        return uptime;
    }
    @Override
    public Frames getFrames() {
        return frames;
    }
}
