package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.minimarket.utils.ReceiptPrinter;
import com.ganesha.minimarket.utils.ReceiptPrinter.ItemBelanja;
import com.ganesha.minimarket.utils.ReceiptPrinterSetting;
import com.ganesha.minimarket.utils.ReceiptPrinterUtils;

public class PrinterSettingForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJPanel pnlPrinter;
	private JTextArea txtReceipt;
	private XJButton btnBatal;
	private XJComboBox cmbReceiptPrinter;
	private XJButton btnTestPrint;
	private XJLabel lblOpenDrawerChar;
	private XJTextField txtOpenDrawerChar;
	private XJLabel lblDelimiter;
	private XJTextField txtDelimiter;
	private XJLabel lblPort;
	private XJTextField txtPort;
	private XJButton btnTestOpenDrawer;
	private ReceiptPrinter receiptPrinter;

	public PrinterSettingForm(Window parent) {
		super(parent);
		setTitle("Printer Setting");
		setPermissionCode(PermissionConstants.SETTING_PRINTER_FORM);
		getContentPane().setLayout(
				new MigLayout("", "[grow]", "[][350,baseline][][][]"));

		pnlPrinter = new XJPanel();
		pnlPrinter.setBorder(new XEtchedBorder());
		getContentPane().add(pnlPrinter, "cell 0 0,grow");
		pnlPrinter.setLayout(new MigLayout("", "[grow][300,grow]", "[][][][]"));

		XJLabel lblReceiptPrinter = new XJLabel();
		pnlPrinter.add(lblReceiptPrinter, "cell 0 0,alignx left");
		lblReceiptPrinter.setText("Receipt Printer");

		cmbReceiptPrinter = new XJComboBox(getReceiptPrinterComboBoxList());
		pnlPrinter.add(cmbReceiptPrinter, "cell 1 0,growx");

		lblOpenDrawerChar = new XJLabel();
		lblOpenDrawerChar.setText("Open Drawer Char");
		pnlPrinter.add(lblOpenDrawerChar, "cell 0 1");

		txtOpenDrawerChar = new XJTextField();
		pnlPrinter.add(txtOpenDrawerChar, "cell 1 1,growx");

		lblDelimiter = new XJLabel();
		lblDelimiter.setText("Delimiter");
		pnlPrinter.add(lblDelimiter, "cell 0 2");

		txtDelimiter = new XJTextField();
		txtDelimiter.setColumns(2);
		pnlPrinter.add(txtDelimiter, "cell 1 2");

		lblPort = new XJLabel();
		lblPort.setText("Port");
		pnlPrinter.add(lblPort, "cell 0 3");

		txtPort = new XJTextField();
		pnlPrinter.add(txtPort, "cell 1 3,growx");

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 1,grow");

		txtReceipt = new JTextArea();
		txtReceipt.setFont(new Font("Courier New", Font.PLAIN, 11));
		scrollPane.setViewportView(txtReceipt);

		btnTestPrint = new XJButton("Test btnPrint");
		btnTestPrint.setText("Test Print");
		btnTestPrint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ReceiptPrinterUtils.print(receiptPrinter);
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this,
							ex);
				}
			}
		});
		btnTestPrint.setMnemonic('P');
		getContentPane().add(btnTestPrint, "cell 0 2,alignx right");

		btnTestOpenDrawer = new XJButton();
		btnTestOpenDrawer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
					ReceiptPrinterUtils.openDrawer("");
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this,
							ex);
				}
			}
		});
		btnTestOpenDrawer.setText("Test Open Drawer");
		getContentPane().add(btnTestOpenDrawer, "cell 0 3,alignx right");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 4,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this,
							ex);
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
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 1 0");

		try {
			initForm();
		} catch (AppException ex) {
			ExceptionHandler.handleException(this, ex);
		}

		pack();
		setLocationRelativeTo(parent);
	}

	public void initForm() throws AppException {
		String selectedItem = (String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT);
		if (selectedItem != null) {
			cmbReceiptPrinter.setSelectedItem(selectedItem);
		}

		ReceiptPrinterSetting receiptPrinterSetting = (ReceiptPrinterSetting) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_PRINTER_SETTING);

		if (receiptPrinterSetting == null) {
			receiptPrinterSetting = new ReceiptPrinterSetting();
		}

		if (receiptPrinterSetting.getAutoCutCharacters() == null) {
			receiptPrinterSetting.setAutoCutCharacters(new int[] { 27, 112, 48,
					25, 250 });
		}

		if (receiptPrinterSetting.getOpenDrawerCharaters() == null) {
			receiptPrinterSetting.setOpenDrawerCharaters(new int[] { 27, 112,
					48, 25, 250 });
		}

		if (receiptPrinterSetting.getDelimiter() == 0) {
			receiptPrinterSetting.setDelimiter(',');
		}

		if (receiptPrinterSetting.getPort() == null) {
			receiptPrinterSetting.setPort("LPT1");
		}

		String openDrawerChars = "";
		for (int openDrawerCharater : receiptPrinterSetting
				.getOpenDrawerCharaters()) {
			openDrawerChars += receiptPrinterSetting.getDelimiter() + ""
					+ +openDrawerCharater;
		}
		openDrawerChars = openDrawerChars.replaceFirst(""
				+ receiptPrinterSetting.getDelimiter(), "");
		txtOpenDrawerChar.setText(openDrawerChars);

		String autoCutChars = "";
		for (int autoCutCharacter : receiptPrinterSetting
				.getAutoCutCharacters()) {
			autoCutChars += receiptPrinterSetting.getDelimiter() + ""
					+ autoCutCharacter;
		}
		autoCutChars = autoCutChars.replaceFirst(
				"" + receiptPrinterSetting.getDelimiter(), "");
		/*
		 * txtAutoCutChar.setText(openAutoCutChars);
		 */

		txtDelimiter.setText("" + receiptPrinterSetting.getDelimiter());
		txtPort.setText(receiptPrinterSetting.getPort());

		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_PRINTER_SETTING,
				receiptPrinterSetting);

		initReceiptText();
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

	private ComboBoxObject[] getReceiptPrinterComboBoxList() {
		PrintService[] printServices = PrinterJob.lookupPrintServices();
		ComboBoxObject[] comboBoxObjects = new ComboBoxObject[printServices.length + 1];
		comboBoxObjects[0] = new ComboBoxObject(null, null);
		for (int i = 0; i < printServices.length; ++i) {
			comboBoxObjects[i + 1] = new ComboBoxObject(
					printServices[i].getName(), printServices[i].getName());
		}
		return comboBoxObjects;
	}

	private void initReceiptText() {

		String companyName = Main.getCompany().getName();
		String companyAddress = Main.getCompany().getAddress();
		String transactionNumber = "No      : " + "PUR0123456789";
		String transactionTimestamp = "Tanggal : "
				+ Formatter.formatTimestampToString(DateUtils
						.getCurrentTimestamp());
		String cashier = "Kasir   : " + Main.getUserLogin().getName();
		String totalBelanja = Formatter.formatNumberToString(312500);
		String pay = Formatter.formatNumberToString(500000);
		String moneyChange = Formatter.formatNumberToString(187500);

		List<ItemBelanja> itemBelanjaList = new ArrayList<>();
		for (int i = 0; i < 5; ++i) {
			String itemName = "Kerupuk Singkong Pedas";
			String quantiy = Formatter.formatNumberToString(5) + "x";
			String pricePerUnit = Formatter.formatNumberToString(12500);
			String discountPercent = "";
			String totalAmount = Formatter.formatNumberToString(62500);

			ItemBelanja itemBelanja = new ItemBelanja(itemName, quantiy,
					pricePerUnit, discountPercent, totalAmount);

			itemBelanjaList.add(itemBelanja);
		}

		receiptPrinter = new ReceiptPrinter(companyName, companyAddress,
				transactionNumber, transactionTimestamp, cashier,
				itemBelanjaList, totalBelanja, pay, moneyChange);

		String receipt = receiptPrinter.buildReceipt();

		txtReceipt.setText(receipt);
	}

	private void save() throws UserException, AppException {
		validateForm();
		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbReceiptPrinter
				.getSelectedItem();
		String printServiceName = (String) comboBoxObject.getObject();
		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT,
				printServiceName);

		dispose();
	}

	private void validateForm() throws UserException {
		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbReceiptPrinter
				.getSelectedItem();
		String printServiceName = (String) comboBoxObject.getObject();
		if (printServiceName == null) {
			throw new UserException("Printer Receipt harus diisi");
		}
	}
}
