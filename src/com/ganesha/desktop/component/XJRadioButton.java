package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.JRadioButton;

public class XJRadioButton extends JRadioButton implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJRadioButton() {
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));
		setBackground(PNL_BG);
	}
}
