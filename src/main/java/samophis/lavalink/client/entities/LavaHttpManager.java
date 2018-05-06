package samophis.lavalink.client.entities;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.function.BiConsumer;

public interface LavaHttpManager {
    LavaClient getClient();
    void resolveTrack(String identifier, BiConsumer<String, AudioTrack> callback);
}
