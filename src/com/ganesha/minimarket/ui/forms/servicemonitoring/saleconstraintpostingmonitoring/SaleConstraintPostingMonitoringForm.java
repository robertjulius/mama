package com.ganesha.minimarket.ui.forms.servicemonitoring.saleconstraintpostingmonitoring;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJPasswordField;
import com.ganesha.desktop.component.XJTextArea;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.minimarket.model.SaleConstraintHeader;
import com.ganesha.minimarket.utils.PermissionConstants;

public class SaleConstraintPostingMonitoringForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;
	private XJPanel pnlDetail;
	private XJButton btnBack;
	private XJTextField txtTransactionNumber;
	private XJLabel lblTransactionTimestamp;
	private XJPasswordField txtTransactionTimestamp;
	private XJLabel lblCustomer;
	private XJTextField txtCustomerName;
	private XJLabel lblSubTotal;
	private XJTextField txtSubTotal;
	private XJLabel lblTaxPercent;
	private XJLabel lblTaxAmount;
	private XJLabel lblTotalAmount;
	private XJLabel lblPay;
	private XJLabel lblMoneyChange;
	private XJLabel lblPostingStatus;
	private XJLabel lblPostingTriedCount;
	private XJLabel lblPostingMessage;
	private XJTextField txtTaxPercent;
	private XJTextField txtTaxAmount;
	private XJTextField txtTotalAmount;
	private XJTextField txtPay;
	private XJTextField txtMoneyChange;
	private XJTextField txtPostingStatus;
	private XJTextField txtPostingTriedCount;
	private JScrollPane scrollPane;
	private XJTextArea txtrPostingMessasge;

	public SaleConstraintPostingMonitoringForm(Window parent) {
		super(parent);
		setTitle("Database Setting & Backup");
		setPermissionCode(PermissionConstants.SALECONSTRAINT_POSTINGMONITORING_FORM);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][]"));

		pnlDetail = new XJPanel();
		pnlDetail.setBorder(new XEtchedBorder());
		getContentPane().add(pnlDetail, "cell 0 0,grow");
		pnlDetail.setLayout(new MigLayout("", "[grow][500]",
				"[][][][][][][][][][][][150]"));

		XJLabel lblTransactionNumber = new XJLabel();
		pnlDetail.add(lblTransactionNumber, "cell 0 0");
		lblTransactionNumber.setText("No Transaksi");

		txtTransactionNumber = new XJTextField();
		txtTransactionNumber.setEditable(false);
		pnlDetail.add(txtTransactionNumber, "cell 1 0,growx");

		lblTransactionTimestamp = new XJLabel();
		lblTransactionTimestamp.setText("Tanggal Transaksi");
		pnlDetail.add(lblTransactionTimestamp, "cell 0 1");

		txtTransactionTimestamp = new XJPasswordField();
		txtTransactionTimestamp.setEditable(false);
		pnlDetail.add(txtTransactionTimestamp, "cell 1 1,growx");

		lblCustomer = new XJLabel();
		lblCustomer.setText("Customer");
		pnlDetail.add(lblCustomer, "cell 0 2");

		txtCustomerName = new XJTextField();
		txtCustomerName.setEditable(false);
		pnlDetail.add(txtCustomerName, "cell 1 2,growx");

		lblSubTotal = new XJLabel();
		lblSubTotal.setText("Sub Total");
		pnlDetail.add(lblSubTotal, "cell 0 3");

		txtSubTotal = new XJTextField();
		txtSubTotal.setEditable(false);
		pnlDetail.add(txtSubTotal, "cell 1 3,growx");

		lblTaxPercent = new XJLabel();
		lblTaxPercent.setText("Pajak (%)");
		pnlDetail.add(lblTaxPercent, "cell 0 4");

		txtTaxPercent = new XJTextField();
		txtTaxPercent.setEditable(false);
		pnlDetail.add(txtTaxPercent, "cell 1 4,growx");

		lblTaxAmount = new XJLabel();
		lblTaxAmount.setText("Total Pajak (Rp)");
		pnlDetail.add(lblTaxAmount, "cell 0 5");

		txtTaxAmount = new XJTextField();
		txtTaxAmount.setEditable(false);
		pnlDetail.add(txtTaxAmount, "cell 1 5,growx");

		lblTotalAmount = new XJLabel();
		lblTotalAmount.setText("Total Belanja");
		pnlDetail.add(lblTotalAmount, "cell 0 6");

		txtTotalAmount = new XJTextField();
		txtTotalAmount.setEditable(false);
		pnlDetail.add(txtTotalAmount, "cell 1 6,growx");

		lblPay = new XJLabel();
		lblPay.setText("Bayar");
		pnlDetail.add(lblPay, "cell 0 7");

		txtPay = new XJTextField();
		txtPay.setEditable(false);
		pnlDetail.add(txtPay, "cell 1 7,growx");

		lblMoneyChange = new XJLabel();
		lblMoneyChange.setText("Kembalian");
		pnlDetail.add(lblMoneyChange, "cell 0 8");

		txtMoneyChange = new XJTextField();
		txtMoneyChange.setEditable(false);
		pnlDetail.add(txtMoneyChange, "cell 1 8,growx");

		lblPostingStatus = new XJLabel();
		lblPostingStatus.setText("Status Posting");
		pnlDetail.add(lblPostingStatus, "cell 0 9");

		txtPostingStatus = new XJTextField();
		txtPostingStatus.setEditable(false);
		pnlDetail.add(txtPostingStatus, "cell 1 9,growx");

		lblPostingTriedCount = new XJLabel();
		lblPostingTriedCount.setText("Counter");
		pnlDetail.add(lblPostingTriedCount, "cell 0 10");

		txtPostingTriedCount = new XJTextField();
		txtPostingTriedCount.setEditable(false);
		pnlDetail.add(txtPostingTriedCount, "cell 1 10,growx");

		lblPostingMessage = new XJLabel();
		lblPostingMessage.setText("Catatan");
		pnlDetail.add(lblPostingMessage, "cell 0 11");

		scrollPane = new JScrollPane();
		pnlDetail.add(scrollPane, "cell 1 11,grow");

		txtrPostingMessasge = new XJTextArea();
		txtrPostingMessasge.setEditable(false);
		scrollPane.setViewportView(txtrPostingMessasge);

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[]", "[]"));

		btnBack = new XJButton();
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBack.setMnemonic('Q');
		btnBack.setText("<html><center>Kembali<br/>[Esc]</center></html>");
		pnlButton.add(btnBack, "cell 0 0");

		pack();
		setLocationRelativeTo(parent);
	}

	public void setFormDetailValue(SaleConstraintHeader header) {
		txtTransactionNumber.setText(header.getTransactionNumber());
		txtTransactionTimestamp.setText(Formatter.formatDateToString(header
				.getTransactionTimestamp()));
		txtCustomerName.setText(header.getCustomer().getName());
		txtSubTotal.setText(Formatter.formatNumberToString(header
				.getSubTotalAmount()));
		txtTaxPercent.setText(Formatter.formatNumberToString(header
				.getTaxPercent()));
		txtTaxAmount.setText(Formatter.formatNumberToString(header
				.getTaxAmount()));
		txtTotalAmount.setText(Formatter.formatNumberToString(header
				.getTotalAmount()));
		txtPay.setText(Formatter.formatNumberToString(header.getPay()));
		txtMoneyChange.setText(Formatter.formatNumberToString(header
				.getMoneyChange()));
		txtPostingStatus.setText(header.getPostingStatus().toString());
		txtPostingTriedCount.setText(Formatter.formatNumberToString(header
				.getPostingTriedCount()));
		txtrPostingMessasge.setText(header.getPostingMessage());
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private void batal() {
		dispose();
	}
}
