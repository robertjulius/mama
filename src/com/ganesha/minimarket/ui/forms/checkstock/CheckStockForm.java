package com.ganesha.minimarket.ui.forms.checkstock;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
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
import com.ganesha.minimarket.facade.ItemFacade;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemSellPrice;
import com.ganesha.minimarket.utils.PermissionConstants;

import net.miginfocom.swing.MigLayout;

public class CheckStockForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;
	private XJTextField txtSatuan;
	private XJTextField txtNama;
	private XJLabel lblBarcode;
	private XJTextField txtBarcode;
	private XJPanel pnlKanan;
	private XJLabel lblStock;
	private XJTextField txtJumlahSaatIni;
	private XJButton btnBatal;
	private XJButton btnCek;

	private JScrollPane scrollPane;
	private XJTable table;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();

	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 100, false, "Harga Ke", false,
				XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(1, 200, false, "Harga Jual (Rp)", false,
				XTableConstants.CELL_RENDERER_RIGHT, Double.class));
	}

	public CheckStockForm(Window parent) {
		super(parent);
		setTitle("Form Barang");
		setPermissionCode(PermissionConstants.CHECK_STOCK_FORM);
		setCloseOnEsc(true);
		getContentPane().setLayout(new MigLayout("", "[500][]", "[grow][grow]"));

		XJPanel pnlKiri = new XJPanel();
		pnlKiri.setBorder(new XEtchedBorder());
		getContentPane().add(pnlKiri, "cell 0 0,grow");
		pnlKiri.setLayout(new MigLayout("", "[150][grow]", "[][][50px][][][][]"));

		lblBarcode = new XJLabel();
		pnlKiri.add(lblBarcode, "cell 0 0");
		lblBarcode.setText("Barcode [F8]");

		txtBarcode = new XJTextField();
		pnlKiri.add(txtBarcode, "cell 1 0,growx");
		txtBarcode.setUpperCaseOnFocusLost(true);

		btnCek = new XJButton();
		btnCek.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					checkStockAndSellingPrice();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CheckStockForm.this, ex);
				}
			}
		});
		btnCek.setText("<html><center>Cek<br/>[Enter]</center></html?");
		pnlKiri.add(btnCek, "cell 1 1,alignx right");

		XJLabel lblNama = new XJLabel();
		pnlKiri.add(lblNama, "cell 0 3");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		txtNama.setEditable(false);
		txtNama.setUpperCaseOnFocusLost(true);
		pnlKiri.add(txtNama, "cell 1 3,growx");

		XJLabel lblSatuan = new XJLabel();
		pnlKiri.add(lblSatuan, "cell 0 4");
		lblSatuan.setText("Satuan");

		txtSatuan = new XJTextField();
		txtSatuan.setEditable(false);
		txtSatuan.setUpperCaseOnFocusLost(true);
		pnlKiri.add(txtSatuan, "cell 1 4,growx");

		lblStock = new XJLabel();
		pnlKiri.add(lblStock, "cell 0 5");
		lblStock.setText("Stok Saat Ini");

		txtJumlahSaatIni = new XJTextField();
		txtJumlahSaatIni.setEditable(false);
		pnlKiri.add(txtJumlahSaatIni, "cell 1 5,growx");

		pnlKanan = new XJPanel();
		getContentPane().add(pnlKanan, "cell 1 0,growy");
		pnlKanan.setBorder(new XEtchedBorder());
		pnlKanan.setLayout(new MigLayout("", "[300px]", "[50px,grow,baseline]"));

		scrollPane = new JScrollPane();
		pnlKanan.add(scrollPane, "cell 0 0");

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);

		scrollPane.setViewportView(table);

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1 2 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[]", "[grow]"));

		btnBatal = new XJButton();
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBatal.setText("<html><center>Selesai<br/>[Esc]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0");

		pack();
		setLocationRelativeTo(parent);
	}

	private void checkStockAndSellingPrice() throws UserException {
		Session session = HibernateUtils.openSession();
		try {

			String barcode = txtBarcode.getText().trim();
			if (barcode.trim().equals("")) {
				throw new UserException("Barcode belum diisi");
			}

			Item item = ItemFacade.getInstance().getByBarcode(barcode, session);
			if (item == null) {
				throw new UserException("Barcode '" + barcode + "' tidak terdaftar");
			}

			txtNama.setText(item.getName());
			txtBarcode.setText(item.getBarcode());
			txtSatuan.setText(item.getUnit());
			txtJumlahSaatIni.setText(Formatter.formatNumberToString(ItemFacade.getInstance().calculateStock(item)));

			List<ItemSellPrice> sellPrices = item.getSellPrices();
			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(sellPrices.size());

			for (int i = 0; i < sellPrices.size(); ++i) {
				ItemSellPrice sellPrice = sellPrices.get(i);
				tableModel.setValueAt(sellPrice.getSequence(), i, tableParameters.get(ColumnEnum.NUM).getColumnIndex());
				tableModel.setValueAt(Formatter.formatNumberToString(sellPrice.getPrimaryKey().getSellPrice()), i,
						tableParameters.get(ColumnEnum.PRICE).getColumnIndex());
			}

			reorderRowNumber();

			txtBarcode.requestFocus();
			txtBarcode.selectAll();

		} finally {
			session.close();
		}
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			if (txtBarcode.isFocusOwner() && !txtBarcode.getText().trim().isEmpty()) {
				btnCek.doClick();
			}
			break;
		default:
			break;
		}
	}

	private void batal() {
		dispose();
	}

	private void reorderRowNumber() {
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			table.setValueAt(i + 1, i, tableParameters.get(ColumnEnum.NUM).getColumnIndex());
		}
	}

	private enum ColumnEnum {
		NUM, PRICE
	}
}
