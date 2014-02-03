package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

public class XJList extends JList<ComboBoxObject> implements
		XComponentConstants {

	private static final long serialVersionUID = 8731044804764016513L;

	public XJList() {
		Font font = new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL);
		setFont(font);

		ListModel<ComboBoxObject> listModel = new DefaultListModel<>();
		setModel(listModel);
	}

}
