package samophis.lavalink.client.entities;

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
    AudioNode getNode();

    /**
     * Fetches the total amount of all connected players.
     * @return The total amount of all connected players.
     */
    int getPlayers();

    /**
     * Fetches the amount of playing players.
     * @return The amount of playing players.
     */
    int getPlayingPlayers();

    /**
     * Fetches the uptime of the {@link AudioNode AudioNode}, in milliseconds.
     * @return The uptime, in milliseconds, of the {@link AudioNode AudioNode}.
     */
    long getUptime();

    /**
     * Fetches the amount of free memory, in bytes.
     * @return The amount of free memory, in bytes.
     */
    int getFreeMemory();

    /**
     * Fetches the amount of used memory, in bytes.
     * @return The amount of used memory, in bytes.
     */
    int getUsedMemory();

    /**
     * Fetches the amount of allocated memory, in bytes.
     * @return The amount of allocated memory, in bytes.
     */
    int getAllocatedMemory();

    /**
     * Fetches the amount of reservable memory, in bytes.
     * @return The amount of reservable memory, in bytes.
     */
    int getReservableMemory();

    /**
     * Fetches the amount of CPU Cores.
     * @return The amount of CPU Cores.
     */
    int getCpuCores();

    /**
     * Fetches the amount of system load.
     * @return The amount of system load imposed on the {@link AudioNode AudioNode}.
     */
    double getSystemLoad();

    /**
     * Fetches the amount of Lavalink load.
     * @return The amount of Lavalink load imposed on the {@link AudioNode AudioNode}.
     */
    double getLavalinkLoad();

    /**
     * Fetches the possibly-null Frames information.
     * @return <b>possibly-null</b> Frames information.
     */
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
        int getAverageFramesSentPerMinute();

        /**
         * Fetches the average amount of null frames per minute, <b>zero-if-null</b>.
         * @return The average amount of null frames per minute, <b>zero-if-null</b>.
         */
        int getAverageFramesNulledPerMinute();

        /**
         * Fetches the average amount of deficit frames per minute, <b>zero-if-null</b>.
         * @return The average amount of deficit frames per minute, <b>zero-if-null</b>.
         */
        int getAverageFrameDeficitPerMinute();
    }
}