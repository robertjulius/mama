package com.ganesha.minimarket.ui.forms.role;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.RoleFacade;
import com.ganesha.model.Role;

public class RoleListDialog extends XJDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtNama;
	private XJTable table;

	private XJButton btnTambah;
	private XJButton btnDetail;
	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.NAME, new XTableParameter(0, 50, false,
				"Nama Role", XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.DESCRIPTION, new XTableParameter(1, 300,
				false, "Deskripsi Role", XTableConstants.CELL_RENDERER_LEFT,
				String.class));
	}

	public RoleListDialog(Window parent) {
		super(parent);

		setTitle("Master Role");
		getContentPane()
				.setLayout(new MigLayout("", "[500]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnDetail.doClick();
			}
		};
		XTableUtils.initTable(table, tableParameters);

		JPanel pnlFilter = new JPanel();
		getContentPane().add(pnlFilter, "cell 0 0,grow");
		pnlFilter.setLayout(new MigLayout("", "[100][grow]", "[]"));

		XJLabel lblNama = new XJLabel();
		lblNama.setText("Nama Role");
		pnlFilter.add(lblNama, "cell 0 0");

		txtNama = new XJTextField();
		txtNama.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadData();
				} catch (Exception ex) {
					ExceptionHandler.handleException(RoleListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(txtNama, "cell 1 0,growx");
		txtNama.setColumns(10);

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));

		btnTambah = new XJButton();
		btnTambah.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tambah();
				} catch (Exception ex) {
					ExceptionHandler.handleException(RoleListDialog.this, ex);
				}
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
				.setText("<html><center>Tambah Role Baru<br/>[F5]</center><html>");

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showDetail();
				} catch (Exception ex) {
					ExceptionHandler.handleException(RoleListDialog.this, ex);
				}
			}
		});
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[Enter]</center></html>");
		panel.add(btnDetail, "cell 2 0");

		try {
			loadData();
		} catch (Exception ex) {
			ExceptionHandler.handleException(this, ex);
		}

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnTambah.doClick();
			break;
		default:
			break;
		}
	}

	private void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String name = txtNama.getText();
			boolean disabled = false;

			RoleFacade facade = RoleFacade.getInstance();

			List<Role> roles = facade.search(name, disabled, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(roles.size());

			for (int i = 0; i < roles.size(); ++i) {
				Role role = roles.get(i);

				tableModel.setValueAt(role.getName(), i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(role.getDescription(), i, tableParameters
						.get(ColumnEnum.DESCRIPTION).getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	private void showDetail() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			String name = (String) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.NAME).getColumnIndex());

			RoleFacade facade = RoleFacade.getInstance();
			Role role = facade.getDetail(name);

			RoleForm roleForm = new RoleForm(this, ActionType.UPDATE);
			roleForm.setFormDetailValue(role);
			roleForm.setVisible(true);

			loadData();
		} finally {
			session.close();
		}
	}

	private void tambah() throws AppException {
		new RoleForm(RoleListDialog.this, ActionType.CREATE).setVisible(true);
		loadData();

		int row = table.getRowCount() - 1;
		int column = table.getSelectedColumn();
		table.requestFocus();
		table.changeSelection(row, column, false, false);
	}

	private enum ColumnEnum {
		NAME, DESCRIPTION
	}
}
