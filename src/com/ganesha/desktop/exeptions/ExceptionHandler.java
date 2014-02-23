package com.ganesha.desktop.exeptions;

import java.awt.Window;

import com.ganesha.core.exception.UserException;

public class ExceptionHandler {

	public static void handleException(Window parent, Exception ex) {

		if (ex instanceof UserException) {
			UserExceptionHandler.handleException(parent, ex);
		} else {
			AppExceptionHandler.handleException(parent, ex);
		}
	}
}
