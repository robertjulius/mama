package com.ganesha.desktop.component;

import java.awt.Font;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

public class XJDateChooser extends JDateChooser implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJDateChooser() {
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));
		JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) getDateEditor();
		dateEditor.setEditable(false);
		dateEditor.setBackground(TXT_BG_NOTEDITABLE);
	}
}
