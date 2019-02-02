package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LavaClient;
import com.github.samophis.lavaclient.entities.LavaPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

@Getter
@Setter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class LavaPlayerImpl implements LavaPlayer {
	private final LavaClient client;
	private final long guildId;
	private long timestamp;
	private long position;
	private int volume;
	private boolean paused;
	private AudioTrack playingTrack;
	private AudioNode connectedNode;

	@Override
	public void pause() {
		// pause logic
	}

	@Override
	public void resume() {
		// resume logic
	}

	@Override
	public void destroy() {
		// destroy logic
	}

	@Override
	public void seek(@Nonnegative final long position) {
		if (position < 0) {
			throw new IllegalArgumentException("negative position!");
		}
		// position update logic
	}

	@Override
	public void connect(@Nonnull final AudioNode node) {
		// connect to node logic
	}

	@Override
	public void initialize(@Nonnull final String session_id, @Nonnull final String voice_token, @Nonnull final String endpoint) {
		// voice update logic
	}

	public void stop() {
		// stop logic
	}

}
