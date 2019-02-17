package com.github.samophis.lavaclient.entities;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface AudioLoadResult {
	@CheckReturnValue
	@Nonnull
	List<AudioTrack> tracks();

	@CheckReturnValue
	@Nullable
	AudioTrack first();

	@CheckReturnValue
	boolean playlist();

	@CheckReturnValue
	@Nonnull
	LoadType type();

	@CheckReturnValue
	@Nullable
	String playlistName();

	@CheckReturnValue
	@Nullable
	@Nonnegative
	Long selectedTrack();
}
