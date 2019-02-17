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
package com.github.samophis.lavaclient.entities.internal;

import com.github.samophis.lavaclient.entities.AudioNode;
import com.github.samophis.lavaclient.entities.LavaClient;
import com.github.samophis.lavaclient.entities.LavaPlayer;
import com.github.samophis.lavaclient.entities.PlayerState;
import com.github.samophis.lavaclient.util.EntityBuilder;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

@Getter
@Setter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class LavaPlayerImpl implements LavaPlayer {
	private static final Logger LOGGER = LoggerFactory.getLogger(LavaPlayerImpl.class);
	private final LavaClient client;
	private final long guildId;
	private long timestamp;
	private long position;
	private int volume;
	private boolean paused;
	private PlayerState state;
	private AudioTrack playingTrack;
	private AudioNodeImpl connectedNode;
	private String guildIdString;

	@Override
	public void volume(@Nonnegative final int volume) {
		if (volume < 0 || volume > 1000) {
			LOGGER.warn("out of bounds volume: {}, guild id: {}", volume, guildIdAsString());
			throw new IllegalArgumentException("volume out of bounds!");
		}
		final var volumeUpdate = EntityBuilder.createVolumePayload(guildIdAsString(), volume);
		send(volumeUpdate);
	}

	@Override
	public void play(@Nonnull final String trackData, @Nonnegative final long startTime,
	                 @Nonnegative final long endTime, final boolean noReplace) {
			if (startTime < 0 || startTime >= endTime) {
				LOGGER.warn("startTime out of bounds: {}, guild id: {}", startTime, guildIdAsString());
				throw new IllegalArgumentException("startTime out of bounds!");
			}
			final var play = EntityBuilder.createPlayPayload(guildIdAsString(), trackData, String.valueOf(startTime),
					String.valueOf(endTime), noReplace);
			send(play);
	}

	@Override
	public void stop() {
		if (playingTrack == null) {
			LOGGER.warn("no track playing during an attempt to stop! guild id: {}", guildIdAsString());
			throw new IllegalStateException("can't stop a track which doesn't exist!");
		}
		final var stop = EntityBuilder.createStopPayload(guildIdAsString());
		send(stop);
	}

	@Override
	public void pause() {
		if (paused) {
			LOGGER.warn("already paused! guild id: {}", guildIdAsString());
			throw new IllegalStateException("already paused!");
		}
		final var pause = EntityBuilder.createPausePayload(guildIdAsString(), true);
		send(pause);
	}

	@Override
	public void resume() {
		if (!paused) {
			LOGGER.warn("already resumed! guild id: {}", guildIdAsString());
			throw new IllegalStateException("already resumed!");
		}
		final var resume = EntityBuilder.createPausePayload(guildIdAsString(), false);
		send(resume);
	}

	@Override
	public void destroy() {
		if (state == PlayerState.DESTROYED) {
			LOGGER.warn("already destroyed! guild id: {}", guildIdAsString());
			throw new IllegalStateException("already destroyed!");
		}
		state = PlayerState.DESTROYED;
		final var destroyed = EntityBuilder.createDestroyPayload(guildIdAsString());
		send(destroyed);
	}

	@Override
	public void seek(@Nonnegative final long position) {
		if (playingTrack == null) {
			LOGGER.warn("no track is playing, seek requested! guild id: {}", guildIdAsString());
			throw new IllegalArgumentException("can't seek when no track is playing!");
		}
		if (position < 0) {
			LOGGER.warn("position is negative, not allowed! guild id: {}", guildIdAsString());
			throw new IllegalArgumentException("negative position!");
		}
		final var seek = EntityBuilder.createSeekPayload(guildIdAsString(), position);
		send(seek);
	}

	@Override
	public void connect(@Nonnull final AudioNode node) {
		if (connectedNode == null) {
			node.openConnection(() -> {
				connectedNode = (AudioNodeImpl) node;
				state = PlayerState.CONNECTED;
			});
			return;
		}
		if (state == PlayerState.INITIALIZED) {
			destroy();
		}
		connectedNode.closeConnection(() -> node.openConnection(() -> {
			connectedNode = (AudioNodeImpl) node;
			state = PlayerState.CONNECTED;
		}));
	}

	@Override
	public void initialize(@Nonnull final String sessionId, @Nonnull final String voiceToken,
	                       @Nonnull final String endpoint) {
		if (state == PlayerState.INITIALIZED) {
			LOGGER.warn("player already initialized! guild id: {}", guildIdAsString());
			throw new IllegalStateException("player already initialized!");
		}
		state = PlayerState.INITIALIZED;
		final var init = EntityBuilder.createVoiceUpdatePayload(guildIdAsString(), sessionId, voiceToken, endpoint);
		send(init);
	}

	private String guildIdAsString() {
		if (guildIdString == null) {
			guildIdString = String.valueOf(guildId);
		}
		return guildIdString;
	}

	private void send(@Nonnull final JsonObject payload) {
		client.vertx().eventBus().publish(connectedNode.sendAddress(), payload);
	}
}
