package com.ganesha.desktop.component.xtableutils;

import java.util.Iterator;
import java.util.Map;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ganesha.desktop.component.XJTable;

public class XTableUtils {

	public static Class<?>[] composeColumnClasses(
			Map<?, XTableParameter> tableParameters) {
		Class<?>[] columnClasses = new Class<?>[tableParameters.size()];
		Iterator<?> iterator = tableParameters.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			XTableParameter tableParameter = tableParameters.get(key);
			columnClasses[tableParameter.getColumnIndex()] = tableParameter
					.getColumnClass();
		}
		return columnClasses;
	}

	public static boolean[] composeColumnEditable(
			Map<?, XTableParameter> tableParameters) {
		boolean[] columnEditables = new boolean[tableParameters.size()];
		Iterator<?> iterator = tableParameters.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			XTableParameter tableParameter = tableParameters.get(key);
			columnEditables[tableParameter.getColumnIndex()] = tableParameter
					.isEditable();
		}
		return columnEditables;
	}

	public static String[] composeColumnIdentifiers(
			Map<?, XTableParameter> tableParameters) {
		String[] columnIdentifiers = new String[tableParameters.size()];
		Iterator<?> iterator = tableParameters.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			XTableParameter tableParameter = tableParameters.get(key);
			columnIdentifiers[tableParameter.getColumnIndex()] = tableParameter
					.getColumnIdentifier();
		}
		return columnIdentifiers;
	}

	public static void initTable(XJTable table,
			Map<?, XTableParameter> tableParameters) {

		XTableModel tableModel = new XTableModel();
		tableModel
				.setColumnIdentifiers(composeColumnIdentifiers(tableParameters));
		tableModel.setXColumnEditable(composeColumnEditable(tableParameters));
		tableModel.setColumnClasses(composeColumnClasses(tableParameters));
		table.setModel(tableModel);

		TableColumnModel columnModel = table.getColumnModel();
		Iterator<?> iterator = tableParameters.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			XTableParameter tableParameter = tableParameters.get(key);
			int index = columnModel.getColumnIndex(tableParameter
					.getColumnIdentifier());
			int width = tableParameter.getColumnWidth();

			TableCellRenderer tableCellRenderer = tableParameter
					.getTableCellRenderer();

			TableColumn tableColumn = columnModel.getColumn(index);
			tableColumn.setPreferredWidth(width);
			tableColumn.setCellRenderer(tableCellRenderer);
			if (tableParameter.isHidden()) {
				table.removeColumn(tableColumn);
			}
		}
	}

	public static void selectLastRow(XJTable table) {
		int lastRowIndex = table.getRowCount() - 1;

		table.changeSelection(lastRowIndex, 0, false, false);
	}
}
