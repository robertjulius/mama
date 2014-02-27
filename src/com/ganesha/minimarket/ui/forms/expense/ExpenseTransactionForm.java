package com.ganesha.minimarket.ui.forms.expense;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.facade.ExpenseFacade;
import com.ganesha.accounting.model.Expense;
import com.ganesha.accounting.model.ExpenseTransaction;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextArea;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.utils.PermissionConstants;

public class ExpenseTransactionForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSelesai;
	private XJComboBox cmbExpense;
	private XJLabel lblCatatan;
	private XJLabel lblAmount;
	private XJTextField txtAmount;
	private XJButton btnBatal;
	private JScrollPane scrollPane;
	private XJTextArea txtNotes;

	public ExpenseTransactionForm(Window parent) {
		super(parent);

		setTitle("Beban Lain-Lain");
		setPermissionCode(PermissionConstants.EXPENSE_TRANSACTION_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(new MigLayout("", "[400]", "[][]"));

		XJPanel pnlKodeExpense = new XJPanel();
		getContentPane().add(pnlKodeExpense, "cell 0 0,grow");
		pnlKodeExpense.setBorder(new XEtchedBorder());
		pnlKodeExpense.setLayout(new MigLayout("", "[150][grow]",
				"[][][][100px]"));

		XJLabel lblBeban = new XJLabel();
		pnlKodeExpense.add(lblBeban, "cell 0 0");
		lblBeban.setText("Beban/Pengeluaran");

		cmbExpense = new XJComboBox();
		pnlKodeExpense.add(cmbExpense, "cell 1 0,growx");

		lblAmount = new XJLabel();
		pnlKodeExpense.add(lblAmount, "cell 0 1");
		lblAmount.setText("Jumlah (Rp)");

		txtAmount = new XJTextField();
		txtAmount
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		pnlKodeExpense.add(txtAmount, "cell 1 1,growx");

		lblCatatan = new XJLabel();
		pnlKodeExpense.add(lblCatatan, "cell 0 2");
		lblCatatan.setText("Catatan");

		scrollPane = new JScrollPane();
		pnlKodeExpense.add(scrollPane, "cell 0 3 2 1,grow");

		txtNotes = new XJTextArea();
		scrollPane.setViewportView(txtNotes);

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnSelesai = new XJButton();
		btnSelesai.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ExpenseTransactionForm.this, ex);
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
		btnSelesai
				.setText("<html><center>Selesai & Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSelesai, "cell 1 0");

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnSelesai.doClick();
			break;
		default:
			break;
		}
	}

	private void batal() {
		dispose();
	}

	private void save() throws Exception {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			ExpenseFacade facade = ExpenseFacade.getInstance();

			Expense expense = (Expense) ((ComboBoxObject) cmbExpense
					.getSelectedItem()).getObject();

			BigDecimal amount = BigDecimal.valueOf(Formatter
					.formatStringToNumber(txtAmount.getText()).doubleValue());

			ExpenseTransaction expenseTransaction = facade.addTransaction(
					expense, amount, txtNotes.getText(), session);

			ActivityLogFacade.doLog(getPermissionCode(),
					ActionType.TRANSACTION, Main.getUserLogin(),
					expenseTransaction, session);
			session.getTransaction().commit();

			dispose();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void validateForm() throws UserException {
		Expense expense = (Expense) ((ComboBoxObject) cmbExpense
				.getSelectedItem()).getObject();
		if (expense == null) {
			throw new UserException("Beban/Pengeluaran harus diisi");
		}

		if (txtAmount.getText().trim().equals("")) {
			throw new UserException("Jumlah (Rp) harus diisi");
		}
	}
}
