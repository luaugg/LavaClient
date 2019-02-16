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
    @Nonnull
    PlayerState state();

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

    void stop();

	void pause();

	void resume();

	void destroy();

	void seek(@Nonnegative final long position);

	void volume(@Nonnegative final int volume);

	void connect(@Nonnull final AudioNode node);

	void initialize(@Nonnull final String sessionId, @Nonnull final String voiceToken,
	                @Nonnull final String endpoint);
}
