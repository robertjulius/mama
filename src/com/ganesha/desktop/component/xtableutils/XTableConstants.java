package com.ganesha.desktop.component.xtableutils;

import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class XTableConstants {

	public static final DefaultTableCellRenderer CELL_RENDERER_LEFT = new DefaultTableCellRenderer();
	public static final DefaultTableCellRenderer CELL_RENDERER_CENTER = new DefaultTableCellRenderer();
	public static final DefaultTableCellRenderer CELL_RENDERER_RIGHT = new DefaultTableCellRenderer();

	public static final Color COLOR_BACKGROUND_NORMAL = Color.LIGHT_GRAY;
	public static final Color COLOR_BACKGROUND_EDITABLE = Color.WHITE;
	public static final Color COLOR_BACKGROUND_SELECTED_ROW = new Color(0, 0,
			150);
	public static final Color COLOR_FOREGROUND_NORMAL = Color.BLACK;
	public static final Color COLOR_FOREGROUND_EDITABLE = Color.BLACK;
	public static final Color COLOR_FOREGROUND_SELECTED_ROW = Color.WHITE;
	public static final Color COLOR_GRID = Color.GRAY;
	public static final Color COLOR_BORDER = Color.RED;

	static {
		CELL_RENDERER_LEFT.setHorizontalAlignment(SwingConstants.LEFT);
		CELL_RENDERER_CENTER.setHorizontalAlignment(SwingConstants.CENTER);
		CELL_RENDERER_RIGHT.setHorizontalAlignment(SwingConstants.RIGHT);
	}
}
