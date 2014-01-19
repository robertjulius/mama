package com.ganesha.core.desktop;

import com.ganesha.core.exception.UserException;

public class ExceptionHandler {

	public static void handleException(Exception ex) {

		if (ex instanceof UserException) {
			UserExceptionHandler.handleException(ex);
		} else {
			AppExceptionHandler.handleException(ex);
		}
	}
}
