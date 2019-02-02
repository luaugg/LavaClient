package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LavaClient;
import com.github.samophis.lavaclient.entities.LavaPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

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

	@Nonnull
	@Override
	public LavaPlayer stop() {
		return this;
	}

	@Nonnull
	@Override
	public LavaPlayer pause() {
		return this;
	}

	@Nonnull
	@Override
	public LavaPlayer resume() {
		return this;
	}

	@Nonnull
	@Override
	public LavaPlayer destroy() {
		return this;
	}

	@Nonnull
	@Override
	public LavaPlayer seek(final long position) {
		return this;
	}

	@Nonnull
	@Override
	public LavaPlayer connect(@Nonnull final AudioNode node) {
		return this;
	}

	@Nonnull
	@Override
	public LavaPlayer initialize(@Nonnull final String session_id, @Nonnull final String voice_token, @Nonnull final String endpoint) {
		return this;
	}
}
