package com.ganesha.accounting.minimarket.ui.forms.forms.purchase;

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
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.formatter.Formatter;
import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.facade.PurchaseFacade;
import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Item;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.accounting.minimarket.model.PurchaseDetail;
import com.ganesha.accounting.minimarket.model.PurchaseHeader;
import com.ganesha.accounting.minimarket.model.Supplier;
import com.ganesha.accounting.minimarket.ui.forms.forms.searchentity.SearchEntityDialog;
import com.ganesha.accounting.minimarket.ui.forms.forms.stock.StockForm;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDateChooser;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.hibernate.HibernateUtils;

public class PembelianForm extends XJDialog {

	private XJTable table;

	private static final long serialVersionUID = 1401014426195840845L;
	private XJTextField txtNoTransaksi;
	private XJDateChooser dateChooser;
	private XJTextField txtKodeSupplier;
	private XJButton btnTambah;
	private XJButton btnCariSupplier;
	private XJButton btnHapus;
	private XJTextField txtBebanLain;
	private XJTextField txtDiskon;
	private XJTextField txtTotalPembelian;
	private XJTextField txtUangMuka;
	private XJTextField txtSisa;
	private XJLabel lblLunas;
	private XJTextField txtTotal;
	private XJButton btnBatal;
	private XJButton btnSelesai;
	private XJLabel lblSisa;
	private XJTextField txtNamaSupplier;

	private final HashMap<ColumnEnum, String> columnNames = new HashMap<>();
	private final HashMap<ColumnEnum, Integer> columnIndex = new HashMap<>();
	private final HashMap<ColumnEnum, Integer> columnWidth = new HashMap<>();
	private final HashMap<ColumnEnum, DefaultTableCellRenderer> columnCellRenderer = new HashMap<>();
	private XJButton btnRegistrasi;
	private XJButton btnDetail;

	{
		columnNames.put(ColumnEnum.NUM, "No");
		columnNames.put(ColumnEnum.CODE, "Kode");
		columnNames.put(ColumnEnum.NAME, "Nama Barang");
		columnNames.put(ColumnEnum.QUANTITY, "Quantity");
		columnNames.put(ColumnEnum.UNIT, "Satuan");
		columnNames.put(ColumnEnum.PRICE, "Harga Satuan");
		columnNames.put(ColumnEnum.LAST_PRICE, "Harga Terakhir");
		columnNames.put(ColumnEnum.TOTAL, "Total");

		columnIndex.put(ColumnEnum.NUM, 0);
		columnIndex.put(ColumnEnum.CODE, 1);
		columnIndex.put(ColumnEnum.NAME, 2);
		columnIndex.put(ColumnEnum.QUANTITY, 3);
		columnIndex.put(ColumnEnum.UNIT, 4);
		columnIndex.put(ColumnEnum.PRICE, 5);
		columnIndex.put(ColumnEnum.LAST_PRICE, 6);
		columnIndex.put(ColumnEnum.TOTAL, 7);

		columnWidth.put(ColumnEnum.NUM, 25);
		columnWidth.put(ColumnEnum.CODE, 75);
		columnWidth.put(ColumnEnum.NAME, 300);
		columnWidth.put(ColumnEnum.QUANTITY, 75);
		columnWidth.put(ColumnEnum.UNIT, 75);
		columnWidth.put(ColumnEnum.PRICE, 100);
		columnWidth.put(ColumnEnum.LAST_PRICE, 150);
		columnWidth.put(ColumnEnum.TOTAL, 100);

		columnCellRenderer.put(ColumnEnum.NUM,
				XTableConstants.CELL_RENDERER_RIGHT);
		columnCellRenderer.put(ColumnEnum.CODE,
				XTableConstants.CELL_RENDERER_LEFT);
		columnCellRenderer.put(ColumnEnum.NAME,
				XTableConstants.CELL_RENDERER_LEFT);
		columnCellRenderer.put(ColumnEnum.QUANTITY,
				XTableConstants.CELL_RENDERER_RIGHT);
		columnCellRenderer.put(ColumnEnum.UNIT,
				XTableConstants.CELL_RENDERER_LEFT);
		columnCellRenderer.put(ColumnEnum.PRICE,
				XTableConstants.CELL_RENDERER_RIGHT);
		columnCellRenderer.put(ColumnEnum.LAST_PRICE,
				XTableConstants.CELL_RENDERER_RIGHT);
		columnCellRenderer.put(ColumnEnum.TOTAL,
				XTableConstants.CELL_RENDERER_RIGHT);
	}

