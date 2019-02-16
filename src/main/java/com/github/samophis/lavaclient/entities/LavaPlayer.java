package com.github.samophis.lavaclient.entities;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface LavaPlayer {
    @CheckReturnValue
    @Nullable
    AudioTrack playingTrack();

    @CheckReturnValue
    @Nonnull
    AudioNode connectedNode();

    @CheckReturnValue
    @Nonnull
    LavaClient client();

    @CheckReturnValue
    @Nonnegative
    long guildId();

    @CheckReturnValue
    @Nonnegative
    long timestamp();

    @CheckReturnValue
    @Nonnegative
    long position();

    @CheckReturnValue
    @Nonnegative
    int volume();

    @CheckReturnValue
    boolean paused();

    @CheckReturnValue
    @Nonnull
    LavaPlayer stop();

	@CheckReturnValue
	@Nonnull
	LavaPlayer pause();

	@CheckReturnValue
	@Nonnull
	LavaPlayer resume();

	@CheckReturnValue
	@Nonnull
	LavaPlayer destroy();

	@CheckReturnValue
	@Nonnull
	LavaPlayer seek(@Nonnegative final long position);

	@CheckReturnValue
	@Nonnull
	LavaPlayer volume(@Nonnegative final int volume);

	@CheckReturnValue
	@Nonnull
	LavaPlayer connect(@Nonnull final AudioNode node);

	@CheckReturnValue
	@Nonnull
	LavaPlayer initialize(@Nonnull final String session_id, @Nonnull final String voice_token, @Nonnull final String endpoint);
}
