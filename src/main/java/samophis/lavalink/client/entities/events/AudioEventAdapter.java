package samophis.lavalink.client.entities.events;

public class AudioEventAdapter implements AudioEventListener {
    @Override
    public void onTrackStart(TrackStartEvent event) {}
    @Override
    public void onTrackStuck(TrackStuckEvent event) {}
    @Override
    public void onTrackEnd(TrackEndEvent event) {}
    @Override
    public void onTrackException(TrackExceptionEvent event) {}
    @Override
    public void onPlayerPause(PlayerPauseEvent event) {}
    @Override
    public void onPlayerResume(PlayerResumeEvent event) {}
}
