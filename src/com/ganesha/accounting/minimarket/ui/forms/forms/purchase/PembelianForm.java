package com.ganesha.accounting.minimarket.ui.forms.forms.purchase;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Good;
import com.ganesha.accounting.minimarket.model.GoodStock;
import com.ganesha.accounting.minimarket.ui.forms.forms.searchentity.SearchEntityDialog;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.hibernate.HibernateUtil;

public class PembelianForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJLabel lblBarcode;
	private XJButton btnCari;
	private JPanel pnlPembelian;
	private XJLabel lblJumlahSaatIni;
	private XJLabel lblJumlahSaatIniValue;
	private XJLabel lblKodeValue;
	private XJLabel lblNamaValue;
	private XJLabel lblBarcodeValue;
	private XJLabel lblSatuanValue;

	public PembelianForm(Window parent) {
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
		setTitle("Form Pembelian");
		getContentPane().setLayout(
				new MigLayout("", "[grow]", "[grow][grow][grow]"));

		JPanel pnlInfoBarang = new JPanel();
		getContentPane().add(pnlInfoBarang, "cell 0 0,grow");
		pnlInfoBarang.setLayout(new MigLayout("", "[100][200:n,grow][]",
				"[][][][][]"));

		XJLabel lblKode = new XJLabel();
		pnlInfoBarang.add(lblKode, "cell 0 0,alignx right");
		lblKode.setText("Kode:");

		btnCari = new XJButton();
		btnCari.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cariBarang();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});

		lblKodeValue = new XJLabel();
		lblKodeValue.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblKodeValue.setText("");
		pnlInfoBarang.add(lblKodeValue, "cell 1 0");
		btnCari.setText("Cari [F1]");
		pnlInfoBarang.add(btnCari, "cell 2 0");

		XJLabel lblNama = new XJLabel();
		pnlInfoBarang.add(lblNama, "cell 0 1,alignx trailing");
		lblNama.setText("Nama:");

		lblNamaValue = new XJLabel();
		lblNamaValue.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNamaValue.setText("");
		pnlInfoBarang.add(lblNamaValue, "cell 1 1");

		lblBarcode = new XJLabel();
		lblBarcode.setText("Barcode:");
		pnlInfoBarang.add(lblBarcode, "cell 0 2,alignx trailing");

		lblBarcodeValue = new XJLabel();
		lblBarcodeValue.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblBarcodeValue.setText("");
		pnlInfoBarang.add(lblBarcodeValue, "cell 1 2");

		XJLabel lblSatuan = new XJLabel();
		pnlInfoBarang.add(lblSatuan, "cell 0 3,alignx right");
		lblSatuan.setText("Satuan:");

		lblSatuanValue = new XJLabel();
		lblSatuanValue.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblSatuanValue.setText("");
		pnlInfoBarang.add(lblSatuanValue, "cell 1 3");

		lblJumlahSaatIni = new XJLabel();
		lblJumlahSaatIni.setText("Jumlah Saat Ini:");
		pnlInfoBarang.add(lblJumlahSaatIni, "cell 0 4");

		lblJumlahSaatIniValue = new XJLabel();
		lblJumlahSaatIniValue.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblJumlahSaatIniValue.setText("");
		pnlInfoBarang.add(lblJumlahSaatIniValue, "cell 1 4");

		pnlPembelian = new JPanel();
		getContentPane().add(pnlPembelian, "cell 0 1,grow");
		pnlPembelian.setLayout(new MigLayout("", "[]", "[]"));

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[]", "[][]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					/*
					 * TODO
					 */
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
		Good good = goodStock.getGood();
		lblKodeValue.setText(good.getCode());
		lblNamaValue.setText(good.getName());
		lblBarcodeValue.setText(good.getBarcode());
		lblSatuanValue.setText(goodStock.getUnit());

		btnSimpan
				.setText("<html><center>Simpan Perubahan<br/>[Alt+S]</center></html>");
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F1:
			btnCari.doClick();
			break;
		default:
			break;
		}
	}

	private void cariBarang() {
		SearchEntityDialog searchEntityDialog = new SearchEntityDialog(this,
				Good.class);

		searchEntityDialog.setVisible(true);
		String selectedCode = searchEntityDialog.getSelectedCode();
		if (selectedCode != null) {
			lblKodeValue.setText(selectedCode);
			loadData();
		}
	}

	private void initForm() {
	}

	private void loadData() {
		Session session = HibernateUtil.openSession();
		try {
			String code = lblKodeValue.getText();
			StockFacade facade = StockFacade.getInstance();
			GoodStock goodStock = facade.getDetail(code, session);
			Good good = goodStock.getGood();
			lblNamaValue.setText(good.getName());
			lblBarcodeValue.setText(good.getBarcode());
			lblSatuanValue.setText(goodStock.getUnit());
			lblJumlahSaatIniValue.setText(goodStock.getStock().toString());
		} finally {
			session.close();
		}
	}
}
