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

import com.github.samophis.lavaclient.entities.*;
import com.github.samophis.lavaclient.exceptions.HttpTrackException;
import com.github.samophis.lavaclient.util.AudioTrackUtil;
import com.github.samophis.lavaclient.util.EntityBuilder;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.escoffier.vertx.completablefuture.VertxCompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class LavaPlayerImpl implements LavaPlayer {
	private static final Logger LOGGER = LoggerFactory.getLogger(LavaPlayerImpl.class);
	private final LavaClientImpl client;
	private final long guildId;
	private long timestamp;
	private long position;
	private int volume;
	private boolean paused;
	private PlayerState state;
	private AudioTrack playingTrack;
	private AudioNodeImpl connectedNode;
	private String guildIdString;
	private String lastSessionId;
	private String lastVoiceToken;
	private String lastEndpoint;

	@Nonnull
	@SuppressWarnings("deprecation") // we're not using vert.x 4, no need to worry on 3.6.2
	@Override
	public CompletionStage<AudioLoadResult> loadTracksAsync(@Nonnull final String identifier) {
		final var future = new VertxCompletableFuture<AudioLoadResult>(client.vertx());
		final var path = String.format("/loadtracks?identifier=%s", URLEncoder.encode(identifier,
				StandardCharsets.UTF_8));
		final var request = client.client().request(HttpMethod.GET, connectedNode.port(), connectedNode.baseUrl(), path);
		if (!connectedNode.password().isEmpty()) {
			request.putHeader("Authorization", connectedNode.password());
		}
		request.send(result -> {
			if (result.succeeded()) {
				final var response = result.result();
				if (response.statusCode() != 200) {
					LOGGER.warn("status code: {}, audio failed to load!", response.statusCode());
					future.completeExceptionally(new HttpTrackException("failed to load audio!", response.statusCode(),
							response.statusMessage()));
					return;
				}
				final var json = response.bodyAsJsonObject();
				final var type = LoadType.from(json.getString("loadType"));
				final var playlistInfo = json.getJsonObject("playlistInfo");
				final var isPlaylist = !playlistInfo.isEmpty();
				final var rawTracks = json.getJsonArray("tracks");
				final var tracks = rawTracks
						.stream()
						.map(obj -> AudioTrackUtil.fromString(((JsonObject) obj).getString("track")))
						.collect(Collectors.toList());
				var playlistName = (String) null;
				var selectedTrack = (Integer) null;

				if (isPlaylist) {
					playlistName = playlistInfo.getString("name");
					selectedTrack = playlistInfo.getInteger("selectedTrack");
				}
				future.complete(new AudioLoadResultImpl(tracks, type, playlistName, selectedTrack, isPlaylist));
			} else {
				LOGGER.error("error executing http request!", result.cause());
				future.completeExceptionally(result.cause());
			}
		});
		return future;
	}

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
		final var node = client.bestNode();
		if (node.equals(connectedNode)) {
			if (startTime < 0 || startTime >= endTime) {
				LOGGER.warn("startTime out of bounds: {}, guild id: {}", startTime, guildIdAsString());
				throw new IllegalArgumentException("startTime out of bounds!");
			}
			final var play = EntityBuilder.createPlayPayload(guildIdAsString(), trackData,startTime, endTime, noReplace);
			send(play);
			return;
		}
		connect(node, () -> {
			initialize(lastSessionId, lastVoiceToken, lastEndpoint);
			if (startTime < 0 || startTime >= endTime) {
				LOGGER.warn("startTime out of bounds: {}, guild id: {}", startTime, guildIdAsString());
				throw new IllegalArgumentException("startTime out of bounds!");
			}
			final var play = EntityBuilder.createPlayPayload(guildIdAsString(), trackData,startTime, endTime, noReplace);
			send(play);
		});
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
	public void connect(@Nonnull final AudioNode node, @Nullable final Runnable runnable) {
		if (node.equals(connectedNode)) {
			return;
		}
		if (state != PlayerState.DESTROYED && connectedNode != null) {
			destroy();
		}
		if (!node.available()) {
			node.openConnection(() -> {
				state = PlayerState.CONNECTED;
				connectedNode = (AudioNodeImpl) node;
				if (runnable != null) {
					runnable.run();
				}
			});
			return;
		}
		connectedNode = (AudioNodeImpl) node;
		state = PlayerState.CONNECTED;
		if (runnable != null) {
			runnable.run();
		}
	}

	@Override
	public void initialize(@Nonnull final String sessionId, @Nonnull final String voiceToken,
	                       @Nonnull final String endpoint) {
		if (state == PlayerState.INITIALIZED) {
			LOGGER.warn("player already initialized! guild id: {}", guildIdAsString());
			throw new IllegalStateException("player already initialized!");
		}
		lastSessionId = sessionId;
		lastVoiceToken = voiceToken;
		lastEndpoint = endpoint;
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
