package com.ganesha.core.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
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

	public static String formatDateToString(Date date) {
		String string = null;
		try {
			string = dateFormat.format(date);
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
			date = dateFormat.parse(string);
		} catch (ParseException e) {
			date = null;
		}
		return date;
	}

	public static Number formatStringToNumber(String string) {
		Number number = null;
		try {
			number = numberFormat.parse(string);
		} catch (ParseException e) {
			number = 0;
		}
		return number;
	}

	public static Date formatStringToTimestamp(String string) {
		Date date = null;
		try {
			date = timestampFormat.parse(string);
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
}
