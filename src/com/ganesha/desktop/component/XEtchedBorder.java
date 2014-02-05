package com.ganesha.desktop.component;

import javax.swing.border.EtchedBorder;

public class XEtchedBorder extends EtchedBorder implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XEtchedBorder() {
		super(LOWERED, BORDER_HIGHLIGHT, BORDER_SHADOW);
	}
}
