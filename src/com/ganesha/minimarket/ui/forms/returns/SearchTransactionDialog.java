package com.ganesha.minimarket.ui.forms.returns;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDateChooser;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.TransactionFacade;

public class SearchTransactionDialog extends XJDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtNoTransaksi;
	private XJTable table;
	private XJButton btnPilih;
	private XJDateChooser dtChBegin;
	private XJDateChooser dtChEnd;

	private String selectedTransactionNumber;
	private Integer selectedOrderNumber;
	private String selectedItemCode;
	private String selectedItemName;
	private TransactionFacade facade;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.TRANSACTION_NUM, new XTableParameter(0,
				200, false, "No. Transaksi",
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.DATE, new XTableParameter(1, 75, false,
				"Tanggal", XTableConstants.CELL_RENDERER_LEFT, Date.class));

		tableParameters.put(ColumnEnum.ORDER_NO, new XTableParameter(2, 5,
				false, "No", XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.ITEM_CODE, new XTableParameter(3, 100,
				false, "Kode Barang", XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.ITEM_NAME, new XTableParameter(4, 300,
				false, "Nama Barang", XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(5, 10,
				false, "Qty", XTableConstants.CELL_RENDERER_RIGHT,
				Integer.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(6, 25, false,
				"Satuan", XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(7, 50, false,
				"Harga", XTableConstants.CELL_RENDERER_RIGHT, Double.class));

		tableParameters.put(ColumnEnum.TOTAL, new XTableParameter(8, 50, false,
				"Total", XTableConstants.CELL_RENDERER_RIGHT, Double.class));
	}

	public SearchTransactionDialog(String title, Window parent,
			TransactionFacade facade) {
		super(parent);
		setTitle(title);
		setPermissionRequired(false);

		this.facade = facade;

		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnPilih.doClick();
			}
		};
		XTableUtils.initTable(table, tableParameters);

		JPanel pnlFilter = new JPanel();
		getContentPane().add(pnlFilter, "cell 0 0,alignx left,growy");
		pnlFilter.setLayout(new MigLayout("", "[grow][50][]", "[]"));

		JPanel pnlKriteria = new JPanel();
		pnlKriteria
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlFilter.add(pnlKriteria, "cell 0 0,grow");
		pnlKriteria.setLayout(new MigLayout("", "[][300,grow]", "[][]"));

		XJLabel lblNoTransaksi = new XJLabel();
		pnlKriteria.add(lblNoTransaksi, "flowx,cell 0 0");
		lblNoTransaksi.setText("No Transaksi");

		txtNoTransaksi = new XJTextField();
		pnlKriteria.add(txtNoTransaksi, "cell 1 0,growx");
		txtNoTransaksi.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					/*
					 * TODO Perbaiki supaya kalo pas key = alt+tab, ga usah load
					 * data
					 */
					loadData();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							SearchTransactionDialog.this, ex);
				}
			}
		});
		txtNoTransaksi.setColumns(10);

		JPanel pnlRangeTanggal = new JPanel();
		pnlKriteria.add(pnlRangeTanggal, "cell 0 1 2 1,growx");
		pnlRangeTanggal.setLayout(new MigLayout("", "[][grow][][grow]",
				"[][grow]"));

		XJLabel lblNama = new XJLabel();
		pnlRangeTanggal.add(lblNama, "cell 0 0 4 1");
		lblNama.setText("Tanggal Transaksi");

		XJLabel lblDari = new XJLabel();
		lblDari.setText("Dari");
		pnlRangeTanggal.add(lblDari, "cell 0 1");

		dtChBegin = new XJDateChooser();
		dtChBegin.getCalendarButton().setMnemonic('D');
		pnlRangeTanggal.add(dtChBegin, "cell 1 1,grow");

		XJLabel lblSampai = new XJLabel();
		lblSampai.setText("Sampai");
		pnlRangeTanggal.add(lblSampai, "cell 2 1");

		dtChEnd = new XJDateChooser();
		pnlRangeTanggal.add(dtChEnd, "cell 3 1,grow");

		XJButton btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadData();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							SearchTransactionDialog.this, ex);
				}
			}
		});
		pnlFilter.add(btnRefresh, "cell 2 0,aligny bottom");
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[]", "[]"));

		btnPilih = new XJButton();
		btnPilih.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					pilih();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							SearchTransactionDialog.this, ex);
				}
			}
		});
		btnPilih.setText("<html><center>Pilih<br/>[Enter]</center></html>");
		panel.add(btnPilih, "cell 0 0");

		pack();
		setLocationRelativeTo(null);

		try {
			loadData();
		} catch (Exception ex) {
			ExceptionHandler.handleException(this, ex);
		}
	}

	public String getSelectedItemCode() {
		return selectedItemCode;
	}

	public String getSelectedItemName() {
		return selectedItemName;
	}

	public Integer getSelectedOrderNumber() {
		return selectedOrderNumber;
	}

	public String getSelectedTransactionNumber() {
		return selectedTransactionNumber;
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private void loadData() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			String transactionNumber = txtNoTransaksi.getText();
			Date beginDate = CommonUtils.validateDateBegin(dtChBegin.getDate());
			Date endDate = CommonUtils.validateDateEnd(dtChEnd.getDate());

			List<Map<String, Object>> searchResults = facade.searchTransaction(
					transactionNumber, beginDate, endDate, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(searchResults.size());

			for (int i = 0; i < searchResults.size(); ++i) {
				Map<String, Object> searchResult = searchResults.get(i);

				String transactionNumberValue = (String) searchResult
						.get("transactionNumber");
				Integer orderNoValue = (Integer) searchResult.get("orderNum");
				Timestamp transactionTimestampValue = (Timestamp) searchResult
						.get("transactionTimestamp");
				String itemCodeValue = (String) searchResult.get("itemCode");
				String itemNameValue = (String) searchResult.get("itemName");
				Integer quantityValue = (Integer) searchResult.get("quantity");
				String unitValue = (String) searchResult.get("unit");
				BigDecimal pricePerUnitValue = (BigDecimal) searchResult
						.get("pricePerUnit");
				BigDecimal totalAmountValue = (BigDecimal) searchResult
						.get("totalAmount");

				tableModel.setValueAt(transactionNumberValue, i,
						tableParameters.get(ColumnEnum.TRANSACTION_NUM)
								.getColumnIndex());

				tableModel
						.setValueAt(Formatter
								.formatDateToString(transactionTimestampValue),
								i, tableParameters.get(ColumnEnum.DATE)
										.getColumnIndex());

				tableModel.setValueAt(Formatter
						.formatNumberToString(orderNoValue), i, tableParameters
						.get(ColumnEnum.ORDER_NO).getColumnIndex());

				tableModel.setValueAt(itemCodeValue, i,
						tableParameters.get(ColumnEnum.ITEM_CODE)
								.getColumnIndex());

				tableModel.setValueAt(itemNameValue, i,
						tableParameters.get(ColumnEnum.ITEM_NAME)
								.getColumnIndex());

				tableModel.setValueAt(Formatter
						.formatNumberToString(quantityValue), i,
						tableParameters.get(ColumnEnum.QUANTITY)
								.getColumnIndex());

				tableModel.setValueAt(unitValue, i,
						tableParameters.get(ColumnEnum.UNIT).getColumnIndex());

				tableModel.setValueAt(
						Formatter.formatNumberToString(pricePerUnitValue), i,
						tableParameters.get(ColumnEnum.PRICE).getColumnIndex());

				tableModel.setValueAt(
						Formatter.formatNumberToString(totalAmountValue), i,
						tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	private void pilih() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			return;
		}
		selectedTransactionNumber = (String) table.getModel().getValueAt(
				selectedRow,
				tableParameters.get(ColumnEnum.TRANSACTION_NUM)
						.getColumnIndex());

		selectedOrderNumber = Integer.parseInt(table
				.getModel()
				.getValueAt(
						selectedRow,
						tableParameters.get(ColumnEnum.ORDER_NO)
								.getColumnIndex()).toString());

		selectedItemCode = (String) table.getModel().getValueAt(selectedRow,
				tableParameters.get(ColumnEnum.ITEM_CODE).getColumnIndex());

		selectedItemName = (String) table.getModel().getValueAt(selectedRow,
				tableParameters.get(ColumnEnum.ITEM_NAME).getColumnIndex());

		dispose();
	}

	private enum ColumnEnum {
		TRANSACTION_NUM, DATE, ORDER_NO, ITEM_CODE, ITEM_NAME, QUANTITY, UNIT, PRICE, TOTAL
	}
}
