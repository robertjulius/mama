package com.ganesha.accounting.minimarket.ui.commons;

import javax.swing.table.DefaultTableModel;

public class MyTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 6724468683822462856L;

	boolean[] columnEditables;

	public MyTableModel() {
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return columnEditables[column];
	}

	public void setColumnEditable(boolean[] columnEditables) {
		this.columnEditables = columnEditables;
	}
}
