package samophis.lavalink.client.entities;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Represents a pair between a track's encoded Base64 data and the track itself.
 * <br><p>The data can be retrieved with LavaClientUtil#fromAudioTrack.</p>
 *
 * @author SamOphis
 * @since 0.2
 */

public class TrackPair {
    private final String track_data;
    private final AudioTrack track;
    public TrackPair(String track_data, AudioTrack track) {
        this.track_data = track_data;
        this.track = track;
    }
    public String getTrackData() {
        return track_data;
    }
    public AudioTrack getTrack() {
        return track;
    }
}
