package com.github.samophis.lavaclient.exceptions;

import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;


@Accessors(fluent = true)
public class HttpTrackException extends RuntimeException {
	@Getter(onMethod_ = {@CheckReturnValue, @Nonnegative})
	private final int statusCode;

	@Getter(onMethod_ = {@CheckReturnValue, @Nonnull})
	private final String statusMessage;

	public HttpTrackException(@Nonnull final String message, @Nonnegative final int statusCode,
	                          @Nonnull final String statusMessage) {
		super(message);
		if (statusCode < 0) {
			throw new IllegalArgumentException("negative status code! " + statusCode);
		}
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}
}
