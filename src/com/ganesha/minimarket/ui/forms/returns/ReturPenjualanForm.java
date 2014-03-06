package com.ganesha.minimarket.ui.forms.returns;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDateChooser;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.SaleFacade;
import com.ganesha.minimarket.facade.SaleReturnFacade;
import com.ganesha.minimarket.model.Customer;
import com.ganesha.minimarket.model.SaleDetail;
import com.ganesha.minimarket.model.SaleHeader;
import com.ganesha.minimarket.model.SaleReturnDetail;
import com.ganesha.minimarket.model.SaleReturnHeader;
import com.ganesha.minimarket.ui.forms.searchentity.SearchEntityDialog;
import com.ganesha.minimarket.utils.PermissionConstants;

public class ReturPenjualanForm extends XJDialog {

	private XJTable table;

	private static final long serialVersionUID = 1401014426195840845L;
	private XJTextField txtNoTransaksi;
	private XJDateChooser dateChooser;
	private XJTextField txtKodeCustomer;
	private XJButton btnTambah;
	private XJButton btnCariCustomer;
	private XJButton btnHapus;
	private XJLabel lblTotalReturValue;
	private XJButton btnBatal;
	private XJButton btnSelesai;
	private XJTextField txtNamaCustomer;

	private Integer customerId;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 5, false,
				"No", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.TRANSACTION_NUM, new XTableParameter(1,
				100, false, "No. Transaksi", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.DATE, new XTableParameter(2, 75, false,
				"Tanggal", false, XTableConstants.CELL_RENDERER_LEFT,
				Date.class));

		tableParameters.put(ColumnEnum.ITEM_CODE, new XTableParameter(3, 100,
				false, "Kode Barang", false,
				XTableConstants.CELL_RENDERER_CENTER, String.class));

		tableParameters.put(ColumnEnum.ITEM_NAME, new XTableParameter(4, 300,
				false, "Nama Barang", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(5, 10,
				true, "Qty", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(6, 30, false,
				"Satuan", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(7, 50, false,
				"Harga", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.DISCOUNT, new XTableParameter(8, 30,
				false, "(%)", false, XTableConstants.CELL_RENDERER_CENTER,
				Double.class));

		tableParameters.put(ColumnEnum.TOTAL, new XTableParameter(9, 50, false,
				"Total", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.SALE_DETAIL_ID, new XTableParameter(10,
				0, false, "Sale Detail ID", true,
				XTableConstants.CELL_RENDERER_LEFT, Integer.class));

	}

	public ReturPenjualanForm(Window parent) {
		super(parent);
		setTitle("Retur Penjualan");
		setPermissionCode(PermissionConstants.SALRTN_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]", "[grow][grow][grow][grow]"));

		table = new XJTable() {
			private static final long serialVersionUID = 6822394065886587363L;

			@Override
			public void cellValueChanged(int row, int column, Object oldValue,
					Object newValue) {
				if (column == tableParameters.get(ColumnEnum.QUANTITY)
						.getColumnIndex()) {
					setTotalPerRow(row);
				}
			}
		};
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlHeader = new XJPanel();
		pnlHeader.setBorder(new XEtchedBorder());
		getContentPane().add(pnlHeader, "cell 0 0,alignx left,growy");
		pnlHeader.setLayout(new MigLayout("", "[150][100][200][]", "[][][]"));

		XJLabel lblNoTransaksi = new XJLabel();
		lblNoTransaksi.setText("No. Retur Penjualan");
		pnlHeader.add(lblNoTransaksi, "cell 0 0");

		txtNoTransaksi = new XJTextField();
		txtNoTransaksi.setText(GeneralConstants.PREFIX_TRX_NUMBER_SALES_RETURN
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

		XJPanel pnlPenjualan = new XJPanel();
		getContentPane().add(pnlPenjualan, "cell 0 1,grow");
		pnlPenjualan.setLayout(new MigLayout("", "[612px,grow]",
				"[grow][::200,baseline]"));

		XJPanel pnlSearchItem = new XJPanel();
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

		XJPanel pnlSubTotal = new XJPanel();
		pnlSubTotal.setBackground(Color.BLACK);
		getContentPane().add(pnlSubTotal, "cell 0 2,grow");
		pnlSubTotal.setLayout(new MigLayout("", "[][grow][300]", "[]"));

		XJLabel lblTotalRetur = new XJLabel();
		lblTotalRetur.setForeground(Color.WHITE);
		lblTotalRetur.setFont(new Font("Tahoma", Font.BOLD, 40));
		pnlSubTotal.add(lblTotalRetur, "cell 0 0");
		lblTotalRetur.setText("Total Retur");

		lblTotalReturValue = new XJLabel();
		lblTotalReturValue.setForeground(Color.WHITE);
		lblTotalReturValue.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblTotalReturValue.setText("0");
		lblTotalReturValue.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlSubTotal.add(lblTotalReturValue, "cell 2 0,alignx right");

		XJPanel pnlButton = new XJPanel();
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
					ExceptionHandler.handleException(ReturPenjualanForm.this,
							ex);
				}
			}
		});
		btnSelesai
				.setText("<html><center>Selesai & Simpan Data<br/>[F12]</center></html>");
		pnlButton.add(btnSelesai, "cell 1 0,growy");

		pack();
		setLocationRelativeTo(parent);
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
			boolean isFocus = table.isFocusOwner();
			if (!isFocus) {
				return;
			}
			if (table.isEditing()) {
				return;
			}
			btnHapus.doClick();
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

		customerId = searchEntityDialog.getSelectedId();
		if (customerId != null) {
			String kode = searchEntityDialog.getSelectedCode();
			String nama = searchEntityDialog.getSelectedName();
			txtKodeCustomer.setText(kode);
			txtNamaCustomer.setText(nama);
		}
	}

	private void cariTransaksi() {
		SearchTransactionDialog searchTransactionDialog = new SearchTransactionDialog(
				"Cari Daftar Transaksi Penjualan", this,
				SaleFacade.getInstance());
		searchTransactionDialog.setVisible(true);
		Integer saleDetailId = searchTransactionDialog
				.getSelectedTransactionDetailId();
		if (saleDetailId != null) {
			tambah(saleDetailId);
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

			double subTotalAmount = Formatter.formatStringToNumber(
					lblTotalReturValue.getText()).doubleValue();

			double taxPercent = 0;
			double taxAmount = 0;

			double totalReturnAmount = Formatter.formatStringToNumber(
					lblTotalReturValue.getText()).doubleValue();

			SaleReturnHeader saleReturnHeader = facade.validateForm(
					transactionNumber, transactionTimestamp, customerId,
					subTotalAmount, taxPercent, taxAmount, totalReturnAmount,
					session);

			List<SaleReturnDetail> saleReturnDetails = new ArrayList<>();
			int rowCount = table.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				SaleReturnDetail saleReturnDetail = new SaleReturnDetail();

				Integer saleDetailId = Formatter.formatStringToNumber(
						table.getModel()
								.getValueAt(
										i,
										tableParameters.get(
												ColumnEnum.SALE_DETAIL_ID)
												.getColumnIndex()).toString())
						.intValue();

				SaleDetail saleDetail = SaleFacade.getInstance().getDetail(
						saleDetailId, session);

				saleReturnDetail.setOrderNum(Formatter.formatStringToNumber(
						table.getModel()
								.getValueAt(
										i,
										tableParameters.get(ColumnEnum.NUM)
												.getColumnIndex()).toString())
						.intValue());

				saleReturnDetail.setSaleDetail(saleDetail);

				saleReturnDetail.setQuantity(Formatter.formatStringToNumber(
						table.getModel()
								.getValueAt(
										i,
										tableParameters
												.get(ColumnEnum.QUANTITY)
												.getColumnIndex()).toString())
						.intValue());

				saleReturnDetail.setTotalAmount(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getModel()
										.getValueAt(
												i,
												tableParameters.get(
														ColumnEnum.TOTAL)
														.getColumnIndex())
										.toString()).doubleValue()));

				saleReturnDetails.add(saleReturnDetail);
			}

