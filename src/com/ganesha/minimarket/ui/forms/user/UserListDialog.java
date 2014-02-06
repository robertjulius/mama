package com.ganesha.minimarket.ui.forms.user;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJRadioButton;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.UserFacade;
import com.ganesha.model.User;
import com.ganesha.model.UserRoleLink;

public class UserListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtLogin;
	private XJTextField txtNama;
	private XJTable table;

	/**
	 * @wbp.nonvisual location=238,101
	 */
	private final ButtonGroup btnGroup = new ButtonGroup();
	private XJButton btnTambah;
	private XJButton btnDetail;
	private XJButton btnRefresh;
	private XJRadioButton rdUserAktif;
	private XJRadioButton rdUserTidakAktif;
	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.LOGIN, new XTableParameter(0, 200,
				false, "Login", XTableConstants.CELL_RENDERER_CENTER,
				String.class));

		tableParameters.put(ColumnEnum.NAME, new XTableParameter(1, 400, false,
				"Nama User", XTableConstants.CELL_RENDERER_LEFT, String.class));
	}

	public UserListDialog(Window parent) {
		super(parent);

		setTitle("Master User");
		getContentPane()
				.setLayout(new MigLayout("", "[600]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnDetail.doClick();
			}
		};
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,grow");
		pnlFilter.setLayout(new MigLayout("", "[100][grow][]", "[][][]"));

		XJLabel lblLogin = new XJLabel();
		lblLogin.setText("Login");
		pnlFilter.add(lblLogin, "cell 0 0");

		txtLogin = new XJTextField();
		txtLogin.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					/*
					 * TODO Perbaiki supaya kalo pas key = alt+tab, ga usah load
					 * data
					 */
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(txtLogin, "cell 1 0 2 1,growx");
		txtLogin.setColumns(10);

		XJLabel lblNama = new XJLabel();
		lblNama.setText("Nama");
		pnlFilter.add(lblNama, "cell 0 1");

		txtNama = new XJTextField();
		txtNama.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(txtNama, "cell 1 1 2 1,growx");
		txtNama.setColumns(10);

		XJPanel pnlRadioButton = new XJPanel();
		pnlFilter.add(pnlRadioButton, "cell 1 2,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][]"));

		rdUserAktif = new XJRadioButton();
		rdUserAktif.setText("User Aktif");
		pnlRadioButton.add(rdUserAktif, "cell 0 0");
		rdUserAktif.setSelected(true);
		btnGroup.add(rdUserAktif);

		rdUserTidakAktif = new XJRadioButton();
		rdUserTidakAktif.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserListDialog.this, ex);
				}
			}
		});
		rdUserTidakAktif.setText("User Tidak Aktif");
		pnlRadioButton.add(rdUserTidakAktif, "cell 0 1");
		btnGroup.add(rdUserTidakAktif);

		btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserListDialog.this, ex);
				}
			}
		});
		btnRefresh.setMnemonic('R');
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");
		pnlFilter.add(btnRefresh, "cell 2 2,grow");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));

		btnTambah = new XJButton();
		btnTambah.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tambah();
			}
		});

		XJButton btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
		panel.add(btnKeluar, "cell 0 0");
		panel.add(btnTambah, "cell 1 0");
		btnTambah
				.setText("<html><center>Tambah User Baru<br/>[F5]</center><html>");

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showDetail();
				} catch (Exception ex) {
					ExceptionHandler.handleException(UserListDialog.this, ex);
				}
			}
		});
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[Enter]</center></html>");
		panel.add(btnDetail, "cell 2 0");

		btnRefresh.doClick();

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String login = txtLogin.getText();
			String name = txtNama.getText();
			boolean disabled = rdUserTidakAktif.isSelected();

			UserFacade facade = UserFacade.getInstance();

			List<User> users = facade.search(login, name, disabled, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(users.size());

			for (int i = 0; i < users.size(); ++i) {
				User user = users.get(i);

				tableModel.setValueAt(user.getLogin(), i,
						tableParameters.get(ColumnEnum.LOGIN).getColumnIndex());

				tableModel.setValueAt(user.getName(), i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	@Override
	protected void keyEventListener(int keyLogin) {
		switch (keyLogin) {
		case KeyEvent.VK_F5:
			btnTambah.doClick();
			break;
		default:
			break;
		}
	}

	private void showDetail() {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			String login = (String) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.LOGIN).getColumnIndex());

			UserFacade facade = UserFacade.getInstance();
			User user = facade.getDetail(login, session);

			List<UserRoleLink> userRoleLinks = facade.getUserRoleLinks(
					user.getId(), session);

			UserForm userForm = new UserForm(this, ActionType.UPDATE);
			userForm.setFormDetailValue(user, userRoleLinks);
			userForm.setVisible(true);

			btnRefresh.doClick();
		} finally {
			session.close();
		}
	}

	private void tambah() {
		new UserForm(UserListDialog.this, ActionType.CREATE).setVisible(true);
		btnRefresh.doClick();

		int row = table.getRowCount() - 1;
		int column = table.getSelectedColumn();
		table.requestFocus();
		table.changeSelection(row, column, false, false);
	}

	private enum ColumnEnum {
		LOGIN, NAME
	}
}
