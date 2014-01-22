package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.JCheckBox;

public class XJCheckBox extends JCheckBox implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJCheckBox() {
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));
	}
}
