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

package samophis.lavalink.client.entities;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Represents the statistics for a given {@link AudioNode AudioNode}, updated every minute.
 *
 * @author SamOphis
 * @since 0.1
 */

@SuppressWarnings("unused")
public interface Statistics {
    /**
     * Fetches the {@link AudioNode AudioNode} connected to this Statistics object.
     * @return The {@link AudioNode AudioNode} associated with this Statistics object.
     */
    @Nonnull
    AudioNode getNode();

    /**
     * Fetches the total amount of all connected players.
     * @return The total amount of all connected players.
     */
    @Nonnegative
    int getPlayers();

    /**
     * Fetches the amount of playing players.
     * @return The amount of playing players.
     */
    @Nonnegative
    int getPlayingPlayers();

    /**
     * Fetches the uptime of the {@link AudioNode AudioNode}, in milliseconds.
     * @return The uptime, in milliseconds, of the {@link AudioNode AudioNode}.
     */
    @Nonnegative
    long getUptime();

    /**
     * Fetches the amount of free memory, in bytes.
     * @return The amount of free memory, in bytes.
     */
    @Nonnegative
    int getFreeMemory();

    /**
     * Fetches the amount of used memory, in bytes.
     * @return The amount of used memory, in bytes.
     */
    @Nonnegative
    int getUsedMemory();

    /**
     * Fetches the amount of allocated memory, in bytes.
     * @return The amount of allocated memory, in bytes.
     */
    @Nonnegative
    int getAllocatedMemory();

    /**
     * Fetches the amount of reservable memory, in bytes.
     * @return The amount of reservable memory, in bytes.
     */
    @Nonnegative
    int getReservableMemory();

    /**
     * Fetches the amount of CPU Cores.
     * @return The amount of CPU Cores.
     */
    @Nonnegative
    int getCpuCores();

    /**
     * Fetches the amount of system load.
     * @return The amount of system load imposed on the {@link AudioNode AudioNode}.
     */
    @Nonnegative
    double getSystemLoad();

    /**
     * Fetches the amount of Lavalink load.
     * @return The amount of Lavalink load imposed on the {@link AudioNode AudioNode}.
     */
    @Nonnegative
    double getLavalinkLoad();

    /**
     * Fetches the possibly-null Frames information.
     * @return <b>possibly-null</b> Frames information.
     */
    @Nonnull
    Frames getFrames();

    /**
     * Represents the <b>possibly-null</b> frame statistics provided every minute by the Lavalink-Server.
     *
     * @author SamOphis
     * @since 0.1
     */
    interface Frames {
        /**
         * Fetches the average amount of frames sent per minute, <b>zero-if-null</b>.
         * @return The average amount of frames sent per minute, <b>zero-if-null</b>.
         */
        @Nonnegative
        int getAverageFramesSentPerMinute();

        /**
         * Fetches the average amount of null frames per minute, <b>zero-if-null</b>.
         * @return The average amount of null frames per minute, <b>zero-if-null</b>.
         */
        @Nonnegative
        int getAverageFramesNulledPerMinute();

        /**
         * Fetches the average amount of deficit frames per minute, <b>zero-if-null</b>.
         * @return The average amount of deficit frames per minute, <b>zero-if-null</b>.
         */
        @Nonnegative
        int getAverageFrameDeficitPerMinute();
    }
}