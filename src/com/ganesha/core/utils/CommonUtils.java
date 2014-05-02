package com.ganesha.core.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {

	private static Calendar calendar = Calendar.getInstance();

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

	public static Date getNextDate(int jump, int calendarUnit, Date date) {
		if (date == null) {
			return null;
		}
		calendar.setTime(date);
		calendar.add(calendarUnit, jump);
		return calendar.getTime();
	}

	public static String getTimestampInString() {
		DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestampInString = formatter.format(getCurrentDate());
		return timestampInString;
	}

	public static void setCalendarMonthAndYearOnly(Calendar calendar) {
		setCalendarMonthAndYearOnly(calendar, calendar.get(Calendar.MONTH),
				calendar.get(Calendar.YEAR));
	}

	public static void setCalendarMonthAndYearOnly(Calendar calendar,
			int month, int year) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	public static Date validateDateBegin(Date date) {
		if (date == null) {
			return null;
		}
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date validateDateEnd(Date date) {
		if (date == null) {
			return null;
		}
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}
}
