package com.ganesha.desktop.component.xtableutils;

import javax.swing.table.TableCellRenderer;

public class XTableParameter {

	private int columnIndex;
	private int columnWidth;
	private boolean editable;
	private String columnIdentifier;
	private TableCellRenderer tableCellRenderer;

	public XTableParameter(int columnIndex, int columnWidth, boolean editable,
			String columnIdentifier, TableCellRenderer tableCellRenderer) {

		this.columnIndex = columnIndex;
		this.columnWidth = columnWidth;
		this.editable = editable;
		this.columnIdentifier = columnIdentifier;
		this.tableCellRenderer = tableCellRenderer;
	}

	public String getColumnIdentifier() {
		return columnIdentifier;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public int getColumnWidth() {
		return columnWidth;
	}

	public TableCellRenderer getTableCellRenderer() {
		return tableCellRenderer;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setColumnIdentifier(String columnIdentifier) {
		this.columnIdentifier = columnIdentifier;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setTableCellRenderer(TableCellRenderer tableCellRenderer) {
		this.tableCellRenderer = tableCellRenderer;
	}
}
