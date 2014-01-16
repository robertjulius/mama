package com.ganesha.accounting.formatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class Formatter {

	public static String formatNumberToString(Number number) {
		NumberFormat numberFormat = new DecimalFormat("#,##0.##");
		String string = numberFormat.format(number);
		return string;
	}

	public static Number formatStringToNumber(String string) {
		Number number = null;
		NumberFormat numberFormat = new DecimalFormat("#,##0.##");
		try {
			number = numberFormat.parse(string);
		} catch (ParseException e) {
			number = 0;
		}
		return number;
	}
}
