package samophis.lavalink.client.entities.internal;

import samophis.lavalink.client.entities.AudioNode;
import samophis.lavalink.client.entities.LoadBalancer;
import samophis.lavalink.client.entities.Statistics;

/*
  Note: This uses the very same equations as described in Frederikam's original JDA Lavalink Client here:
        https://github.com/Frederikam/Lavalink/blob/master/LavalinkClient/src/main/java/lavalink/client/io/LavalinkLoadBalancer.java

  All credit goes to Frederikam for the load balancing methods!
  This client library is only meant to increase speed (via faster libraries) and to remove the dependency on JDA so that it can be more easily ported to other libraries.

  Although it is almost entirely my own code, certain examples like this are copied from Frederikam to produce consistent, proven results in production (and to maintain full coverage
  against the original client, too!)
 */

public class LoadBalancerImpl implements LoadBalancer {
    private final AudioNode node;
    private int playerPenalty, cpuPenalty, defFramePenalty, nullFramePenalty;
    @SuppressWarnings("WeakerAccess")
    public LoadBalancerImpl(AudioNode node) {
        this.node = node;
    }
    @Override
    public AudioNode getNode() {
        return node;
    }
    @Override
    public int getPlayerPenalty() {
        return playerPenalty;
    }
    @Override
    public int getCpuPenalty() {
        return cpuPenalty;
    }
    @Override
    public int getDeficitFramePenalty() {
        return defFramePenalty;
    }
    @Override
    public int getNullFramePenalty() {
        return nullFramePenalty;
    }
    @Override
    public int getTotalPenalty() {
        return node.getStatistics() == null || !node.isAvailable()
            ? Integer.MAX_VALUE - 1
            : playerPenalty + cpuPenalty + defFramePenalty + nullFramePenalty;
    }
    public LoadBalancerImpl initWithNode() {
        Statistics stats = node.getStatistics();
        if (stats == null)
            return this;
        playerPenalty = stats.getPlayingPlayers();
        cpuPenalty = (int) Math.pow(1.05d, 100 * stats.getSystemLoad()) * 10 - 10;
        defFramePenalty = (int) (Math.pow(1.03d, 500f * ((float) stats.getFrames().getAverageFrameDeficitPerMinute() / 3000f)) * 600 - 600);
        nullFramePenalty = ((int) (Math.pow(1.03d, 500f * ((float) stats.getFrames().getAverageFramesNulledPerMinute() / 3000f)) * 300 - 300)) * 2;
        return this;
    }
}
