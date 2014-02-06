package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.utils.ReceiptPrinter;
import com.ganesha.minimarket.utils.ReceiptPrinter.ItemBelanja;

public class TestReceiptPrinter extends XJDialog {

	private static final long serialVersionUID = -3986040446960213196L;

	private JButton btnPrint;
	private JTextArea txtReceipt;
	private JButton btnCancel;
	private JButton btnDone;

	public static void showDialog(Window parent) {
		TestReceiptPrinter exceptionHandler = new TestReceiptPrinter(parent);
		exceptionHandler.initReceiptText();
		exceptionHandler.pack();
		exceptionHandler.setLocationRelativeTo(null);
		exceptionHandler.setVisible(true);
	}

	private TestReceiptPrinter(Window parent) {
		super(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Test Receipt Printer");
		getContentPane().setLayout(
				new MigLayout("", "[400,grow]", "[400,grow][]"));

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 0,grow");

		txtReceipt = new JTextArea();
		txtReceipt.setEditable(false);
		txtReceipt.setFont(new Font("Courier New", Font.PLAIN, 11));
		scrollPane.setViewportView(txtReceipt);

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[100][100][100]", "[]"));

		btnPrint = new JButton();
		btnPrint.setMnemonic('P');
		btnPrint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					print();
				} catch (Exception ex) {
					ExceptionHandler.handleException(TestReceiptPrinter.this,
							ex);
				}
			}
		});

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setMnemonic('C');
		pnlButton.add(btnCancel, "cell 0 0,growx");
		btnPrint.setText("Print");
		pnlButton.add(btnPrint, "cell 1 0,growx");

		btnDone = new JButton("Done");
		btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnDone.setMnemonic('D');
		pnlButton.add(btnDone, "cell 2 0,growx");
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
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
				.get(SystemSettingForm.SYSTEM_SETTING_PRINTER_RECEIPT);
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
						System.out.println("Printing done ...");
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
