package samophis.lavalink.client.util;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import net.iharder.Base64;

import java.io.IOException;

/**
 * Contains two utility methods used to convert to and from Lavaplayer AudioTracks.
 *
 * @author SamOphis
 * @since 0.1
 */

public class LavaClientUtil {
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private LavaClientUtil() {}

    /**
     * Converts a Lavaplayer AudioTrack into an encoded Base64 stream.
     * @param track The Lavaplayer AudioTrack to convert.
     * @return An encoded Base64 stream representing the AudioTrack.
     */
    public static String fromAudioTrack(AudioTrack track) {
        String data;
        try {
            FastByteArrayOutputStream out = new FastByteArrayOutputStream();
            MANAGER.encodeTrack(new MessageOutput(out), track);
            data = Base64.encodeBytes(out.array);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
        return data;
    }

    /**
     * Converts an encoded Base64 stream into a Lavaplayer AudioTrack object.
     * @param data The encoded Base64 stream.
     * @return An AudioTrack object.
     */
    public static AudioTrack toAudioTrack(String data) {
        AudioTrack track;
        try {
            track = MANAGER.decodeTrack(new MessageInput(new FastByteArrayInputStream(Base64.decode(data)))).decodedTrack;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
        return track;
    }
    static {
        MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
        MANAGER.registerSourceManager(new SoundCloudAudioSourceManager());
        MANAGER.registerSourceManager(new VimeoAudioSourceManager());
        MANAGER.registerSourceManager(new BandcampAudioSourceManager());
        MANAGER.registerSourceManager(new TwitchStreamAudioSourceManager());
        MANAGER.registerSourceManager(new HttpAudioSourceManager());
        Runtime.getRuntime().addShutdownHook(new Thread(MANAGER::shutdown));
    }
}
