package com.ganesha.minimarket.ui.forms.purchase;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.minimarket.utils.PermissionConstants;

public class Calculator extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	public static int DONE = 1;

	private XJTextField txtQuantity;
	private XJTextField txtTotalAmount;
	private XJTextField txtPricePerUnit;
	private XJButton btnBatal;
	private XJButton btnSelesai;
	private int returnValue;

	public static Calculator openDialog(Window parent, int quantity,
			double totalAmount) {
		Calculator calculator = new Calculator(parent, quantity, totalAmount);
		calculator.setVisible(true);
		return calculator;
	}

	private Calculator(Window parent, int quantity, double totalAmount) {
		super(parent);
		setTitle("Calculator");
		setPermissionCode(PermissionConstants.PUR_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(new MigLayout("", "[400,grow]", "[][]"));

		XJPanel pnlHeader = new XJPanel();
		pnlHeader.setBorder(new XEtchedBorder());
		getContentPane().add(pnlHeader, "cell 0 0,grow");
		pnlHeader.setLayout(new MigLayout("", "[150][grow]", "[grow][][]"));

		XJLabel lblQuantity = new XJLabel();
		lblQuantity.setText("Quantity");
		pnlHeader.add(lblQuantity, "cell 0 0");

		txtQuantity = new XJTextField();
		txtQuantity.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					calculate();
				} catch (Exception ex) {
					ExceptionHandler.handleException(Calculator.this, ex);
				}
			}
		});
		txtQuantity
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		txtQuantity.setValue(quantity);
		pnlHeader.add(txtQuantity, "cell 1 0,growx");

		XJLabel lblTotalAmount = new XJLabel();
		lblTotalAmount.setText("Harga Total");
		pnlHeader.add(lblTotalAmount, "cell 0 1");

		txtTotalAmount = new XJTextField();
		txtTotalAmount.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					calculate();
				} catch (Exception ex) {
					ExceptionHandler.handleException(Calculator.this, ex);
				}
			}
		});
		txtTotalAmount
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		txtTotalAmount.setValue(totalAmount);
		pnlHeader.add(txtTotalAmount, "cell 1 1,grow");

		XJLabel lblPricePerUnit = new XJLabel();
		lblPricePerUnit.setText("Harga Satuan");
		pnlHeader.add(lblPricePerUnit, "cell 0 2");

		txtPricePerUnit = new XJTextField();
		txtPricePerUnit
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		txtPricePerUnit.setEditable(false);
		pnlHeader.add(txtPricePerUnit, "cell 1 2,growx");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1,grow");
		pnlButton.setLayout(new MigLayout("", "[100][grow][100]", "[]"));

		btnBatal = new XJButton();
		btnBatal.setMnemonic('Q');
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnBatal.setText("<html><center>Batal<br/>[Alt+Q]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0,grow");

		btnSelesai = new XJButton();
		btnSelesai.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					selesai();
				} catch (Exception ex) {
					ExceptionHandler.handleException(Calculator.this, ex);
				}
			}
		});
		btnSelesai.setText("<html><center>Selesai<br/>[F12]</center></html>");
		pnlButton.add(btnSelesai, "cell 2 0,grow");

		calculate();

		pack();
		setLocationRelativeTo(parent);
	}

	public double getPricePerUnit() {
		return Formatter.formatStringToNumber(txtPricePerUnit.getText())
				.doubleValue();
	}

	public int getQuantity() {
		return ((Number) txtQuantity.getValue()).intValue();
	}

	public int getReturnValue() {
		return returnValue;
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

	private void calculate() {
		int quantity = Formatter.formatStringToNumber(txtQuantity.getText())
				.intValue();

		double totalAmount = Formatter.formatStringToNumber(
				txtTotalAmount.getText()).doubleValue();

		double pricePerUnit = quantity == 0 ? 0 : totalAmount / quantity;
		txtPricePerUnit.setText(Formatter.formatNumberToString(pricePerUnit));
	}

	private void selesai() throws Exception {
		returnValue = DONE;
		dispose();
	}
}
