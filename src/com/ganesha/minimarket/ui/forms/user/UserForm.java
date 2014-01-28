package com.ganesha.minimarket.ui.forms.user;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.ActionTypeNotSupported;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPasswordField;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.UserFacade;
import com.ganesha.model.User;

public class UserForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtLogin;
	private XJTextField txtNama;
	private ActionType actionType;
	private XJLabel lblPassword;
	private XJPasswordField txtPassword;
	private XJButton btnBatal;
	private XJButton btnHapus;

	private boolean deleted;
	private XJCheckBox chkDisabled;
	private JSeparator separator;

	public UserForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Form User");
		setCloseOnEsc(false);

		getContentPane().setLayout(new MigLayout("", "[400]", "[][][10][]"));

		JPanel pnlInput = new JPanel();
		getContentPane().add(pnlInput, "cell 0 0,growx");
		pnlInput.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlInput.setLayout(new MigLayout("", "[][200][grow]", "[][][]"));

		XJLabel lblLogin = new XJLabel();
		pnlInput.add(lblLogin, "cell 0 0");
		lblLogin.setText("Login ID");

		txtLogin = new XJTextField();
		pnlInput.add(txtLogin, "cell 1 0,growx");

		XJLabel lblNama = new XJLabel();
		pnlInput.add(lblNama, "cell 0 1");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		pnlInput.add(txtNama, "cell 1 1,growx");

		lblPassword = new XJLabel();
		lblPassword.setText("Password");
		pnlInput.add(lblPassword, "cell 0 2");

		txtPassword = new XJPasswordField();
		pnlInput.add(txtPassword, "cell 1 2,growx");

		chkDisabled = new XJCheckBox();
		chkDisabled.setText("User ini sudah tidak aktif lagi");
		getContentPane().add(chkDisabled, "cell 0 1,alignx trailing");

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 2,growx,aligny center");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 3,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserForm.this, ex);
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

		btnHapus = new XJButton();
		btnHapus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					deleted = true;
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserForm.this, ex);
				}
			}
		});
		btnHapus.setText("<html><center>Hapus<br/>User</center></html>");
		pnlButton.add(btnHapus, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		pack();
		setLocationRelativeTo(null);
	}

	public void setFormDetailValue(User user) {
		txtLogin.setEditable(false);
		txtLogin.setText(user.getLogin());
		txtNama.setEditable(false);
		txtNama.setText(user.getName());
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

	private void save() throws ActionTypeNotSupported, UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			UserFacade facade = UserFacade.getInstance();

			if (actionType == ActionType.CREATE) {
				facade.addNewUser(txtLogin.getText(), txtNama.getText(),
						new String(txtPassword.getPassword()),
						chkDisabled.isSelected(), deleted, session);

				dispose();
			} else if (actionType == ActionType.UPDATE) {
				facade.updateExistingUser(txtLogin.getText(), new String(
						txtPassword.getPassword()), chkDisabled.isSelected(),
						deleted, session);
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
		if (txtLogin.getText().trim().equals("")) {
			throw new UserException("Kode User harus diisi");
		}

		if (txtNama.getText().trim().equals("")) {
			throw new UserException("Nama User harus diisi");
		}

		if (actionType == ActionType.CREATE
				&& new String(txtPassword.getPassword()).trim().equals("")) {
			throw new UserException("Password harus diisi");
		}
	}
}
