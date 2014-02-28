package com.ganesha.minimarket.ui.forms.user;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.exception.ActionTypeNotSupported;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJList;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.RoleFacade;
import com.ganesha.minimarket.facade.UserFacade;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.model.Role;
import com.ganesha.model.User;
import com.ganesha.model.UserRoleLink;

public class UserForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtLogin;
	private XJTextField txtNama;
	private ActionType actionType;
	private XJLabel lblPassword;
	private XJTextField txtPassword;
	private XJButton btnBatal;
	private XJButton btnHapus;

	private XJCheckBox chkDisabled;
	private JSeparator separator;
	private XJPanel pnlRoles;
	private JScrollPane scrollPaneTop;
	private JScrollPane scrollPaneBottom;
	private XJList listRoleTop;
	private XJList listRoleBottom;
	private XJPanel pnlMove;
	private XJButton btnAdd;
	private XJButton btnAddAll;
	private XJButton btnRemove;
	private XJButton btnRemoveAll;
	private XJButton btnGeneratePassword;

	public UserForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Form User");
		setPermissionCode(PermissionConstants.USER_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[400][grow]", "[grow][][10][]"));

		XJPanel pnlInput = new XJPanel();
		getContentPane().add(pnlInput, "cell 0 0,grow");
		pnlInput.setBorder(new XEtchedBorder());
		pnlInput.setLayout(new MigLayout("", "[][grow]", "[][][][]"));

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

		txtPassword = new XJTextField();
		txtPassword.setEditable(false);
		pnlInput.add(txtPassword, "cell 1 2,growx");

		btnGeneratePassword = new XJButton();
		btnGeneratePassword.setMnemonic('G');
		btnGeneratePassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generatePassword();
			}
		});
		btnGeneratePassword
				.setText("<html><center>Generate Password<br/>[Alt+G]</center></html>");
		pnlInput.add(btnGeneratePassword, "cell 1 3,alignx right");

		pnlRoles = new XJPanel();
		getContentPane().add(pnlRoles, "cell 1 0,grow");
		pnlRoles.setLayout(new MigLayout("", "[grow]", "[200][grow][200]"));

		scrollPaneTop = new JScrollPane();
		pnlRoles.add(scrollPaneTop, "cell 0 0,grow");

		listRoleTop = new XJList();
		scrollPaneTop.setViewportView(listRoleTop);

		pnlMove = new XJPanel();
		pnlRoles.add(pnlMove, "cell 0 1,grow");
		pnlMove.setLayout(new MigLayout("", "[150][150]", "[][10][]"));

		btnAdd = new XJButton();
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tambah();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserForm.this, ex);
				}
			}
		});
		btnAdd.setText("Add");
		pnlMove.add(btnAdd, "cell 0 0,growx");

		btnRemoveAll = new XJButton();
		btnRemoveAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					kurangAll();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserForm.this, ex);
				}
			}
		});

		btnAddAll = new XJButton();
		btnAddAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tambahAll();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserForm.this, ex);
				}
			}
		});
		btnAddAll.setText("Add All");
		pnlMove.add(btnAddAll, "cell 1 0,growx");

		btnRemove = new XJButton();
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					kurang();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserForm.this, ex);
				}
			}
		});
		btnRemove.setText("Remove");
		pnlMove.add(btnRemove, "cell 0 2,growx");
		btnRemoveAll.setText("Remove All");
		pnlMove.add(btnRemoveAll, "cell 1 2,growx");

		scrollPaneBottom = new JScrollPane();
		pnlRoles.add(scrollPaneBottom, "cell 0 2,grow");

		listRoleBottom = new XJList();
		scrollPaneBottom.setViewportView(listRoleBottom);

		chkDisabled = new XJCheckBox();
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_SMALLEST));
		chkDisabled.setText("User ini sudah tidak aktif lagi");
		getContentPane().add(chkDisabled, "cell 0 1 2 1,alignx trailing");

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
					save(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserForm.this, ex);
				}
			}
		});
		btnHapus.setText("<html><center>Hapus<br/>User</center></html>");
		pnlButton.add(btnHapus, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		initForm();

		pack();
		setLocationRelativeTo(parent);
	}

	public void setFormDetailValue(User user, List<UserRoleLink> roleRoleLinks) {
		txtLogin.setEditable(false);
		txtLogin.setText(user.getLogin());
		txtNama.setText(user.getName());
		chkDisabled.setSelected(user.getDisabled());

		DefaultListModel<ComboBoxObject> listModelTop = (DefaultListModel<ComboBoxObject>) listRoleTop
				.getModel();

		for (UserRoleLink roleRoleLink : roleRoleLinks) {
			Role role = roleRoleLink.getPrimaryKey().getRole();

			for (int i = 0; i < listModelTop.size(); ++i) {
				Role roleAtTop = (Role) listModelTop.getElementAt(i).getId();

				if (roleAtTop.getId().equals(role.getId())) {
					listRoleTop.setSelectedIndex(i);
					tambah();
				}
			}
		}

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

	private void generatePassword() {
		int random = (int) (Math.random() * 1000000);
		if (random < 100000) {
			generatePassword();
		} else {
			txtPassword.setText(String.valueOf(random));
		}
	}

	private void initForm() {
		Session session = HibernateUtils.openSession();
		try {
			List<Role> roles = RoleFacade.getInstance().getAll(session);
			DefaultListModel<ComboBoxObject> listModel = (DefaultListModel<ComboBoxObject>) listRoleTop
					.getModel();
			listModel.clear();
			for (Role role : roles) {
				ComboBoxObject comboBoxObject = new ComboBoxObject(role,
						role.getName());
				listModel.addElement(comboBoxObject);
			}
		} finally {
			session.close();
		}
	}

	private void kurang() {
		DefaultListModel<ComboBoxObject> listModelTop = (DefaultListModel<ComboBoxObject>) listRoleTop
				.getModel();
		DefaultListModel<ComboBoxObject> listModelBottom = (DefaultListModel<ComboBoxObject>) listRoleBottom
				.getModel();

		int index = listRoleBottom.getSelectedIndex();
		if (index < 0 || index >= listModelBottom.size()) {
			return;
		}

		ComboBoxObject comboBoxObject = listModelBottom.get(index);
		int i = 0;
		for (; i < listModelTop.size(); ++i) {
			Role roleAtTop = (Role) listModelTop.getElementAt(i).getId();

			Role roleAtBottom = (Role) comboBoxObject.getId();

			if (roleAtBottom.getId() > roleAtTop.getId()) {
				/*
				 * Do nothing. Lets assign i = i+1
				 */
			} else {
				break;
			}
		}

		listModelBottom.remove(index);
		listModelTop.add(i, comboBoxObject);
	}

	private void kurangAll() {
		DefaultListModel<ComboBoxObject> listModelBottom = (DefaultListModel<ComboBoxObject>) listRoleBottom
				.getModel();
		while (listModelBottom.size() > 0) {
			listRoleBottom.setSelectedIndex(0);
			kurang();
		}
	}

	private void save(boolean deleted) throws ActionTypeNotSupported,
			UserException, AppException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			UserFacade facade = UserFacade.getInstance();

			List<Role> roles = new ArrayList<>();
			DefaultListModel<ComboBoxObject> listModelBottom = (DefaultListModel<ComboBoxObject>) listRoleBottom
					.getModel();
			for (int i = 0; i < listModelBottom.size(); ++i) {
				ComboBoxObject comboBoxObject = listModelBottom.get(i);
				Role role = (Role) comboBoxObject.getId();
				roles.add(role);
			}

			User user = null;
			if (actionType == ActionType.CREATE) {
				user = facade.addNewUser(txtLogin.getText(), txtNama.getText(),
						txtPassword.getText(), roles, chkDisabled.isSelected(),
						deleted, session);

				dispose();
			} else if (actionType == ActionType.UPDATE) {
				user = facade.updateExistingUser(txtLogin.getText(),
						txtNama.getText(), txtPassword.getText(), roles,
						chkDisabled.isSelected(), deleted, session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}

			ActivityLogFacade.doLog(getPermissionCode(), actionType,
					Main.getUserLogin(), user, session);
			session.getTransaction().commit();

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void tambah() {
		DefaultListModel<ComboBoxObject> listModelTop = (DefaultListModel<ComboBoxObject>) listRoleTop
				.getModel();
		DefaultListModel<ComboBoxObject> listModelBottom = (DefaultListModel<ComboBoxObject>) listRoleBottom
				.getModel();

		int index = listRoleTop.getSelectedIndex();
		if (index < 0 || index >= listModelTop.size()) {
			return;
		}

		ComboBoxObject comboBoxObject = listModelTop.get(index);
		int i = 0;
		for (; i < listModelBottom.size(); ++i) {
			Role roleAtBottom = (Role) listModelBottom.getElementAt(i).getId();

			Role roleAtTop = (Role) comboBoxObject.getId();

			if (roleAtTop.getId() > roleAtBottom.getId()) {
				/*
				 * Do nothing. Lets assign i = i+1
				 */
			} else {
				break;
			}
		}

		listModelTop.remove(index);
		listModelBottom.add(i, comboBoxObject);
	}

	private void tambahAll() {
		DefaultListModel<ComboBoxObject> listModelTop = (DefaultListModel<ComboBoxObject>) listRoleTop
				.getModel();
		while (listModelTop.size() > 0) {
			listRoleTop.setSelectedIndex(0);
			tambah();
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
				&& txtPassword.getText().trim().equals("")) {
			throw new UserException("Password harus diisi");
		}
	}
}
