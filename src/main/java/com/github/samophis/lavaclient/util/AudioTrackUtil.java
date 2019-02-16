package com.github.samophis.lavaclient.util;

import com.github.samophis.lavaclient.exceptions.LocalTrackException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.iharder.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AudioTrackUtil {
	private AudioTrackUtil() {}
	private static final AudioPlayerManager PLAYER_MANAGER = new DefaultAudioPlayerManager();
	private static final Logger LOGGER = LoggerFactory.getLogger(AudioTrackUtil.class);

	@CheckReturnValue
	@Nonnull
	public static AudioTrack fromString(@Nonnull final String data) {
		try {
			final var stream = new ByteArrayInputStream(Base64.decode(data));
			return PLAYER_MANAGER.decodeTrack(new MessageInput(stream)).decodedTrack;
		} catch (final IOException exc) {
			LOGGER.error("error when decoding track: {} | {}", data, exc);
			throw new LocalTrackException(exc);
		}
	}

	@CheckReturnValue
	@Nonnull
	public static String fromTrack(@Nonnull final AudioTrack track) {
		try {
			final var stream = new ByteArrayOutputStream();
			PLAYER_MANAGER.encodeTrack(new MessageOutput(stream), track);
			return Base64.encodeBytes(stream.toByteArray());
		} catch (final IOException exc) {
			LOGGER.error("error when encoding track: {} | {}", track.getIdentifier(), exc);
			throw new LocalTrackException(exc);
		}
	}

	static {
		PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager(true));
		PLAYER_MANAGER.registerSourceManager(new SoundCloudAudioSourceManager(true));
		PLAYER_MANAGER.registerSourceManager(new BeamAudioSourceManager());
		PLAYER_MANAGER.registerSourceManager(new BandcampAudioSourceManager());
		PLAYER_MANAGER.registerSourceManager(new TwitchStreamAudioSourceManager());
		PLAYER_MANAGER.registerSourceManager(new HttpAudioSourceManager());
		PLAYER_MANAGER.registerSourceManager(new VimeoAudioSourceManager());
	}
}
