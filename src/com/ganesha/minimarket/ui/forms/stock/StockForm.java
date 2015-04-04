package com.ganesha.minimarket.ui.forms.stock;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.exception.ActionTypeNotSupported;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
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
import com.ganesha.minimarket.facade.ItemFacade;
import com.ganesha.minimarket.facade.SaleConstraintFacade;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemSellPrice;
import com.ganesha.minimarket.model.ItemSellPricePK;
import com.ganesha.minimarket.model.SaleConstraintDetail;
import com.ganesha.minimarket.model.SaleConstraintHeader;
import com.ganesha.minimarket.ui.forms.commons.alerts.XCaptchaAlert;
import com.ganesha.minimarket.utils.BarcodeUtils;
import com.ganesha.minimarket.utils.PermissionConstants;

public class StockForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtSatuan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJLabel lblBarcode;
	private XJTextField txtBarcode;
	private ActionType actionType;
	private JSeparator separator;
	private XJPanel pnlKanan;
	private XJLabel lblJumlahSaatIni;
	private XJTextField txtJumlahSaatIni;
	private XJLabel lblHargaBeli;
	private XJTextField txtHargaBeli;
	private XJPanel pnlKode;
	private XJLabel lblHargaJual;
	private XJTextField txtHargaJual;
	private XJButton btnBatal;
	private XJLabel lblStokMinimum;
	private XJTextField txtStokMinimum;
	private XJButton btnGenerateBarcode;
	private XJButton btnHapusBarang;
	private XJPanel pnlDisable;
	private XJCheckBox chkDisabled;

	private Integer itemId;
	private JScrollPane scrollPane;
	private XJTable table;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJPanel pnlSellPrices;
	private XJButton btnTambah;
	private XJButton btnHapus;
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 100, false,
				"Harga Ke", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(1, 200,
				false, "Harga Jual (Rp)", false,
				XTableConstants.CELL_RENDERER_RIGHT, Double.class));
	}

	public StockForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Form Barang");
		setPermissionCode(PermissionConstants.STOCK_FORM);
		setCloseOnEsc(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockForm.this, ex);
				}
			}
		});
		getContentPane()
				.setLayout(
						new MigLayout("", "[500][300]",
								"[grow][grow][grow][10][grow]"));

		pnlKode = new XJPanel();
		pnlKode.setBorder(new XEtchedBorder());
		getContentPane().add(pnlKode, "cell 0 0 2 1,grow");
		pnlKode.setLayout(new MigLayout("", "[150][300][]", "[][]"));

		XJLabel lblKode = new XJLabel();
		pnlKode.add(lblKode, "cell 0 0");
		lblKode.setText("Kode");

		txtKode = new XJTextField();
		txtKode.setEditable(false);
		txtKode.setUpperCaseOnFocusLost(true);
		pnlKode.add(txtKode, "cell 1 0,growx");

		lblBarcode = new XJLabel();
		pnlKode.add(lblBarcode, "cell 0 1");
		lblBarcode.setText("Barcode [F8]");

		txtBarcode = new XJTextField();
		txtBarcode.setUpperCaseOnFocusLost(true);
		pnlKode.add(txtBarcode, "cell 1 1,growx");

		btnGenerateBarcode = new XJButton();
		btnGenerateBarcode.setMnemonic('G');
		btnGenerateBarcode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					setBarcode(BarcodeUtils.generateBarcode());
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockForm.this, ex);
				}
			}
		});
		btnGenerateBarcode.setText("Generate Barcode [Alt+G]");
		pnlKode.add(btnGenerateBarcode, "cell 2 1");

		XJPanel pnlKiri = new XJPanel();
		pnlKiri.setBorder(new XEtchedBorder());
		getContentPane().add(pnlKiri, "cell 0 1,grow");
		pnlKiri.setLayout(new MigLayout("", "[150][grow]", "[][][][][]"));

		XJLabel lblNama = new XJLabel();
		pnlKiri.add(lblNama, "cell 0 0");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		txtNama.setUpperCaseOnFocusLost(true);
		pnlKiri.add(txtNama, "cell 1 0,growx");

		XJLabel lblSatuan = new XJLabel();
		pnlKiri.add(lblSatuan, "cell 0 1");
		lblSatuan.setText("Satuan");

		txtSatuan = new XJTextField();
		txtSatuan.setUpperCaseOnFocusLost(true);
		pnlKiri.add(txtSatuan, "cell 1 1,growx");

		lblJumlahSaatIni = new XJLabel();
		pnlKiri.add(lblJumlahSaatIni, "cell 0 2");
		lblJumlahSaatIni.setText("Jumlah Saat Ini");

		txtJumlahSaatIni = new XJTextField();
		txtJumlahSaatIni.setEditable(false);
		pnlKiri.add(txtJumlahSaatIni, "cell 1 2,growx");

		lblStokMinimum = new XJLabel();
		lblStokMinimum.setText("Stok Minimum");
		pnlKiri.add(lblStokMinimum, "cell 0 3");

		txtStokMinimum = new XJTextField();
		txtStokMinimum
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		txtStokMinimum.setText("0");
		pnlKiri.add(txtStokMinimum, "cell 1 3,growx");

		lblHargaBeli = new XJLabel();
		pnlKiri.add(lblHargaBeli, "cell 0 4");
		lblHargaBeli.setText("Harga Beli");

		txtHargaBeli = new XJTextField();
		pnlKiri.add(txtHargaBeli, "cell 1 4,growx");
		txtHargaBeli.setEditable(false);

		pnlKanan = new XJPanel();
		pnlKanan.setBorder(new XEtchedBorder());
		getContentPane().add(pnlKanan, "cell 1 1,grow");
		pnlKanan.setLayout(new MigLayout("", "[grow]", "[][250]"));

		lblHargaJual = new XJLabel();
		lblHargaJual.setText("Harga Jual [F7]");
		pnlKanan.add(lblHargaJual, "cell 0 0");

		pnlSellPrices = new XJPanel();
		pnlKanan.add(pnlSellPrices, "cell 0 1,grow");
		pnlSellPrices.setLayout(new MigLayout("", "[]", "[][][][]"));

		txtHargaJual = new XJTextField();
		txtHargaJual.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlSellPrices.add(txtHargaJual, "cell 0 0,growx");
		txtHargaJual
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);

		btnTambah = new XJButton();
		btnTambah.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tambah();
			}
		});
		btnTambah.setText("Tambah [Enter]");
		pnlSellPrices.add(btnTambah, "cell 0 1,alignx right");

		scrollPane = new JScrollPane();
		pnlSellPrices.add(scrollPane, "flowx,cell 0 2");

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);

		scrollPane.setViewportView(table);

		btnHapus = new XJButton();
		btnHapus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hapus();
			}
		});
		btnHapus.setText("Hapus [Delete]");
		pnlSellPrices.add(btnHapus, "cell 0 3,alignx right,aligny bottom");

		pnlDisable = new XJPanel();
		getContentPane().add(pnlDisable, "cell 0 2 2 1,alignx right,growy");
		pnlDisable.setLayout(new MigLayout("", "[]", "[]"));

		chkDisabled = new XJCheckBox();
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_SMALLEST));
		chkDisabled.setText("Item ini sudah tidak aktif lagi");
		pnlDisable.add(chkDisabled, "cell 0 0");

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 3 2 1,grow");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 4 2 1,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[grow]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(false);
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockForm.this, ex);
				}
			}
		});

		btnBatal = new XJButton();
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBatal.setMnemonic('Q');
		btnBatal.setText("<html><center>Batal<br/>[Alt+Q]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0");

		btnHapusBarang = new XJButton();
		btnHapusBarang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockForm.this, ex);
				}
			}
		});
		btnHapusBarang
				.setText("<html><center>Hapus<br/>Barang</center></html>");
		pnlButton.add(btnHapusBarang, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		pack();
		setLocationRelativeTo(parent);
	}

	public Integer getIdBarang() {
		return itemId;
	}

	public void setBarcode(Object barcode) {
		txtBarcode.setText(barcode.toString());
	}

	public void setFormDetailValue(Item item) {
		itemId = item.getId();

		txtKode.setText(item.getCode());
		txtNama.setText(item.getName());
		txtBarcode.setText(item.getBarcode());
		txtSatuan.setText(item.getUnit());
		txtJumlahSaatIni.setText(Formatter.formatNumberToString(ItemFacade
				.getInstance().calculateStock(item)));
		txtStokMinimum.setText(Formatter.formatNumberToString(item
				.getMinimumStock()));
		txtHargaBeli.setText(Formatter.formatNumberToString(ItemFacade
				.getInstance().getLastBuyPrice(item)));

		List<ItemSellPrice> sellPrices = item.getSellPrices();
		XTableModel tableModel = (XTableModel) table.getModel();
		tableModel.setRowCount(sellPrices.size());

		for (int i = 0; i < sellPrices.size(); ++i) {
			ItemSellPrice sellPrice = sellPrices.get(i);
			tableModel.setValueAt(sellPrice.getSequence(), i, tableParameters
					.get(ColumnEnum.NUM).getColumnIndex());
			tableModel.setValueAt(Formatter.formatNumberToString(sellPrice
					.getPrimaryKey().getSellPrice()), i,
					tableParameters.get(ColumnEnum.PRICE).getColumnIndex());
		}

		chkDisabled.setSelected(item.getDisabled());

		btnSimpan
				.setText("<html><center>Simpan Perubahan<br/>[F12]</center></html>");
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F7:
			txtHargaJual.requestFocus();
			break;
		case KeyEvent.VK_F8:
			txtBarcode.requestFocus();
			break;
		case KeyEvent.VK_F12:
			btnSimpan.doClick();
			break;
		case KeyEvent.VK_ENTER:
			if (!txtHargaJual.isFocusOwner()) {
				return;
			}
			btnTambah.doClick();
			break;
		case KeyEvent.VK_DELETE:
			if (!table.isFocusOwner()) {
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
		dispose();
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
		reorderRowNumber();
	}

	private void initForm() throws ActionTypeNotSupported {
		String kodeTerakhir = DBUtils.getInstance().getLastValue("items",
				"code", String.class);
		int newCode = kodeTerakhir == null ? 1 : Formatter.formatCodeToInt(
				kodeTerakhir).intValue() + 1;
		String newCodeInString = Formatter.formatIntToCode(newCode);
		txtKode.setText(newCodeInString);

		if (actionType == ActionType.CREATE) {
			txtJumlahSaatIni.setText("0");
			txtHargaBeli.setText("0");
			txtHargaJual.setText("0");
			btnGenerateBarcode.setVisible(true);
		} else if (actionType == ActionType.UPDATE) {
			txtSatuan.setEditable(false);
			if (txtBarcode.getText().trim().equals("")) {
				txtBarcode.setEditable(true);
				btnGenerateBarcode.setVisible(true);
			} else {
				txtBarcode.setEditable(false);
				btnGenerateBarcode.setVisible(false);
			}
		} else if (actionType == ActionType.READ) {
			txtBarcode.setEditable(false);
			txtNama.setEditable(false);
			txtSatuan.setEditable(false);
			txtStokMinimum.setEditable(false);
			txtHargaJual.setEditable(false);
			btnSimpan.setEnabled(false);
			btnGenerateBarcode.setVisible(false);
		} else {
			throw new ActionTypeNotSupported(actionType);
		}
	}

	private void reorderRowNumber() {
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			table.setValueAt(i + 1, i, tableParameters.get(ColumnEnum.NUM)
					.getColumnIndex());
		}
	}

	private void save(boolean deleted) throws ActionTypeNotSupported,
			UserException {

		if (deleted) {
			String message = "Apakah Anda yakin ingin menghapus barang ini?";
			int selectedOption = XCaptchaAlert.showDialog(this, message,
					"Menghapus Barang");
			if (selectedOption != XCaptchaAlert.YES_OPTION) {
				return;
			}
		}

		tambah();
		validateForm(deleted);

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			ItemFacade facade = ItemFacade.getInstance();

			String code = txtKode.getText();
			String name = txtNama.getText();
			String barcode = txtBarcode.getText();
			String unit = txtSatuan.getText();
			BigDecimal buyPrice = BigDecimal
					.valueOf(Formatter.formatStringToNumber(
							txtHargaBeli.getText()).doubleValue());
			int minimumStock = Formatter.formatStringToNumber(
					txtStokMinimum.getText()).intValue();

			boolean disabled = chkDisabled.isSelected();

			List<ItemSellPrice> sellPrices = new ArrayList<>();
			int rowCount = table.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				BigDecimal sellPrice = BigDecimal.valueOf(Formatter
						.formatStringToNumber(
								table.getModel()
										.getValueAt(
												i,
												tableParameters.get(
														ColumnEnum.PRICE)
														.getColumnIndex())
										.toString()).doubleValue());

				Integer sequence = Formatter.formatStringToNumber(
						table.getModel()
								.getValueAt(
										i,
										tableParameters.get(ColumnEnum.NUM)
												.getColumnIndex()).toString())
						.intValue();

				ItemSellPricePK primaryKey = new ItemSellPricePK();
				primaryKey.setSellPrice(sellPrice);

				ItemSellPrice itemSellPrice = new ItemSellPrice();
				itemSellPrice.setPrimaryKey(primaryKey);
				itemSellPrice.setSequence(sequence);

				sellPrices.add(itemSellPrice);
			}

			Item item = null;
			if (actionType == ActionType.CREATE) {
				item = facade.addNewItem(code.toUpperCase(),
						name.toUpperCase(), barcode, unit, buyPrice,
						sellPrices, minimumStock, disabled, deleted, session);
				itemId = item.getId();
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				item = facade.updateExistingItem(itemId, name.toUpperCase(),
						barcode, unit, buyPrice, sellPrices, minimumStock,
						disabled, deleted, session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}

			ActivityLogFacade.doLog(getPermissionCode(), actionType,
					Main.getUserLogin(), item, session);
			session.getTransaction().commit();

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void tambah() {
		Double newSellPrice = Formatter.formatStringToNumber(
				txtHargaJual.getText()).doubleValue();
		if (newSellPrice == null || newSellPrice == 0) {
			return;
		}
		XTableModel tableModel = (XTableModel) table.getModel();
		tableModel.setRowCount(tableModel.getRowCount() + 1);
		int rowIndex = tableModel.getRowCount() - 1;
		tableModel.setValueAt(Formatter.formatNumberToString(newSellPrice),
				rowIndex, tableParameters.get(ColumnEnum.PRICE)
						.getColumnIndex());
		txtHargaJual.setValue(null);

		reorderRowNumber();
	}

	private void validateForm(boolean deleted) throws UserException {
		if (deleted) {
			Session session = HibernateUtils.openSession();
			try {
				SaleConstraintFacade facade = SaleConstraintFacade
						.getInstance();

				List<SaleConstraintHeader> headers = facade
						.getAllHeaders(session);
				for (SaleConstraintHeader header : headers) {
					List<SaleConstraintDetail> details = facade
							.getSaleConstraintDetails(header.getId(), session);
					for (SaleConstraintDetail detail : details) {
						if (itemId.equals(detail.getItemId())) {
							throw new UserException(
									"Tidak bisa menghapus barang karena masih dalam antrian di dalam Monitoring Constrain");
						}
					}
				}
			} finally {
				session.close();
			}
		} else {
			if (txtKode.getText().trim().equals("")) {
				throw new UserException("Kode Barang harus diisi");
			}
			if (txtNama.getText().trim().equals("")) {
				throw new UserException("Nama Barang harus diisi");
			}
			if (txtSatuan.getText().trim().equals("")) {
				throw new UserException("Satuan harus diisi");
			}
			if (chkDisabled.isSelected()) {
				int jumlahSaatIni = Formatter.formatStringToNumber(
						txtJumlahSaatIni.getText()).intValue();
				if (jumlahSaatIni > 0) {
					throw new UserException(
							"Tidak bisa menonaktifkan barang karena jumlah stok untuk barang ini masih ada sebanyak "
									+ jumlahSaatIni);
				}
			}
			if (!(table.getRowCount() > 0)) {
				throw new UserException("Harga Jual harus diisi");
			}
		}
	}

	private enum ColumnEnum {
		NUM, PRICE
	}
}
