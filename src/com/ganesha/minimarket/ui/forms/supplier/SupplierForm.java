package com.ganesha.minimarket.ui.forms.supplier;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.exception.ActionTypeNotSupported;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextArea;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.SupplierFacade;
import com.ganesha.minimarket.model.Supplier;
import com.ganesha.minimarket.utils.PermissionConstants;

public class SupplierForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private ActionType actionType;
	private XJLabel lblDeskripsi;
	private XJTextField txtDeskripsi;
	private XJPanel pnlAlamat1;
	private XJLabel lblAlamat1;
	private JScrollPane scrollPaneAlamat1;
	private XJLabel lblAlamat2;
	private JScrollPane scrollPaneAlamat2;
	private XJTextArea txtAlamat2;
	private XJTextArea txtAlamat1;
	private XJPanel pnlKanan;
	private XJLabel lblKontakPerson1;
	private XJLabel lblKontakPerson2;
	private XJTextField txtKontakPerson1;
	private XJTextField txtKontakPerson2;
	private XJPanel pnlKontakPerson1;
	private XJLabel lblPhone1;
	private XJTextField txtPhone1;
	private XJLabel lblEmail1;
	private XJTextField txtEmail1;
	private XJLabel lblPhone2;
	private XJTextField txtPhone2;
	private XJLabel lblEmail2;
	private XJTextField txtEmail2;
	private XJPanel pnlAlamat2;
	private XJPanel pnlKiri;
	private JSeparator separator;
	private XJPanel lblKontakPerson2Email;
	private XJLabel lblKontakPerson1Phone;
	private XJTextField txtKontakPerson1Phone;
	private XJLabel lblKontakPerson1Email;
	private XJTextField txtKontakPerson1Email;
	private XJTextField txtKontakPerson2Phone;
	private XJTextField txtKontakPerson2Email;
	private XJLabel lblKontakPerson2Phone;
	private XJLabel lblEmail;
	private XJButton btnBatal;
	private XJPanel pnlDisable;
	private XJCheckBox chkDisabled;
	private XJButton btnhapussupplier;

	private Integer supplierId;

	public SupplierForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(SupplierForm.this, ex);
				}
			}
		});
		setTitle("Form Supplier");
		setPermissionCode(PermissionConstants.SUPPLIER_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[grow][grow]", "[grow][grow][][grow]"));

		pnlKiri = new XJPanel();
		getContentPane().add(pnlKiri, "cell 0 0,grow");
		pnlKiri.setLayout(new MigLayout("", "[400,grow]", "[][][grow]"));

		XJPanel pnlKodeSupplier = new XJPanel();
		pnlKodeSupplier.setBorder(new XEtchedBorder());
		pnlKiri.add(pnlKodeSupplier, "cell 0 0,growx");
		pnlKodeSupplier.setLayout(new MigLayout("", "[150][grow]", "[][][]"));

		XJLabel lblKode = new XJLabel();
		pnlKodeSupplier.add(lblKode, "cell 0 0");
		lblKode.setText("Kode");

		txtKode = new XJTextField();
		txtKode.setEditable(false);
		txtKode.setUpperCaseOnFocusLost(true);
		pnlKodeSupplier.add(txtKode, "cell 1 0,growx");

		XJLabel lblNama = new XJLabel();
		pnlKodeSupplier.add(lblNama, "cell 0 1");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		txtNama.setUpperCaseOnFocusLost(true);
		pnlKodeSupplier.add(txtNama, "cell 1 1,growx");

		lblDeskripsi = new XJLabel();
		lblDeskripsi.setText("Deskripsi");
		pnlKodeSupplier.add(lblDeskripsi, "cell 0 2");

		txtDeskripsi = new XJTextField();
		pnlKodeSupplier.add(txtDeskripsi, "cell 1 2,growx");

		pnlKontakPerson1 = new XJPanel();
		pnlKontakPerson1.setBorder(new XEtchedBorder());
		pnlKiri.add(pnlKontakPerson1, "cell 0 1,growx");
		pnlKontakPerson1.setLayout(new MigLayout("", "[150][grow]", "[][][]"));

		lblKontakPerson1 = new XJLabel();
		pnlKontakPerson1.add(lblKontakPerson1, "cell 0 0");
		lblKontakPerson1.setText("Kontak Person 1");

		txtKontakPerson1 = new XJTextField();
		txtKontakPerson1.setUpperCaseOnFocusLost(true);
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

		lblKontakPerson2Email = new XJPanel();
		lblKontakPerson2Email.setBorder(new XEtchedBorder());
		pnlKiri.add(lblKontakPerson2Email, "cell 0 2,grow");
		lblKontakPerson2Email.setLayout(new MigLayout("", "[150][grow]",
				"[][][]"));

		lblKontakPerson2 = new XJLabel();
		lblKontakPerson2Email.add(lblKontakPerson2, "cell 0 0");
		lblKontakPerson2.setText("Kontak Person 2");

		txtKontakPerson2 = new XJTextField();
		txtKontakPerson2.setUpperCaseOnFocusLost(true);
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

		pnlKanan = new XJPanel();
		getContentPane().add(pnlKanan, "cell 1 0,grow");
		pnlKanan.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		pnlAlamat1 = new XJPanel();
		pnlAlamat1.setBorder(new XEtchedBorder());
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

		pnlAlamat2 = new XJPanel();
		pnlAlamat2.setBorder(new XEtchedBorder());
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

		pnlDisable = new XJPanel();
		getContentPane().add(pnlDisable, "cell 0 1 2 1,alignx right,growy");
		pnlDisable.setLayout(new MigLayout("", "[]", "[]"));

		chkDisabled = new XJCheckBox();
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_SMALLEST));
		chkDisabled.setText("Supplier ini sudah tidak aktif lagi");
		pnlDisable.add(chkDisabled, "cell 0 0");

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 2 2 1,grow");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 3 2 1,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(false);
				} catch (Exception ex) {
					ExceptionHandler.handleException(SupplierForm.this, ex);
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

		btnhapussupplier = new XJButton();
		btnhapussupplier.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(SupplierForm.this, ex);
				}
			}
		});
		btnhapussupplier
				.setText("<html><center>Hapus<br/>Supplier</center></html>");
		pnlButton.add(btnhapussupplier, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		pack();
		setLocationRelativeTo(null);
	}

	public void setFormDetailValue(Supplier supplier) {
		supplierId = supplier.getId();

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
		chkDisabled.setSelected(supplier.getDisabled());

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
		int newCode = kodeTerakhir == null ? 1 : Formatter.formatCodeToInt(
				kodeTerakhir).intValue() + 1;
		String newCodeInString = Formatter.formatIntToCode(newCode);
		txtKode.setText(newCodeInString);
	}

	private void save(boolean deleted) throws ActionTypeNotSupported,
			UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			SupplierFacade facade = SupplierFacade.getInstance();

			Supplier supplier = null;
			if (actionType == ActionType.CREATE) {
				supplier = facade.addNewSupplier(txtAlamat1.getText(),
						txtAlamat2.getText(), txtKode.getText().toUpperCase(),
						txtKontakPerson1.getText().toUpperCase(),
						txtKontakPerson1Email.getText(), txtKontakPerson1Phone
								.getText(), txtKontakPerson2.getText()
								.toUpperCase(),
						txtKontakPerson2Email.getText(), txtKontakPerson2Phone
								.getText(), txtDeskripsi.getText(), txtEmail1
								.getText(), txtEmail2.getText(), txtNama
								.getText().toUpperCase(), txtPhone1.getText(),
						txtPhone2.getText(), chkDisabled.isSelected(), deleted,
						session);
				supplierId = supplier.getId();
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				supplier = facade.updateExistingSupplier(supplierId, txtAlamat1
						.getText(), txtAlamat2.getText(), txtKontakPerson1
						.getText().toUpperCase(), txtKontakPerson1Email
						.getText(), txtKontakPerson1Phone.getText(),
						txtKontakPerson2.getText().toUpperCase(),
						txtKontakPerson2Email.getText(), txtKontakPerson2Phone
								.getText(), txtDeskripsi.getText(), txtEmail1
								.getText(), txtEmail2.getText(), txtNama
								.getText().toUpperCase(), txtPhone1.getText(),
						txtPhone2.getText(), chkDisabled.isSelected(), deleted,
						session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}

			ActivityLogFacade.doLog(getPermissionCode(), actionType,
					Main.getUserLogin(), supplier, session);
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
