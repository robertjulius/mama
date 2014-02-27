package com.ganesha.minimarket.ui.forms.returns;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
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
import com.ganesha.minimarket.facade.PurchaseFacade;
import com.ganesha.minimarket.facade.PurchaseReturnFacade;
import com.ganesha.minimarket.model.PurchaseDetail;
import com.ganesha.minimarket.model.PurchaseHeader;
import com.ganesha.minimarket.model.PurchaseReturnDetail;
import com.ganesha.minimarket.model.PurchaseReturnHeader;
import com.ganesha.minimarket.model.Supplier;
import com.ganesha.minimarket.ui.forms.searchentity.SearchEntityDialog;
import com.ganesha.minimarket.utils.PermissionConstants;

public class ReturPembelianForm extends XJDialog {

	private XJTable table;

	private static final long serialVersionUID = 1401014426195840845L;
	private XJTextField txtNoTransaksi;
	private XJDateChooser dateChooser;
	private XJTextField txtKodeSupplier;
	private XJButton btnTambah;
	private XJButton btnCariSupplier;
	private XJButton btnHapus;
	private XJTextField txtBebanLain;
	private XJTextField txtKembalikanDiskon;
	private XJTextField txtTotalRetur;
	private XJTextField txtUangDiterima;
	private XJTextField txtSisa;
	private XJLabel lblLunas;
	private XJTextField txtTotal;
	private XJButton btnBatal;
	private XJButton btnSelesai;
	private XJLabel lblSisa;
	private XJTextField txtNamaSupplier;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJTextField txtPotongHutang;
	private XJLabel lblUangDiterima;
	private Integer supplierId;

	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 5, false,
				"No", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.TRANSACTION_NUM, new XTableParameter(1,
				200, false, "No. Transaksi", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.DATE, new XTableParameter(2, 75, false,
				"Tanggal", false, XTableConstants.CELL_RENDERER_LEFT,
				Date.class));

