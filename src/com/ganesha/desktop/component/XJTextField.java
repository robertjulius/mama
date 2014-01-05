package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.JTextField;

public class XJTextField extends JTextField {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJTextField() {
		setFont(new Font("Tahoma", Font.PLAIN,
				XComponentConstants.FONT_SIZE_NORMAL));
	}

}
