package com.ganesha.minimarket.ui.forms.sale;

import java.awt.Color;
import java.awt.Font;
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

import javax.print.PrintException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
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
import com.ganesha.minimarket.facade.CustomerFacade;
import com.ganesha.minimarket.facade.DiscountFacade;
import com.ganesha.minimarket.facade.GlobalFacade;
import com.ganesha.minimarket.facade.ItemFacade;
import com.ganesha.minimarket.facade.SaleFacade;
import com.ganesha.minimarket.model.Customer;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.SaleDetail;
import com.ganesha.minimarket.model.SaleHeader;
import com.ganesha.minimarket.ui.forms.searchentity.SearchEntityDialog;
import com.ganesha.minimarket.utils.PermissionConstants;

public class PenjualanForm extends XJDialog {

	private XJTable table;

	private static final long serialVersionUID = 1401014426195840845L;
	private XJTextField txtNoTransaksi;
	private XJDateChooser dateChooser;
	private XJTextField txtKodeCustomer;
	private XJButton btnCari;
	private XJButton btnCariCustomer;
	private XJButton btnHapus;
	private XJTextField txtTaxPercent;
	private XJTextField txtTotalPenjualan;
	private XJTextField txtBayar;
	private JTextField txtKembalian;
	private JTextField txtTotal;
	private XJButton btnBatal;
	private XJButton btnSelesai;
	private XJLabel lblKembali;
	private XJTextField txtNamaCustomer;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJLabel lblBayar;
	private XJLabel lblTaxAmount;
	private XJLabel lblBarcodef;
	private XJTextField txtBarcode;
	private XJPanel pnlSearch;

