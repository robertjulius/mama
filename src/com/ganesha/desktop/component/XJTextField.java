package com.ganesha.desktop.component;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JFormattedTextField;

public class XJTextField extends JFormattedTextField implements
		XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJTextField() {
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));

		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String text = getText();
				String upperCaseText = text.toUpperCase();
				setText(upperCaseText);
			}
		});
	}
}
