package com.ganesha.core.exception;

public class UserException extends GaneshaException {

	private static final long serialVersionUID = -9098238364164875570L;

	private String resourceId;

	public UserException(String resourceId) {
		this.resourceId = resourceId;
	}

	public UserException(Throwable cause) {
		super(cause);
	}

	public String getMessageId() {
		return resourceId;
	}
}
