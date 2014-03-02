package com.ganesha.accounting.ui.forms.expense;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.constants.CoaGroupCodeConstants;
import com.ganesha.accounting.facade.CircleFacade;
import com.ganesha.accounting.facade.CoaFacade;
import com.ganesha.accounting.facade.ExpenseFacade;
import com.ganesha.accounting.model.Circle;
import com.ganesha.accounting.model.Coa;
import com.ganesha.accounting.model.Expense;
import com.ganesha.core.exception.AppException;
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
import com.ganesha.minimarket.facade.GlobalFacade;
import com.ganesha.minimarket.utils.PermissionConstants;

public class ExpenseListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtName;
	private XJComboBox cmbCoa;
	private XJTable table;

	/**
	 * @wbp.nonvisual location=238,101
	 */
	private final ButtonGroup btnGroup = new ButtonGroup();
	private XJButton btnTambah;
	private XJButton btnDetail;
	private XJComboBox cmbCircle;
	private XJButton btnRefresh;
	private XJRadioButton rdExpenseAktif;
	private XJRadioButton rdExpenseTidakAktif;
	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();

	protected static void initComboBox(XJComboBox cmbCoa, XJComboBox cmbCircle) {
		Session session = HibernateUtils.openSession();
		try {
			List<Coa> coaList = CoaFacade.getInstance().search(null,
					CoaGroupCodeConstants.BEBAN, false, session);
			{
				List<ComboBoxObject> comboBoxObjects = new ArrayList<>();
				for (Coa coa : coaList) {
					comboBoxObjects.add(new ComboBoxObject(coa, coa.getName()));
				}
				comboBoxObjects.add(0, new ComboBoxObject(null, null));
				cmbCoa.setModel(new DefaultComboBoxModel<ComboBoxObject>(
						comboBoxObjects.toArray(new ComboBoxObject[0])));
			}

			List<Circle> circles = CircleFacade.getInstance().search(null,
					null, false, session);
			{
				List<ComboBoxObject> comboBoxObjects = new ArrayList<>();
				for (Circle circle : circles) {
					comboBoxObjects.add(new ComboBoxObject(circle, circle
							.getName()));
				}
				comboBoxObjects.add(0, new ComboBoxObject(null, null));
				cmbCircle.setModel(new DefaultComboBoxModel<ComboBoxObject>(
						comboBoxObjects.toArray(new ComboBoxObject[0])));
			}
		} finally {
			session.close();
		}
	}

	{
		tableParameters.put(ColumnEnum.NAME, new XTableParameter(0, 50, false,
				"Nama Beban", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.COA, new XTableParameter(1, 50, false,
				"Jurnal", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.CIRCLE, new XTableParameter(2, 50,
				false, "Siklus", false, XTableConstants.CELL_RENDERER_CENTER,
				String.class));

		tableParameters.put(ColumnEnum.ID, new XTableParameter(3, 0, false,
				"ID", true, XTableConstants.CELL_RENDERER_LEFT, Integer.class));
	}

	public ExpenseListDialog(Window parent) {
		super(parent);

		setTitle("Master Beban");
		setPermissionCode(PermissionConstants.EXPENSE_LIST);
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
		pnlFilter.setLayout(new MigLayout("", "[100][grow][]", "[][][][grow]"));

		XJLabel lblName = new XJLabel();
		lblName.setText("Nama Beban");
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
					ExceptionHandler
							.handleException(ExpenseListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(txtName, "cell 1 0 2 1,growx");
		txtName.setColumns(10);

		XJLabel lblCoa = new XJLabel();
		lblCoa.setText("Akun");
		pnlFilter.add(lblCoa, "cell 0 1");

		cmbCoa = new XJComboBox();
		cmbCoa.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(ExpenseListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(cmbCoa, "cell 1 1 2 1,growx");

		XJLabel lblCircle = new XJLabel();
		lblCircle.setText("Siklus");
		pnlFilter.add(lblCircle, "cell 0 2");

		cmbCircle = new XJComboBox();
		cmbCircle.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(ExpenseListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(cmbCircle, "cell 1 2 2 1,growx");

		XJPanel pnlRadioButton = new XJPanel();
		pnlFilter.add(pnlRadioButton, "cell 1 3,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][]"));

		rdExpenseAktif = new XJRadioButton();
		rdExpenseAktif.setText("Beban Aktif");
		pnlRadioButton.add(rdExpenseAktif, "cell 0 0");
		rdExpenseAktif.setSelected(true);
		btnGroup.add(rdExpenseAktif);

		rdExpenseTidakAktif = new XJRadioButton();
		rdExpenseTidakAktif.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(ExpenseListDialog.this, ex);
				}
			}
		});
		rdExpenseTidakAktif.setText("Beban Tidak Aktif");
		pnlRadioButton.add(rdExpenseTidakAktif, "cell 0 1");
		btnGroup.add(rdExpenseTidakAktif);

		btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(ExpenseListDialog.this, ex);
				}
			}
		});
		btnRefresh.setMnemonic('R');
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");
		pnlFilter.add(btnRefresh, "cell 2 3,grow");

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
				.setText("<html><center>Tambah Beban Baru<br/>[F5]</center><html>");

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

		initComboBox(cmbCoa, cmbCircle);
		btnRefresh.doClick();

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String name = txtName.getText();
			Coa coa = (Coa) GlobalFacade.getInstance().getComboBoxSelectedItem(
					cmbCoa);
			Circle circle = (Circle) GlobalFacade.getInstance()
					.getComboBoxSelectedItem(cmbCircle);
			boolean disabled = rdExpenseTidakAktif.isSelected();

			ExpenseFacade facade = ExpenseFacade.getInstance();

			List<Expense> expenses = facade.search(name, coa, circle, "id",
					disabled, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(expenses.size());

			for (int i = 0; i < expenses.size(); ++i) {
				Expense expense = expenses.get(i);

				tableModel.setValueAt(expense.getName(), i, tableParameters
						.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(expense.getCoa().getName(), i,
						tableParameters.get(ColumnEnum.COA).getColumnIndex());

				tableModel
						.setValueAt(expense.getCircle().getName(), i,
								tableParameters.get(ColumnEnum.CIRCLE)
										.getColumnIndex());

				tableModel.setValueAt(expense.getId(), i,
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

			ExpenseFacade facade = ExpenseFacade.getInstance();
			Expense expense = facade.getDetail(id, session);

			ExpenseForm expenseForm = new ExpenseForm(this, ActionType.UPDATE);
			expenseForm.setFormDetailValue(expense);
			expenseForm.setVisible(true);

			btnRefresh.doClick();
		} finally {
			session.close();
		}
	}

	private void tambah() {
		new ExpenseForm(ExpenseListDialog.this, ActionType.CREATE)
				.setVisible(true);
		btnRefresh.doClick();

		int row = table.getRowCount() - 1;
		int column = table.getSelectedColumn();
		table.requestFocus();
		table.changeSelection(row, column, false, false);
	}

	private enum ColumnEnum {
		NAME, COA, CIRCLE, ID
	}
}
