package com.ganesha.desktop.exeptions;

import java.awt.Window;

import com.ganesha.core.exception.UserException;

public class ExceptionHandler {

	public static void handleException(Window parent, Throwable ex) {
		handleException(parent, ex, null);
	}

	public static void handleException(Window parent, Throwable ex, String title) {
		if (ex instanceof UserException) {
			UserExceptionHandler.handleException(parent, ex, title);
		} else {
			AppExceptionHandler.handleException(parent, ex, title);
		}
	}
}
