package com.ganesha.desktop.component;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

public class XComponentConstants {

	public static final int FONT_SIZE_NORMAL = 18;
	public static final int FONT_SIZE_SMALL = FONT_SIZE_NORMAL - 2;
	public static final int FONT_SIZE_SMALLER = FONT_SIZE_SMALL - 2;
	public static final int FONT_SIZE_BIG = FONT_SIZE_NORMAL + 2;
	public static final int FONT_SIZE_BIGGER = FONT_SIZE_BIG + 2;

	public static final AbstractFormatterFactory FORMATTER_FACTORY_NUMBER;

	static {
		NumberFormat format = new DecimalFormat("#,##0.##");
		NumberFormatter formatter = new NumberFormatter(format);
		FORMATTER_FACTORY_NUMBER = new DefaultFormatterFactory(formatter);
	}
}
