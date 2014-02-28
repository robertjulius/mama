package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.minimarket.utils.ReceiptPrinter;
import com.ganesha.minimarket.utils.ReceiptPrinter.ItemBelanja;

public class PrinterSettingForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJPanel pnlPrinter;
	private JTextArea txtReceipt;
	private XJButton btnBatal;
	private XJComboBox cmbReceiptPrinter;
	private JButton Print;

	public PrinterSettingForm(Window parent) {
		super(parent);
		setTitle("Printer Setting");
		setPermissionCode(PermissionConstants.SETTING_PRINTER_FORM);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][450][][]"));

		pnlPrinter = new XJPanel();
		pnlPrinter.setBorder(new XEtchedBorder());
		getContentPane().add(pnlPrinter, "cell 0 0,grow");
		pnlPrinter.setLayout(new MigLayout("", "[grow][300]", "[]"));

		XJLabel lblReceiptPrinter = new XJLabel();
		pnlPrinter.add(lblReceiptPrinter, "cell 0 0,alignx left");
		lblReceiptPrinter.setText("Receipt Printer");

		cmbReceiptPrinter = new XJComboBox(getReceiptPrinterComboBoxList());
		pnlPrinter.add(cmbReceiptPrinter, "cell 1 0,growx");

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 1,grow");

		txtReceipt = new JTextArea();
		txtReceipt.setEditable(false);
		txtReceipt.setFont(new Font("Courier New", Font.PLAIN, 11));
		scrollPane.setViewportView(txtReceipt);

		Print = new JButton("Test Print");
		Print.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					print();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrinterSettingForm.this,
							ex);
				}
			}
		});
		Print.setMnemonic('P');
		getContentPane().add(Print, "cell 0 2,alignx right");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 3,alignx center,growy");
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
		cmbReceiptPrinter.setSelectedItem(selectedItem);

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
				+ Formatter.formatTimestampToString(CommonUtils
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

		ReceiptPrinter receiptPrinter = new ReceiptPrinter(companyName,
				companyAddress, transactionNumber, transactionTimestamp,
				cashier, itemBelanjaList, totalBelanja, pay, moneyChange);

		String receipt = receiptPrinter.buildReceipt();
		txtReceipt.setText(receipt);
	}

	private void print() throws AppException {
		String printerName = (String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT);
		PrintService[] services = PrinterJob.lookupPrintServices();
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(txtReceipt.getText().getBytes());
			for (PrintService printService : services) {
				if (printService.getName().equals(printerName)) {
					DocFlavor flavor = DocFlavor.STRING.INPUT_STREAM.AUTOSENSE;
					Doc doc = new SimpleDoc(is, flavor, null);
					DocPrintJob printJob = printService.createPrintJob();
					PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
					pras.add(new Copies(1));

					PrintJobWatcher pjw = new PrintJobWatcher(printJob);
					printJob.print(doc, pras);

					pjw.waitForDone();
				}
			}
		} catch (PrintException e) {
			throw new AppException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new AppException(e);
				}
			}
		}
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

	class PrintJobWatcher {
		boolean done = false;

		PrintJobWatcher(DocPrintJob job) {
			job.addPrintJobListener(new PrintJobAdapter() {
				@Override
				public void printJobCanceled(PrintJobEvent pje) {
					allDone();
				}

				@Override
				public void printJobCompleted(PrintJobEvent pje) {
					allDone();
				}

				@Override
				public void printJobFailed(PrintJobEvent pje) {
					allDone();
				}

				@Override
				public void printJobNoMoreEvents(PrintJobEvent pje) {
					allDone();
				}

				void allDone() {
					synchronized (PrintJobWatcher.this) {
						done = true;
						PrintJobWatcher.this.notify();
					}
				}
			});
		}

		public synchronized void waitForDone() {
			try {
				while (!done) {
					wait();
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
