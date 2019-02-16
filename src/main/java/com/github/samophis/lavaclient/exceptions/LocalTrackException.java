package com.github.samophis.lavaclient.exceptions;

import javax.annotation.Nonnull;

public class LocalTrackException extends RuntimeException {
	public LocalTrackException(@Nonnull final Exception exc) {
		super(exc);
	}
}
