package com.ganesha.minimarket.ui.forms.revenue;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.constants.CoaCodeConstants;
import com.ganesha.accounting.facade.CoaFacade;
import com.ganesha.accounting.facade.RevenueFacade;
import com.ganesha.accounting.model.Coa;
import com.ganesha.accounting.model.RevenueTransaction;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Enums.ActionType;
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
import com.ganesha.minimarket.utils.PermissionConstants;

public class RevenueTransactionForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSelesai;
	private XJLabel lblCatatan;
	private XJLabel lblAmount;
	private XJTextField txtAmount;
	private XJButton btnBatal;
	private JScrollPane scrollPane;
	private XJTextArea txtNotes;

	public RevenueTransactionForm(Window parent) {
		super(parent);

		setTitle("Input Pendapatan Lain-Lain");
		setPermissionCode(PermissionConstants.REVENUE_TRANSACTION_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(new MigLayout("", "[400]", "[][]"));

		XJPanel pnlKodeRevenue = new XJPanel();
		getContentPane().add(pnlKodeRevenue, "cell 0 0,grow");
		pnlKodeRevenue.setBorder(new XEtchedBorder());
		pnlKodeRevenue
				.setLayout(new MigLayout("", "[150][grow]", "[][][100px]"));

		lblAmount = new XJLabel();
		pnlKodeRevenue.add(lblAmount, "cell 0 0");
		lblAmount.setText("Jumlah (Rp)");

		txtAmount = new XJTextField();
		txtAmount
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		pnlKodeRevenue.add(txtAmount, "cell 1 0,growx");

		lblCatatan = new XJLabel();
		pnlKodeRevenue.add(lblCatatan, "cell 0 1");
		lblCatatan.setText("Catatan");

		scrollPane = new JScrollPane();
		pnlKodeRevenue.add(scrollPane, "cell 0 2 2 1,grow");

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
							RevenueTransactionForm.this, ex);
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

			Coa coa = CoaFacade.getInstance().getDetail(
					CoaCodeConstants.PENDAPATAN_LAIN_LAIN, session);

			RevenueFacade facade = RevenueFacade.getInstance();

			BigDecimal amount = BigDecimal.valueOf(Formatter
					.formatStringToNumber(txtAmount.getText()).doubleValue());

			RevenueTransaction revenueTransaction = facade.performTransaction(coa,
					amount, txtNotes.getText(), session);

			ActivityLogFacade.doLog(getPermissionCode(),
					ActionType.TRANSACTION, Main.getUserLogin(),
					revenueTransaction, session);
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
		if (txtAmount.getText().trim().equals("")) {
			throw new UserException("Jumlah (Rp) harus diisi");
		}

		if (txtNotes.getText().trim().equals("")) {
			throw new UserException("Catatan harus diisi");
		}
	}
}
