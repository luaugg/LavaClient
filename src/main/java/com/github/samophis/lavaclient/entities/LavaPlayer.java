/*
   Copyright 2019 Sam Pritchard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.github.samophis.lavaclient.entities;

import com.github.samophis.lavaclient.util.AudioTrackUtil;
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

    void play(@Nonnull final String trackData, @Nonnegative final long startTime, @Nonnegative final long endTime,
              final boolean noReplace);

    void stop();

	void pause();

	void resume();

	void destroy();

	void seek(@Nonnegative final long position);

	void volume(@Nonnegative final int volume);

	void connect(@Nonnull final AudioNode node);

	void initialize(@Nonnull final String sessionId, @Nonnull final String voiceToken,
	                @Nonnull final String endpoint);

	default void play(@Nonnull final AudioTrack track, @Nonnegative final long startTime,
	                  @Nonnegative final long endTime, final boolean noReplace) {
		play(AudioTrackUtil.fromTrack(track), startTime, endTime, noReplace);
	}

	default void play(@Nonnull final AudioTrack track, @Nonnegative final long startTime,
	                  @Nonnegative final long endTime) {
		play(track, startTime, endTime, false);
	}

	default void play(@Nonnull final AudioTrack track, @Nonnegative final long startTime) {
		play(track, startTime, track.getDuration());
	}

	default void play(@Nonnull final AudioTrack track, final boolean noReplace) {
		play(track, 0, track.getDuration(), noReplace);
	}

	default void play(@Nonnull final AudioTrack track) {
		play(track, false);
	}
}
