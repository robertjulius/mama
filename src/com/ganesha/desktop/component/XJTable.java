package com.ganesha.desktop.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.xtableutils.XCellEditor;
import com.ganesha.desktop.component.xtableutils.XCellValueEditor;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;

public class XJTable extends JTable implements XComponentConstants {

	private static final long serialVersionUID = 8731044804764016513L;

	private boolean requestFocusOnF1;

	public XJTable() {
		this(true);
	}

	public XJTable(boolean requestFocusOnF1) {
		this.requestFocusOnF1 = requestFocusOnF1;
		Font font = new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL);

		setFont(font);
		setRowHeight((int) Math.ceil(font.getSize() * 1.30));
		getTableHeader().setFont(font);
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

		setDefaultEditor(Object.class, new XCellEditor());

		setGridColor(XTableConstants.COLOR_GRID);

		setBackground(XTableConstants.COLOR_BACKGROUND_NORMAL);
		// setSelectionBackground(COLOR_BACKGROUND_SELECTED_ROW);

		setForeground(XTableConstants.COLOR_FOREGROUND_NORMAL);
		// setSelectionForeground(COLOR_FOREGROUND_SELECTED_ROW);

		setKeyBinding();
		setIntercellSpacing(new Dimension(5, 0));
	}

	public void cellValueChanged(int row, int column, Object oldValue,
			Object newValue) {
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,
			int column) {
		Component component = super.prepareRenderer(renderer, row, column);
		boolean editable = ((XTableModel) getModel()).isXCellEditable(row,
				column);
		boolean rowSelected = isRowSelected(row);
		if (rowSelected) {

			boolean cellSelected = isCellSelected(row, column);
			Object value = getValueAt(row, column);

			boolean rowIsLead = (selectionModel.getLeadSelectionIndex() == row);
			boolean colIsLead = (columnModel.getSelectionModel()
					.getLeadSelectionIndex() == column);

			boolean hasFocus = (rowIsLead && colIsLead) && isFocusOwner();

			JComponent cellRenderer = (JComponent) renderer
					.getTableCellRendererComponent(this, value, cellSelected,
							hasFocus, row, column);

			if (cellSelected && hasFocus && editable) {
				cellRenderer.setBorder(BorderFactory.createLineBorder(
						XTableConstants.COLOR_BORDER, 2, false));
			} else {
				cellRenderer.setBorder(null);
			}

			if (editable) {
				component
						.setBackground(XTableConstants.COLOR_BACKGROUND_EDITABLE);
				component
						.setForeground(XTableConstants.COLOR_FOREGROUND_EDITABLE);
			} else {
				component
						.setBackground(XTableConstants.COLOR_BACKGROUND_SELECTED_ROW);
				component
						.setForeground(XTableConstants.COLOR_FOREGROUND_SELECTED_ROW);
			}
		} else {
			component.setBackground(XTableConstants.COLOR_BACKGROUND_NORMAL);
			component.setForeground(XTableConstants.COLOR_FOREGROUND_NORMAL);
		}

		return component;
	}

	public void rowSelected() {
	}

	private void setKeyBinding() {
		InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actionMap = getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
				"openCellValueEditor");
		actionMap.put("openCellValueEditor", new OpenCellValueEditorAction());

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectRow");
		actionMap.put("selectRow", new SelectRowAction());

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
				"selectNextEditableColumnCell");
		actionMap.put("selectNextEditableColumnCell",
				new SelectNextEditableColumnCellAction());

		if (requestFocusOnF1) {
			InputMap inputMapWindow = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			inputMapWindow.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
					"focusOnTable");
			actionMap.put("focusOnTable", new FocusOnTableAction());
		}
	}

	public class FocusOnTableAction extends AbstractAction {
		private static final long serialVersionUID = 7568686306388657677L;

		@Override
		public void actionPerformed(ActionEvent e) {
			requestFocus();
			changeSelection(0, 0, false, false);

			XTableModel tableModel = (XTableModel) getModel();
			int columnCount = tableModel.getColumnCount();
			for (int i = 0; i < columnCount; ++i) {
				boolean editable = tableModel.isXCellEditable(0, i);
				if (editable) {
					changeSelection(0, i, false, false);
					break;
				}
			}
		}
	}

	public class OpenCellValueEditorAction extends AbstractAction {
		private static final long serialVersionUID = 913188132447616512L;

		@Override
		public void actionPerformed(ActionEvent e) {
			XTableModel tableModel = (XTableModel) getModel();
			int row = getSelectedRow();
			int column = getSelectedColumn();

			String initialValue = tableModel.getValueAt(row, column).toString();
			String title = tableModel.getColumnName(column);
			Container parent = getParent();
			while (!(parent instanceof Window)) {
				parent = parent.getParent();
			}

			XCellValueEditor valueEditor = new XCellValueEditor(
					(Window) parent, title, initialValue);
			valueEditor.setVisible(true);

			int returnValue = valueEditor.getReturnValue();
			if (returnValue == XCellValueEditor.OK) {
				String newValue = valueEditor.getValue();
				Class<?> clazz = tableModel.getColumnClass(column);
				if (Number.class.isAssignableFrom(clazz)) {
					newValue = Formatter.formatNumberToString(Formatter
							.formatStringToNumber(newValue));
				}
				tableModel.setValueAt(newValue, row, column);
				cellValueChanged(row, column, initialValue, newValue);
			}
		}
	}

	public class SelectNextEditableColumnCellAction extends AbstractAction {
		private static final long serialVersionUID = 7568686306388657677L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int startedRow = getSelectedRow();
			int startedColumn = getSelectedColumn();

			if (startedRow < 0 || startedColumn < 0) {
				/*
				 * Do nothing
				 * 
				 * Berarti tidak ada data di table
				 */
				return;
			}

			int columnCount = getColumnCount();
			int rowCount = getRowCount();

			boolean editable = false;
			int col = (startedColumn + 1) % columnCount;
			int row = startedRow % rowCount;
			do {
				editable = ((XTableModel) getModel()).isXCellEditable(
						startedRow, col);
				if (editable) {
					changeSelection(row, col, false, false);
				}
				row = (row + (col + 1) / columnCount) % rowCount;
				col = (col + 1) % columnCount;

				if (row == startedRow && col == startedColumn) {
					break;
				}
				if (editable) {
					break;
				}
			} while (true);

			TableCellEditor cellEditor = getCellEditor();
			if (cellEditor != null) {
				cellEditor.stopCellEditing();
			}
		}
	}

	public class SelectRowAction extends AbstractAction {
		private static final long serialVersionUID = 7568686306388657677L;

		@Override
		public void actionPerformed(ActionEvent e) {
			XTableModel tableModel = (XTableModel) getModel();
			int row = getSelectedRow();
			int column = getSelectedColumn();
			boolean editable = tableModel.isXCellEditable(row, column);
			if (!editable) {
				rowSelected();
			}
		}
	}
}