		tableParameters.put(ColumnEnum.ITEM_CODE, new XTableParameter(3, 75,
				false, "Kode Barang", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.ITEM_NAME, new XTableParameter(4, 300,
				false, "Nama Barang", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(5, 10,
				true, "Qty", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(6, 30, false,
				"Satuan", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(7, 50, true,
				"Harga", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.TOTAL, new XTableParameter(8, 50, false,
				"Total", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.PURCHASE_DETAIL_ID, new XTableParameter(
				9, 0, false, "Purchase Detail ID", true,
				XTableConstants.CELL_RENDERER_LEFT, Integer.class));

	}

	public ReturPembelianForm(Window parent) {
		super(parent);
		setTitle("Retur Pembelian");
		setPermissionCode(PermissionConstants.PURRTN_FORM);
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
					} else if (column == tableParameters.get(ColumnEnum.PRICE)
							.getColumnIndex()) {
						setTotalPerRow(row);
					}
				}
			}
		});

		XJPanel pnlHeader = new XJPanel();
		pnlHeader.setBorder(new XEtchedBorder());
		getContentPane().add(pnlHeader, "cell 0 0,alignx left,growy");
		pnlHeader.setLayout(new MigLayout("", "[150][100][200][]", "[][][]"));

		XJLabel lblNoTransaksi = new XJLabel();
		lblNoTransaksi.setText("No. Retur Pembelian");
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

		XJPanel pnlPembelian = new XJPanel();
		getContentPane().add(pnlPembelian, "cell 0 1,grow");
		pnlPembelian.setLayout(new MigLayout("", "[612px,grow]",
				"[grow][::200,baseline]"));

		XJPanel pnlSearchItem = new XJPanel();
		pnlPembelian.add(pnlSearchItem, "cell 0 0,grow");
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
		pnlPembelian.add(scrollPane, "cell 0 1,growx");

		XJPanel pnlSubTotal = new XJPanel();
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

		XJPanel pnlPerhitungan = new XJPanel();
		getContentPane().add(pnlPerhitungan, "cell 0 3,alignx left,growy");
		pnlPerhitungan.setLayout(new MigLayout("", "[grow][grow][grow]",
				"[grow]"));

		XJPanel pnlBeban = new XJPanel();
		pnlBeban.setBorder(new XEtchedBorder());
		pnlPerhitungan.add(pnlBeban, "cell 0 0,grow");
		pnlBeban.setLayout(new MigLayout("", "[][200]", "[][][10][]"));

		XJLabel lblBiaya = new XJLabel();
		lblBiaya.setText("(-) Biaya Lain-Lain");
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

		XJLabel lblKembalikanDiskon = new XJLabel();
		lblKembalikanDiskon.setText("(-) Kembalikan Diskon");
		pnlBeban.add(lblKembalikanDiskon, "cell 0 1");

		txtKembalikanDiskon = new XJTextField();
		txtKembalikanDiskon.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalBiayaDanDiskon();
			}
		});
		txtKembalikanDiskon.setHorizontalAlignment(SwingConstants.TRAILING);
		txtKembalikanDiskon.setText("0");
		pnlBeban.add(txtKembalikanDiskon, "cell 1 1,growx");

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

		XJPanel pnlUangDiterima = new XJPanel();
		pnlUangDiterima.setBorder(new XEtchedBorder());
		pnlPerhitungan.add(pnlUangDiterima, "cell 1 0,grow");
		pnlUangDiterima.setLayout(new MigLayout("", "[150][200,grow]",
				"[][][10][]"));

		lblUangDiterima = new XJLabel();
		lblUangDiterima.setText("Uang yang Diterima");
		pnlUangDiterima.add(lblUangDiterima, "cell 0 0");

		txtUangDiterima = new XJTextField();
		txtUangDiterima.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalTerimaDanPiutang();
			}
		});
		txtUangDiterima.setHorizontalAlignment(SwingConstants.TRAILING);
		txtUangDiterima.setText("0");
		pnlUangDiterima.add(txtUangDiterima, "cell 1 0,growx,aligny top");

		XJLabel lblPotongHutang = new XJLabel();
		lblPotongHutang.setText("Potong dari Hutang");
		pnlUangDiterima.add(lblPotongHutang, "cell 0 1");

		txtPotongHutang = new XJTextField();
		txtPotongHutang.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalTerimaDanPiutang();
			}
		});
		txtPotongHutang.setHorizontalAlignment(SwingConstants.TRAILING);
		txtPotongHutang.setText("0");
		pnlUangDiterima.add(txtPotongHutang, "cell 1 1,growx");

		JSeparator separator_2 = new JSeparator();
		pnlUangDiterima.add(separator_2, "cell 0 2 2 1,growx,aligny center");

		lblSisa = new XJLabel();
		lblSisa.setText("Sisa");
		pnlUangDiterima.add(lblSisa, "cell 0 3");

		txtSisa = new XJTextField();
		txtSisa.setHorizontalAlignment(SwingConstants.TRAILING);
		txtSisa.setText("0");
		txtSisa.setEditable(false);
		pnlUangDiterima.add(txtSisa, "cell 1 3,growx,aligny top");

		XJPanel pnlPiutang = new XJPanel();
		pnlPerhitungan.add(pnlPiutang, "cell 2 0,growx,aligny center");
		pnlPiutang.setLayout(new MigLayout("", "[][]", "[]"));

		lblLunas = new XJLabel();
		pnlPiutang.add(lblLunas, "cell 0 0");
		lblLunas.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblLunas.setText("LUNAS");
		lblLunas.setVisible(false);

		JSeparator separator_1 = new JSeparator();
		getContentPane().add(separator_1, "cell 0 4,grow");

		XJPanel pnlButton = new XJPanel();
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
					ExceptionHandler.handleException(ReturPembelianForm.this,
							ex);
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
		String message = "Apakah Anda yakin ingin membatalkan retur pembelian ini?";
		int selectedOption = JOptionPane.showConfirmDialog(this, message,
				"Membatalkan Retur Pembelian", JOptionPane.YES_NO_OPTION);
		if (selectedOption == JOptionPane.YES_OPTION) {
			dispose();
		}
	}

	private void cariSupplier() {
		SearchEntityDialog searchEntityDialog = new SearchEntityDialog(
				"Cari Supplier", this, Supplier.class);
		searchEntityDialog.setVisible(true);

		supplierId = searchEntityDialog.getSelectedId();
		if (supplierId != null) {
			String kodeSupplier = searchEntityDialog.getSelectedCode();
			String namaSupplier = searchEntityDialog.getSelectedName();
			txtKodeSupplier.setText(kodeSupplier);
			txtNamaSupplier.setText(namaSupplier);
		}
	}

	private void cariTransaksi() {
		SearchTransactionDialog searchEntityDialog = new SearchTransactionDialog(
				"Cari Daftar Transaksi Pembelian", this,
				PurchaseFacade.getInstance());
		searchEntityDialog.setVisible(true);

		Integer purchaseDetailId = searchEntityDialog
				.getSelectedTransactionDetailId();
		if (purchaseDetailId != null) {
			tambah(purchaseDetailId);
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

			PurchaseReturnFacade facade = PurchaseReturnFacade.getInstance();

			String transactionNumber = txtNoTransaksi.getText();

			Timestamp transactionTimestamp = CommonUtils
					.castDateToTimestamp(dateChooser.getDate());

			double subTotalAmount = Formatter.formatStringToNumber(
					txtTotalRetur.getText()).doubleValue();

			double expenses = Formatter.formatStringToNumber(
					txtBebanLain.getText()).doubleValue();

			double discountReturned = Formatter.formatStringToNumber(
					txtKembalikanDiskon.getText()).doubleValue();

			double totalReturnAmount = Formatter.formatStringToNumber(
					txtTotal.getText()).doubleValue();

			double amountReturned = Formatter.formatStringToNumber(
					txtUangDiterima.getText()).doubleValue();

			double debtCut = Formatter.formatStringToNumber(
					txtPotongHutang.getText()).doubleValue();

			double remainingReturnAmount = Formatter.formatStringToNumber(
					txtSisa.getText()).doubleValue();

			boolean returnedInFullFlag = !(remainingReturnAmount > 0);

			PurchaseReturnHeader purchaseReturnHeader = facade.validateForm(
					transactionNumber, transactionTimestamp, supplierId,
					subTotalAmount, expenses, discountReturned,
					totalReturnAmount, amountReturned, debtCut,
					remainingReturnAmount, returnedInFullFlag, session);

			List<PurchaseReturnDetail> purchaseReturnDetails = new ArrayList<>();
			int rowCount = table.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				PurchaseReturnDetail purchaseReturnDetail = new PurchaseReturnDetail();

				Integer purchaseDetailId = Formatter.formatStringToNumber(
						table.getModel()
								.getValueAt(
										i,
										tableParameters.get(
												ColumnEnum.PURCHASE_DETAIL_ID)
												.getColumnIndex()).toString())
						.intValue();

				PurchaseDetail purchaseDetail = PurchaseFacade.getInstance()
						.getDetail(purchaseDetailId, session);

				purchaseReturnDetail.setOrderNum(Formatter
						.formatStringToNumber(
								table.getModel()
										.getValueAt(
												i,
												tableParameters.get(
														ColumnEnum.NUM)
														.getColumnIndex())
										.toString()).intValue());

				purchaseReturnDetail.setPurchaseDetail(purchaseDetail);

				purchaseReturnDetail.setQuantity(Formatter
						.formatStringToNumber(
								table.getModel()
										.getValueAt(
												i,
												tableParameters.get(
														ColumnEnum.QUANTITY)
														.getColumnIndex())
										.toString()).intValue());

				purchaseReturnDetail.setTotalAmount(BigDecimal
						.valueOf(Formatter.formatStringToNumber(
								table.getModel()
										.getValueAt(
												i,
												tableParameters.get(
														ColumnEnum.TOTAL)
														.getColumnIndex())
										.toString()).doubleValue()));

				purchaseReturnDetails.add(purchaseReturnDetail);
			}

			facade.performPurchaseReturn(purchaseReturnHeader,
					purchaseReturnDetails, session);

			ActivityLogFacade.doLog(getPermissionCode(),
					ActionType.TRANSACTION, Main.getUserLogin(),
					purchaseReturnHeader, session);
			session.getTransaction().commit();

			dispose();

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void setTotalBiayaDanDiskon() {
		double totalRetur = Formatter.formatStringToNumber(
				txtTotalRetur.getText()).doubleValue();

		double biaya = Formatter.formatStringToNumber(txtBebanLain.getText())
				.doubleValue();
		txtBebanLain.setText(Formatter.formatNumberToString(biaya));

		double diskonDikembalikan = Formatter.formatStringToNumber(
				txtKembalikanDiskon.getText()).doubleValue();
		txtKembalikanDiskon.setText(Formatter
				.formatNumberToString(diskonDikembalikan));

		double total = totalRetur - biaya - diskonDikembalikan;
		txtTotal.setText(Formatter.formatNumberToString(total));

		setTotalTerimaDanPiutang();
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

		double total = jumlah * hargaSatuan;
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
		txtTotalRetur.setText(Formatter.formatNumberToString(totalRetur));

		setTotalBiayaDanDiskon();
	}

	private void setTotalTerimaDanPiutang() {
		double total = Formatter.formatStringToNumber(txtTotal.getText())
				.doubleValue();

		double uangDiterima = Formatter.formatStringToNumber(
				txtUangDiterima.getText()).doubleValue();
		txtUangDiterima.setText(Formatter.formatNumberToString(uangDiterima));

		double potongHutang = Formatter.formatStringToNumber(
				txtPotongHutang.getText()).doubleValue();
		txtPotongHutang.setText(Formatter.formatNumberToString(potongHutang));

		double jumlahYangSudahDibereskan = uangDiterima + potongHutang;

		double sisa = total - jumlahYangSudahDibereskan;
		txtSisa.setText(Formatter.formatNumberToString(sisa));

		if (jumlahYangSudahDibereskan > total) {
			btnSelesai.setEnabled(false);
			lblLunas.setVisible(false);
		} else {
			btnSelesai.setEnabled(true);
			lblLunas.setVisible(true);
			if (jumlahYangSudahDibereskan < total) {
				lblSisa.setForeground(LBL_WARNING);
				lblLunas.setText("PIUTANG");
				lblLunas.setForeground(LBL_WARNING);
			} else {
				lblSisa.setForeground(LBL_NORMAL);
				lblLunas.setText("LUNAS");
				lblLunas.setForeground(COLOR_GOOD);
			}
		}
	}

	private void tambah(Integer purchaseDetailId) {
		if (purchaseDetailId == null) {
			return;
		}

		Session session = HibernateUtils.openSession();
		try {
			PurchaseFacade facade = PurchaseFacade.getInstance();
			PurchaseDetail purchaseDetail = facade.getDetail(purchaseDetailId,
					session);
			PurchaseHeader purchaseHeader = purchaseDetail.getPurchaseHeader();

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(tableModel.getRowCount() + 1);
			int rowIndex = tableModel.getRowCount() - 1;

			tableModel.setValueAt(purchaseHeader.getTransactionNumber(),
					rowIndex, tableParameters.get(ColumnEnum.TRANSACTION_NUM)
							.getColumnIndex());

			tableModel.setValueAt(Formatter.formatDateToString(purchaseHeader
					.getTransactionTimestamp()), rowIndex,
					tableParameters.get(ColumnEnum.DATE).getColumnIndex());

			tableModel.setValueAt(purchaseDetail.getItemCode(), rowIndex,
					tableParameters.get(ColumnEnum.ITEM_CODE).getColumnIndex());

			tableModel.setValueAt(purchaseDetail.getItemName(), rowIndex,
					tableParameters.get(ColumnEnum.ITEM_NAME).getColumnIndex());

			tableModel.setValueAt(purchaseDetail.getQuantity(), rowIndex,
					tableParameters.get(ColumnEnum.QUANTITY).getColumnIndex());

			tableModel.setValueAt(purchaseDetail.getUnit(), rowIndex,
					tableParameters.get(ColumnEnum.UNIT).getColumnIndex());

			tableModel.setValueAt(Formatter.formatNumberToString(purchaseDetail
					.getPricePerUnit()), rowIndex,
					tableParameters.get(ColumnEnum.PRICE).getColumnIndex());

			tableModel.setValueAt(Formatter.formatNumberToString(purchaseDetail
					.getTotalAmount()), rowIndex,
					tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());

			tableModel.setValueAt(purchaseDetail.getId(), rowIndex,
					tableParameters.get(ColumnEnum.PURCHASE_DETAIL_ID)
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
		NUM, TRANSACTION_NUM, DATE, ITEM_CODE, ITEM_NAME, QUANTITY, UNIT, PRICE, TOTAL, PURCHASE_DETAIL_ID
	}
}
