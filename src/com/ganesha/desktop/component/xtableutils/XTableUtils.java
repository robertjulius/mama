package com.ganesha.desktop.component.xtableutils;

import java.util.Iterator;
import java.util.Map;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.ganesha.desktop.component.XJTable;

public class XTableUtils {

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
		tableModel.setColumnEditable(composeColumnEditable(tableParameters));
		table.setModel(tableModel);

		TableColumnModel columnModel = table.getColumnModel();
		Iterator<?> iterator = tableParameters.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			XTableParameter tableParameter = tableParameters.get(key);
			int index = tableParameter.getColumnIndex();
			int width = tableParameter.getColumnWidth();

			TableCellRenderer tableCellRenderer = tableParameter
					.getTableCellRenderer();

			columnModel.getColumn(index).setPreferredWidth(width);
			columnModel.getColumn(index).setCellRenderer(tableCellRenderer);
		}
	}
}
