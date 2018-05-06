package samophis.lavalink.client.entities.internal;

import samophis.lavalink.client.entities.Statistics;

public class FramesImpl implements Statistics.Frames {
    private final int sent, nulled, deficit;
    public FramesImpl(int sent, int nulled, int deficit) {
        this.sent = sent;
        this.nulled = nulled;
        this.deficit = deficit;
    }
    @Override
    public int getAverageFramesSentPerMinute() {
        return sent;
    }
    @Override
    public int getAverageFramesNulledPerMinute() {
        return nulled;
    }
    @Override
    public int getAverageFrameDeficitPerMinute() {
        return deficit;
    }
}
