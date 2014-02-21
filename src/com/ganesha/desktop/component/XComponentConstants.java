package com.ganesha.desktop.component;

import java.awt.Color;

public interface XComponentConstants {

	public static final Color PNL_BG = new Color(200, 200, 255);

	public static final int FONT_SIZE_NORMAL = 18;
	public static final int FONT_SIZE_SMALL = (int) (FONT_SIZE_NORMAL * 0.9);
	public static final int FONT_SIZE_SMALLER = (int) (FONT_SIZE_NORMAL * 0.8);
	public static final int FONT_SIZE_SMALLEST = (int) (FONT_SIZE_NORMAL * 0.7);
	public static final int FONT_SIZE_BIG = (int) (FONT_SIZE_NORMAL * 1.1);
	public static final int FONT_SIZE_BIGGER = (int) (FONT_SIZE_NORMAL * 1.2);
	public static final int FONT_SIZE_BIGGEST = (int) (FONT_SIZE_NORMAL * 1.3);

	public static final Color COLOR_BACKGROUND_NORMAL = Color.LIGHT_GRAY;

	public static final Color COLOR_GOOD = Color.BLUE;
	public static final Color LBL_WARNING = Color.RED;
	public static final Color LBL_NORMAL = Color.WHITE;

	public static final Color TXT_BG_NOTEDITABLE = Color.GREEN;
	public static final Color TXT_BG_EDITABLE = Color.WHITE;
	public static final Color TXT_BG_ATTENTION = Color.ORANGE;

	public static final Color BTN_BG = new Color(150, 150, 255);
	// public static final Color BTN_BG = PNL_BG.darker();

	public static final Color BORDER_HIGHLIGHT = null;
	public static final Color BORDER_SHADOW = Color.BLACK;

}
