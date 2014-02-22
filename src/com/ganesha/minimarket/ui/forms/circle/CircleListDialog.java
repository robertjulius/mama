package com.ganesha.minimarket.ui.forms.circle;

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

import com.ganesha.accounting.constants.Enums.CircleUnit;
import com.ganesha.accounting.facade.CircleFacade;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
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
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.model.Circle;
import com.ganesha.minimarket.utils.PermissionConstants;

public class CircleListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtName;
	private XJComboBox cmbUnit;
	private XJTable table;

	/**
	 * @wbp.nonvisual location=238,101
	 */
	private final ButtonGroup btnGroup = new ButtonGroup();
	private XJButton btnTambah;
	private XJButton btnDetail;
	private XJButton btnRefresh;
	private XJRadioButton rdCircleAktif;
	private XJRadioButton rdCircleTidakAktif;
	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.NAME, new XTableParameter(0, 50, false,
				"Nama Siklus", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(1, 50, false,
				"Satuan", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.DURATION, new XTableParameter(2, 50,
				false, "Durasi", false, XTableConstants.CELL_RENDERER_CENTER,
				String.class));

		tableParameters.put(ColumnEnum.ID, new XTableParameter(3, 0, false,
				"ID", true, XTableConstants.CELL_RENDERER_LEFT, Integer.class));
	}

	public CircleListDialog(Window parent) {
		super(parent);

		setTitle("Master Circle");
		setPermissionCode(PermissionConstants.SUPPLIER_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[1000,grow]", "[][300,grow][]"));

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
		pnlFilter.setLayout(new MigLayout("", "[100][grow][]", "[][][grow]"));

		XJLabel lblName = new XJLabel();
		lblName.setText("Name Siklus");
		pnlFilter.add(lblName, "cell 0 0");

		txtName = new XJTextField();
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					/*
					 * TODO Perbaiki supaya kalo pas key = alt+tab, ga usah load
					 * data
					 */
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CircleListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(txtName, "cell 1 0 2 1,growx");
		txtName.setColumns(10);

		XJLabel lblSatuan = new XJLabel();
		lblSatuan.setText("Satuan");
		pnlFilter.add(lblSatuan, "cell 0 1");

		cmbUnit = new XJComboBox(GeneralConstants.CMB_BOX_CIRCLE_UNITS);
		cmbUnit.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CircleListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(cmbUnit, "cell 1 1 2 1,growx");

		XJPanel pnlRadioButton = new XJPanel();
		pnlFilter.add(pnlRadioButton, "cell 1 2,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][]"));

		rdCircleAktif = new XJRadioButton();
		rdCircleAktif.setText("Circle Aktif");
		pnlRadioButton.add(rdCircleAktif, "cell 0 0");
		rdCircleAktif.setSelected(true);
		btnGroup.add(rdCircleAktif);

		rdCircleTidakAktif = new XJRadioButton();
		rdCircleTidakAktif.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CircleListDialog.this, ex);
				}
			}
		});
		rdCircleTidakAktif.setText("Circle Tidak Aktif");
		pnlRadioButton.add(rdCircleTidakAktif, "cell 0 1");
		btnGroup.add(rdCircleTidakAktif);

		btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CircleListDialog.this, ex);
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
				.setText("<html><center>Tambah Circle Baru<br/>[F5]</center><html>");

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDetail();
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
			String name = txtName.getText();
			CircleUnit unit = (CircleUnit) ((ComboBoxObject) cmbUnit
					.getSelectedItem()).getId();
			boolean disabled = rdCircleTidakAktif.isSelected();

			CircleFacade facade = CircleFacade.getInstance();

			List<Circle> circles = facade.search(name, unit, disabled, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(circles.size());

			for (int i = 0; i < circles.size(); ++i) {
				Circle circle = circles.get(i);

				tableModel.setValueAt(circle.getName(), i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(circle.getUnit(), i,
						tableParameters.get(ColumnEnum.UNIT).getColumnIndex());

				tableModel.setValueAt(circle.getDuration(), i, tableParameters
						.get(ColumnEnum.DURATION).getColumnIndex());

				tableModel.setValueAt(circle.getId(), i,
						tableParameters.get(ColumnEnum.ID).getColumnIndex());
			}
		} finally {
			session.close();
		}
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

	private void showDetail() {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			int id = (int) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.ID).getColumnIndex());

			CircleFacade facade = CircleFacade.getInstance();
			Circle circle = facade.getDetail(id, session);

			CircleForm circleForm = new CircleForm(this, ActionType.UPDATE);
			circleForm.setFormDetailValue(circle);
			circleForm.setVisible(true);

			btnRefresh.doClick();
		} finally {
			session.close();
		}
	}

	private void tambah() {
		new CircleForm(CircleListDialog.this, ActionType.CREATE)
				.setVisible(true);
		btnRefresh.doClick();

		int row = table.getRowCount() - 1;
		int column = table.getSelectedColumn();
		table.requestFocus();
		table.changeSelection(row, column, false, false);
	}

	private enum ColumnEnum {
		NAME, UNIT, DURATION, ID
	}
}
