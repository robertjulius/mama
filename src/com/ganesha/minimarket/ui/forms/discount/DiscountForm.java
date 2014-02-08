package com.ganesha.minimarket.ui.forms.discount;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.ActionTypeNotSupported;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.DiscountFacade;
import com.ganesha.minimarket.model.Discount;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.ui.forms.searchentity.SearchEntityDialog;

public class DiscountForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJLabel lblQuantity;
	private XJTextField txtQuantity;
	private JPanel pnlKode;
	private XJLabel lblDiscount;
	private XJTextField txtDiscount;
	private XJButton btnBatal;
	private XJButton btnCariBarang;
	private JPanel pnlDisabled;
	private XJCheckBox chkDisabled;

	public DiscountForm(Window parent) {
		super(parent);
		setCloseOnEsc(false);
		setTitle("Diskon");
		getContentPane().setLayout(
				new MigLayout("", "[grow]", "[grow][grow][grow]"));

		pnlKode = new JPanel();
		pnlKode.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlKode, "cell 0 0,grow");
		pnlKode.setLayout(new MigLayout("", "[][100px][200]", "[][][][]"));

		XJLabel lblBarang = new XJLabel();
		pnlKode.add(lblBarang, "cell 0 0");
		lblBarang.setText("Barang");

		txtKode = new XJTextField();
		txtKode.setEditable(false);
		pnlKode.add(txtKode, "cell 1 0,growx");

		txtNama = new XJTextField();
		txtNama.setEditable(false);
		pnlKode.add(txtNama, "cell 2 0,growx");

		btnCariBarang = new XJButton();
		btnCariBarang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cariBarang();
			}
		});
		btnCariBarang.setText("Cari Barang [F5]");
		pnlKode.add(btnCariBarang, "cell 2 1,alignx right");

		lblQuantity = new XJLabel();
		pnlKode.add(lblQuantity, "cell 0 2");
		lblQuantity.setText("Quantity");

		txtQuantity = new XJTextField();
		txtQuantity.setText("0");
		pnlKode.add(txtQuantity, "cell 1 2,growx");

		lblDiscount = new XJLabel();
		pnlKode.add(lblDiscount, "cell 0 3");
		lblDiscount.setText("Diskon");

		txtDiscount = new XJTextField();
		pnlKode.add(txtDiscount, "cell 1 3,growx");
		txtDiscount
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		txtDiscount.setText("0");

		pnlDisabled = new JPanel();
		getContentPane().add(pnlDisabled, "cell 0 1,alignx right,growy");
		pnlDisabled.setLayout(new MigLayout("", "[]", "[][]"));

		chkDisabled = new XJCheckBox();
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, 12));
		chkDisabled.setText("Promo ini sudah tidak aktif");
		pnlDisabled.add(chkDisabled, "cell 0 0");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

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
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 1 0");

		pack();
		setLocationRelativeTo(null);
	}

	public String getKodeBarang() {
		return txtKode.getText();
	}

	public void setFormDetailValue(Discount discount) {
		Item item = discount.getItem();

		txtKode.setText(item.getCode());
		txtNama.setText(item.getName());
		txtQuantity.setText(Formatter.formatNumberToString(discount
				.getQuantity()));
		txtDiscount.setText(Formatter.formatNumberToString(discount
				.getDiscountPercent()));
		chkDisabled.setSelected(discount.getDisabled());

		btnSimpan
				.setText("<html><center>Simpan Perubahan<br/>[F12]</center></html>");
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnSimpan.doClick();
			break;
		default:
			break;
		}
	}

	private void batal() {
		dispose();
	}

	private void cariBarang() {
		SearchEntityDialog searchEntityDialog = new SearchEntityDialog(
				"Cari Barang", this, Item.class);
		searchEntityDialog.setVisible(true);

		String kodeBarang = searchEntityDialog.getSelectedCode();
		if (kodeBarang != null) {
			txtKode.setText(kodeBarang);
			txtNama.setText(searchEntityDialog.getSelectedName());
		}
	}

	private void save() throws ActionTypeNotSupported, UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			DiscountFacade facade = DiscountFacade.getInstance();

			String code = txtKode.getText();
			int quantity = Formatter
					.formatStringToNumber(txtQuantity.getText()).intValue();
			BigDecimal discountPercent = BigDecimal.valueOf(Formatter
					.formatStringToNumber(txtDiscount.getText()).doubleValue());
			boolean disabled = chkDisabled.isSelected();

			facade.saveOrUpdate(code, quantity, discountPercent, disabled,
					session);
			dispose();

			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void validateForm() throws UserException {
		if (txtKode.getText().trim().equals("")) {
			throw new UserException("Kode Barang harus diisi");
		}

		if (txtQuantity.getText().trim().equals("")) {
			throw new UserException("Quantity harus diisi");
		}

		if (txtDiscount.getText().trim().equals("")) {
			throw new UserException("Diskon harus diisi");
		}
	}
}