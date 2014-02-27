package com.ganesha.accounting.ui.forms.expense;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.facade.ExpenseFacade;
import com.ganesha.accounting.model.Circle;
import com.ganesha.accounting.model.Coa;
import com.ganesha.accounting.model.Expense;
import com.ganesha.core.exception.UserException;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.exception.ActionTypeNotSupported;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.utils.PermissionConstants;

public class ExpenseForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJComboBox cmbCoa;
	private XJTextField txtName;
	private ActionType actionType;
	private JSeparator separator;
	private XJLabel lblSiklus;
	private XJComboBox cmbCircle;
	private XJButton btnBatal;
	private XJButton btnHapus;
	private XJPanel pnlDisable;
	private XJCheckBox chkDisabled;

	private Integer expenseId;

	public ExpenseForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Form Pengaturan Beban");
		setPermissionCode(PermissionConstants.EXPENSE_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[600,grow]", "[grow][grow][10][grow]"));

		XJPanel pnlInput = new XJPanel();
		pnlInput.setBorder(new XEtchedBorder());
		getContentPane().add(pnlInput, "cell 0 0,grow");
		pnlInput.setLayout(new MigLayout("", "[150][grow]", "[][][]"));

		XJLabel lblNama = new XJLabel();
		pnlInput.add(lblNama, "cell 0 0");
		lblNama.setText("Nama Beban");

		txtName = new XJTextField();
		txtName.setUpperCaseOnFocusLost(true);
		pnlInput.add(txtName, "cell 1 0,growx");

		XJLabel lblAkun = new XJLabel();
		pnlInput.add(lblAkun, "cell 0 1");
		lblAkun.setText("Akun");

		cmbCoa = new XJComboBox();
		pnlInput.add(cmbCoa, "cell 1 1,growx");

		lblSiklus = new XJLabel();
		pnlInput.add(lblSiklus, "cell 0 2");
		lblSiklus.setText("Siklus");

		cmbCircle = new XJComboBox();
		cmbCircle.setEditable(false);
		pnlInput.add(cmbCircle, "cell 1 2,growx");

		pnlDisable = new XJPanel();
		getContentPane().add(pnlDisable, "cell 0 1,alignx right,growy");
		pnlDisable.setLayout(new MigLayout("", "[]", "[]"));

		chkDisabled = new XJCheckBox();
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_SMALLEST));
		chkDisabled.setText("Beban ini sudah tidak aktif lagi");
		pnlDisable.add(chkDisabled, "cell 0 0");

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 2,grow");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 3,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[grow]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(false);
				} catch (Exception ex) {
					ExceptionHandler.handleException(ExpenseForm.this, ex);
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
					ExceptionHandler.handleException(ExpenseForm.this, ex);
				}
			}
		});
		btnHapus.setText("<html><center>Hapus<br/>Beban</center></html>");
		pnlButton.add(btnHapus, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		try {
			initForm();
		} catch (Exception ex) {
			ExceptionHandler.handleException(ExpenseForm.this, ex);
		}

		pack();
		setLocationRelativeTo(null);
	}

	public Integer getIdBarang() {
		return expenseId;
	}

	public void setFormDetailValue(Expense expense) {
		expenseId = expense.getId();

		txtName.setText(expense.getName());
		cmbCoa.setSelectedItem(expense.getCoa());
		cmbCircle.setSelectedItem(expense.getCircle());
		chkDisabled.setSelected(expense.getDisabled());

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

	private void initForm() throws ActionTypeNotSupported {
		ExpenseListDialog.initComboBox(cmbCoa, cmbCircle);
		if (actionType == ActionType.CREATE) {
			/*
			 * Do nothing
			 */
		} else if (actionType == ActionType.UPDATE) {
			txtName.setEditable(false);
			cmbCoa.setEditable(false);
		} else {
			throw new ActionTypeNotSupported(actionType);
		}
	}

	private void save(boolean deleted) throws ActionTypeNotSupported,
			UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			ExpenseFacade facade = ExpenseFacade.getInstance();

			String name = txtName.getText();

			Coa coa = (Coa) ((ComboBoxObject) cmbCoa.getSelectedItem())
					.getObject();
			Circle circle = (Circle) ((ComboBoxObject) cmbCircle
					.getSelectedItem()).getObject();

			boolean disabled = chkDisabled.isSelected();

			Expense expense = null;
			if (actionType == ActionType.CREATE) {
				expense = facade.addNewExpense(name, coa, circle, disabled,
						deleted, session);
				expenseId = expense.getId();
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				expense = facade.updateExistingExpense(expenseId, name, coa,
						circle, disabled, deleted, session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}

			ActivityLogFacade.doLog(getPermissionCode(), actionType,
					Main.getUserLogin(), expense, session);
			session.getTransaction().commit();

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void validateForm() throws UserException {
		if (txtName.getText().trim().equals("")) {
			throw new UserException("Nama Beban harus diisi");
		}

		Coa coa = (Coa) ((ComboBoxObject) cmbCoa.getSelectedItem()).getObject();
		if (coa == null) {
			throw new UserException("Akun Harus harus diisi");
		}

		Circle circle = (Circle) ((ComboBoxObject) cmbCircle.getSelectedItem())
				.getObject();
		if (circle == null) {
			throw new UserException("Siklus harus diisi");
		}
	}
}
