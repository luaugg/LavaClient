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

import samophis.lavalink.client.entities.AudioNode;
import samophis.lavalink.client.entities.LoadBalancer;
import samophis.lavalink.client.entities.Statistics;
import samophis.lavalink.client.util.Asserter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

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
    public LoadBalancerImpl(@Nonnull AudioNode node) {
        this.node = Asserter.requireNotNull(node);
    }
    @Override
    @Nonnull
    public AudioNode getNode() {
        return node;
    }
    @Override
    @Nonnegative
    public int getPlayerPenalty() {
        return playerPenalty;
    }
    @Override
    @Nonnegative
    public int getCpuPenalty() {
        return cpuPenalty;
    }
    @Override
    @Nonnegative
    public int getDeficitFramePenalty() {
        return defFramePenalty;
    }
    @Override
    @Nonnegative
    public int getNullFramePenalty() {
        return nullFramePenalty;
    }
    @Override
    @Nonnegative
    public int getTotalPenalty() {
        return node.getStatistics() == null || !node.isAvailable()
            ? Integer.MAX_VALUE - 1
            : playerPenalty + cpuPenalty + defFramePenalty + nullFramePenalty;
    }
    @Nonnull
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
