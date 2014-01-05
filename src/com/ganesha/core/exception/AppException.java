package com.ganesha.core.exception;

public class AppException extends GaneshaException {

	private static final long serialVersionUID = -9098238364164875570L;

	public AppException(String message) {
		super(message);
	}

	public AppException(Throwable cause) {
		super(cause);
	}
}
