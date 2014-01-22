package com.ganesha.accounting.minimarket.ui.forms.forms.returns;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.formatter.Formatter;
import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.facade.SaleFacade;
import com.ganesha.accounting.minimarket.facade.SaleReturnFacade;
import com.ganesha.accounting.minimarket.model.Customer;
import com.ganesha.accounting.minimarket.model.SaleDetail;
import com.ganesha.accounting.minimarket.model.SaleHeader;
import com.ganesha.accounting.minimarket.model.SaleReturnDetail;
import com.ganesha.accounting.minimarket.model.SaleReturnHeader;
import com.ganesha.accounting.minimarket.ui.forms.forms.searchentity.SearchEntityDialog;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.GeneralConstants;
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

public class ReturPenjualanForm extends XJDialog {

	private XJTable table;

	private static final long serialVersionUID = 1401014426195840845L;
	private XJTextField txtNoTransaksi;
	private XJDateChooser dateChooser;
	private XJTextField txtKodeCustomer;
	private XJButton btnTambah;
	private XJButton btnCariCustomer;
	private XJButton btnHapus;
	private XJTextField txtTotalRetur;
	private XJButton btnBatal;
	private XJButton btnSelesai;
	private XJTextField txtNamaCustomer;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 5, false,
				"No", XTableConstants.CELL_RENDERER_CENTER));

		tableParameters.put(ColumnEnum.TRANSACTION_NUM,
				new XTableParameter(1, 100, false, "No. Transaksi",
						XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.DATE, new XTableParameter(2, 75, false,
				"Tanggal", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.ITEM_CODE, new XTableParameter(3, 100,
				false, "Kode Barang", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.ITEM_NAME, new XTableParameter(4, 300,
				false, "Nama Barang", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(5, 10,
				true, "Qty", XTableConstants.CELL_RENDERER_CENTER));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(6, 30, false,
				"Satuan", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(7, 50, false,
				"Harga", XTableConstants.CELL_RENDERER_RIGHT));

		tableParameters.put(ColumnEnum.DISCOUNT, new XTableParameter(8, 100,
				false, "Discount (%)", XTableConstants.CELL_RENDERER_CENTER));

		tableParameters.put(ColumnEnum.TOTAL, new XTableParameter(9, 50, false,
				"Total", XTableConstants.CELL_RENDERER_RIGHT));

	}

	public ReturPenjualanForm(Window parent) {
		super(parent);
		setTitle("Retur Penjualan");
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]", "[grow][grow][grow][grow]"));

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);
		table.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("tableCellEditor")) {
					int row = table.getEditingRow();
					int column = table.getEditingColumn();
					if (column == tableParameters.get(ColumnEnum.QUANTITY)
							.getColumnIndex()) {
						setTotalPerRow(row);
					}
				}
			}
		});

		JPanel pnlHeader = new JPanel();
		pnlHeader.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlHeader, "cell 0 0,alignx left,growy");
		pnlHeader.setLayout(new MigLayout("", "[150][100][200][]", "[][][]"));

		XJLabel lblNoTransaksi = new XJLabel();
		lblNoTransaksi.setText("No. Retur Penjualan");
		pnlHeader.add(lblNoTransaksi, "cell 0 0");

		txtNoTransaksi = new XJTextField();
		txtNoTransaksi
				.setText(GeneralConstants.PREFIX_TRX_NUMBER_PURCHASE_RETURN
						+ CommonUtils.getTimestampInString());
		txtNoTransaksi.setEditable(false);
		pnlHeader.add(txtNoTransaksi, "cell 1 0 2 1,growx");

		XJLabel lblTanggal = new XJLabel();
		lblTanggal.setText("Tanggal Retur");
		pnlHeader.add(lblTanggal, "cell 0 1");

		dateChooser = new XJDateChooser();
		dateChooser.setDate(CommonUtils.getCurrentDate());
		dateChooser.getCalendarButton().setMnemonic('T');
		pnlHeader.add(dateChooser, "cell 1 1 2 1,grow");

		XJLabel lblCustomer = new XJLabel();
		lblCustomer.setText("Customer");
		pnlHeader.add(lblCustomer, "cell 0 2");

		txtKodeCustomer = new XJTextField();
		txtKodeCustomer.setEditable(false);
		pnlHeader.add(txtKodeCustomer, "cell 1 2,growx");

		txtNamaCustomer = new XJTextField();
		txtNamaCustomer.setEditable(false);
		pnlHeader.add(txtNamaCustomer, "flowx,cell 2 2,growx");

		btnCariCustomer = new XJButton();
		btnCariCustomer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cariCustomer();
			}
		});
		btnCariCustomer.setText("Cari Customer [F5]");
		pnlHeader.add(btnCariCustomer, "cell 3 2");

		JPanel pnlPenjualan = new JPanel();
		getContentPane().add(pnlPenjualan, "cell 0 1,grow");
		pnlPenjualan.setLayout(new MigLayout("", "[612px,grow]",
				"[grow][::200,baseline]"));

		JPanel pnlSearchItem = new JPanel();
		pnlPenjualan.add(pnlSearchItem, "cell 0 0,grow");
		pnlSearchItem.setLayout(new MigLayout("", "[][grow][]", "[]"));

		btnTambah = new XJButton();
		btnTambah.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cariTransaksi();
			}
		});
		pnlSearchItem.add(btnTambah, "cell 0 0,alignx left");
		btnTambah
				.setText("<html><center>Tambah ke Dalam List<br/>[F6]</center></html>");

		btnHapus = new XJButton();
		btnHapus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hapus();
			}
		});
		btnHapus.setText("<html><center>Hapus<br/>[Delete]</center></html>");
		pnlSearchItem.add(btnHapus, "cell 2 0");

		JScrollPane scrollPane = new JScrollPane(table);
		pnlPenjualan.add(scrollPane, "cell 0 1,growx");

		JPanel pnlSubTotal = new JPanel();
		getContentPane().add(pnlSubTotal, "cell 0 2,grow");
		pnlSubTotal.setLayout(new MigLayout("", "[grow][][200]", "[]"));

		XJLabel lblTotalRetur = new XJLabel();
		pnlSubTotal.add(lblTotalRetur, "cell 1 0");
		lblTotalRetur.setText("Total Retur");

		txtTotalRetur = new XJTextField();
		txtTotalRetur.setText("0");
		txtTotalRetur.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlSubTotal.add(txtTotalRetur, "cell 2 0,growx");
		txtTotalRetur.setEditable(false);

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 3,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnBatal = new XJButton();
		btnBatal.setMnemonic('Q');
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBatal.setText("<html><center>Batal<br/>[Alt+Q]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0,growy");

		btnSelesai = new XJButton();
		btnSelesai.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					selesaiDanSimpan();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		btnSelesai
				.setText("<html><center>Selesai & Simpan Data<br/>[F12]</center></html>");
		pnlButton.add(btnSelesai, "cell 1 0,growy");

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnCariCustomer.doClick();
			break;
		case KeyEvent.VK_F6:
			btnTambah.doClick();
			break;
		case KeyEvent.VK_F12:
			btnSelesai.doClick();
			break;
		case KeyEvent.VK_DELETE:
			if (!table.isEditing()) {
				btnHapus.doClick();
			}
			break;
		default:
			break;
		}
	}

	private void batal() {
		String message = "Apakah Anda yakin ingin membatalkan retur penjualan ini?";
		int selectedOption = JOptionPane.showConfirmDialog(this, message,
				"Membatalkan Retur Penjualan", JOptionPane.YES_NO_OPTION);
		if (selectedOption == JOptionPane.YES_OPTION) {
			dispose();
		}
	}

	private void cariCustomer() {
		SearchEntityDialog searchEntityDialog = new SearchEntityDialog(
				"Cari Customer", this, Customer.class);
		searchEntityDialog.setVisible(true);

		String kodeCustomer = searchEntityDialog.getSelectedCode();
		if (kodeCustomer != null) {
			String namaCustomer = searchEntityDialog.getSelectedName();
			txtKodeCustomer.setText(kodeCustomer);
			txtNamaCustomer.setText(namaCustomer);
		}
	}

	private void cariTransaksi() {
		SearchTransactionDialog searchEntityDialog = new SearchTransactionDialog(
				"Cari Daftar Transaksi Penjualan", this,
				SaleFacade.getInstance());
		searchEntityDialog.setVisible(true);
		String noTransaksi = searchEntityDialog.getSelectedTransactionNumber();
		Integer orderNumber = searchEntityDialog.getSelectedOrderNumber();
		if (noTransaksi != null) {
			tambah(noTransaksi, orderNumber);
		}
	}

	private void hapus() {
		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();
		if (row < 0) {
			return;
		}

		XTableModel tableModel = (XTableModel) table.getModel();
		tableModel.removeRow(row);

		int rowCount = table.getRowCount();
		if (rowCount <= row) {
			--row;
		}
		table.changeSelection(row, column, false, false);
		setTotalRetur();
		reorderRowNumber();
	}

	private void reorderRowNumber() {
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			table.setValueAt(i + 1, i, tableParameters.get(ColumnEnum.NUM)
					.getColumnIndex());
		}
	}

	private void selesaiDanSimpan() throws Exception {

		if (!(table.getRowCount() > 0)) {
			throw new UserException(
					"Proses tidak dapat dilanjutkan. Anda belum memasukan item apapun untuk transaksi ini.");
		}

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			SaleReturnFacade facade = SaleReturnFacade.getInstance();

			String transactionNumber = txtNoTransaksi.getText();

			Timestamp transactionTimestamp = CommonUtils
					.castDateToTimestamp(dateChooser.getDate());

			String customerCode = txtKodeCustomer.getText();

			double subTotalAmount = Formatter.formatStringToNumber(
					txtTotalRetur.getText()).doubleValue();

			double taxPercent = 0;
			double taxAmount = 0;

			double totalReturnAmount = Formatter.formatStringToNumber(
					txtTotalRetur.getText()).doubleValue();

			SaleReturnHeader saleReturnHeader = facade.validateForm(
					transactionNumber, transactionTimestamp, customerCode,
					subTotalAmount, taxPercent, taxAmount, totalReturnAmount,
					session);

			List<SaleReturnDetail> saleReturnDetails = new ArrayList<>();
			int rowCount = table.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				SaleReturnDetail saleReturnDetail = new SaleReturnDetail();

				saleReturnDetail.setOrderNum(Formatter.formatStringToNumber(
						table.getValueAt(
								i,
								tableParameters.get(ColumnEnum.NUM)
										.getColumnIndex()).toString())
						.intValue());

				saleReturnDetail.setSaleTransactionNumber(table.getValueAt(
						i,
						tableParameters.get(ColumnEnum.TRANSACTION_NUM)
								.getColumnIndex()).toString());

				saleReturnDetail
						.setSaleTransactionTimestamp(CommonUtils
								.castDateToTimestamp(Formatter
										.formatStringToDate(table.getValueAt(
												i,
												tableParameters.get(
														ColumnEnum.DATE)
														.getColumnIndex())
												.toString())));

				saleReturnDetail.setItemCode(table.getValueAt(
						i,
						tableParameters.get(ColumnEnum.ITEM_CODE)
								.getColumnIndex()).toString());

				saleReturnDetail.setItemName(table.getValueAt(
						i,
						tableParameters.get(ColumnEnum.ITEM_NAME)
								.getColumnIndex()).toString());

				saleReturnDetail.setQuantity(Formatter.formatStringToNumber(
						table.getValueAt(
								i,
								tableParameters.get(ColumnEnum.QUANTITY)
										.getColumnIndex()).toString())
						.intValue());

				saleReturnDetail.setUnit(table.getValueAt(i,
						tableParameters.get(ColumnEnum.UNIT).getColumnIndex())
						.toString());

				saleReturnDetail.setPricePerUnit(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getValueAt(
										i,
										tableParameters.get(ColumnEnum.PRICE)
												.getColumnIndex()).toString())
						.doubleValue()));

				saleReturnDetail.setDiscountPercent(BigDecimal
						.valueOf(Formatter.formatStringToNumber(
								table.getValueAt(
										i,
										tableParameters
												.get(ColumnEnum.DISCOUNT)
												.getColumnIndex()).toString())
								.doubleValue()));

				saleReturnDetail.setTotalAmount(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getValueAt(
										i,
										tableParameters.get(ColumnEnum.TOTAL)
												.getColumnIndex()).toString())
						.doubleValue()));

				saleReturnDetail.setDisabled(false);
				saleReturnDetail.setDeleted(false);
				saleReturnDetail.setLastUpdatedBy(Main.getUserLogin().getId());
				saleReturnDetail.setLastUpdatedTimestamp(CommonUtils
						.getCurrentTimestamp());

				saleReturnDetails.add(saleReturnDetail);
			}

			facade.performSaleReturn(saleReturnHeader, saleReturnDetails,
					session);

			session.getTransaction().commit();
			dispose();

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void setTotalPerRow(int row) {
		if (row < 0) {
			return;
		}

		int jumlah = Formatter.formatStringToNumber(
				table.getValueAt(
						row,
						tableParameters.get(ColumnEnum.QUANTITY)
								.getColumnIndex()).toString()).intValue();
		table.setValueAt(Formatter.formatNumberToString(jumlah), row,
				tableParameters.get(ColumnEnum.QUANTITY).getColumnIndex());

		double hargaSatuan = Formatter.formatStringToNumber(
				table.getValueAt(row,
						tableParameters.get(ColumnEnum.PRICE).getColumnIndex())
						.toString()).doubleValue();
		table.setValueAt(Formatter.formatNumberToString(hargaSatuan), row,
				tableParameters.get(ColumnEnum.PRICE).getColumnIndex());

		double totalBeforeDiscount = jumlah * hargaSatuan;

		double discountPercent = Formatter.formatStringToNumber(
				table.getValueAt(
						row,
						tableParameters.get(ColumnEnum.DISCOUNT)
								.getColumnIndex()).toString()).doubleValue();
		double diskonAmount = discountPercent / 100 * totalBeforeDiscount;

		double total = Math.floor(totalBeforeDiscount - diskonAmount);
		table.setValueAt(Formatter.formatNumberToString(total), row,
				tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());

		setTotalRetur();
	}

	private void setTotalRetur() {
		int rowCount = table.getRowCount();
		double totalRetur = 0;
		for (int i = 0; i < rowCount; ++i) {
			String string = table.getValueAt(i,
					tableParameters.get(ColumnEnum.TOTAL).getColumnIndex())
					.toString();
			double totalPerRow = Formatter.formatStringToNumber(string)
					.doubleValue();
			totalRetur += totalPerRow;
		}
		txtTotalRetur.setText(Formatter.formatNumberToString(totalRetur));
	}

	private void tambah(String transactionNumber, Integer orderNum) {
		if (transactionNumber.trim().equals("") || orderNum == null) {
			return;
		}

		Session session = HibernateUtils.openSession();
		try {
			SaleFacade facade = SaleFacade.getInstance();
			SaleDetail saleDetail = facade.getDetail(transactionNumber,
					orderNum, session);
			SaleHeader saleHeader = saleDetail.getSaleHeader();

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(tableModel.getRowCount() + 1);
			int rowIndex = tableModel.getRowCount() - 1;

			tableModel.setValueAt(saleHeader.getTransactionNumber(), rowIndex,
					tableParameters.get(ColumnEnum.TRANSACTION_NUM)
							.getColumnIndex());

			tableModel.setValueAt(Formatter.formatDateToString(saleHeader
					.getTransactionTimestamp()), rowIndex,
					tableParameters.get(ColumnEnum.DATE).getColumnIndex());

			tableModel.setValueAt(saleDetail.getItemCode(), rowIndex,
					tableParameters.get(ColumnEnum.ITEM_CODE).getColumnIndex());

			tableModel.setValueAt(saleDetail.getItemName(), rowIndex,
					tableParameters.get(ColumnEnum.ITEM_NAME).getColumnIndex());

			tableModel.setValueAt(saleDetail.getQuantity(), rowIndex,
					tableParameters.get(ColumnEnum.QUANTITY).getColumnIndex());

			tableModel.setValueAt(saleDetail.getUnit(), rowIndex,
					tableParameters.get(ColumnEnum.UNIT).getColumnIndex());

			tableModel.setValueAt(Formatter.formatNumberToString(saleDetail
					.getDiscountPercent()), rowIndex,
					tableParameters.get(ColumnEnum.DISCOUNT).getColumnIndex());

			tableModel.setValueAt(Formatter.formatNumberToString(saleDetail
					.getPricePerUnit()), rowIndex,
					tableParameters.get(ColumnEnum.PRICE).getColumnIndex());

			tableModel
					.setValueAt(Formatter.formatNumberToString(saleDetail
							.getTotalAmount()), rowIndex,
							tableParameters.get(ColumnEnum.TOTAL)
									.getColumnIndex());

			reorderRowNumber();

			int row = table.getRowCount() - 1;
			table.requestFocus();
			table.changeSelection(row, tableParameters.get(ColumnEnum.QUANTITY)
					.getColumnIndex(), false, false);

			setTotalRetur();

		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		NUM, TRANSACTION_NUM, DATE, ITEM_CODE, ITEM_NAME, QUANTITY, UNIT, PRICE, DISCOUNT, TOTAL
	}
}
