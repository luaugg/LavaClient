package samophis.lavalink.client.entities;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.function.BiConsumer;

/**
 * This class is the manager which allows LavaClient to send requests to get information from a track for use in both the client and the server.
 *
 * @author SamOphis
 * @since 0.1
 */

public interface LavaHttpManager {
    /**
     * Fetches the {@link LavaClient LavaClient} associated with this manager.
     * @return The {@link LavaClient LavaClient} attached to this manager.
     */
    LavaClient getClient();

    /**
     * Sends a request to the {@link AudioNode AudioNode} with the least load on it to get back the AudioTrack.
     * <br><p>This sends back both the data of the AudioTrack and its encoded representation, for use in playing tracks.</p>
     * @param identifier The identifier of the source to be played -- automatically URL-Encoded.
     * @param callback The callback fired upon a successful response. The first parameter is the encoded track and the second is the AudioTrack itself.
     */
    void resolveTrack(String identifier, BiConsumer<String, AudioTrack> callback);
}
