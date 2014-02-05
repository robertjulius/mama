package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.JButton;

public class XJButton extends JButton implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJButton() {
		this(null);
	}

	public XJButton(String text) {
		super(text);
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));
		setBackground(BTN_BG);
	}
}
