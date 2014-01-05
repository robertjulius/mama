package com.ganesha.core.exception;

public abstract class GaneshaException extends Exception {
	private static final long serialVersionUID = -1256416338317933171L;

	public GaneshaException() {
		super();
	}

	public GaneshaException(String message) {
		super(message);
	}

	public GaneshaException(Throwable cause) {
		super(cause);
	}
}
