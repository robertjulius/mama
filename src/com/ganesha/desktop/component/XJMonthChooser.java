package com.ganesha.desktop.component;

import java.awt.Font;

import com.toedter.calendar.JMonthChooser;

public class XJMonthChooser extends JMonthChooser implements
		XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJMonthChooser() {
		setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_NORMAL));
	}
}