			facade.performSaleReturn(saleReturnHeader, saleReturnDetails,
					session);

			ActivityLogFacade.doLog(getPermissionCode(),
					ActionType.TRANSACTION, Main.getUserLogin(),
					saleReturnHeader, session);
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
				table.getModel()
						.getValueAt(
								row,
								tableParameters.get(ColumnEnum.QUANTITY)
										.getColumnIndex()).toString())
				.intValue();
		table.setValueAt(Formatter.formatNumberToString(jumlah), row,
				tableParameters.get(ColumnEnum.QUANTITY).getColumnIndex());

		double hargaSatuan = Formatter.formatStringToNumber(
				table.getModel()
						.getValueAt(
								row,
								tableParameters.get(ColumnEnum.PRICE)
										.getColumnIndex()).toString())
				.doubleValue();
		table.setValueAt(Formatter.formatNumberToString(hargaSatuan), row,
				tableParameters.get(ColumnEnum.PRICE).getColumnIndex());

		double totalBeforeDiscount = jumlah * hargaSatuan;

		double discountPercent = Formatter.formatStringToNumber(
				table.getModel()
						.getValueAt(
								row,
								tableParameters.get(ColumnEnum.DISCOUNT)
										.getColumnIndex()).toString())
				.doubleValue();
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
			String string = table
					.getModel()
					.getValueAt(
							i,
							tableParameters.get(ColumnEnum.TOTAL)
									.getColumnIndex()).toString();
			double totalPerRow = Formatter.formatStringToNumber(string)
					.doubleValue();
			totalRetur += totalPerRow;
		}
		lblTotalReturValue.setText(Formatter.formatNumberToString(totalRetur));
	}

	private void tambah(Integer saleDetailId) {
		if (saleDetailId == null) {
			return;
		}

		Session session = HibernateUtils.openSession();
		try {
			SaleFacade facade = SaleFacade.getInstance();
			SaleDetail saleDetail = facade.getDetail(saleDetailId, session);
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

			tableModel.setValueAt(saleDetail.getId(), rowIndex, tableParameters
					.get(ColumnEnum.SALE_DETAIL_ID).getColumnIndex());

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
		NUM, TRANSACTION_NUM, DATE, ITEM_CODE, ITEM_NAME, QUANTITY, UNIT, PRICE, DISCOUNT, TOTAL, SALE_DETAIL_ID
	}
}
