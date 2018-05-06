package samophis.lavalink.client.entities;

public interface Statistics {
    int getPlayers();
    int getPlayingPlayers();
    long getUptime();
    int getFreeMemory();
    int getUsedMemory();
    int getAllocatedMemory();
    int getReservableMemory();
    int getCpuCores();
    double getSystemLoad();
    double getLavalinkLoad();
    Frames getFrames();
    interface Frames {
        int getAverageFramesSentPerMinute();
        int getAverageFramesNulledPerMinute();
        int getAverageFrameDeficitPerMinute();
    }
}