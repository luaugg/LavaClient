package com.github.samophis.lavaclient.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface LavalinkTrackEvent extends LavalinkPlayerEvent {
	@Nonnull
	@CheckReturnValue
	AudioTrack track();
}
