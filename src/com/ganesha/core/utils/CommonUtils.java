package com.ganesha.core.utils;

import java.sql.Timestamp;

public class CommonUtils {

	public static Timestamp getCurrentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}
}
