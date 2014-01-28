package com.ganesha.minimarket.ui.forms.purchase;

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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
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
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.PurchaseFacade;
import com.ganesha.minimarket.facade.StockFacade;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemStock;
import com.ganesha.minimarket.model.PurchaseDetail;
import com.ganesha.minimarket.model.PurchaseHeader;
import com.ganesha.minimarket.model.Supplier;
import com.ganesha.minimarket.ui.forms.searchentity.SearchEntityDialog;
import com.ganesha.minimarket.ui.forms.stock.StockForm;

public class PembelianForm extends XJDialog {

	private XJTable table;

	private static final long serialVersionUID = 1401014426195840845L;
	private XJTextField txtNoTransaksi;
	private XJDateChooser dateChooser;
	private XJTextField txtKodeSupplier;
	private XJButton btnCariBarang;
	private XJButton btnCariSupplier;
	private XJButton btnHapus;
	private XJTextField txtBebanLain;
	private XJTextField txtDiskon;
	private XJTextField txtTotalPembelian;
	private XJTextField txtBayar;
	private XJTextField txtSisa;
	private XJLabel lblLunas;
	private XJTextField txtTotal;
	private XJButton btnBatal;
	private XJButton btnSelesai;
	private XJLabel lblSisa;
	private XJTextField txtNamaSupplier;
	private XJButton btnRegistrasi;
	private XJButton btnDetail;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJLabel lblBayar;
	private XJTextField txtBarcode;
	private JPanel pnlBarcode;
	private XJLabel lblBarcode;
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 5, false,
				"No", XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.CODE, new XTableParameter(1, 50, false,
				"Kode", XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.NAME,
				new XTableParameter(2, 300, false, "Nama Barang",
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.QUANTITY,
				new XTableParameter(3, 10, true, "Qty",
						XTableConstants.CELL_RENDERER_RIGHT, Integer.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(4, 50, false,
				"Satuan", XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(5, 75, true,
				"Harga Satuan", XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.LAST_PRICE, new XTableParameter(6, 75,
				false, "Harga Terakhir", XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.TOTAL, new XTableParameter(7, 75, false,
				"Total", XTableConstants.CELL_RENDERER_RIGHT, Double.class));
	}

	public PembelianForm(Window parent) {
		super(parent);
		setTitle("Transaksi Pembelian");
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]",
						"[grow][grow][grow][grow][][grow]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnDetail.doClick();
			}
		};
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
					} else if (column == tableParameters.get(ColumnEnum.PRICE)
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
		lblNoTransaksi.setText("No. Transaksi Pembelian");
		pnlHeader.add(lblNoTransaksi, "cell 0 0");

		txtNoTransaksi = new XJTextField();
		txtNoTransaksi.setText(GeneralConstants.PREFIX_TRX_NUMBER_PURCHASE
				+ CommonUtils.getTimestampInString());
		txtNoTransaksi.setEditable(false);
		pnlHeader.add(txtNoTransaksi, "cell 1 0 2 1,growx");

		XJLabel lblTanggal = new XJLabel();
		lblTanggal.setText("Tanggal Pembelian");
		pnlHeader.add(lblTanggal, "cell 0 1");

		dateChooser = new XJDateChooser();
		dateChooser.setDate(CommonUtils.getCurrentDate());
		dateChooser.getCalendarButton().setMnemonic('T');
		pnlHeader.add(dateChooser, "cell 1 1 2 1,grow");

		XJLabel lblSupplier = new XJLabel();
		lblSupplier.setText("Supplier");
		pnlHeader.add(lblSupplier, "cell 0 2");

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
		pnlSearchItem.setLayout(new MigLayout("", "[][][grow][]", "[][grow]"));

		btnCariBarang = new XJButton();
		btnCariBarang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cariBarang();
			}
		});
		pnlSearchItem.add(btnCariBarang, "cell 0 0,alignx left");
		btnCariBarang
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

		pnlBarcode = new JPanel();
		pnlSearchItem.add(pnlBarcode, "cell 0 1 2 1,grow");
		pnlBarcode.setLayout(new MigLayout("", "[][grow]", "[]"));

		lblBarcode = new XJLabel();
		lblBarcode.setText("Barcode [F8]");
		pnlBarcode.add(lblBarcode, "cell 0 0,alignx trailing");

		txtBarcode = new XJTextField();
		pnlBarcode.add(txtBarcode, "cell 1 0,growx");
		txtBarcode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cariBarcode();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PembelianForm.this, ex);
				}
			}
		});

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
		lblBiaya.setText("Biaya Lain-Lain (+)");
		pnlBeban.add(lblBiaya, "cell 0 0");

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
		lblDiskon.setText("Diskon (-)");
		pnlBeban.add(lblDiskon, "cell 0 1");

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
				setTotalBayarDanHutang();
			}
		});
		txtBayar.setHorizontalAlignment(SwingConstants.TRAILING);
		txtBayar.setText("0");
		pnlUangMuka.add(txtBayar, "cell 1 0,growx,aligny top");

		lblSisa = new XJLabel();
		lblSisa.setText("Sisa");
		pnlUangMuka.add(lblSisa, "cell 0 1");

		txtSisa = new XJTextField();
		txtSisa.setHorizontalAlignment(SwingConstants.TRAILING);
		txtSisa.setText("0");
		txtSisa.setEditable(false);
		pnlUangMuka.add(txtSisa, "cell 1 1,growx,aligny top");

		lblLunas = new XJLabel();
		pnlUangMuka.add(lblLunas, "cell 0 2 2 1,alignx center");

		lblLunas.setForeground(COLOR_GOOD);
		lblLunas.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblLunas.setText("LUNAS");
		lblLunas.setVisible(false);

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
					ExceptionHandler.handleException(PembelianForm.this, ex);
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
			btnCariSupplier.doClick();
			break;
		case KeyEvent.VK_F6:
			btnCariBarang.doClick();
			break;
		case KeyEvent.VK_F7:
			btnRegistrasi.doClick();
			break;
		case KeyEvent.VK_F8:
			txtBarcode.setText("");
			txtBarcode.requestFocus();
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

	private void cariBarcode() throws UserException {
		String barcode = txtBarcode.getText();
		if (barcode == null || barcode.trim().equals("")) {
			return;
		}
		Session session = HibernateUtils.openSession();
		try {
			Item item = StockFacade.getInstance()
					.getByBarcode(barcode, session);

			if (item == null) {
				throw new UserException("Barang dengan barcode " + barcode
						+ " tidak ditemukan.");
			}
			String kodeBarang = item.getCode();
			tambah(kodeBarang);
			txtBarcode.setText("");

		} finally {
			session.close();
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
		setTotalPembelian();
		reorderRowNumber();
	}

	private void registrasiBarangBaru() {
		StockForm stockForm = new StockForm(this, ActionType.CREATE);
		stockForm.setBarcode(txtBarcode.getText());
		stockForm.setVisible(true);
		String kodeBarang = stockForm.getKodeBarang();
		tambah(kodeBarang);
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
					txtBayar.getText()).doubleValue();

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

				purchaseDetail.setOrderNum(Formatter.formatStringToNumber(
						table.getValueAt(
								i,
								tableParameters.get(ColumnEnum.NUM)
										.getColumnIndex()).toString())
						.intValue());

				purchaseDetail.setItemCode(table.getValueAt(i,
						tableParameters.get(ColumnEnum.CODE).getColumnIndex())
						.toString());

				purchaseDetail.setItemName(table.getValueAt(i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex())
						.toString());

				purchaseDetail.setQuantity(Formatter.formatStringToNumber(
						table.getValueAt(
								i,
								tableParameters.get(ColumnEnum.QUANTITY)
										.getColumnIndex()).toString())
						.intValue());

				purchaseDetail.setUnit(table.getValueAt(i,
						tableParameters.get(ColumnEnum.UNIT).getColumnIndex())
						.toString());

				purchaseDetail.setPricePerUnit(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getValueAt(
										i,
										tableParameters.get(ColumnEnum.PRICE)
												.getColumnIndex()).toString())
						.doubleValue()));

				purchaseDetail.setTotalAmount(BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getValueAt(
										i,
										tableParameters.get(ColumnEnum.TOTAL)
												.getColumnIndex()).toString())
						.doubleValue()));

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

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void setTotalBayarDanHutang() {
		double total = Formatter.formatStringToNumber(txtTotal.getText())
				.doubleValue();

		double uangMuka = Formatter.formatStringToNumber(txtBayar.getText())
				.doubleValue();
		txtBayar.setText(Formatter.formatNumberToString(uangMuka));

		double sisa = total - uangMuka;
		txtSisa.setText(Formatter.formatNumberToString(sisa));

		if (uangMuka > total) {
			btnSelesai.setEnabled(false);
			lblLunas.setVisible(false);
		} else {
			btnSelesai.setEnabled(true);
			lblLunas.setVisible(true);
			if (uangMuka < total) {
				lblSisa.setForeground(COLOR_WARNING);
				txtSisa.setForeground(COLOR_WARNING);
				lblLunas.setText("HUTANG");
				lblLunas.setForeground(COLOR_WARNING);
			} else {
				lblSisa.setForeground(COLOR_NORMAL_FOREGROUND);
				txtSisa.setForeground(COLOR_NORMAL_FOREGROUND);
				lblLunas.setText("LUNAS");
				lblLunas.setForeground(COLOR_GOOD);
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

		setTotalBayarDanHutang();
	}

	private void setTotalPembelian() {
		int rowCount = table.getRowCount();
		double totalPembelian = 0;
		for (int i = 0; i < rowCount; ++i) {
			String string = table.getValueAt(i,
					tableParameters.get(ColumnEnum.TOTAL).getColumnIndex())
					.toString();
			double totalPerRow = Formatter.formatStringToNumber(string)
					.doubleValue();
			totalPembelian += totalPerRow;
		}
		txtTotalPembelian.setText(Formatter
				.formatNumberToString(totalPembelian));

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

		double total = jumlah * hargaSatuan;
		table.setValueAt(Formatter.formatNumberToString(total), row,
				tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());

		setTotalPembelian();
	}

	private void showDetail() {
		Session session = HibernateUtils.openSession();
		try {
			boolean isFocus = table.isFocusOwner();
			if (!isFocus) {
				return;
			}

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

			tableModel.setValueAt(item.getCode(), rowIndex, tableParameters
					.get(ColumnEnum.CODE).getColumnIndex());

			tableModel.setValueAt(item.getName(), rowIndex, tableParameters
					.get(ColumnEnum.NAME).getColumnIndex());

			tableModel.setValueAt(0, rowIndex,
					tableParameters.get(ColumnEnum.QUANTITY).getColumnIndex());

			tableModel.setValueAt(itemStock.getUnit(), rowIndex,
					tableParameters.get(ColumnEnum.UNIT).getColumnIndex());

			tableModel.setValueAt(0, rowIndex,
					tableParameters.get(ColumnEnum.PRICE).getColumnIndex());

			tableModel
					.setValueAt(Formatter.formatNumberToString(itemStock
							.getBuyPrice()), rowIndex,
							tableParameters.get(ColumnEnum.LAST_PRICE)
									.getColumnIndex());

			tableModel.setValueAt(0, rowIndex,
					tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());

			reorderRowNumber();

			int row = table.getRowCount() - 1;
			table.requestFocus();
			table.changeSelection(row, tableParameters.get(ColumnEnum.QUANTITY)
					.getColumnIndex(), false, false);

			setTotalPembelian();

		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		NUM, CODE, NAME, QUANTITY, UNIT, PRICE, LAST_PRICE, TOTAL
	}
}
