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
import samophis.lavalink.client.entities.Statistics;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class StatisticsImpl implements Statistics {
    private final AudioNode node;
    private final int players, playingPlayers, free, used, allocated, reservable, cores;
    private final long uptime;
    private final double systemLoad, lavalinkLoad;
    private final Frames frames;
    // These values aren't checked, so all LavaClient-provided Statistics will be fine but custom made ones can be different.
    // Yes, it is possible to edit the source code of the server or client to provide negative values.
    // You don't get any benefit from it.
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
    @Nonnull
    public AudioNode getNode() {
        return node;
    }
    @Override
    @Nonnegative
    public int getPlayers() {
        return players;
    }
    @Override
    @Nonnegative
    public int getPlayingPlayers() {
        return playingPlayers;
    }
    @Override
    @Nonnegative
    public int getFreeMemory() {
        return free;
    }
    @Override
    @Nonnegative
    public int getUsedMemory() {
        return used;
    }
    @Override
    @Nonnegative
    public int getAllocatedMemory() {
        return allocated;
    }
    @Override
    @Nonnegative
    public int getReservableMemory() {
        return reservable;
    }
    @Override
    @Nonnegative
    public int getCpuCores() {
        return cores;
    }
    @Override
    @Nonnegative
    public double getSystemLoad() {
        return systemLoad;
    }
    @Override
    @Nonnegative
    public double getLavalinkLoad() {
        return lavalinkLoad;
    }
    @Override
    @Nonnegative
    public long getUptime() {
        return uptime;
    }
    @Override
    @Nonnull
    public Frames getFrames() {
        return frames;
    }
}
