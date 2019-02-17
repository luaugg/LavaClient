package com.github.samophis.lavaclient.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public enum LoadType {
	TRACK_LOADED,
	PLAYLIST_LOADED,
	SEARCH_RESULT,
	NO_MATCHES,
	LOAD_FAILED,
	UNKNOWN;

	@CheckReturnValue
	@Nonnull
	public static LoadType from(@Nonnull final String type) {
		for (final var tp : values()) {
			if (tp.name().equals(type)) {
				return tp;
			}
		}
		return UNKNOWN;
	}
}
