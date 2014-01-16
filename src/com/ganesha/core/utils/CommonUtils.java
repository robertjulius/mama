package com.ganesha.core.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

	public static Timestamp castDateToTimestamp(Date date) {
		Timestamp timestamp = null;
		if (date instanceof Timestamp) {
			timestamp = (Timestamp) date;
		} else {
			timestamp = new Timestamp(date.getTime());
		}
		return timestamp;
	}

	public static Date getCurrentDate() {
		Date date = new Date(System.currentTimeMillis());
		return date;
	}

	public static Timestamp getCurrentTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp;
	}

	public static String getTimestampInString() {
		DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestampInString = formatter.format(getCurrentDate());
		return timestampInString;
	}
}
