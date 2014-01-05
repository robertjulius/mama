package com.ganesha.accounting.formatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Formatter {

	public static String formatPriceBigDecimalToString(BigDecimal bigDecimal) {
		String string = formatPriceDoubleToString(bigDecimal.doubleValue());
		return string;
	}

	public static String formatPriceDoubleToString(double d) {
		NumberFormat numberFormat = new DecimalFormat("#,##0.##");
		String string = numberFormat.format(d);
		return string;
	}
}