	public PembelianForm(Window parent) {
		super(parent);
		setTitle("Transaksi Pembelian");
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]",
						"[grow][grow][grow][grow][][grow]"));

		table = new XJTable();
		initTable();

		JPanel pnlHeader = new JPanel();
		pnlHeader.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlHeader, "cell 0 0,alignx left,growy");
		pnlHeader.setLayout(new MigLayout("", "[150][100][200][]", "[][][]"));

		XJLabel lblNoTransaksi = new XJLabel();
		lblNoTransaksi.setText("No. Transaksi");
		pnlHeader.add(lblNoTransaksi, "cell 0 0,alignx trailing");

		txtNoTransaksi = new XJTextField();
		txtNoTransaksi.setText(GeneralConstants.PREFIX_TRX_NUMBER_PURCHASE
				+ CommonUtils.getTimestampInString());
		txtNoTransaksi.setEditable(false);
		pnlHeader.add(txtNoTransaksi, "cell 1 0 2 1,growx");

		XJLabel lblTanggal = new XJLabel();
		lblTanggal.setText("Tanggal");
		pnlHeader.add(lblTanggal, "cell 0 1,alignx trailing");

		dateChooser = new XJDateChooser();
		dateChooser.setDate(CommonUtils.getCurrentDate());
		dateChooser.getCalendarButton().setMnemonic('T');
		pnlHeader.add(dateChooser, "cell 1 1 2 1,grow");

		XJLabel lblSupplier = new XJLabel();
		lblSupplier.setText("Supplier");
		pnlHeader.add(lblSupplier, "cell 0 2,alignx trailing");

		txtKodeSupplier = new XJTextField();
		txtKodeSupplier.setEditable(false);
		pnlHeader.add(txtKodeSupplier, "cell 1 2,growx");

		txtNamaSupplier = new XJTextField();
		txtNamaSupplier.setEditable(false);
		pnlHeader.add(txtNamaSupplier, "flowx,cell 2 2,growx");

		btnCariSupplier = new XJButton();
		btnCariSupplier.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cariSupplier();
			}
		});
		btnCariSupplier.setText("Cari Supplier [F5]");
		pnlHeader.add(btnCariSupplier, "cell 3 2");

		JPanel pnlPembelian = new JPanel();
		getContentPane().add(pnlPembelian, "cell 0 1,grow");
		pnlPembelian.setLayout(new MigLayout("", "[612px,grow]",
				"[grow][::200,baseline]"));

		JPanel pnlSearchItem = new JPanel();
		pnlPembelian.add(pnlSearchItem, "cell 0 0,grow");
		pnlSearchItem.setLayout(new MigLayout("", "[][][grow][]", "[]"));

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

		btnRegistrasi = new XJButton();
		btnRegistrasi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				registrasiBarangBaru();
			}
		});
		btnRegistrasi
				.setText("<html><center>Registrasi Barang Baru<br/>[F7]</center></html>");
		pnlSearchItem.add(btnRegistrasi, "cell 1 0");
		btnHapus.setText("<html><center>Hapus<br/>[Delete]</center></html>");
		pnlSearchItem.add(btnHapus, "cell 3 0");

		JScrollPane scrollPane = new JScrollPane(table);
		pnlPembelian.add(scrollPane, "cell 0 1,growx");

		JPanel pnlSubTotal = new JPanel();
		getContentPane().add(pnlSubTotal, "cell 0 2,grow");
		pnlSubTotal.setLayout(new MigLayout("", "[][grow][][200]", "[]"));

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDetail();
			}
		});
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[Enter]</center></html>");
		pnlSubTotal.add(btnDetail, "cell 0 0");

		XJLabel lblTotalPembelian = new XJLabel();
		pnlSubTotal.add(lblTotalPembelian, "cell 2 0");
		lblTotalPembelian.setText("Total Pembelian");

		txtTotalPembelian = new XJTextField();
		txtTotalPembelian.setText("0");
		txtTotalPembelian.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlSubTotal.add(txtTotalPembelian, "cell 3 0,growx");
		txtTotalPembelian.setEditable(false);

		JPanel pnlPerhitungan = new JPanel();
		getContentPane().add(pnlPerhitungan, "cell 0 3,alignx center,growy");
		pnlPerhitungan.setLayout(new MigLayout("", "[300][300]", "[grow]"));

		JPanel pnlBeban = new JPanel();
		pnlBeban.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlPerhitungan.add(pnlBeban, "cell 0 0,grow");
		pnlBeban.setLayout(new MigLayout("", "[][grow]", "[][][10][]"));

		XJLabel lblBiaya = new XJLabel();
		lblBiaya.setText("Biaya Lain-Lain");
		pnlBeban.add(lblBiaya, "cell 0 0,alignx trailing");

		txtBebanLain = new XJTextField();
		txtBebanLain.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalBiayaDanDiskon();
			}
		});
		txtBebanLain.setHorizontalAlignment(SwingConstants.TRAILING);
		txtBebanLain.setText("0");
		pnlBeban.add(txtBebanLain, "cell 1 0,growx");

		XJLabel lblDiskon = new XJLabel();
		lblDiskon.setText("Diskon");
		pnlBeban.add(lblDiskon, "cell 0 1,alignx trailing");

		txtDiskon = new XJTextField();
		txtDiskon.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalBiayaDanDiskon();
			}
		});
		txtDiskon.setHorizontalAlignment(SwingConstants.TRAILING);
		txtDiskon.setText("0");
		pnlBeban.add(txtDiskon, "cell 1 1,growx");

		JSeparator separator = new JSeparator();
		pnlBeban.add(separator, "cell 0 2 2 1,growx,aligny center");

		XJLabel lblTotal = new XJLabel();
		lblTotal.setText("TOTAL");
		pnlBeban.add(lblTotal, "cell 0 3,alignx trailing");

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

		XJLabel lblBayar = new XJLabel();
		lblBayar.setText("Bayar");
		pnlUangMuka.add(lblBayar, "cell 0 0,alignx trailing");

		txtUangMuka = new XJTextField();
		txtUangMuka.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalBayarDanHutang();
			}
		});
		txtUangMuka.setHorizontalAlignment(SwingConstants.TRAILING);
		txtUangMuka.setText("0");
		pnlUangMuka.add(txtUangMuka, "cell 1 0,growx,aligny top");

		lblSisa = new XJLabel();
		lblSisa.setForeground(Color.RED);
		lblSisa.setText("Sisa");
		pnlUangMuka.add(lblSisa, "cell 0 1,alignx trailing");

		txtSisa = new XJTextField();
		txtSisa.setHorizontalAlignment(SwingConstants.TRAILING);
		txtSisa.setText("0");
		txtSisa.setForeground(Color.RED);
		txtSisa.setEditable(false);
		pnlUangMuka.add(txtSisa, "cell 1 1,growx,aligny top");

		lblLunas = new XJLabel();
		lblLunas.setForeground(Color.BLUE);
		lblLunas.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblLunas.setText("LUNAS");
		pnlUangMuka.add(lblLunas, "cell 0 2 2 1,alignx center");

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

		lblSisa.setVisible(false);
		txtSisa.setVisible(false);
		lblLunas.setVisible(false);

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnCariSupplier.doClick();
			break;
		case KeyEvent.VK_F6:
			btnTambah.doClick();
			break;
		case KeyEvent.VK_F7:
			btnRegistrasi.doClick();
			break;
		case KeyEvent.VK_F12:
			btnSelesai.doClick();
			break;
		case KeyEvent.VK_ENTER:
			btnDetail.doClick();
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
		String message = "Apakah Anda yakin ingin membatalkan transaksi pembelian ini?";
		int selectedOption = JOptionPane.showConfirmDialog(this, message,
				"Membatalkan Transaksi Pembelian", JOptionPane.YES_NO_OPTION);
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

	private void cariSupplier() {
		SearchEntityDialog searchEntityDialog = new SearchEntityDialog(
				"Cari Supplier", this, Supplier.class);
		searchEntityDialog.setVisible(true);

		String kodeSupplier = searchEntityDialog.getSelectedCode();
		if (kodeSupplier != null) {
			String namaSupplier = searchEntityDialog.getSelectedName();
			txtKodeSupplier.setText(kodeSupplier);
			txtNamaSupplier.setText(namaSupplier);
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
		setTotal();
		reorderRowNumber();
	}

	private void initTable() {
		XTableModel tableModel = new XTableModel();
		tableModel.setColumnIdentifiers(new String[] { "No", "Kode",
				"Nama Barang", "Banyak", "Satuan", "Harga Satuan",
				"Harga Beli Terakhir", "Total" });
		tableModel.setColumnEditable(new boolean[] { false, false, false, true,
				false, true, false, false });
		table.setModel(tableModel);

		TableColumnModel columnModel = table.getColumnModel();
		Iterator<ColumnEnum> iterator = columnIndex.keySet().iterator();
		while (iterator.hasNext()) {
			ColumnEnum key = iterator.next();
			int index = columnIndex.get(key);
			int width = columnWidth.get(key);
			DefaultTableCellRenderer cellRenderer = columnCellRenderer.get(key);
			columnModel.getColumn(index).setPreferredWidth(width);
			columnModel.getColumn(index).setCellRenderer(cellRenderer);
		}

		table.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("tableCellEditor")) {
					int row = table.getEditingRow();
					int column = table.getEditingColumn();
					if (column == columnIndex.get(ColumnEnum.QUANTITY)) {
						setTotalPerColumn(row);
						setTotal();
					} else if (column == columnIndex.get(ColumnEnum.PRICE)) {
						setTotalPerColumn(row);
						setTotal();
					}
				}
			}
		});
	}

	private void registrasiBarangBaru() {
		StockForm stockForm = new StockForm(this, ActionType.CREATE);
		stockForm.setVisible(true);
		String kodeBarang = stockForm.getKodeBarang();
		tambah(kodeBarang);
	}

	private void reorderRowNumber() {
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			table.setValueAt(i + 1, i, 0);
		}
	}

	private void selesaiDanSimpan() throws UserException {
		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			PurchaseFacade facade = PurchaseFacade.getInstance();

			String transactionNumber = txtNoTransaksi.getText();

			Timestamp transactionTimestamp = CommonUtils
					.castDateToTimestamp(dateChooser.getDate());

			String supplierCode = txtKodeSupplier.getText();

			double subTotalAmount = Formatter.formatStringToNumber(
					txtTotalPembelian.getText()).doubleValue();

			double expenses = Formatter.formatStringToNumber(
					txtBebanLain.getText()).doubleValue();

			double discount = Formatter.formatStringToNumber(
					txtDiskon.getText()).doubleValue();

			double totalAmount = Formatter.formatStringToNumber(
					txtTotal.getText()).doubleValue();

			double advancePayment = Formatter.formatStringToNumber(
					txtUangMuka.getText()).doubleValue();

			double remainingPayment = Formatter.formatStringToNumber(
					txtSisa.getText()).doubleValue();

			boolean paidInFullFlag = !(remainingPayment > 0);

			PurchaseHeader purchaseHeader = facade.validateForm(
					transactionNumber, transactionTimestamp, supplierCode,
					subTotalAmount, expenses, discount, totalAmount,
					advancePayment, remainingPayment, paidInFullFlag, session);

			List<PurchaseDetail> purchaseDetails = new ArrayList<>();
			int rowCount = table.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				PurchaseDetail purchaseDetail = new PurchaseDetail();

				purchaseDetail.setItemCode(table.getValueAt(i,
						columnIndex.get(ColumnEnum.CODE)).toString());

				purchaseDetail.setItemName(table.getValueAt(i,
						columnIndex.get(ColumnEnum.NAME)).toString());

				purchaseDetail.setQuantity(Formatter.formatStringToNumber(
						table.getValueAt(i,
								columnIndex.get(ColumnEnum.QUANTITY))
								.toString()).intValue());

				purchaseDetail.setUnit(table.getValueAt(i,
						columnIndex.get(ColumnEnum.UNIT)).toString());

				purchaseDetail.setPricePerUnit(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getValueAt(i,
										columnIndex.get(ColumnEnum.PRICE))
										.toString()).doubleValue()));

				purchaseDetail.setTotalPrice(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getValueAt(i,
										columnIndex.get(ColumnEnum.TOTAL))
										.toString()).doubleValue()));

				purchaseDetail.setDisabled(false);
				purchaseDetail.setDeleted(false);
				purchaseDetail.setLastUpdatedBy(Main.getUserLogin().getId());
				purchaseDetail.setLastUpdatedTimestamp(CommonUtils
						.getCurrentTimestamp());

				purchaseDetails.add(purchaseDetail);
			}

			facade.performPurchase(purchaseHeader, purchaseDetails, session);

			session.getTransaction().commit();
			dispose();

		} finally {
			session.close();
		}
	}

	private void setTotal() {
		setTotalPembelian();
		setTotalBiayaDanDiskon();
		setTotalBayarDanHutang();
	}

	private void setTotalBayarDanHutang() {
		double total = Formatter.formatStringToNumber(txtTotal.getText())
				.doubleValue();

		double uangMuka = Formatter.formatStringToNumber(txtUangMuka.getText())
				.doubleValue();
		txtUangMuka.setText(Formatter.formatNumberToString(uangMuka));

		double sisa = total - uangMuka;
		txtSisa.setText(Formatter.formatNumberToString(sisa));

		if (uangMuka > total) {
			btnSelesai.setEnabled(false);
			lblLunas.setVisible(false);
		} else {
			btnSelesai.setEnabled(true);
			lblLunas.setVisible(true);
			if (uangMuka < total) {
				lblSisa.setVisible(true);
				txtSisa.setVisible(true);
				lblLunas.setText("HUTANG");
				lblLunas.setForeground(Color.RED);
			} else {
				lblSisa.setVisible(false);
				txtSisa.setVisible(false);
				lblLunas.setText("LUNAS");
				lblLunas.setForeground(Color.BLUE);
			}
		}
	}

	private void setTotalBiayaDanDiskon() {
		double totalPembelian = Formatter.formatStringToNumber(
				txtTotalPembelian.getText()).doubleValue();

		double biaya = Formatter.formatStringToNumber(txtBebanLain.getText())
				.doubleValue();
		txtBebanLain.setText(Formatter.formatNumberToString(biaya));

		double diskon = Formatter.formatStringToNumber(txtDiskon.getText())
				.doubleValue();
		txtDiskon.setText(Formatter.formatNumberToString(diskon));

		double total = totalPembelian + biaya - diskon;
		txtTotal.setText(Formatter.formatNumberToString(total));
	}

	private void setTotalPembelian() {
		int rowCount = table.getRowCount();
		double totalPembelian = 0;
		for (int i = 0; i < rowCount; ++i) {
			String string = table.getValueAt(i,
					columnIndex.get(ColumnEnum.TOTAL)).toString();
			double totalPerRow = Formatter.formatStringToNumber(string)
					.doubleValue();
			totalPembelian += totalPerRow;
		}
		txtTotalPembelian.setText(Formatter
				.formatNumberToString(totalPembelian));
	}

	private void setTotalPerColumn(int row) {
		if (row < 0) {
			return;
		}

		int jumlah = Formatter.formatStringToNumber(
				table.getValueAt(row, columnIndex.get(ColumnEnum.QUANTITY))
						.toString()).intValue();
		table.setValueAt(Formatter.formatNumberToString(jumlah), row,
				columnIndex.get(ColumnEnum.QUANTITY));

		double hargaSatuan = Formatter.formatStringToNumber(
				table.getValueAt(row, columnIndex.get(ColumnEnum.PRICE))
						.toString()).doubleValue();
		table.setValueAt(Formatter.formatNumberToString(hargaSatuan), row,
				columnIndex.get(ColumnEnum.PRICE));

		double total = jumlah * hargaSatuan;
		table.setValueAt(Formatter.formatNumberToString(total), row,
				columnIndex.get(ColumnEnum.TOTAL));

		setTotalPembelian();
	}

	private void showDetail() {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			String code = (String) table.getModel().getValueAt(selectedRow, 1);

			StockFacade facade = StockFacade.getInstance();
			ItemStock itemStock = facade.getDetail(code, session);

			StockForm stockForm = new StockForm(this, ActionType.READ);
			stockForm.setFormDetailValue(itemStock);
			stockForm.setVisible(true);
		} finally {
			session.close();
		}
	};

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

			tableModel.setValueAt(item.getCode(), rowIndex,
					columnIndex.get(ColumnEnum.CODE));

			tableModel.setValueAt(item.getName(), rowIndex,
					columnIndex.get(ColumnEnum.NAME));

			tableModel.setValueAt(0, rowIndex,
					columnIndex.get(ColumnEnum.QUANTITY));

			tableModel.setValueAt(itemStock.getUnit(), rowIndex,
					columnIndex.get(ColumnEnum.UNIT));

			tableModel.setValueAt(0, rowIndex,
					columnIndex.get(ColumnEnum.PRICE));

			tableModel.setValueAt(
					Formatter.formatNumberToString(itemStock.getBuyPrice()),
					rowIndex, columnIndex.get(ColumnEnum.LAST_PRICE));

			tableModel.setValueAt(0, rowIndex,
					columnIndex.get(ColumnEnum.TOTAL));

			reorderRowNumber();

			int row = table.getRowCount() - 1;
			int column = table.getSelectedColumn();
			table.requestFocus();
			table.changeSelection(row, column, false, false);
		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		NUM, CODE, NAME, QUANTITY, UNIT, PRICE, LAST_PRICE, TOTAL
	}
}
