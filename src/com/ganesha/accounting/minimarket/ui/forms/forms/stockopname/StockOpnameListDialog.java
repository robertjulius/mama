package com.ganesha.accounting.minimarket.ui.forms.forms.stockopname;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.formatter.Formatter;
import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Item;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;

public class StockOpnameListDialog extends XJDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTable table;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJButton btnSelesai;
	private XJButton btnKeluar;
	private JPanel pnlInformation;
	private XJLabel lblPerformedBy;
	private XJTextField txtPerformedBy;
	private XJLabel lblPerformedDate;
	private XJTextField txtStartTimestamp;
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 10, false,
				"No", XTableConstants.CELL_RENDERER_CENTER));

		tableParameters.put(ColumnEnum.CODE, new XTableParameter(1, 50, false,
				"Kode", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.NAME, new XTableParameter(2, 300, false,
				"Nama Barang", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(3, 50, false,
				"Satuan", XTableConstants.CELL_RENDERER_CENTER));

		tableParameters.put(ColumnEnum.QUANTITY_SISTEM, new XTableParameter(4,
				50, false, "Qty Sistem", XTableConstants.CELL_RENDERER_RIGHT));

		tableParameters.put(ColumnEnum.QUANTITY_MANUAL, new XTableParameter(5,
				50, true, "Qty Manual", XTableConstants.CELL_RENDERER_RIGHT));

		tableParameters.put(ColumnEnum.DEVIATION, new XTableParameter(6, 75,
				false, "Selisih", XTableConstants.CELL_RENDERER_RIGHT));
	}

	public StockOpnameListDialog(Window parent) {
		super(parent);

		setTitle("Stock Opname");
		getContentPane().setLayout(
				new MigLayout("", "[1000,grow]", "[grow][300,grow][]"));

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);
		table.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("tableCellEditor")) {
					int row = table.getEditingRow();
					int column = table.getEditingColumn();
					if (column == tableParameters.get(
							ColumnEnum.QUANTITY_MANUAL).getColumnIndex()) {
						setTotalPerRow(row);
					}
				}
			}
		});

		pnlInformation = new JPanel();
		getContentPane().add(pnlInformation, "cell 0 0");
		pnlInformation.setLayout(new MigLayout("", "[][300]", "[][]"));

		lblPerformedBy = new XJLabel();
		lblPerformedBy.setText("Dilakukan Oleh");
		pnlInformation.add(lblPerformedBy, "cell 0 0");

		txtPerformedBy = new XJTextField();
		txtPerformedBy.setText(Main.getUserLogin().getName());
		txtPerformedBy.setEditable(false);
		pnlInformation.add(txtPerformedBy, "cell 1 0,growx");

		lblPerformedDate = new XJLabel();
		lblPerformedDate.setText("Tanggal");
		pnlInformation.add(lblPerformedDate, "cell 0 1");

		txtStartTimestamp = new XJTextField();
		txtStartTimestamp.setText(Formatter.formatTimestampToString(CommonUtils
				.getCurrentDate()));
		txtStartTimestamp.setEditable(false);
		pnlInformation.add(txtStartTimestamp, "cell 1 1,growx");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Batal<br/>[ESC]</center></html>");
		pnlButton.add(btnKeluar, "cell 0 0");

		btnSelesai = new XJButton();
		btnSelesai.setText("<html></center>Selesai<br/>[F12]</center></html>");
		pnlButton.add(btnSelesai, "cell 1 0");

		try {
			loadData();
		} catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnSelesai.doClick();
			break;
		default:
			break;
		}
	}

	private void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			StockFacade facade = StockFacade.getInstance();
			List<ItemStock> itemStocks = facade.search(null, null, false,
					session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(itemStocks.size());

			for (int i = 0; i < itemStocks.size(); ++i) {
				ItemStock itemStock = itemStocks.get(i);
				Item item = itemStock.getItem();

				tableModel.setValueAt(i + 1, i,
						tableParameters.get(ColumnEnum.NUM).getColumnIndex());

				tableModel.setValueAt(item.getCode(), i,
						tableParameters.get(ColumnEnum.CODE).getColumnIndex());

				tableModel.setValueAt(item.getName(), i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(itemStock.getUnit(), i, tableParameters
						.get(ColumnEnum.UNIT).getColumnIndex());

				tableModel.setValueAt(Formatter.formatNumberToString(itemStock
						.getStock()), i,
						tableParameters.get(ColumnEnum.QUANTITY_SISTEM)
								.getColumnIndex());

				tableModel.setValueAt(0, i,
						tableParameters.get(ColumnEnum.QUANTITY_MANUAL)
								.getColumnIndex());

				tableModel.setValueAt(0, i,
						tableParameters.get(ColumnEnum.DEVIATION)
								.getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	private void setTotalPerRow(int row) {
		if (row < 0) {
			return;
		}

		int quantitySistem = Formatter.formatStringToNumber(
				table.getValueAt(
						row,
						tableParameters.get(ColumnEnum.QUANTITY_SISTEM)
								.getColumnIndex()).toString()).intValue();
		table.setValueAt(Formatter.formatNumberToString(quantitySistem), row,
				tableParameters.get(ColumnEnum.QUANTITY_SISTEM)
						.getColumnIndex());

		int quantityManual = Formatter.formatStringToNumber(
				table.getValueAt(
						row,
						tableParameters.get(ColumnEnum.QUANTITY_MANUAL)
								.getColumnIndex()).toString()).intValue();
		table.setValueAt(Formatter.formatNumberToString(quantityManual), row,
				tableParameters.get(ColumnEnum.QUANTITY_MANUAL)
						.getColumnIndex());

		double deviation = quantityManual - quantitySistem;
		table.setValueAt(Formatter.formatNumberToString(deviation), row,
				tableParameters.get(ColumnEnum.DEVIATION).getColumnIndex());
	}

	private enum ColumnEnum {
		NUM, CODE, NAME, UNIT, QUANTITY_SISTEM, QUANTITY_MANUAL, DEVIATION
	}
}
