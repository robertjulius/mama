package com.ganesha.accounting.minimarket.ui.forms.forms.stock;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Good;
import com.ganesha.accounting.minimarket.model.GoodStock;
import com.ganesha.accounting.util.DBUtils;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTextField;

public class StockForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;
	private static final String BUTTON_SAVE_LABEL_UPDATE = "Simpan Perubahan";

	private XJButton btnSimpan;
	private XJTextField txtSatuan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJLabel lblKodeTerakhirValue;
	private XJLabel lblBarcode;
	private XJTextField txtBarcode;
	private XJLabel lblKodeTerakhir;

	public StockForm(Window parent) {
		super(parent);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		setTitle("Form Barang");
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		JPanel pnlKodeBarang = new JPanel();
		getContentPane().add(pnlKodeBarang, "cell 0 0,grow");
		pnlKodeBarang.setLayout(new MigLayout("",
				"[100][200:n,grow][200,grow][50:n]", "[][][][]"));

		XJLabel lblKode = new XJLabel();
		pnlKodeBarang.add(lblKode, "cell 0 0,alignx right");
		lblKode.setText("Kode");

		txtKode = new XJTextField();
		pnlKodeBarang.add(txtKode, "cell 1 0,growx");

		lblKodeTerakhir = new XJLabel();
		lblKodeTerakhir.setText("Kode Terakhir:");
		pnlKodeBarang.add(lblKodeTerakhir, "cell 2 0,alignx right");

		lblKodeTerakhirValue = new XJLabel();
		lblKodeTerakhirValue.setText("");
		pnlKodeBarang.add(lblKodeTerakhirValue, "cell 3 0");

		XJLabel lblNama = new XJLabel();
		pnlKodeBarang.add(lblNama, "cell 0 1,alignx trailing");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		pnlKodeBarang.add(txtNama, "cell 1 1 3 1,growx");

		lblBarcode = new XJLabel();
		lblBarcode.setText("Barcode");
		pnlKodeBarang.add(lblBarcode, "cell 0 2,alignx trailing");

		txtBarcode = new XJTextField();
		txtBarcode.setEditable(false);
		pnlKodeBarang.add(txtBarcode, "cell 1 2 3 1,growx");

		XJLabel lblSatuan = new XJLabel();
		pnlKodeBarang.add(lblSatuan, "cell 0 3,alignx right");
		lblSatuan.setText("Satuan");

		txtSatuan = new XJTextField();
		pnlKodeBarang.add(txtSatuan, "cell 1 3,growx");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[]", "[][]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		btnSimpan.setMnemonic('S');
		btnSimpan.setText("<html><center>Simpan<br/>[Alt+S]</center></html>");
		pnlButton.add(btnSimpan, "cell 0 0");

		pack();
		setLocationRelativeTo(null);
	}

	public void setFormDetailValue(GoodStock goodStock) {
		lblKodeTerakhir.setVisible(false);
		lblKodeTerakhirValue.setVisible(false);
		txtKode.setEditable(false);

		Good good = goodStock.getGood();
		txtKode.setText(good.getCode());
		txtNama.setText(good.getName());
		txtBarcode.setText(good.getBarcode());
		txtSatuan.setText(goodStock.getUnit());

		btnSimpan.setText(BUTTON_SAVE_LABEL_UPDATE);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			btnSimpan.doClick();
			break;
		default:
			break;
		}
	}

	private void initForm() {
		String kodeTerakhir = DBUtils.getInstance().getLastValue("goods",
				"code", String.class);
		lblKodeTerakhirValue.setText(String.valueOf(kodeTerakhir));
	}

	private void save() {
		StockFacade facade = StockFacade.getInstance();

		if (btnSimpan.getText().equals(BUTTON_SAVE_LABEL_UPDATE)) {
			BigDecimal zeroValue = new BigDecimal(0);
			facade.updateExistingGood(txtKode.getText(), txtNama.getText(),
					txtBarcode.getText(), txtSatuan.getText(), zeroValue,
					zeroValue, zeroValue, 0, 0);

			dispose();
		} else {
			BigDecimal zeroValue = new BigDecimal(0);
			facade.addNewGood(txtKode.getText(), txtNama.getText(),
					txtBarcode.getText(), txtSatuan.getText(), zeroValue,
					zeroValue, zeroValue, 0, 0);
		}
		dispose();
	}
}
