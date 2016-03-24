package com.ganesha.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatter {

	private static final NumberFormat numberFormat = new DecimalFormat(
			"#,##0.##");
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"dd MMM yyyy");
	private static final DateFormat timestampFormat = new SimpleDateFormat(
			"dd MMM yyyy HH:mm:ss");
	private static final DateFormat clockFormat = new SimpleDateFormat(
			"HH:mm:ss");
	private static final NumberFormat codeFormat = new DecimalFormat(
			"0000000000");

	public static String formatClockToString(Date date) {
		String string = null;
		try {
			string = clockFormat.format(date);
		} catch (Exception e) {
			string = null;
		}
		return string;
	}

	public static Number formatCodeToInt(String code) {
		Integer number = null;
		try {
			number = retryParse(codeFormat, code, Integer.class);
		} catch (ParseException e) {
			number = 0;
		}
		return number;
	}

	public static String formatDateToString(Date date) {
		String string = null;
		try {
			string = dateFormat.format(date);
		} catch (Exception e) {
			string = null;
		}
		return string;
	}

	public static String formatIntToCode(Integer number) {
		String string = null;
		try {
			string = codeFormat.format(number);
		} catch (Exception e) {
			string = null;
		}
		return string;
	}

	public static String formatNumberToString(Number number) {
		String string = null;
		try {
			string = numberFormat.format(number);
		} catch (Exception e) {
			string = null;
		}
		return string;
	}

	public static Date formatStringToDate(String string) {
		Date date = null;
		try {
			date = retryParse(dateFormat, string, Date.class);
		} catch (ParseException e) {
			date = null;
		}
		return date;
	}

	public static Number formatStringToNumber(String string) {
		Number number = null;
		try {
			number = retryParse(numberFormat, string, Number.class);
		} catch (ParseException e) {
			number = 0;
		}
		return number;
	}

	public static Date formatStringToTimestamp(String string) {
		Date date = null;
		try {
			date = retryParse(timestampFormat, string, Date.class);
		} catch (ParseException e) {
			date = null;
		}
		return date;
	}

	public static String formatTimestampToString(Date date) {
		String string = null;
		try {
			string = timestampFormat.format(date);
		} catch (Exception e) {
			string = null;
		}
		return string;
	}

	/*
	 * This method is needed because there is unknown bugs that happen
	 * intermitance if we call formatter.format() in multi thread mode
	 */
	public static <E> E retryParse(Format formatter, String string, Class<E> clazz) throws ParseException {
		int maxTry = 1000;
		for (int i = 0; i < maxTry; ++i) {
			try {
				Method method = formatter.getClass().getMethod("parse", String.class);
				@SuppressWarnings("unchecked")
				E value = (E) method.invoke(formatter, string);
				return value;
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof ParseException) {
					throw (ParseException) e.getCause();
				} else if (e.getCause() instanceof IllegalArgumentException) {
					if (i < maxTry) {
						// Do nothing, lets retry parse this string
					} else {
						throw (IllegalArgumentException) e.getCause();
					}
				} else {
					throw new RuntimeException(e.getCause());
				}
			}
		}
		throw new RuntimeException("Failed parsing string '" + string + "' to " + clazz.getName());
	}
}
