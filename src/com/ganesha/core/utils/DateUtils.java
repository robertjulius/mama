package com.ganesha.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

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

	public static <T extends Date> T getCurrent(Class<T> clazz) {
		try {
			if (!Date.class.isAssignableFrom(clazz)) {
				throw new RuntimeException(clazz.getName()
						+ "is not subclass of " + Date.class.getName());
			}
			Constructor<T> constructor = clazz.getConstructor(Long.TYPE);
			T current = constructor.newInstance(System.currentTimeMillis());
			return current;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends Date> T getNextDate(int jump, int calendarUnit,
			T date) {
		try {
			if (date == null) {
				return null;
			}
			calendar.setTime(date);
			calendar.add(calendarUnit, jump);

			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) date.getClass();

			Constructor<T> constructor = clazz.getConstructor(Long.TYPE);
			T nextDate = constructor.newInstance(calendar.getTimeInMillis());

			return nextDate;

		} catch (IllegalArgumentException | SecurityException
				| NoSuchMethodException | InstantiationException
				| IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getTimestampInString() {
		DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestampInString = formatter.format(getCurrent(Date.class));
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

	public static <T extends Date> T validateDateBegin(T date) {
		try {
			if (date == null) {
				return null;
			}
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) date.getClass();

			Constructor<T> constructor = clazz.getConstructor(Long.TYPE);
			T dateBegin = constructor.newInstance(calendar.getTimeInMillis());

			return dateBegin;

		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends Date> T validateDateEnd(T date) {
		try {
			if (date == null) {
				return null;
			}
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);

			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) date.getClass();

			Constructor<T> constructor = clazz.getConstructor(Long.TYPE);
			T dateEnd = constructor.newInstance(calendar.getTimeInMillis());

			return dateEnd;

		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
