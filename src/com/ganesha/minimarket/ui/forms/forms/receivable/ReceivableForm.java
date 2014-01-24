package com.ganesha.minimarket.ui.forms.forms.receivable;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants.AccountAction;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTextArea;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.ReceivableFacade;
import com.ganesha.minimarket.model.ReceivableSummary;
import com.ganesha.minimarket.model.Supplier;

public class ReceivableForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJLabel lblLastRemainingPayment;
	private XJTextField txtLastRemainingPayment;
	private XJLabel lblCatatan;
	private JPanel pnlKontakPerson1;
	private XJLabel lblAmount;
	private XJTextField txtAmount;
	private XJLabel lblRemainingPayment;
	private XJTextField txtRemainingPayment;
	private XJButton btnBatal;
	private JScrollPane scrollPane;
	private XJTextArea txtCatatan;
	private JSeparator separator;

	private Supplier supplier;

	public ReceivableForm(Window parent) {
		super(parent);

		setTitle("Pembayaran Piutang");
		setCloseOnEsc(false);

		getContentPane().setLayout(new MigLayout("", "[400][]", "[][]"));

		JPanel pnlKodeReceivable = new JPanel();
		getContentPane().add(pnlKodeReceivable, "cell 0 0,grow");
		pnlKodeReceivable.setBorder(new EtchedBorder(EtchedBorder.LOWERED,
				null, null));
		pnlKodeReceivable.setLayout(new MigLayout("", "[150][grow]",
				"[][][][10][][]"));

		XJLabel lblKode = new XJLabel();
		pnlKodeReceivable.add(lblKode, "cell 0 0");
		lblKode.setText("Kode");

		txtKode = new XJTextField();
		txtKode.setEditable(false);
		pnlKodeReceivable.add(txtKode, "cell 1 0,growx");

		XJLabel lblNama = new XJLabel();
		pnlKodeReceivable.add(lblNama, "cell 0 1");
		lblNama.setText("Nama");

		txtNama = new XJTextField();
		txtNama.setEditable(false);
		pnlKodeReceivable.add(txtNama, "cell 1 1,growx");

		lblLastRemainingPayment = new XJLabel();
		lblLastRemainingPayment.setText("Sisa Piutang");
		pnlKodeReceivable.add(lblLastRemainingPayment, "cell 0 2");

		txtLastRemainingPayment = new XJTextField();
		txtLastRemainingPayment.setEditable(false);
		pnlKodeReceivable.add(txtLastRemainingPayment, "cell 1 2,growx");

		separator = new JSeparator();
		pnlKodeReceivable.add(separator, "cell 0 3 2 1,growx,aligny center");

		lblAmount = new XJLabel();
		pnlKodeReceivable.add(lblAmount, "cell 0 4");
		lblAmount.setText("Uang Diterima");

		txtAmount = new XJTextField();
		txtAmount.setForeground(COLOR_TRASACTIONABLE);
		txtAmount.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				setTotalDanSisaPembayan();
			}
		});
		pnlKodeReceivable.add(txtAmount, "cell 1 4,growx");

		lblRemainingPayment = new XJLabel();
		pnlKodeReceivable.add(lblRemainingPayment, "cell 0 5");
		lblRemainingPayment.setText("Sisa");

		txtRemainingPayment = new XJTextField();
		txtRemainingPayment.setEditable(false);
		txtRemainingPayment.setForeground(COLOR_WARNING);
		pnlKodeReceivable.add(txtRemainingPayment, "cell 1 5,growx");

		pnlKontakPerson1 = new JPanel();
		getContentPane().add(pnlKontakPerson1, "cell 1 0,grow");
		pnlKontakPerson1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));
		pnlKontakPerson1.setLayout(new MigLayout("", "[300,grow]", "[][grow]"));

		lblCatatan = new XJLabel();
		pnlKontakPerson1.add(lblCatatan, "cell 0 0");
		lblCatatan.setText("Catatan");

		scrollPane = new JScrollPane();
		pnlKontakPerson1.add(scrollPane, "cell 0 1,grow");

		txtCatatan = new XJTextArea();
		scrollPane.setViewportView(txtCatatan);

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 1 2 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
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
		btnSimpan
				.setText("<html><center>Selesai & Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 1 0");

		pack();
		setLocationRelativeTo(null);
	}

	public void setFormDetailValue(ReceivableSummary receivableSummary,
			Supplier supplier) {
		Session session = HibernateUtils.openSession();
		try {
			this.supplier = supplier;

			txtKode.setText(supplier.getCode());
			txtNama.setText(supplier.getName());
			txtLastRemainingPayment.setText(Formatter
					.formatNumberToString(receivableSummary
							.getRemainingAmount().doubleValue()));
			txtAmount.setText("0");
			txtRemainingPayment.setText(txtLastRemainingPayment.getText());
		} finally {
			session.close();
		}
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

	private void save() throws Exception {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			ReceivableFacade facade = ReceivableFacade.getInstance();

			Date maturityDate = CommonUtils.getNextDate(1, Calendar.YEAR,
					CommonUtils.getCurrentDate());

			facade.addTransaction(
					supplier.getId(),
					AccountAction.DECREASE,
					maturityDate,
					BigDecimal.valueOf(Formatter.formatStringToNumber(
							txtAmount.getText()).doubleValue()),
					txtCatatan.getText(), session);

			session.getTransaction().commit();
			dispose();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void setTotalDanSisaPembayan() {
		double sisaSebelumnya = Formatter.formatStringToNumber(
				txtLastRemainingPayment.getText()).doubleValue();

		double pembayaran = Formatter.formatStringToNumber(txtAmount.getText())
				.doubleValue();
		txtAmount.setText(Formatter.formatNumberToString(pembayaran));

		double sisaSaatIni = sisaSebelumnya - pembayaran;
		txtRemainingPayment
				.setText(Formatter.formatNumberToString(sisaSaatIni));

		if (sisaSaatIni < 0) {
			btnSimpan.setEnabled(false);
		} else {
			btnSimpan.setEnabled(true);
		}
	}

	private void validateForm() throws UserException {
		if (txtKode.getText().trim().equals("")) {
			throw new UserException("Kode Receivable harus diisi");
		}

		if (txtNama.getText().trim().equals("")) {
			throw new UserException("Nama Receivable harus diisi");
		}
	}
}
