package com.ganesha.desktop.component.xtableutils;

import javax.swing.table.DefaultTableModel;

public class XTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 6724468683822462856L;

	boolean[] columnEditables;

	public XTableModel() {
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return columnEditables[column];
	}

	public void setColumnEditable(boolean[] columnEditables) {
		this.columnEditables = columnEditables;
	}
}
