package com.github.samophis.lavaclient.events;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LavaPlayer;
import com.github.samophis.lavaclient.exceptions.RemoteTrackException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@Getter(onMethod_ = {@CheckReturnValue, @Nonnull})
@Accessors(fluent = true)
@RequiredArgsConstructor
public class TrackExceptionEvent implements LavalinkTrackEvent {
	private final AudioTrack track;
	private final AudioNode node;
	private final LavaPlayer player;
	private final RemoteTrackException exception;
	private final EventType type = EventType.TRACK_EXCEPTION_EVENT;
}
