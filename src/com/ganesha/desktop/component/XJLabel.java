package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.JLabel;

public class XJLabel extends JLabel implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJLabel() {
		this(null);
	}

	public XJLabel(String text) {
		super(text);
		setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_NORMAL));
	}

}
