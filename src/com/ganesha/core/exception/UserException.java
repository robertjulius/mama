package com.ganesha.core.exception;

public class UserException extends GaneshaException {

	private static final long serialVersionUID = -9098238364164875570L;

	public UserException(String message) {
		super(message);
	}

	public UserException(Throwable cause) {
		super(cause);
	}
}
