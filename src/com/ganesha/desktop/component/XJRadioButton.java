package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.JRadioButton;

public class XJRadioButton extends JRadioButton {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJRadioButton() {
		setFont(new Font("Tahoma", Font.PLAIN,
				XComponentConstants.FONT_SIZE_NORMAL));
	}

}
