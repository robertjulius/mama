package com.ganesha.minimarket.ui.forms.company;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JScrollPane;

import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.exception.ActionTypeNotSupported;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextArea;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.CompanyFacade;
import com.ganesha.minimarket.model.Company;
import com.ganesha.minimarket.utils.PermissionConstants;

import net.miginfocom.swing.MigLayout;

public class CompanyForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtNama;
	private XJLabel lblAlamat;
	private JScrollPane scrollPaneAlamat1;
	private XJTextArea txtAlamat;
	private XJPanel pnlKontakPerson1;
	private XJLabel lblPhone1;
	private XJTextField txtPhone1;
	private XJLabel lblFax;
	private XJTextField txtFax;
	private XJTextField txtPhone2;
	private XJLabel lblPhone2;
	private XJButton btnBatal;

	public CompanyForm(Window parent) {
		super(parent);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CompanyForm.this, ex);
				}
			}
		});
		setTitle("Form Company");
		setPermissionCode(PermissionConstants.COMPANY_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(new MigLayout("", "[]", "[grow][grow]"));

		pnlKontakPerson1 = new XJPanel();
		getContentPane().add(pnlKontakPerson1, "cell 0 0");
		pnlKontakPerson1.setBorder(new XEtchedBorder());
		pnlKontakPerson1.setLayout(new MigLayout("", "[][400px,grow]", "[][100px,grow,baseline][][][]"));

		XJLabel lblNama = new XJLabel();
		pnlKontakPerson1.add(lblNama, "cell 0 0");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		pnlKontakPerson1.add(txtNama, "cell 1 0,growx");
		txtNama.setUpperCaseOnFocusLost(true);

		lblAlamat = new XJLabel();
		pnlKontakPerson1.add(lblAlamat, "cell 0 1");
		lblAlamat.setText("Alamat");
		scrollPaneAlamat1 = new JScrollPane();
		pnlKontakPerson1.add(scrollPaneAlamat1, "cell 1 1,grow");

		txtAlamat = new XJTextArea();
		scrollPaneAlamat1.setViewportView(txtAlamat);

		lblPhone1 = new XJLabel();
		lblPhone1.setText("Phone 1");
		pnlKontakPerson1.add(lblPhone1, "cell 0 2");

		txtPhone1 = new XJTextField();
		pnlKontakPerson1.add(txtPhone1, "cell 1 2,growx");

		lblPhone2 = new XJLabel();
		pnlKontakPerson1.add(lblPhone2, "cell 0 3");
		lblPhone2.setText("Phone 2");

		txtPhone2 = new XJTextField();
		pnlKontakPerson1.add(txtPhone2, "cell 1 3,growx");

		lblFax = new XJLabel();
		lblFax.setText("Fax");
		pnlKontakPerson1.add(lblFax, "cell 0 4");

		txtFax = new XJTextField();
		pnlKontakPerson1.add(txtFax, "cell 1 4,growx");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CompanyForm.this, ex);
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
		pnlButton.add(btnSimpan, "cell 3 0");

		pack();
		setLocationRelativeTo(parent);
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

	private void initForm() throws ActionTypeNotSupported {
		Session session = HibernateUtils.openSession();
		try {
			Company company = CompanyFacade.getInstance().getDetail(session);
			txtNama.setText(company.getName());
			txtAlamat.setText(company.getAddress());
			txtPhone1.setText(company.getPhone1());
			txtPhone2.setText(company.getPhone2());
			txtFax.setText(company.getFax());
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void save() throws ActionTypeNotSupported, UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			CompanyFacade facade = CompanyFacade.getInstance();

			String name = txtNama.getText();
			String address = txtAlamat.getText();
			String phone1 = txtPhone1.getText();
			String phone2 = txtPhone2.getText();
			String fax = txtFax.getText();

			Company company = facade.updateExistingCompany(name, address, phone1, phone2, fax, session);
			dispose();

			ActivityLogFacade.doLog(getPermissionCode(), ActionType.UPDATE, Main.getUserLogin(), company, session);
			session.getTransaction().commit();

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void validateForm() throws UserException {
		if (txtNama.getText().trim().equals("")) {
			throw new UserException("Nama Company harus diisi");
		}
	}
}
