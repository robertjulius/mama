package com.ganesha.desktop.component;

import java.awt.Font;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

public class XJDateChooser extends JDateChooser {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJDateChooser() {
		setFont(new Font("Tahoma", Font.PLAIN,
				XComponentConstants.FONT_SIZE_NORMAL));

		((JTextFieldDateEditor) getDateEditor()).setEditable(false);
	}
}
