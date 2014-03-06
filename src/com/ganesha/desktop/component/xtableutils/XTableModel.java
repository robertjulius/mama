package com.ganesha.desktop.component.xtableutils;

import javax.swing.table.DefaultTableModel;

public class XTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 6724468683822462856L;

	private boolean[] columnEditables;
	private Class<?>[] columnClasses;

	public XTableModel() {
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return columnClasses[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public boolean isXCellEditable(int row, int column) {
		return columnEditables[column];
	}

	public void setColumnClasses(Class<?>[] columnClasses) {
		this.columnClasses = columnClasses;
	}

	public void setXColumnEditable(boolean[] columnEditables) {
		this.columnEditables = columnEditables;
	}
}
