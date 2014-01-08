package com.ganesha.accounting.minimarket.ui.forms.forms.stock;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Good;
import com.ganesha.accounting.minimarket.model.GoodStock;
import com.ganesha.accounting.util.DBUtils;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.ActionTypeNotSupported;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.hibernate.HibernateUtil;

public class StockForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtSatuan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJLabel lblKodeTerakhirValue;
	private XJLabel lblBarcode;
	private XJTextField txtBarcode;
	private XJLabel lblKodeTerakhir;
	private ActionType actionType;
	private JSeparator separator;

	public StockForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
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
		getContentPane().setLayout(
				new MigLayout("", "[grow]", "[grow][][grow]"));

		JPanel pnlKodeBarang = new JPanel();
		pnlKodeBarang.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));
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

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 1,grow");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[]", "[]"));

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

		btnSimpan
				.setText("<html><center>Simpan Perubahan<br/>[Alt+S]</center></html>");
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private void initForm() {
		String kodeTerakhir = DBUtils.getInstance().getLastValue("goods",
				"code", String.class);
		lblKodeTerakhirValue.setText(String.valueOf(kodeTerakhir));
	}

	private void save() throws ActionTypeNotSupported {
		Session session = HibernateUtil.openSession();
		try {
			session.beginTransaction();
			StockFacade facade = StockFacade.getInstance();

			if (actionType == ActionType.CREATE) {
				BigDecimal zeroValue = new BigDecimal(0);
				facade.addNewGood(txtKode.getText(), txtNama.getText(),
						txtBarcode.getText(), txtSatuan.getText(), zeroValue,
						zeroValue, zeroValue, 0, 0, session);
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				BigDecimal zeroValue = new BigDecimal(0);
				facade.updateExistingGood(txtKode.getText(), txtNama.getText(),
						txtBarcode.getText(), txtSatuan.getText(), zeroValue,
						zeroValue, zeroValue, 0, 0, session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}
			session.getTransaction().commit();
		} finally {
			session.close();
		}
	}
}
