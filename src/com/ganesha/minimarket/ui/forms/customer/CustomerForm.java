package com.ganesha.minimarket.ui.forms.customer;

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
import com.ganesha.minimarket.facade.CustomerFacade;
import com.ganesha.minimarket.model.Customer;
import com.ganesha.minimarket.utils.PermissionConstants;

public class CustomerForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private ActionType actionType;
	private XJLabel lblDeskripsi;
	private XJTextField txtDeskripsi;
	private XJLabel lblAlamat;
	private XJPanel pnlKontakPerson1;
	private XJLabel lblPhone;
	private XJTextField txtPhone;
	private XJLabel lblEmail;
	private XJTextField txtEmail;
	private XJButton btnBatal;
	private JScrollPane scrollPane;
	private XJTextArea txtAlamat;
	private XJCheckBox chkDisabled;
	private XJPanel pnlDisabled;
	private JSeparator separator;
	private XJButton btnhapuscustomer;

	private Integer customerId;

	public CustomerForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CustomerForm.this, ex);
				}
			}
		});
		setTitle("Form Customer");
		setPermissionCode(PermissionConstants.CUST_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[400][grow]", "[][][10][]"));

		XJPanel pnlKodeCustomer = new XJPanel();
		getContentPane().add(pnlKodeCustomer, "cell 0 0,grow");
		pnlKodeCustomer.setBorder(new XEtchedBorder());
		pnlKodeCustomer
				.setLayout(new MigLayout("", "[150][grow]", "[][][][][]"));

		XJLabel lblKode = new XJLabel();
		pnlKodeCustomer.add(lblKode, "cell 0 0");
		lblKode.setText("Kode");

		txtKode = new XJTextField();
		txtKode.setEditable(false);
		txtKode.setUpperCaseOnFocusLost(true);
		pnlKodeCustomer.add(txtKode, "cell 1 0,growx");

		XJLabel lblNama = new XJLabel();
		pnlKodeCustomer.add(lblNama, "cell 0 1");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		txtNama.setUpperCaseOnFocusLost(true);
		pnlKodeCustomer.add(txtNama, "cell 1 1,growx");

		lblDeskripsi = new XJLabel();
		lblDeskripsi.setText("Deskripsi");
		pnlKodeCustomer.add(lblDeskripsi, "cell 0 2");

		txtDeskripsi = new XJTextField();
		pnlKodeCustomer.add(txtDeskripsi, "cell 1 2,growx");

		lblPhone = new XJLabel();
		pnlKodeCustomer.add(lblPhone, "cell 0 3");
		lblPhone.setText("Phone");

		txtPhone = new XJTextField();
		pnlKodeCustomer.add(txtPhone, "cell 1 3,growx");

		lblEmail = new XJLabel();
		pnlKodeCustomer.add(lblEmail, "cell 0 4");
		lblEmail.setText("Email");

		txtEmail = new XJTextField();
		pnlKodeCustomer.add(txtEmail, "cell 1 4,growx");

		pnlKontakPerson1 = new XJPanel();
		getContentPane().add(pnlKontakPerson1, "cell 1 0,grow");
		pnlKontakPerson1.setBorder(new XEtchedBorder());
		pnlKontakPerson1.setLayout(new MigLayout("", "[300,grow]", "[][grow]"));

		lblAlamat = new XJLabel();
		pnlKontakPerson1.add(lblAlamat, "cell 0 0");
		lblAlamat.setText("Alamat");

		scrollPane = new JScrollPane();
		pnlKontakPerson1.add(scrollPane, "cell 0 1,grow");

		txtAlamat = new XJTextArea();
		scrollPane.setViewportView(txtAlamat);

		pnlDisabled = new XJPanel();
		getContentPane().add(pnlDisabled, "cell 0 1 2 1,alignx right,growy");
		pnlDisabled.setLayout(new MigLayout("", "[]", "[][]"));

		chkDisabled = new XJCheckBox();
		pnlDisabled.add(chkDisabled, "cell 0 0");
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_SMALLEST));
		chkDisabled.setText("Customer ini sudah tidak aktif");

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 2 2 1,growx,aligny center");

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
					ExceptionHandler.handleException(CustomerForm.this, ex);
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

		btnhapuscustomer = new XJButton();
		btnhapuscustomer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(CustomerForm.this, ex);
				}
			}
		});
		btnhapuscustomer
				.setText("<html><center>Hapus<br/>Customer</center></html>");
		pnlButton.add(btnhapuscustomer, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		pack();
		setLocationRelativeTo(parent);
	}

	public void setFormDetailValue(Customer customer) {

		customerId = customer.getId();

		txtKode.setText(customer.getCode());
		txtNama.setText(customer.getName());
		txtDeskripsi.setText(customer.getDescription());
		txtPhone.setText(customer.getPhone());
		txtEmail.setText(customer.getEmail());
		txtAlamat.setText(customer.getAddress());
		chkDisabled.setSelected(customer.getDisabled());

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
		String kodeTerakhir = DBUtils.getInstance().getLastValue("customers",
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
			CustomerFacade facade = CustomerFacade.getInstance();

			Customer customer = null;
			if (actionType == ActionType.CREATE) {
				customer = facade.addNewCustomer(txtAlamat.getText(), txtKode
						.getText().toUpperCase(), txtDeskripsi.getText(),
						txtEmail.getText(), txtNama.getText().toUpperCase(),
						txtPhone.getText(), chkDisabled.isSelected(), deleted,
						session);
				customerId = customer.getId();
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				customer = facade.updateExistingCustomer(customerId,
						txtAlamat.getText(), txtDeskripsi.getText(),
						txtEmail.getText(), txtNama.getText().toUpperCase(),
						txtPhone.getText(), chkDisabled.isSelected(), deleted,
						session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}

			ActivityLogFacade.doLog(getPermissionCode(), actionType,
					Main.getUserLogin(), customer, session);
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
			throw new UserException("Kode Customer harus diisi");
		}

		if (txtNama.getText().trim().equals("")) {
			throw new UserException("Nama Customer harus diisi");
		}
	}
}
