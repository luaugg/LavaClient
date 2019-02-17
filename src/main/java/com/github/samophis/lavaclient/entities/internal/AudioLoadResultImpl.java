package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.AudioLoadResult;
import com.github.samophis.lavaclient.entities.LoadType;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Getter(onMethod_ = {@CheckReturnValue, @Nonnull})
@Accessors(fluent = true)
@RequiredArgsConstructor
public class AudioLoadResultImpl implements AudioLoadResult {
	private final List<AudioTrack> tracks;
	private final LoadType type;

	@Getter(onMethod_ = {@CheckReturnValue, @Nullable})
	private final String playlistName;

	@Getter(onMethod_ = {@CheckReturnValue, @Nullable, @Nonnegative})
	private final Long selectedTrack;

	@Getter(onMethod_ = @CheckReturnValue)
	private final boolean playlist;

	@CheckReturnValue
	@Nonnull
	@Override
	public List<AudioTrack> tracks() {
		return Collections.unmodifiableList(tracks);
	}

	@CheckReturnValue
	@Nullable
	@Override
	public AudioTrack first() {
		return tracks.isEmpty() ? null : tracks.get(0);
	}
}
