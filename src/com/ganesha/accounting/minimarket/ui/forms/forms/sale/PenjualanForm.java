package com.ganesha.accounting.minimarket.ui.forms.forms.sale;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
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
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.formatter.Formatter;
import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.facade.CustomerFacade;
import com.ganesha.accounting.minimarket.facade.SaleFacade;
import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Customer;
import com.ganesha.accounting.minimarket.model.Item;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.accounting.minimarket.model.SaleDetail;
import com.ganesha.accounting.minimarket.model.SaleHeader;
import com.ganesha.accounting.minimarket.ui.forms.forms.searchentity.SearchEntityDialog;
import com.ganesha.core.desktop.ExceptionHandler;
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

public class PenjualanForm extends XJDialog {

	private XJTable table;

	private static final long serialVersionUID = 1401014426195840845L;
	private XJTextField txtNoTransaksi;
	private XJDateChooser dateChooser;
	private XJTextField txtKodeCustomer;
	private XJButton btnTambah;
	private XJButton btnCariCustomer;
	private XJButton btnHapus;
	private XJTextField txtTaxPercent;
	private XJTextField txtTotalPenjualan;
	private XJTextField txtBayar;
	private XJTextField txtKembalian;
	private XJTextField txtTotal;
	private XJButton btnBatal;
	private XJButton btnSelesai;
	private XJLabel lblKembalian;
	private XJTextField txtNamaCustomer;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJLabel lblBayar;
	private XJLabel lblTaxAmount;
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 5, false,
				"No", XTableConstants.CELL_RENDERER_CENTER));

		tableParameters.put(ColumnEnum.CODE, new XTableParameter(1, 50, false,
				"Kode", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.NAME, new XTableParameter(2, 300, false,
				"Nama Barang", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(3, 10,
				true, "Qty", XTableConstants.CELL_RENDERER_RIGHT));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(4, 50, false,
				"Satuan", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(5, 50, false,
				"Harga", XTableConstants.CELL_RENDERER_RIGHT));

		tableParameters.put(ColumnEnum.DISCOUNT, new XTableParameter(6, 30,
				false, "Diskon (%)", XTableConstants.CELL_RENDERER_RIGHT));

		tableParameters.put(ColumnEnum.TOTAL, new XTableParameter(7, 75, false,
				"Total", XTableConstants.CELL_RENDERER_RIGHT));
	}

	public PenjualanForm(Window parent) {
		super(parent);
		setTitle("Transaksi Penjualan");
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]",
						"[grow][grow][grow][grow][][grow]"));

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
		lblNoTransaksi.setText("No. Transaksi Penjualan");
		pnlHeader.add(lblNoTransaksi, "cell 0 0,alignx trailing");

		txtNoTransaksi = new XJTextField();
		txtNoTransaksi.setText(GeneralConstants.PREFIX_TRX_NUMBER_PURCHASE
				+ CommonUtils.getTimestampInString());
		txtNoTransaksi.setEditable(false);
		pnlHeader.add(txtNoTransaksi, "cell 1 0 2 1,growx");

		XJLabel lblTanggal = new XJLabel();
		lblTanggal.setText("Tanggal Penjualan");
		pnlHeader.add(lblTanggal, "cell 0 1,alignx trailing");

		dateChooser = new XJDateChooser();
		dateChooser.setDate(CommonUtils.getCurrentDate());
		dateChooser.getCalendarButton().setMnemonic('T');
		pnlHeader.add(dateChooser, "cell 1 1 2 1,grow");

		XJLabel lblCustomer = new XJLabel();
		lblCustomer.setText("Customer");
		pnlHeader.add(lblCustomer, "cell 0 2,alignx trailing");

		Customer defaultCustomer = null;
		Session session = HibernateUtils.openSession();
		try {
			defaultCustomer = CustomerFacade.getInstance().getDefaultCustomer(
					session);
		} finally {
			session.close();
		}
		txtKodeCustomer = new XJTextField();
		txtKodeCustomer.setText(defaultCustomer.getCode());
		txtKodeCustomer.setEditable(false);
		pnlHeader.add(txtKodeCustomer, "cell 1 2,growx");

		txtNamaCustomer = new XJTextField();
		txtNamaCustomer.setText(defaultCustomer.getName());
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
				cariBarang();
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

		XJLabel lblTotalPenjualan = new XJLabel();
		pnlSubTotal.add(lblTotalPenjualan, "cell 1 0");
		lblTotalPenjualan.setText("Total Penjualan");

		txtTotalPenjualan = new XJTextField();
		txtTotalPenjualan.setText("0");
		txtTotalPenjualan.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlSubTotal.add(txtTotalPenjualan, "cell 2 0,growx");
		txtTotalPenjualan.setEditable(false);

		JPanel pnlPerhitungan = new JPanel();
		getContentPane().add(pnlPerhitungan, "cell 0 3,alignx center,growy");
		pnlPerhitungan.setLayout(new MigLayout("", "[300][300]", "[grow]"));

		JPanel pnlBeban = new JPanel();
		pnlBeban.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlPerhitungan.add(pnlBeban, "cell 0 0,grow");
		pnlBeban.setLayout(new MigLayout("", "[][grow]", "[][][10][]"));

		XJLabel lblTaxPercent = new XJLabel();
		lblTaxPercent.setText("Pajak (%)");
		pnlBeban.add(lblTaxPercent, "cell 0 0");

		txtTaxPercent = new XJTextField();
		txtTaxPercent.setEditable(false);
		txtTaxPercent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalBiayaDanDiskon();
			}
		});
		txtTaxPercent.setHorizontalAlignment(SwingConstants.TRAILING);
		txtTaxPercent.setText(Formatter
				.formatNumberToString(GeneralConstants.TAX_PERCENT));
		pnlBeban.add(txtTaxPercent, "cell 1 0,growx");

		lblTaxAmount = new XJLabel();
		lblTaxAmount.setText("0");
		pnlBeban.add(lblTaxAmount, "cell 1 1,alignx right");

		JSeparator separator = new JSeparator();
		pnlBeban.add(separator, "cell 0 2 2 1,growx,aligny center");

		XJLabel lblTotal = new XJLabel();
		lblTotal.setText("TOTAL");
		pnlBeban.add(lblTotal, "cell 0 3");

		txtTotal = new XJTextField();
		txtTotal.setHorizontalAlignment(SwingConstants.TRAILING);
		txtTotal.setText("0");
		txtTotal.setEditable(false);
		pnlBeban.add(txtTotal, "cell 1 3,growx");

		JPanel pnlUangMuka = new JPanel();
		pnlUangMuka
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlPerhitungan.add(pnlUangMuka, "cell 1 0,grow");
		pnlUangMuka.setLayout(new MigLayout("", "[150][grow]", "[][][]"));

		lblBayar = new XJLabel();
		lblBayar.setText("Bayar");
		pnlUangMuka.add(lblBayar, "cell 0 0");

		txtBayar = new XJTextField();
		txtBayar.setForeground(COLOR_TRASACTIONABLE);
		txtBayar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalBayarDanKembalian();
			}
		});
		txtBayar.setHorizontalAlignment(SwingConstants.TRAILING);
		txtBayar.setText("0");
		pnlUangMuka.add(txtBayar, "cell 1 0,growx,aligny top");

		lblKembalian = new XJLabel();
		lblKembalian.setText("Kembalian");
		pnlUangMuka.add(lblKembalian, "cell 0 1");

		txtKembalian = new XJTextField();
		txtKembalian.setHorizontalAlignment(SwingConstants.TRAILING);
		txtKembalian.setText("0");
		txtKembalian.setEditable(false);
		txtKembalian.setForeground(COLOR_WARNING);
		pnlUangMuka.add(txtKembalian, "cell 1 1,growx,aligny top");

		JSeparator separator_1 = new JSeparator();
		getContentPane().add(separator_1, "cell 0 4,grow");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 5,alignx center,growy");
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
		String message = "Apakah Anda yakin ingin membatalkan transaksi penjualan ini?";
		int selectedOption = JOptionPane.showConfirmDialog(this, message,
				"Membatalkan Transaksi Penjualan", JOptionPane.YES_NO_OPTION);
		if (selectedOption == JOptionPane.YES_OPTION) {
			dispose();
		}
	}

	private void cariBarang() {
		SearchEntityDialog searchEntityDialog = new SearchEntityDialog(
				"Cari Barang", this, Item.class);
		searchEntityDialog.setVisible(true);

		String kodeBarang = searchEntityDialog.getSelectedCode();
		if (kodeBarang != null) {
			tambah(kodeBarang);
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
		setTotalPenjualan();
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
		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			SaleFacade facade = SaleFacade.getInstance();

			String transactionNumber = txtNoTransaksi.getText();

			Timestamp transactionTimestamp = CommonUtils
					.castDateToTimestamp(dateChooser.getDate());

			String customerCode = txtKodeCustomer.getText();

			double subTotalAmount = Formatter.formatStringToNumber(
					txtTotalPenjualan.getText()).doubleValue();

			double taxPercent = Formatter.formatStringToNumber(
					txtTaxPercent.getText()).doubleValue();

			double taxAmount = Formatter.formatStringToNumber(
					lblTaxAmount.getText()).doubleValue();

			double totalAmount = Formatter.formatStringToNumber(
					txtTotal.getText()).doubleValue();

			double pay = Formatter.formatStringToNumber(txtBayar.getText())
					.doubleValue();

			double moneyChange = Formatter.formatStringToNumber(
					txtKembalian.getText()).doubleValue();

			SaleHeader saleHeader = facade.validateForm(transactionNumber,
					transactionTimestamp, customerCode, subTotalAmount,
					taxPercent, taxAmount, totalAmount, pay, moneyChange,
					session);

			List<SaleDetail> saleDetails = new ArrayList<>();
			int rowCount = table.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				SaleDetail saleDetail = new SaleDetail();

				saleDetail.setOrderNum(Formatter.formatStringToNumber(
						table.getValueAt(
								i,
								tableParameters.get(ColumnEnum.NUM)
										.getColumnIndex()).toString())
						.intValue());

				saleDetail.setItemCode(table.getValueAt(i,
						tableParameters.get(ColumnEnum.CODE).getColumnIndex())
						.toString());

				saleDetail.setItemName(table.getValueAt(i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex())
						.toString());

				saleDetail.setQuantity(Formatter.formatStringToNumber(
						table.getValueAt(
								i,
								tableParameters.get(ColumnEnum.QUANTITY)
										.getColumnIndex()).toString())
						.intValue());

				saleDetail.setUnit(table.getValueAt(i,
						tableParameters.get(ColumnEnum.UNIT).getColumnIndex())
						.toString());

				saleDetail.setPricePerUnit(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getValueAt(
										i,
										tableParameters.get(ColumnEnum.PRICE)
												.getColumnIndex()).toString())
						.doubleValue()));

				saleDetail.setDiscountPercent(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getValueAt(
										i,
										tableParameters
												.get(ColumnEnum.DISCOUNT)
												.getColumnIndex()).toString())
						.doubleValue()));

				saleDetail.setTotalAmount(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getValueAt(
										i,
										tableParameters.get(ColumnEnum.TOTAL)
												.getColumnIndex()).toString())
						.doubleValue()));

				saleDetail.setDisabled(false);
				saleDetail.setDeleted(false);
				saleDetail.setLastUpdatedBy(Main.getUserLogin().getId());
				saleDetail.setLastUpdatedTimestamp(CommonUtils
						.getCurrentTimestamp());

				saleDetails.add(saleDetail);
			}

			facade.performSale(saleHeader, saleDetails, session);

			session.getTransaction().commit();
			dispose();

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void setTotalBayarDanKembalian() {
		double total = Formatter.formatStringToNumber(txtTotal.getText())
				.doubleValue();

		double bayar = Formatter.formatStringToNumber(txtBayar.getText())
				.doubleValue();
		txtBayar.setText(Formatter.formatNumberToString(bayar));

		double kembalian = bayar - total;
		kembalian = kembalian < 0 ? 0 : kembalian;
		txtKembalian.setText(Formatter.formatNumberToString(kembalian));
	}

	private void setTotalBiayaDanDiskon() {
		double totalPenjualan = Formatter.formatStringToNumber(
				txtTotalPenjualan.getText()).doubleValue();

		double biaya = Formatter.formatStringToNumber(lblTaxAmount.getText())
				.doubleValue();
		lblTaxAmount.setText(Formatter.formatNumberToString(biaya));

		double taxPercent = Formatter.formatStringToNumber(
				txtTaxPercent.getText()).doubleValue();
		txtTaxPercent.setText(Formatter.formatNumberToString(taxPercent));

		double taxAmount = taxPercent / 100 * totalPenjualan;
		lblTaxAmount.setText(Formatter.formatNumberToString(taxAmount));

		double total = Math.floor(totalPenjualan + biaya + taxAmount);
		txtTotal.setText(Formatter.formatNumberToString(total));

		setTotalBayarDanKembalian();
	}

	private void setTotalPenjualan() {
		int rowCount = table.getRowCount();
		double totalPenjualan = 0;
		for (int i = 0; i < rowCount; ++i) {
			String string = table.getValueAt(i,
					tableParameters.get(ColumnEnum.TOTAL).getColumnIndex())
					.toString();
			double totalPerRow = Formatter.formatStringToNumber(string)
					.doubleValue();
			totalPenjualan += totalPerRow;
		}
		txtTotalPenjualan.setText(Formatter
				.formatNumberToString(totalPenjualan));

		setTotalBiayaDanDiskon();
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

		String itemCode = (String) table.getValueAt(row,
				tableParameters.get(ColumnEnum.CODE).getColumnIndex());

		double diskonPercent = 0;
		Session session = HibernateUtils.openSession();
		try {
			diskonPercent = SaleFacade.getInstance().getDiscountPercent(
					itemCode, jumlah, session);
		} finally {
			session.close();
		}
		table.setValueAt(Formatter.formatNumberToString(diskonPercent), row,
				tableParameters.get(ColumnEnum.DISCOUNT).getColumnIndex());

		double diskonAmount = diskonPercent / 100 * totalBeforeDiscount;

		double total = Math.floor(totalBeforeDiscount - diskonAmount);
		table.setValueAt(Formatter.formatNumberToString(total), row,
				tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());

		setTotalPenjualan();
	}

	private void tambah(String kodeBarang) {
		if (kodeBarang.trim().equals("")) {
			return;
		}

		Session session = HibernateUtils.openSession();
		try {
			StockFacade facade = StockFacade.getInstance();
			ItemStock itemStock = facade.getDetail(kodeBarang, session);
			Item item = itemStock.getItem();

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(tableModel.getRowCount() + 1);
			int rowIndex = tableModel.getRowCount() - 1;

			tableModel.setValueAt(item.getCode(), rowIndex, tableParameters
					.get(ColumnEnum.CODE).getColumnIndex());

			tableModel.setValueAt(item.getName(), rowIndex, tableParameters
					.get(ColumnEnum.NAME).getColumnIndex());

			tableModel.setValueAt(0, rowIndex,
					tableParameters.get(ColumnEnum.QUANTITY).getColumnIndex());

			tableModel.setValueAt(itemStock.getUnit(), rowIndex,
					tableParameters.get(ColumnEnum.UNIT).getColumnIndex());

			tableModel.setValueAt(Formatter.formatNumberToString(itemStock
					.getSellPrice()), rowIndex,
					tableParameters.get(ColumnEnum.PRICE).getColumnIndex());

			tableModel.setValueAt(0, rowIndex,
					tableParameters.get(ColumnEnum.DISCOUNT).getColumnIndex());

			tableModel.setValueAt(0, rowIndex,
					tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());

			reorderRowNumber();

			int row = table.getRowCount() - 1;
			table.requestFocus();
			table.changeSelection(row, tableParameters.get(ColumnEnum.QUANTITY)
					.getColumnIndex(), false, false);

			setTotalPenjualan();

		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		NUM, CODE, NAME, QUANTITY, UNIT, PRICE, DISCOUNT, TOTAL
	}
}
