package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
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
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.minimarket.utils.ReceiptPrinter;
import com.ganesha.minimarket.utils.ReceiptPrinter.ItemBelanja;
import com.ganesha.minimarket.utils.ReceiptPrinterSetting;
import com.ganesha.minimarket.utils.ReceiptPrinterUtils;

import net.miginfocom.swing.MigLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PrinterSettingForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJPanel pnlPaperSetting;
	private JTextArea txtReceipt;
	private XJButton btnBatal;
	private XJComboBox cmbPrinterName;
	private XJButton btnTestPrint;
	private XJLabel lblCashDrawerControlCode;
	private XJTextField txtCashDrawerControlCode;
	private XJLabel lblDelimiter;
	private XJTextField txtDelimiter;
	private XJLabel lblCutterControlCode;
	private XJTextField txtCutterControlCode;
	private XJButton btnTestOpenDrawer;
	private XJLabel lblPaperWidth;
	private XJTextField txtPaperWidth;
	private XJLabel lblQuantityLength;
	private XJTextField txtQuantityLength;
	private XJLabel lblPriceLength;
	private XJTextField txtPriceLength;
	private XJLabel lblDiscountLength;
	private XJTextField txtDiscountLength;
	private XJLabel lblAmountLength;
	private XJTextField txtAmountLength;
	private XJPanel pnlPrinterSetting;
	private XJCheckBox chkCashDrawerEnabled;
	private XJCheckBox chkCutterEnabled;
	private XJButton btnPreview;
	private XJCheckBox chkControlCodeEnabled;
	private XJButton btnTestCutter;

	private XJLabel lblSummaryLeftLength;
	private XJTextField txtSummaryLeftLength;
	private XJLabel lblSummaryRightLength;
	private XJTextField txtSummaryRightLength;
	private XJLabel lblRowCount;
	private XJLabel lblBeforeHeader;
	private XJTextField txtRowCountBeforeHeader;
	private XJLabel lblAfterFooter;
	private XJTextField txtRowCountAfterFooter;

	private ReceiptPrinterSetting receiptPrinterSetting = ReceiptPrinterUtils.PRINTER_SETTING;

	public PrinterSettingForm(Window parent) {
		super(parent);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				ReceiptPrinterUtils.reloadReceiptPrinterSetting();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				ReceiptPrinterUtils.reloadReceiptPrinterSetting();
			}
		});
		setTitle("Printer Setting");
		setPermissionCode(PermissionConstants.SETTING_PRINTER_FORM);
		getContentPane().setLayout(new MigLayout("", "[500px,grow][500px,grow]", "[grow][350,baseline][][]"));

		pnlPaperSetting = new XJPanel();
		pnlPaperSetting.setBorder(new XEtchedBorder());
		getContentPane().add(pnlPaperSetting, "cell 0 0,growx");
		pnlPaperSetting.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][25px][][][][]"));

		lblPaperWidth = new XJLabel();
		lblPaperWidth.setText("Paper Width");
		pnlPaperSetting.add(lblPaperWidth, "cell 0 0");

		txtPaperWidth = new XJTextField();
		pnlPaperSetting.add(txtPaperWidth, "cell 1 0,growx");

		lblQuantityLength = new XJLabel();
		lblQuantityLength.setText("Quantity Length");
		pnlPaperSetting.add(lblQuantityLength, "cell 0 1");

		txtQuantityLength = new XJTextField();
		pnlPaperSetting.add(txtQuantityLength, "cell 1 1,growx");

		lblPriceLength = new XJLabel();
		lblPriceLength.setText("Price Length");
		pnlPaperSetting.add(lblPriceLength, "cell 0 2");

		txtPriceLength = new XJTextField();
		pnlPaperSetting.add(txtPriceLength, "cell 1 2,growx");

		lblDiscountLength = new XJLabel();
		lblDiscountLength.setText("Discount Length");
		pnlPaperSetting.add(lblDiscountLength, "cell 0 3");

		txtDiscountLength = new XJTextField();
		pnlPaperSetting.add(txtDiscountLength, "cell 1 3,growx");

		lblAmountLength = new XJLabel();
		lblAmountLength.setText("Amount Length");
		pnlPaperSetting.add(lblAmountLength, "cell 0 4");

		txtAmountLength = new XJTextField();
		pnlPaperSetting.add(txtAmountLength, "cell 1 4,growx");

		lblSummaryLeftLength = new XJLabel();
		lblSummaryLeftLength.setText("Summary Left Length");
		pnlPaperSetting.add(lblSummaryLeftLength, "cell 0 5");

		txtSummaryLeftLength = new XJTextField();
		pnlPaperSetting.add(txtSummaryLeftLength, "cell 1 5,growx");

		lblSummaryRightLength = new XJLabel();
		lblSummaryRightLength.setText("Summary Right Length");
		pnlPaperSetting.add(lblSummaryRightLength, "cell 0 6");

		txtSummaryRightLength = new XJTextField();
		pnlPaperSetting.add(txtSummaryRightLength, "cell 1 6,growx");

		btnPreview = new XJButton();
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					previewDummyReceipt();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this, ex);
				}
			}
		});

		lblRowCount = new XJLabel();
		lblRowCount.setText("Row Count");
		pnlPaperSetting.add(lblRowCount, "cell 0 8 2 1");

		lblBeforeHeader = new XJLabel();
		lblBeforeHeader.setText("Before Header");
		pnlPaperSetting.add(lblBeforeHeader, "cell 0 9,alignx trailing");

		txtRowCountBeforeHeader = new XJTextField();
		pnlPaperSetting.add(txtRowCountBeforeHeader, "cell 1 9,growx");

		lblAfterFooter = new XJLabel();
		lblAfterFooter.setText("After Footer");
		pnlPaperSetting.add(lblAfterFooter, "cell 0 10,alignx trailing");

		txtRowCountAfterFooter = new XJTextField();
		pnlPaperSetting.add(txtRowCountAfterFooter, "cell 1 10,growx");
		btnPreview.setMnemonic('P');
		btnPreview.setText("<html><center>Preview<br/>[Alt+P]</center></html>");
		pnlPaperSetting.add(btnPreview, "cell 1 11,alignx right");

		pnlPrinterSetting = new XJPanel();
		pnlPrinterSetting.setBorder(new XEtchedBorder());
		getContentPane().add(pnlPrinterSetting, "cell 1 0 1 3,grow");
		pnlPrinterSetting.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][][][][]"));

		XJLabel lblPrinterName = new XJLabel();
		pnlPrinterSetting.add(lblPrinterName, "cell 0 0");
		lblPrinterName.setText("Printer Name");

		cmbPrinterName = new XJComboBox(getReceiptPrinterComboBoxList());
		pnlPrinterSetting.add(cmbPrinterName, "cell 1 0,growx");

		chkControlCodeEnabled = new XJCheckBox();
		chkControlCodeEnabled.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				try {
					setControlCodeEnabled();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this, ex);
				}
			}
		});
		chkControlCodeEnabled.setText("Control Code Enabled");
		pnlPrinterSetting.add(chkControlCodeEnabled, "cell 0 2 2 1");

		lblDelimiter = new XJLabel();
		pnlPrinterSetting.add(lblDelimiter, "cell 0 3");
		lblDelimiter.setText("Delimiter");

		txtDelimiter = new XJTextField();
		pnlPrinterSetting.add(txtDelimiter, "cell 1 3");
		txtDelimiter.setColumns(2);

		chkCashDrawerEnabled = new XJCheckBox();
		chkCashDrawerEnabled.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				try {
					setCashDrawerEnabled();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this, ex);
				}
			}
		});
		chkCashDrawerEnabled.setText("Cash Drawer Enabled");
		pnlPrinterSetting.add(chkCashDrawerEnabled, "cell 0 5 2 1");

		lblCashDrawerControlCode = new XJLabel();
		pnlPrinterSetting.add(lblCashDrawerControlCode, "cell 0 6");
		lblCashDrawerControlCode.setText("Control Code");

		txtCashDrawerControlCode = new XJTextField();
		pnlPrinterSetting.add(txtCashDrawerControlCode, "cell 1 6,growx");

		btnTestOpenDrawer = new XJButton();
		pnlPrinterSetting.add(btnTestOpenDrawer, "cell 1 7,alignx right");
		btnTestOpenDrawer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					formToObject();
					ReceiptPrinterUtils.openDrawer();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this, ex);
				}
			}
		});
		btnTestOpenDrawer.setText("Test Open Drawer");

		chkCutterEnabled = new XJCheckBox();
		chkCutterEnabled.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				try {
					setCutterEnabled();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this, ex);
				}
			}
		});
		chkCutterEnabled.setText("Cutter Enabled");
		pnlPrinterSetting.add(chkCutterEnabled, "cell 0 9 2 1");

		lblCutterControlCode = new XJLabel();
		pnlPrinterSetting.add(lblCutterControlCode, "cell 0 10");
		lblCutterControlCode.setText("Control Code");

		txtCutterControlCode = new XJTextField();
		pnlPrinterSetting.add(txtCutterControlCode, "cell 1 10,growx");

		btnTestCutter = new XJButton();
		btnTestCutter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					formToObject();
					ReceiptPrinterUtils.cutPaper();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this, ex);
				}
			}
		});
		btnTestCutter.setText("Test Cutter");
		pnlPrinterSetting.add(btnTestCutter, "cell 1 11,alignx right");

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 1,grow");

		txtReceipt = new JTextArea();
		txtReceipt.setEditable(false);
		txtReceipt.setFont(new Font("Courier New", Font.PLAIN, 11));
		scrollPane.setViewportView(txtReceipt);

		btnTestPrint = new XJButton("Test btnPrint");
		btnTestPrint.setText("Test Print");
		btnTestPrint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ReceiptPrinterUtils.print(createDummyReceiptPrinter());
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this, ex);
				}
			}
		});
		btnTestPrint.setMnemonic('P');
		getContentPane().add(btnTestPrint, "cell 0 2,alignx right");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 3 2 1,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this, ex);
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
		pnlButton.add(btnSimpan, "cell 2 0");

		try {
			initForm();
		} catch (Exception ex) {
			ExceptionHandler.handleException(this, ex);
		}

		setControlCodeEnabled();

		pack();
		setLocationRelativeTo(parent);
	}

	public void initForm() throws AppException, UserException {
		objectToForm();
		previewDummyReceipt();
	}

	private void objectToForm() {

		// Paper Setting

		if (receiptPrinterSetting.getPaperWidth() == 0) {
			txtPaperWidth.setText(String.valueOf(GeneralConstants.RECEIPT_PAPER_WIDTH));
		} else {
			txtPaperWidth.setText(String.valueOf(receiptPrinterSetting.getPaperWidth()));
		}

		if (receiptPrinterSetting.getQuantityLength() == 0) {
			txtQuantityLength.setText(String.valueOf(GeneralConstants.RECEIPT_QUANTITY_LENGTH));
		} else {
			txtQuantityLength.setText(String.valueOf(receiptPrinterSetting.getQuantityLength()));
		}

		if (receiptPrinterSetting.getPriceLength() == 0) {
			txtPriceLength.setText(String.valueOf(GeneralConstants.RECEIPT_PRICE_LENGTH));
		} else {
			txtPriceLength.setText(String.valueOf(receiptPrinterSetting.getPriceLength()));
		}

		if (receiptPrinterSetting.getDiscountLength() == 0) {
			txtDiscountLength.setText(String.valueOf(GeneralConstants.RECEIPT_DISCOUNT_LENGTH));
		} else {
			txtDiscountLength.setText(String.valueOf(receiptPrinterSetting.getDiscountLength()));
		}

		if (receiptPrinterSetting.getAmountLength() == 0) {
			txtAmountLength.setText(String.valueOf(GeneralConstants.RECEIPT_AMOUNT_LENGTH));
		} else {
			txtAmountLength.setText(String.valueOf(receiptPrinterSetting.getAmountLength()));
		}

		if (receiptPrinterSetting.getSummaryLeftLength() == 0) {
			txtSummaryLeftLength.setText(String.valueOf(GeneralConstants.RECEIPT_SUMMARY_LEFT_LEGTH));
		} else {
			txtSummaryLeftLength.setText(String.valueOf(receiptPrinterSetting.getSummaryLeftLength()));
		}

		if (receiptPrinterSetting.getSummaryRightLength() == 0) {
			txtSummaryRightLength.setText(String.valueOf(GeneralConstants.RECEIPT_SUMMARY_RIGHT_LEGTH));
		} else {
			txtSummaryRightLength.setText(String.valueOf(receiptPrinterSetting.getSummaryRightLength()));
		}

		txtRowCountBeforeHeader.setText(String.valueOf(receiptPrinterSetting.getRowCountBeforeHeader()));
		txtRowCountAfterFooter.setText(String.valueOf(receiptPrinterSetting.getRowCountAfterFooter()));

		// Printer setting

		if (receiptPrinterSetting.getPrinterName() == null) {
			cmbPrinterName.setSelectedItem("");
		} else {
			cmbPrinterName.setSelectedItem(receiptPrinterSetting.getPrinterName());
		}

		chkControlCodeEnabled.setSelected(receiptPrinterSetting.isControlCodeEnabled());

		char delimiter;
		if (receiptPrinterSetting.getDelimiter() == 0) {
			delimiter = GeneralConstants.RECEIPT_DELIMITER;
		} else {
			delimiter = receiptPrinterSetting.getDelimiter();
		}

		if (receiptPrinterSetting.getDelimiter() == 0) {
			txtDelimiter.setText(new String(new char[] { delimiter }));
		} else {
			txtDelimiter.setText(new String(new char[] { delimiter }));
		}

		chkCashDrawerEnabled.setSelected(receiptPrinterSetting.isOpenDrawerEnabled());

		if (receiptPrinterSetting.getOpenDrawerCommand() == null) {
			txtCashDrawerControlCode.setText(ReceiptPrinterUtils
					.convertToCommandInString(GeneralConstants.RECEIPT_OPEN_DRAWER_COMMANDS, delimiter));
		} else {
			txtCashDrawerControlCode.setText(ReceiptPrinterUtils
					.convertToCommandInString(receiptPrinterSetting.getOpenDrawerCommand(), delimiter));
		}

		chkCutterEnabled.setSelected(receiptPrinterSetting.isCutEnabled());

		if (receiptPrinterSetting.getCutCommand() == null) {
			txtCutterControlCode.setText(
					ReceiptPrinterUtils.convertToCommandInString(GeneralConstants.RECEIPT_CUT_COMMANDS, delimiter));
		} else {
			txtCutterControlCode.setText(
					ReceiptPrinterUtils.convertToCommandInString(receiptPrinterSetting.getCutCommand(), delimiter));
		}
	}

	private void formToObject() {

		// Paper Setting
		receiptPrinterSetting.setPaperWidth(Formatter.formatStringToNumber(txtPaperWidth.getText()).intValue());
		receiptPrinterSetting.setQuantityLength(Formatter.formatStringToNumber(txtQuantityLength.getText()).intValue());
		receiptPrinterSetting.setPriceLength(Formatter.formatStringToNumber(txtPriceLength.getText()).intValue());
		receiptPrinterSetting.setDiscountLength(Formatter.formatStringToNumber(txtDiscountLength.getText()).intValue());
		receiptPrinterSetting.setAmountLength(Formatter.formatStringToNumber(txtAmountLength.getText()).intValue());
		receiptPrinterSetting
				.setSummaryLeftLength(Formatter.formatStringToNumber(txtSummaryLeftLength.getText()).intValue());
		receiptPrinterSetting
				.setSummaryRightLength(Formatter.formatStringToNumber(txtSummaryRightLength.getText()).intValue());
		receiptPrinterSetting
				.setRowCountBeforeHeader(Formatter.formatStringToNumber(txtRowCountBeforeHeader.getText()).intValue());
		receiptPrinterSetting
				.setRowCountAfterFooter(Formatter.formatStringToNumber(txtRowCountAfterFooter.getText()).intValue());

		// Printer setting
		receiptPrinterSetting.setPrinterName(cmbPrinterName.getSelectedItem().toString());
		receiptPrinterSetting.setControlCodeEnabled(chkControlCodeEnabled.isSelected());
		receiptPrinterSetting.setDelimiter(txtDelimiter.getText().charAt(0));
		receiptPrinterSetting.setOpenDrawerEnabled(chkCashDrawerEnabled.isSelected());
		receiptPrinterSetting.setOpenDrawerCommand(ReceiptPrinterUtils
				.convertToCommandInBytes(txtCashDrawerControlCode.getText(), receiptPrinterSetting.getDelimiter()));
		receiptPrinterSetting.setCutEnabled(chkCutterEnabled.isSelected());
		receiptPrinterSetting.setCutCommand(ReceiptPrinterUtils.convertToCommandInBytes(txtCutterControlCode.getText(),
				receiptPrinterSetting.getDelimiter()));
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
			comboBoxObjects[i + 1] = new ComboBoxObject(printServices[i].getName(), printServices[i].getName());
		}
		return comboBoxObjects;
	}

	private ReceiptPrinter createDummyReceiptPrinter() throws UserException {
		String companyName = Main.getCompany().getName();
		String companyAddress = Main.getCompany().getAddress();
		String transactionNumber = "No      : " + "PUR0123456789";
		String transactionTimestamp = "Tanggal : "
				+ Formatter.formatTimestampToString(DateUtils.getCurrent(Timestamp.class));
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

			ItemBelanja itemBelanja = new ItemBelanja(itemName, quantiy, pricePerUnit, discountPercent, totalAmount);

			itemBelanjaList.add(itemBelanja);
		}

		formToObject();
		validateForm();
		return new ReceiptPrinter(companyName, companyAddress, transactionNumber, transactionTimestamp, cashier,
				itemBelanjaList, totalBelanja, pay, moneyChange);
	}

	private void previewDummyReceipt() throws UserException {
		String receipt = createDummyReceiptPrinter().buildReceipt();
		txtReceipt.setText(receipt);
	}

	private void save() throws UserException, AppException {
		formToObject();
		validateForm();

		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_PRINTER_SETTING, receiptPrinterSetting);
		dispose();
	}

	private void validateForm() throws UserException {

		if (receiptPrinterSetting.getPaperWidth() == 0) {
			throw new UserException("Paper With belum diisi");
		}

		if (receiptPrinterSetting.getQuantityLength() == 0) {
			throw new UserException("Quantity Length belum diisi");
		}

		if (receiptPrinterSetting.getPriceLength() == 0) {
			throw new UserException("Price Length belum diisi");
		}

		if (receiptPrinterSetting.getDiscountLength() == 0) {
			throw new UserException("Discount Length belum diisi");
		}

		if (receiptPrinterSetting.getAmountLength() == 0) {
			throw new UserException("Amount Length belum diisi");
		}

		if (receiptPrinterSetting.getSummaryLeftLength() == 0) {
			throw new UserException("Summary Left Length belum diisi");
		}

		if (receiptPrinterSetting.getSummaryRightLength() == 0) {
			throw new UserException("Summary Right Length belum diisi");
		}

		if (receiptPrinterSetting.getPrinterName().equals("")) {
			throw new UserException("Printer Name belum diisi");
		}

		if (receiptPrinterSetting.isControlCodeEnabled() && receiptPrinterSetting.getDelimiter() == ' ') {
			throw new UserException("Delimiter belum diisi");
		} else {

			if (receiptPrinterSetting.isOpenDrawerEnabled()
					&& ReceiptPrinterUtils.convertToCommandInString(receiptPrinterSetting.getOpenDrawerCommand(),
							receiptPrinterSetting.getDelimiter()).trim().isEmpty()) {
				throw new UserException("Cash Drawer Control Code belum diisi");
			}

			if (receiptPrinterSetting.isOpenDrawerEnabled()
					&& ReceiptPrinterUtils.convertToCommandInString(receiptPrinterSetting.getOpenDrawerCommand(),
							receiptPrinterSetting.getDelimiter()).trim().isEmpty()) {
				throw new UserException("Cash Drawer Control Code belum diisi");
			}
		}
	}

	private void setControlCodeEnabled() {
		boolean enabled = chkControlCodeEnabled.isSelected();

		lblDelimiter.setEnabled(enabled);
		txtDelimiter.setEnabled(enabled);

		if (!enabled) {
			chkCashDrawerEnabled.setSelected(enabled);
			chkCutterEnabled.setSelected(enabled);
		}

		setCashDrawerEnabled(enabled);
		setCutterEnabled(enabled);
	}

	private void setCashDrawerEnabled(boolean enabled) {
		chkCashDrawerEnabled.setEnabled(enabled);
		setCashDrawerEnabled();
	}

	private void setCashDrawerEnabled() {
		boolean enabled = chkCashDrawerEnabled.isSelected();
		lblCashDrawerControlCode.setEnabled(enabled);
		txtCashDrawerControlCode.setEnabled(enabled);
		btnTestOpenDrawer.setEnabled(enabled);
	}

	private void setCutterEnabled(boolean enabled) {
		chkCutterEnabled.setEnabled(enabled);
		setCutterEnabled();
	}

	private void setCutterEnabled() {
		boolean enabled = chkCutterEnabled.isSelected();
		lblCutterControlCode.setEnabled(enabled);
		txtCutterControlCode.setEnabled(enabled);
		btnTestCutter.setEnabled(enabled);
	}
}
