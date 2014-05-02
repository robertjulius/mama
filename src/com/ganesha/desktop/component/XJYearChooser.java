package com.ganesha.desktop.component;

import java.awt.Font;

import com.toedter.calendar.JYearChooser;

public class XJYearChooser extends JYearChooser implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJYearChooser() {
		setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_NORMAL));
	}
}
