package com.ganesha.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowableUtils {

	public static Throwable getRootCause(Throwable throwable) {
		Throwable cause = throwable.getCause();
		if (cause != null) {
			return getRootCause(cause);
		} else {
			return throwable;
		}
	}

	public static String getStackTraceString(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);
		printWriter.flush();
		return stringWriter.toString();
	}
}
