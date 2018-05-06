package samophis.lavalink.client.entities.events;

public interface AudioEventListener {
    void onTrackStart(TrackStartEvent event);
    void onTrackStuck(TrackStuckEvent event);
    void onTrackEnd(TrackEndEvent event);
    void onTrackException(TrackExceptionEvent event);
    void onPlayerPause(PlayerPauseEvent event);
    void onPlayerResume(PlayerResumeEvent event);
}
