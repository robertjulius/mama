package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.JTable;

public class XJTable extends JTable {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJTable() {
		Font font = new Font("Tahoma", Font.PLAIN,
				XComponentConstants.FONT_SIZE_NORMAL);

		setFont(font);
		setRowHeight((int) Math.ceil(font.getSize() * 1.20));
		getTableHeader().setFont(font);
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
	}
}