	private Integer customerId;

	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 5, false,
				"No", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.CODE,
				new XTableParameter(1, 75, false, "Kode", false,
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.NAME, new XTableParameter(2, 400, false,
				"Nama Barang", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(3, 10,
				true, "Qty", false, XTableConstants.CELL_RENDERER_RIGHT,
				Integer.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(4, 50, false,
				"Satuan", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(5, 75, false,
				"Harga", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.DISCOUNT, new XTableParameter(6, 5,
				false, "%", false, XTableConstants.CELL_RENDERER_CENTER,
				Double.class));

		tableParameters.put(ColumnEnum.TOTAL, new XTableParameter(7, 75, false,
				"Total", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.ID, new XTableParameter(8, 0, false,
				"ID", true, XTableConstants.CELL_RENDERER_LEFT, Integer.class));
	}

	public PenjualanForm(Window parent) {
		super(parent);
		setTitle("Transaksi Penjualan");
		setPermissionCode(PermissionConstants.SAL_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]", "[][][][][][]"));

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

		XJPanel pnlHeader = new XJPanel();
		pnlHeader.setBorder(new XEtchedBorder());
		getContentPane().add(pnlHeader, "cell 0 0,grow");
		pnlHeader.setLayout(new MigLayout("", "[150][100][200][grow][250]",
				"[][]"));

		XJLabel lblNoTransaksi = new XJLabel();
		lblNoTransaksi.setText("No. Transaksi Penjualan");
		pnlHeader.add(lblNoTransaksi, "cell 0 0,alignx trailing");

		txtNoTransaksi = new XJTextField();
		txtNoTransaksi.setText(GeneralConstants.PREFIX_TRX_NUMBER_SALES
				+ CommonUtils.getTimestampInString());
		txtNoTransaksi.setEditable(false);
		pnlHeader.add(txtNoTransaksi, "cell 1 0 2 1,growx");

		XJLabel lblTanggal = new XJLabel();
		lblTanggal.setText("Tanggal Penjualan");
		pnlHeader.add(lblTanggal, "cell 4 0");

		XJLabel lblCustomer = new XJLabel();
		lblCustomer.setText("Customer");
		pnlHeader.add(lblCustomer, "cell 0 1,alignx trailing");

		Customer defaultCustomer = null;
		Session session = HibernateUtils.openSession();
		try {
			defaultCustomer = CustomerFacade.getInstance().getDefaultCustomer(
					session);
			customerId = defaultCustomer.getId();
		} finally {
			session.close();
		}
		txtKodeCustomer = new XJTextField();
		txtKodeCustomer.setText(defaultCustomer.getCode());
		txtKodeCustomer.setEditable(false);
		pnlHeader.add(txtKodeCustomer, "cell 1 1,growx");

		txtNamaCustomer = new XJTextField();
		txtNamaCustomer.setText(defaultCustomer.getName());
		txtNamaCustomer.setEditable(false);
		pnlHeader.add(txtNamaCustomer, "flowx,cell 2 1,growx");

		btnCariCustomer = new XJButton();
		btnCariCustomer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cariCustomer();
			}
		});
		btnCariCustomer.setText("Cari Customer [F5]");
		pnlHeader.add(btnCariCustomer, "cell 3 1");

		dateChooser = new XJDateChooser();
		dateChooser.setDate(CommonUtils.getCurrentDate());
		dateChooser.getCalendarButton().setMnemonic('T');
		pnlHeader.add(dateChooser, "cell 4 1,grow");

		XJPanel pnlPenjualan = new XJPanel();
		getContentPane().add(pnlPenjualan, "cell 0 1,grow");
		pnlPenjualan.setLayout(new MigLayout("", "[][grow]", "[][200]"));

		XJPanel pnlBarcode = new XJPanel();
		pnlPenjualan.add(pnlBarcode, "cell 1 0,grow");
		pnlBarcode.setLayout(new MigLayout("", "[][300]", "[]"));

		lblBarcodef = new XJLabel();
		pnlBarcode.add(lblBarcodef, "cell 0 0");
		lblBarcodef.setText("Barcode [F8]");

		txtBarcode = new XJTextField();
		pnlBarcode.add(txtBarcode, "cell 1 0,growx");
		txtBarcode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cariBarcode();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PenjualanForm.this, ex);
				}
			}
		});

		pnlSearch = new XJPanel();
		pnlPenjualan.add(pnlSearch, "cell 0 1,growx,aligny center");
		pnlSearch.setLayout(new MigLayout("", "[]", "[][]"));

		btnCari = new XJButton();
		pnlSearch.add(btnCari, "cell 0 0,growx");
		btnCari.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cariBarang();
			}
		});
		btnCari.setText("<html><center>Cari Barang<br/>[F6]</center></html>");

		btnHapus = new XJButton();
		pnlSearch.add(btnHapus, "cell 0 1,growx");
		btnHapus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hapus();
			}
		});
		btnHapus.setText("<html><center>Hapus<br/>[Delete]</center></html>");

		JScrollPane scrollPane = new JScrollPane(table);
		pnlPenjualan.add(scrollPane, "cell 1 1,growx");

		XJPanel pnlTotalBelanja = new XJPanel();
		getContentPane().add(pnlTotalBelanja, "cell 0 2,alignx right");
		pnlTotalBelanja.setBorder(new XEtchedBorder());
		pnlTotalBelanja.setLayout(new MigLayout("", "[][200]", "[][][]"));

		XJLabel lblTotalPenjualan = new XJLabel();
		pnlTotalBelanja.add(lblTotalPenjualan, "cell 0 0");
		lblTotalPenjualan.setText("Total Penjualan");

		txtTotalPenjualan = new XJTextField();
		pnlTotalBelanja.add(txtTotalPenjualan, "cell 1 0,growx");
		txtTotalPenjualan.setText("0");
		txtTotalPenjualan.setHorizontalAlignment(SwingConstants.TRAILING);
		txtTotalPenjualan.setEditable(false);

		XJLabel lblTaxPercent = new XJLabel();
		lblTaxPercent.setText("Pajak (%)");
		pnlTotalBelanja.add(lblTaxPercent, "cell 0 1");

		txtTaxPercent = new XJTextField();
		txtTaxPercent.setEditable(false);
		txtTaxPercent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalBiayaDanDiskon();
			}
		});
		txtTaxPercent.setHorizontalAlignment(SwingConstants.TRAILING);
		txtTaxPercent.setText(Formatter.formatNumberToString(GlobalFacade
				.getInstance().getTaxPercent()));
		pnlTotalBelanja.add(txtTaxPercent, "cell 1 1,growx");

		lblTaxAmount = new XJLabel();
		lblTaxAmount.setText("0");
		pnlTotalBelanja.add(lblTaxAmount, "cell 1 2,alignx right");

		XJPanel pnlKembalian = new XJPanel();
		pnlKembalian.setBackground(Color.BLACK);
		getContentPane().add(pnlKembalian, "cell 0 3,growx");
		pnlKembalian.setBorder(new XEtchedBorder());
		pnlKembalian.setLayout(new MigLayout("", "[300][grow][300][grow][300]",
				"[][grow]"));

		XJLabel lblTotal = new XJLabel();
		lblTotal.setForeground(Color.WHITE);
		lblTotal.setFont(new Font("Tahoma", Font.BOLD, 40));
		pnlKembalian.add(lblTotal, "cell 0 0");
		lblTotal.setText("TOTAL");

		lblBayar = new XJLabel();
		lblBayar.setForeground(Color.WHITE);
		lblBayar.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblBayar.setText("Bayar [F11]");
		pnlKembalian.add(lblBayar, "cell 2 0");

		lblKembali = new XJLabel();
		lblKembali.setForeground(Color.WHITE);
		lblKembali.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblKembali.setText("Kembali");
		pnlKembalian.add(lblKembali, "cell 4 0");

		txtTotal = new JTextField();
		txtTotal.setBackground(Color.YELLOW);
		txtTotal.setEditable(false);
		txtTotal.setFont(new Font("Tahoma", Font.BOLD, 40));
		pnlKembalian.add(txtTotal, "cell 0 1,growx");
		txtTotal.setHorizontalAlignment(SwingConstants.TRAILING);
		txtTotal.setText("0");

		txtBayar = new XJTextField();
		txtBayar.setFont(new Font("Tahoma", Font.BOLD, 40));
		txtBayar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalBayarDanKembalian();
			}
		});
		txtBayar.setHorizontalAlignment(SwingConstants.TRAILING);
		txtBayar.setText("0");
		pnlKembalian.add(txtBayar, "cell 2 1,growx");

		txtKembalian = new JTextField();
		txtKembalian.setBackground(Color.PINK);
		txtKembalian.setFont(new Font("Tahoma", Font.BOLD, 40));
		txtKembalian.setHorizontalAlignment(SwingConstants.TRAILING);
		txtKembalian.setText("0");
		txtKembalian.setEditable(false);
		pnlKembalian.add(txtKembalian, "cell 4 1,growx");

		JSeparator separator_1 = new JSeparator();
		getContentPane().add(separator_1, "cell 0 4,grow");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 5,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][]", "[]"));

		btnSelesai = new XJButton();
		btnSelesai.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					selesaiDanSimpan();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PenjualanForm.this, ex);
				}
			}
		});

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
		btnSelesai
				.setText("<html><center>Selesai & Simpan Data<br/>[F12]</center></html>");
		pnlButton.add(btnSelesai, "cell 2 0");

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
			btnCari.doClick();
			break;
		case KeyEvent.VK_F8:
			setFocusToBarcodeField();
			break;
		case KeyEvent.VK_F11:
			setFocusToFieldBayar();
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

		Integer idBarang = searchEntityDialog.getSelectedId();
		if (idBarang != null) {
			tambah(idBarang);
		}
	}

	private void cariBarcode() throws UserException {
		String barcode = txtBarcode.getText();
		if (barcode == null || barcode.trim().equals("")) {
			return;
		}
		Session session = HibernateUtils.openSession();
		try {
			Item item = ItemFacade.getInstance().getByBarcode(barcode, session);

			if (item == null) {
				throw new UserException("Barang dengan barcode " + barcode
						+ " tidak ditemukan.");
			}
			Integer itemId = item.getId();
			tambah(itemId);
			txtBarcode.setText("");

		} finally {
			session.close();
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

	private void selesaiDanSimpan() throws AppException, UserException {

		if (!(table.getRowCount() > 0)) {
			throw new UserException(
					"Proses tidak dapat dilanjutkan. Anda belum memasukan item apapun untuk transaksi ini.");
		}

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			SaleFacade facade = SaleFacade.getInstance();

			String transactionNumber = txtNoTransaksi.getText();

			Timestamp transactionTimestamp = CommonUtils
					.castDateToTimestamp(dateChooser.getDate());

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

			if (pay < totalAmount) {
				throw new UserException("Pembayaran kurang");
			}

			SaleHeader saleHeader = facade.validateForm(transactionNumber,
					transactionTimestamp, customerId, subTotalAmount,
					taxPercent, taxAmount, totalAmount, pay, moneyChange,
					session);

			List<SaleDetail> saleDetails = new ArrayList<>();
			int rowCount = table.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				SaleDetail saleDetail = new SaleDetail();

				saleDetail.setOrderNum(Formatter.formatStringToNumber(
						table.getModel()
								.getValueAt(
										i,
										tableParameters.get(ColumnEnum.NUM)
												.getColumnIndex()).toString())
						.intValue());

				saleDetail.setItemId(Formatter.formatStringToNumber(
						table.getModel()
								.getValueAt(
										i,
										tableParameters.get(ColumnEnum.ID)
												.getColumnIndex()).toString())
						.intValue());

				saleDetail.setItemCode(table
						.getModel()
						.getValueAt(
								i,
								tableParameters.get(ColumnEnum.CODE)
										.getColumnIndex()).toString());

				saleDetail.setItemName(table
						.getModel()
						.getValueAt(
								i,
								tableParameters.get(ColumnEnum.NAME)
										.getColumnIndex()).toString());

				saleDetail.setQuantity(Formatter.formatStringToNumber(
						table.getModel()
								.getValueAt(
										i,
										tableParameters
												.get(ColumnEnum.QUANTITY)
												.getColumnIndex()).toString())
						.intValue());

				saleDetail.setUnit(table
						.getModel()
						.getValueAt(
								i,
								tableParameters.get(ColumnEnum.UNIT)
										.getColumnIndex()).toString());

				saleDetail.setPricePerUnit(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getModel()
										.getValueAt(
												i,
												tableParameters.get(
														ColumnEnum.PRICE)
														.getColumnIndex())
										.toString()).doubleValue()));

				saleDetail.setDiscountPercent(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getModel()
										.getValueAt(
												i,
												tableParameters.get(
														ColumnEnum.DISCOUNT)
														.getColumnIndex())
										.toString()).doubleValue()));

				saleDetail.setTotalAmount(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getModel()
										.getValueAt(
												i,
												tableParameters.get(
														ColumnEnum.TOTAL)
														.getColumnIndex())
										.toString()).doubleValue()));

				saleDetails.add(saleDetail);
			}

			facade.performSale(saleHeader, saleDetails, session);

			ActivityLogFacade.doLog(getPermissionCode(),
					ActionType.TRANSACTION, Main.getUserLogin(), saleHeader,
					session);
			session.getTransaction().commit();

			facade.cetakReceipt(saleHeader, saleDetails);
			dispose();

		} catch (PrintException e) {
			throw new AppException(e);
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void setFocusToBarcodeField() {
		TableCellEditor cellEditor = table.getCellEditor();
		if (cellEditor != null) {
			cellEditor.stopCellEditing();
		}
		txtBarcode.setText("");
		txtBarcode.requestFocus();
	}

	private void setFocusToFieldBayar() {
		TableCellEditor cellEditor = table.getCellEditor();
		if (cellEditor != null) {
			cellEditor.stopCellEditing();
		}
		txtBayar.selectAll();
		txtBayar.requestFocus();
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

		double taxPercent = Formatter.formatStringToNumber(
				txtTaxPercent.getText()).doubleValue();
		txtTaxPercent.setText(Formatter.formatNumberToString(taxPercent));

		double taxAmount = taxPercent / 100 * totalPenjualan;
		lblTaxAmount.setText(Formatter.formatNumberToString(taxAmount));

		double total = Math.floor(totalPenjualan + taxAmount);
		txtTotal.setText(Formatter.formatNumberToString(total));

		setTotalBayarDanKembalian();
	}

	private void setTotalPenjualan() {
		int rowCount = table.getRowCount();
		double totalPenjualan = 0;
		for (int i = 0; i < rowCount; ++i) {
			String string = table
					.getModel()
					.getValueAt(
							i,
							tableParameters.get(ColumnEnum.TOTAL)
									.getColumnIndex()).toString();
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

		int itemId = (int) table.getModel().getValueAt(row,
				tableParameters.get(ColumnEnum.ID).getColumnIndex());

		double discountPercent = 0;
		Session session = HibernateUtils.openSession();
		try {
			discountPercent = DiscountFacade.getInstance().getDiscountPercent(
					itemId, jumlah, session);
		} finally {
			session.close();
		}
		table.setValueAt(Formatter.formatNumberToString(discountPercent), row,
				tableParameters.get(ColumnEnum.DISCOUNT).getColumnIndex());

		double diskonAmount = discountPercent / 100 * totalBeforeDiscount;

		double total = Math.floor(totalBeforeDiscount - diskonAmount);
		table.setValueAt(Formatter.formatNumberToString(total), row,
				tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());

		setTotalPenjualan();
	}

	private void tambah(Integer itemId) {
		if (itemId == null) {
			return;
		}

		Session session = HibernateUtils.openSession();
		try {
			ItemFacade facade = ItemFacade.getInstance();
			Item item = facade.getDetail(itemId, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(tableModel.getRowCount() + 1);
			int rowIndex = tableModel.getRowCount() - 1;

			tableModel.setValueAt(item.getCode(), rowIndex, tableParameters
					.get(ColumnEnum.CODE).getColumnIndex());

			tableModel.setValueAt(item.getName(), rowIndex, tableParameters
					.get(ColumnEnum.NAME).getColumnIndex());

			tableModel.setValueAt(1, rowIndex,
					tableParameters.get(ColumnEnum.QUANTITY).getColumnIndex());

			tableModel.setValueAt(item.getUnit(), rowIndex, tableParameters
					.get(ColumnEnum.UNIT).getColumnIndex());

			tableModel.setValueAt(Formatter.formatNumberToString(item
					.getSellPrice()), rowIndex,
					tableParameters.get(ColumnEnum.PRICE).getColumnIndex());

			tableModel.setValueAt(0, rowIndex,
					tableParameters.get(ColumnEnum.DISCOUNT).getColumnIndex());

			tableModel.setValueAt(0, rowIndex,
					tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());

			tableModel.setValueAt(item.getId(), rowIndex,
					tableParameters.get(ColumnEnum.ID).getColumnIndex());

			reorderRowNumber();

			int row = table.getRowCount() - 1;
			table.requestFocus();
			table.changeSelection(row, tableParameters.get(ColumnEnum.QUANTITY)
					.getColumnIndex(), false, false);

			TableCellEditor cellEditor = table.getCellEditor();
			if (cellEditor != null) {
				cellEditor.stopCellEditing();
			}

			setTotalPerRow(row);
			setTotalPenjualan();

		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		NUM, CODE, NAME, QUANTITY, UNIT, PRICE, DISCOUNT, TOTAL, ID
	}
}
