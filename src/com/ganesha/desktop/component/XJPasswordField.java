package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.JPasswordField;

public class XJPasswordField extends JPasswordField implements
		XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJPasswordField() {
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));
	}
}
