package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.JMenu;

public class XJMenu extends JMenu implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJMenu(String text) {
		super(text);
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));
	}

	public void add(XJMenuItem comp) {
		if (comp.isVisible()) {
			super.add(comp);
		}
	}
}