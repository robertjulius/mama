package com.ganesha.minimarket.ui.forms.role;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.ActionTypeNotSupported;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJList;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.PermissionFacade;
import com.ganesha.minimarket.facade.RoleFacade;
import com.ganesha.model.Permission;
import com.ganesha.model.Role;
import com.ganesha.model.RolePermissionLink;

public class RoleForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtRoleName;
	private ActionType actionType;
	private XJButton btnBatal;
	private XJButton btnHapus;
	private XJPanel pnlRoleName;
	private XJPanel pnlPermissionList;
	private JScrollPane scrollPaneLeft;
	private JScrollPane scrollPaneRight;
	private XJList listPermissionLeft;
	private XJList listPermissionRight;
	private XJPanel pnlMove;
	private XJButton btnAddAll;
	private XJButton btnAdd;
	private XJButton btnRemove;
	private XJButton btnRemoveAll;
	private XJLabel lblDescription;
	private XJTextField txtDescription;
	private XJLabel lblRoleId;
	private XJLabel lblRoleIdValue;

	public RoleForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Form Role");
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[grow][grow]", "[][grow][]"));

		pnlRoleName = new XJPanel();
		getContentPane().add(pnlRoleName, "cell 0 0 2 1,grow");
		pnlRoleName.setLayout(new MigLayout("", "[][grow]", "[][][]"));

		lblRoleId = new XJLabel();
		lblRoleId.setText("ID");
		pnlRoleName.add(lblRoleId, "cell 0 0");

		lblRoleIdValue = new XJLabel();
		lblRoleIdValue.setText("[Automatic Generated]");
		pnlRoleName.add(lblRoleIdValue, "cell 1 0");

		XJLabel lblNameRole = new XJLabel();
		pnlRoleName.add(lblNameRole, "cell 0 1");
		lblNameRole.setText("Nama Role");

		txtRoleName = new XJTextField();
		pnlRoleName.add(txtRoleName, "cell 1 1,growx");

		lblDescription = new XJLabel();
		lblDescription.setText("Deskripsi Role");
		pnlRoleName.add(lblDescription, "cell 0 2");

		txtDescription = new XJTextField();
		pnlRoleName.add(txtDescription, "cell 1 2,growx");

		pnlPermissionList = new XJPanel();
		getContentPane().add(pnlPermissionList, "cell 0 1 2 1,grow");
		pnlPermissionList
				.setLayout(new MigLayout("", "[500][][500]", "[grow]"));

		scrollPaneLeft = new JScrollPane();
		pnlPermissionList.add(scrollPaneLeft, "cell 0 0,grow");

		listPermissionLeft = new XJList();
		scrollPaneLeft.setViewportView(listPermissionLeft);

		pnlMove = new XJPanel();
		pnlPermissionList.add(pnlMove, "cell 1 0,grow");
		pnlMove.setLayout(new MigLayout("", "[]", "[][][20][][]"));

		btnAdd = new XJButton();
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tambah();
				} catch (Exception ex) {
					ExceptionHandler.handleException(RoleForm.this, ex);
				}
			}
		});
		btnAdd.setText("Add");
		pnlMove.add(btnAdd, "cell 0 0,growx");

		btnAddAll = new XJButton();
		btnAddAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tambahAll();
				} catch (Exception ex) {
					ExceptionHandler.handleException(RoleForm.this, ex);
				}
			}
		});
		btnAddAll.setText("Add All");
		pnlMove.add(btnAddAll, "cell 0 1,growx");

		btnRemove = new XJButton();
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					kurang();
				} catch (Exception ex) {
					ExceptionHandler.handleException(RoleForm.this, ex);
				}
			}
		});
		btnRemove.setText("Remove");
		pnlMove.add(btnRemove, "cell 0 3,growx");

		btnRemoveAll = new XJButton();
		btnRemoveAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					kurangAll();
				} catch (Exception ex) {
					ExceptionHandler.handleException(RoleForm.this, ex);
				}
			}
		});
		btnRemoveAll.setText("Remove All");
		pnlMove.add(btnRemoveAll, "cell 0 4,growx");

		scrollPaneRight = new JScrollPane();
		pnlPermissionList.add(scrollPaneRight, "cell 2 0,grow");

		listPermissionRight = new XJList();
		scrollPaneRight.setViewportView(listPermissionRight);

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 2 2 1,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(false);
				} catch (Exception ex) {
					ExceptionHandler.handleException(RoleForm.this, ex);
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
					ExceptionHandler.handleException(RoleForm.this, ex);
				}
			}
		});
		btnHapus.setText("<html><center>Hapus<br/>Role</center></html>");
		pnlButton.add(btnHapus, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		initForm();

		pack();
		setLocationRelativeTo(null);
	}

	public void setFormDetailValue(Role role,
			List<RolePermissionLink> rolePermissionLinks) {
		lblRoleIdValue.setText(String.valueOf(role.getId()));
		txtRoleName.setText(role.getName());
		txtDescription.setText(role.getDescription());

		DefaultListModel<ComboBoxObject> listModelLeft = (DefaultListModel<ComboBoxObject>) listPermissionLeft
				.getModel();

		for (RolePermissionLink rolePermissionLink : rolePermissionLinks) {
			Permission permission = rolePermissionLink.getPrimaryKey()
					.getPermission();

			for (int i = 0; i < listModelLeft.size(); ++i) {
				Permission permissionAtLeft = (Permission) listModelLeft
						.getElementAt(i).getId();

				if (permissionAtLeft.getCode().equals(permission.getCode())) {
					listPermissionLeft.setSelectedIndex(i);
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

	private void initForm() {
		Session session = HibernateUtils.openSession();
		try {
			List<Permission> permissions = PermissionFacade.getInstance()
					.getAll(session);
			DefaultListModel<ComboBoxObject> listModel = (DefaultListModel<ComboBoxObject>) listPermissionLeft
					.getModel();
			listModel.clear();
			for (Permission permission : permissions) {
				ComboBoxObject comboBoxObject = new ComboBoxObject(permission,
						permission.getName());
				listModel.addElement(comboBoxObject);
			}
		} finally {
			session.close();
		}
	}

	private void kurang() {
		DefaultListModel<ComboBoxObject> listModelLeft = (DefaultListModel<ComboBoxObject>) listPermissionLeft
				.getModel();
		DefaultListModel<ComboBoxObject> listModelRight = (DefaultListModel<ComboBoxObject>) listPermissionRight
				.getModel();

		int index = listPermissionRight.getSelectedIndex();
		if (index < 0 || index >= listModelRight.size()) {
			return;
		}

		ComboBoxObject comboBoxObject = listModelRight.get(index);
		int i = 0;
		for (; i < listModelLeft.size(); ++i) {
			Permission permissionAtLeft = (Permission) listModelLeft
					.getElementAt(i).getId();

			Permission permissionAtRight = (Permission) comboBoxObject.getId();

			if (permissionAtRight.getOrderNum() > permissionAtLeft
					.getOrderNum()) {
				/*
				 * Do nothing. Lets assign i = i+1
				 */
			} else {
				break;
			}
		}

		listModelRight.remove(index);
		listModelLeft.add(i, comboBoxObject);
	}

	private void kurangAll() {
		DefaultListModel<ComboBoxObject> listModelRight = (DefaultListModel<ComboBoxObject>) listPermissionRight
				.getModel();
		while (listModelRight.size() > 0) {
			listPermissionRight.setSelectedIndex(0);
			kurang();
		}
	}

	private void save(boolean deleted) throws ActionTypeNotSupported,
			UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			RoleFacade facade = RoleFacade.getInstance();

			List<Permission> permissions = new ArrayList<>();
			DefaultListModel<ComboBoxObject> listModelRight = (DefaultListModel<ComboBoxObject>) listPermissionRight
					.getModel();
			for (int i = 0; i < listModelRight.size(); ++i) {
				ComboBoxObject comboBoxObject = listModelRight.get(i);
				Permission permission = (Permission) comboBoxObject.getId();
				permissions.add(permission);
			}

			if (actionType == ActionType.CREATE) {
				facade.addNewRole(txtRoleName.getText(),
						txtDescription.getText(), permissions, session);
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				facade.updateExistingRole(
						Integer.parseInt(lblRoleIdValue.getText()),
						txtRoleName.getText(), txtDescription.getText(),
						permissions, session);
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

	private void tambah() {
		DefaultListModel<ComboBoxObject> listModelLeft = (DefaultListModel<ComboBoxObject>) listPermissionLeft
				.getModel();
		DefaultListModel<ComboBoxObject> listModelRight = (DefaultListModel<ComboBoxObject>) listPermissionRight
				.getModel();

		int index = listPermissionLeft.getSelectedIndex();
		if (index < 0 || index >= listModelLeft.size()) {
			return;
		}

		ComboBoxObject comboBoxObject = listModelLeft.get(index);
		int i = 0;
		for (; i < listModelRight.size(); ++i) {
			Permission permissionAtRight = (Permission) listModelRight
					.getElementAt(i).getId();

			Permission permissionAtLeft = (Permission) comboBoxObject.getId();

			if (permissionAtLeft.getOrderNum() > permissionAtRight
					.getOrderNum()) {
				/*
				 * Do nothing. Lets assign i = i+1
				 */
			} else {
				break;
			}
		}

		listModelLeft.remove(index);
		listModelRight.add(i, comboBoxObject);
	}

	private void tambahAll() {
		DefaultListModel<ComboBoxObject> listModelLeft = (DefaultListModel<ComboBoxObject>) listPermissionLeft
				.getModel();
		while (listModelLeft.size() > 0) {
			listPermissionLeft.setSelectedIndex(0);
			tambah();
		}
	}

	private void validateForm() throws UserException {
		if (txtRoleName.getText().trim().equals("")) {
			throw new UserException("Kode Role harus diisi");
		}
	}
}
