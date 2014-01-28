package com.ganesha.minimarket.ui.forms.customer;

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

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.ActionTypeNotSupported;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTextArea;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.CustomerFacade;
import com.ganesha.minimarket.model.Customer;

public class CustomerForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJLabel lblKodeTerakhir;
	private ActionType actionType;
	private XJLabel lblDeskripsi;
	private XJTextField txtDeskripsi;
	private XJLabel lblAlamat;
	private JPanel pnlKontakPerson1;
	private XJLabel lblPhone;
	private XJTextField txtPhone;
	private XJLabel lblEmail;
	private XJTextField txtEmail;
	private XJButton btnBatal;
	private JScrollPane scrollPane;
	private XJTextArea txtAlamat;
	private XJLabel lblKodeTerakhirValue;
	private XJCheckBox chkDisabled;
	private JPanel pnlDisabled;
	private JSeparator separator;
	private XJButton btnhapuscustomer;

	private boolean deleted;

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
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[400][grow]", "[][][10][]"));

		JPanel pnlKodeCustomer = new JPanel();
		getContentPane().add(pnlKodeCustomer, "cell 0 0,grow");
		pnlKodeCustomer.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));
		pnlKodeCustomer.setLayout(new MigLayout("", "[150][][grow]",
				"[][][][][][]"));

		XJLabel lblKode = new XJLabel();
		pnlKodeCustomer.add(lblKode, "cell 0 0");
		lblKode.setText("Kode");

		txtKode = new XJTextField();
		pnlKodeCustomer.add(txtKode, "cell 1 0 2 1,growx");

		lblKodeTerakhir = new XJLabel();
		lblKodeTerakhir.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblKodeTerakhir.setText("Kode Terakhir:");
		pnlKodeCustomer.add(lblKodeTerakhir, "cell 1 1");

		lblKodeTerakhirValue = new XJLabel();
		lblKodeTerakhirValue.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblKodeTerakhirValue.setText("");
		pnlKodeCustomer.add(lblKodeTerakhirValue, "cell 2 1");

		XJLabel lblNama = new XJLabel();
		pnlKodeCustomer.add(lblNama, "cell 0 2");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		pnlKodeCustomer.add(txtNama, "cell 1 2 2 1,growx");

		lblDeskripsi = new XJLabel();
		lblDeskripsi.setText("Deskripsi");
		pnlKodeCustomer.add(lblDeskripsi, "cell 0 3");

		txtDeskripsi = new XJTextField();
		pnlKodeCustomer.add(txtDeskripsi, "cell 1 3 2 1,growx");

		lblPhone = new XJLabel();
		pnlKodeCustomer.add(lblPhone, "cell 0 4");
		lblPhone.setText("Phone");

		txtPhone = new XJTextField();
		pnlKodeCustomer.add(txtPhone, "cell 1 4 2 1,growx");

		lblEmail = new XJLabel();
		pnlKodeCustomer.add(lblEmail, "cell 0 5");
		lblEmail.setText("Email");

		txtEmail = new XJTextField();
		pnlKodeCustomer.add(txtEmail, "cell 1 5 2 1,growx");

		pnlKontakPerson1 = new JPanel();
		getContentPane().add(pnlKontakPerson1, "cell 1 0,grow");
		pnlKontakPerson1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));
		pnlKontakPerson1.setLayout(new MigLayout("", "[300,grow]", "[][grow]"));

		lblAlamat = new XJLabel();
		pnlKontakPerson1.add(lblAlamat, "cell 0 0");
		lblAlamat.setText("Alamat");

		scrollPane = new JScrollPane();
		pnlKontakPerson1.add(scrollPane, "cell 0 1,grow");

		txtAlamat = new XJTextArea();
		scrollPane.setViewportView(txtAlamat);

		pnlDisabled = new JPanel();
		getContentPane().add(pnlDisabled, "cell 0 1 2 1,alignx right,growy");
		pnlDisabled.setLayout(new MigLayout("", "[]", "[][]"));

		chkDisabled = new XJCheckBox();
		pnlDisabled.add(chkDisabled, "cell 0 0");
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, 12));
		chkDisabled.setText("Customer ini sudah tidak aktif");

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 2 2 1,growx,aligny center");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 3 2 1,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
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
					deleted = true;
					save();
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
		setLocationRelativeTo(null);
	}

	public void setFormDetailValue(Customer customer) {
		lblKodeTerakhir.setVisible(false);
		lblKodeTerakhirValue.setVisible(false);
		txtKode.setEditable(false);
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
		lblKodeTerakhirValue.setText(String.valueOf(kodeTerakhir));
	}

	private void save() throws ActionTypeNotSupported, UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			CustomerFacade facade = CustomerFacade.getInstance();

			if (actionType == ActionType.CREATE) {
				facade.addNewCustomer(txtAlamat.getText(), txtKode.getText(),
						txtDeskripsi.getText(), txtEmail.getText(),
						txtNama.getText(), txtPhone.getText(),
						chkDisabled.isSelected(), deleted, session);
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				facade.updateExistingCustomer(txtAlamat.getText(),
						txtKode.getText(), txtDeskripsi.getText(),
						txtEmail.getText(), txtNama.getText(),
						txtPhone.getText(), chkDisabled.isSelected(), deleted,
						session);
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
			throw new UserException("Kode Customer harus diisi");
		}

		if (txtNama.getText().trim().equals("")) {
			throw new UserException("Nama Customer harus diisi");
		}
	}
}
