package com.ganesha.minimarket.utils;

import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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

import org.slf4j.LoggerFactory;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Loggers;

public class ReceiptPrinterUtils {

	public static ReceiptPrinterSetting PRINTER_SETTING;
	
	static {
		reloadReceiptPrinterSetting();
	}

	public static void reloadReceiptPrinterSetting() {
		try {
			if ((ReceiptPrinterSetting) SystemSetting.get(GeneralConstants.SYSTEM_SETTING_PRINTER_SETTING) == null) {
				PRINTER_SETTING = new ReceiptPrinterSetting();
			} else {
				PRINTER_SETTING = (ReceiptPrinterSetting) SystemSetting
						.get(GeneralConstants.SYSTEM_SETTING_PRINTER_SETTING);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void openDrawer() {
		try {
			ReceiptPrinterUtils.print(PRINTER_SETTING.getOpenDrawerCommand());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void cutPaper() {
		try {
			ReceiptPrinterUtils.print(PRINTER_SETTING.getCutCommand());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] convertToCommandInBytes(String commandInString, char delimiter) {
		String[] strings = commandInString.split(new String(new char[] { delimiter }));
		byte[] bytes = new byte[strings.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = Formatter.formatStringToNumber(strings[i].trim()).byteValue();
		}
		return bytes;
	}

	public static String convertToCommandInString(byte[] commandInBytes, char delimiter) {
		StringBuilder builder = new StringBuilder();
		for (byte b : commandInBytes) {
			builder.append(b).append(delimiter);
		}
		return builder.substring(0, builder.length() - 1);
	}

	public static void print(ReceiptPrinter receiptPrinter) throws PrintException {

		if (PRINTER_SETTING.isOpenDrawerEnabled()) {
			openDrawer();
		}

		String receipt = receiptPrinter.buildReceipt();
		print(receipt.getBytes());

		if (PRINTER_SETTING.isCutEnabled()) {
			cutPaper();
		}

	}

	public static void print(byte[] bytes) throws PrintException {
		PrintService[] services = PrinterJob.lookupPrintServices();
		InputStream is = null;
		try {

			String printerName = ReceiptPrinterUtils.PRINTER_SETTING.getPrinterName();
			LoggerFactory.getLogger(Loggers.UTILS).debug("Finding printer '" + printerName + "' ...");

			is = new ByteArrayInputStream(bytes);

			boolean printerFound = false;
			for (PrintService printService : services) {

				LoggerFactory.getLogger(Loggers.UTILS).debug("Found... " + printService.getName());

				if (printService.getName().equals(printerName)) {

					printerFound = true;

					DocFlavor flavor = DocFlavor.STRING.INPUT_STREAM.AUTOSENSE;
					Doc doc = new SimpleDoc(is, flavor, null);
					DocPrintJob printJob = printService.createPrintJob();
					PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
					pras.add(new Copies(1));

					PrintJobWatcher pjw = new PrintJobWatcher(printJob);
					printJob.print(doc, pras);

					pjw.waitForDone();
					break;
				}
			}

			if (!printerFound) {
				LoggerFactory.getLogger(Loggers.UTILS).debug("Finding printer '" + printerName + "' is not found!");
			}

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new PrintException(e);
				}
			}
		}
	}

	private static class PrintJobWatcher {
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
