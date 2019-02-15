package com.github.samophis.lavaclient.exceptions;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LavaPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@Getter(onMethod_ = {@CheckReturnValue, @Nonnull})
@Accessors(fluent = true)
public class RemoteTrackException extends RuntimeException {
	private final AudioTrack track;
	private final AudioNode node;
	private final LavaPlayer player;
	private final String reason;

	public RemoteTrackException(final AudioTrack track, final AudioNode node, final LavaPlayer player,
	                            final String reason) {
		super(reason);
		this.track = track;
		this.node = node;
		this.player = player;
		this.reason = reason;
	}
}
