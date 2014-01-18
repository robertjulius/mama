package com.ganesha.accounting.minimarket.ui.forms.forms.supplier;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.minimarket.facade.SupplierFacade;
import com.ganesha.accounting.minimarket.model.Supplier;
import com.ganesha.accounting.util.DBUtils;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.ActionTypeNotSupported;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTextArea;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.hibernate.HibernateUtils;

public class SupplierForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJLabel lblKodeTerakhirValue;
	private XJLabel lblKodeTerakhir;
	private ActionType actionType;
	private XJLabel lblDeskripsi;
	private XJTextField txtDeskripsi;
	private JPanel pnlAlamat1;
	private XJLabel lblAlamat1;
	private JScrollPane scrollPaneAlamat1;
	private XJLabel lblAlamat2;
	private JScrollPane scrollPaneAlamat2;
	private XJTextArea txtAlamat2;
	private XJTextArea txtAlamat1;
	private JPanel pnlKanan;
	private XJLabel lblKontakPerson1;
	private XJLabel lblKontakPerson2;
	private XJTextField txtKontakPerson1;
	private XJTextField txtKontakPerson2;
	private JPanel pnlKontakPerson1;
	private XJLabel lblPhone1;
	private XJTextField txtPhone1;
	private XJLabel lblEmail1;
	private XJTextField txtEmail1;
	private XJLabel lblPhone2;
	private XJTextField txtPhone2;
	private XJLabel lblEmail2;
	private XJTextField txtEmail2;
	private JPanel pnlAlamat2;
	private JPanel pnlKiri;
	private JSeparator separator;
	private JPanel lblKontakPerson2Email;
	private XJLabel lblKontakPerson1Phone;
	private XJTextField txtKontakPerson1Phone;
	private XJLabel lblKontakPerson1Email;
	private XJTextField txtKontakPerson1Email;
	private XJTextField txtKontakPerson2Phone;
	private XJTextField txtKontakPerson2Email;
	private XJLabel lblKontakPerson2Phone;
	private XJLabel lblEmail;
	private XJButton btnBatal;

	public SupplierForm(Window parent, ActionType actionType) {
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
		setTitle("Form Supplier");
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[grow][grow]", "[grow][][grow]"));

		pnlKiri = new JPanel();
		getContentPane().add(pnlKiri, "cell 0 0,grow");
		pnlKiri.setLayout(new MigLayout("", "[400,grow]", "[][][]"));

		JPanel pnlKodeSupplier = new JPanel();
		pnlKodeSupplier.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));
		pnlKiri.add(pnlKodeSupplier, "cell 0 0,growx");
		pnlKodeSupplier
				.setLayout(new MigLayout("", "[150][][grow]", "[][][][]"));

		XJLabel lblKode = new XJLabel();
		pnlKodeSupplier.add(lblKode, "cell 0 0");
		lblKode.setText("Kode");

		txtKode = new XJTextField();
		pnlKodeSupplier.add(txtKode, "cell 1 0 2 1,growx");

		lblKodeTerakhir = new XJLabel();
		lblKodeTerakhir.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblKodeTerakhir.setText("Kode Terakhir:");
		pnlKodeSupplier.add(lblKodeTerakhir, "cell 1 1");

		lblKodeTerakhirValue = new XJLabel();
		lblKodeTerakhirValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblKodeTerakhirValue.setText("");
		pnlKodeSupplier.add(lblKodeTerakhirValue, "cell 2 1");

		XJLabel lblNama = new XJLabel();
		pnlKodeSupplier.add(lblNama, "cell 0 2");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		pnlKodeSupplier.add(txtNama, "cell 1 2 2 1,growx");

		lblDeskripsi = new XJLabel();
		lblDeskripsi.setText("Deskripsi");
		pnlKodeSupplier.add(lblDeskripsi, "cell 0 3");

		txtDeskripsi = new XJTextField();
		pnlKodeSupplier.add(txtDeskripsi, "cell 1 3 2 1,growx");

		pnlKontakPerson1 = new JPanel();
		pnlKontakPerson1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));
		pnlKiri.add(pnlKontakPerson1, "cell 0 1,growx");
		pnlKontakPerson1.setLayout(new MigLayout("", "[150][grow]", "[][][]"));

		lblKontakPerson1 = new XJLabel();
		pnlKontakPerson1.add(lblKontakPerson1, "cell 0 0");
		lblKontakPerson1.setText("Kontak Person 1");

		txtKontakPerson1 = new XJTextField();
		pnlKontakPerson1.add(txtKontakPerson1, "cell 1 0,growx");

		lblKontakPerson1Phone = new XJLabel();
		lblKontakPerson1Phone.setText("Phone");
		pnlKontakPerson1.add(lblKontakPerson1Phone, "cell 0 1");

		txtKontakPerson1Phone = new XJTextField();
		pnlKontakPerson1.add(txtKontakPerson1Phone, "cell 1 1,growx");

		lblKontakPerson1Email = new XJLabel();
		lblKontakPerson1Email.setText("Email");
		pnlKontakPerson1.add(lblKontakPerson1Email, "cell 0 2");

		txtKontakPerson1Email = new XJTextField();
		pnlKontakPerson1.add(txtKontakPerson1Email, "cell 1 2,growx");

		lblKontakPerson2Email = new JPanel();
		lblKontakPerson2Email.setBorder(new EtchedBorder(EtchedBorder.LOWERED,
				null, null));
		pnlKiri.add(lblKontakPerson2Email, "cell 0 2,grow");
		lblKontakPerson2Email.setLayout(new MigLayout("", "[150][grow]",
				"[][][]"));

		lblKontakPerson2 = new XJLabel();
		lblKontakPerson2Email.add(lblKontakPerson2, "cell 0 0");
		lblKontakPerson2.setText("Kontak Person 2");

		txtKontakPerson2 = new XJTextField();
		lblKontakPerson2Email.add(txtKontakPerson2, "cell 1 0,growx");

		lblKontakPerson2Phone = new XJLabel();
		lblKontakPerson2Phone.setText("Phone");
		lblKontakPerson2Email.add(lblKontakPerson2Phone, "cell 0 1");

		txtKontakPerson2Phone = new XJTextField();
		lblKontakPerson2Email.add(txtKontakPerson2Phone, "cell 1 1,growx");

		lblEmail = new XJLabel();
		lblEmail.setText("Email");
		lblKontakPerson2Email.add(lblEmail, "cell 0 2");

		txtKontakPerson2Email = new XJTextField();
		lblKontakPerson2Email.add(txtKontakPerson2Email, "cell 1 2,growx");

		pnlKanan = new JPanel();
		getContentPane().add(pnlKanan, "cell 1 0,grow");
		pnlKanan.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		pnlAlamat1 = new JPanel();
		pnlAlamat1
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlKanan.add(pnlAlamat1, "cell 0 0,grow");
		pnlAlamat1.setLayout(new MigLayout("", "[100][300:n,grow]",
				"[100,grow][][]"));

		lblAlamat1 = new XJLabel();
		lblAlamat1.setText("Alamat 1");
		pnlAlamat1.add(lblAlamat1, "cell 0 0");
		scrollPaneAlamat1 = new JScrollPane();
		pnlAlamat1.add(scrollPaneAlamat1, "cell 1 0,grow");

		txtAlamat1 = new XJTextArea();
		scrollPaneAlamat1.setViewportView(txtAlamat1);

		lblPhone1 = new XJLabel();
		lblPhone1.setText("Phone");
		pnlAlamat1.add(lblPhone1, "cell 0 1");

		txtPhone1 = new XJTextField();
		pnlAlamat1.add(txtPhone1, "cell 1 1,growx");

		lblEmail1 = new XJLabel();
		lblEmail1.setText("Email");
		pnlAlamat1.add(lblEmail1, "cell 0 2");

		txtEmail1 = new XJTextField();
		pnlAlamat1.add(txtEmail1, "cell 1 2,growx");

		pnlAlamat2 = new JPanel();
		pnlAlamat2
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlKanan.add(pnlAlamat2, "cell 0 1,grow");
		pnlAlamat2.setLayout(new MigLayout("", "[100][300:n,grow]",
				"[100,grow][][]"));

		lblAlamat2 = new XJLabel();
		pnlAlamat2.add(lblAlamat2, "cell 0 0");
		lblAlamat2.setText("Alamat 2");

		txtAlamat2 = new XJTextArea();
		scrollPaneAlamat2 = new JScrollPane(txtAlamat2);
		pnlAlamat2.add(scrollPaneAlamat2, "cell 1 0,grow");

		lblPhone2 = new XJLabel();
		pnlAlamat2.add(lblPhone2, "cell 0 1");
		lblPhone2.setText("Phone");

		txtPhone2 = new XJTextField();
		pnlAlamat2.add(txtPhone2, "cell 1 1,growx");

		lblEmail2 = new XJLabel();
		pnlAlamat2.add(lblEmail2, "cell 0 2");
		lblEmail2.setText("Email");

		txtEmail2 = new XJTextField();
		pnlAlamat2.add(txtEmail2, "cell 1 2,growx");

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 1 2 1,grow");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 2 2 1,alignx center,growy");
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

	public void setFormDetailValue(Supplier supplier) {
		lblKodeTerakhir.setVisible(false);
		lblKodeTerakhirValue.setVisible(false);
		txtKode.setEditable(false);
		txtKode.setText(supplier.getCode());
		txtNama.setText(supplier.getName());
		txtDeskripsi.setText(supplier.getDescription());
		txtKontakPerson1.setText(supplier.getContactPerson1());
		txtKontakPerson1Phone.setText(supplier.getContactPerson1Phone());
		txtKontakPerson1Email.setText(supplier.getContactPerson1Email());
		txtKontakPerson2.setText(supplier.getContactPerson2());
		txtKontakPerson2Phone.setText(supplier.getContactPerson2Phone());
		txtKontakPerson2Email.setText(supplier.getContactPerson2Email());
		txtAlamat1.setText(supplier.getAddress1());
		txtPhone1.setText(supplier.getPhone1());
		txtEmail1.setText(supplier.getEmail1());
		txtAlamat2.setText(supplier.getAddress2());
		txtPhone2.setText(supplier.getPhone2());
		txtEmail2.setText(supplier.getEmail2());

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

	private void initForm() {
		String kodeTerakhir = DBUtils.getInstance().getLastValue("suppliers",
				"code", String.class);
		lblKodeTerakhirValue.setText(String.valueOf(kodeTerakhir));
	}

	private void save() throws ActionTypeNotSupported, UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			SupplierFacade facade = SupplierFacade.getInstance();

			if (actionType == ActionType.CREATE) {
				facade.addNewSupplier(txtAlamat1.getText(),
						txtAlamat2.getText(), txtKode.getText(),
						txtKontakPerson1.getText(),
						txtKontakPerson1Email.getText(),
						txtKontakPerson1Phone.getText(),
						txtKontakPerson2.getText(),
						txtKontakPerson2Email.getText(),
						txtKontakPerson2Phone.getText(),
						txtDeskripsi.getText(), txtEmail1.getText(),
						txtEmail2.getText(), txtNama.getText(),
						txtPhone1.getText(), txtPhone2.getText(), session);
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				facade.updateExistingSupplier(txtAlamat1.getText(),
						txtAlamat2.getText(), txtKode.getText(),
						txtKontakPerson1.getText(),
						txtKontakPerson1Email.getText(),
						txtKontakPerson1Phone.getText(),
						txtKontakPerson2.getText(),
						txtKontakPerson2Email.getText(),
						txtKontakPerson2Phone.getText(),
						txtDeskripsi.getText(), txtEmail1.getText(),
						txtEmail2.getText(), txtNama.getText(),
						txtPhone1.getText(), txtPhone2.getText(), session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}
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
			throw new UserException("Kode Supplier harus diisi");
		}

		if (txtNama.getText().trim().equals("")) {
			throw new UserException("Nama Supplier harus diisi");
		}
	}
}
