package com.ganesha.minimarket.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
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

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants;

public class ReceiptPrinterUtils {

	public static void openDrawer(String printedString) throws AppException {
		String printerName = (String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT);
		PrintService[] services = PrinterJob.lookupPrintServices();
		try {
			for (PrintService printService : services) {
				if (printService.getName().equals(printerName)) {

					PrinterJob job = PrinterJob.getPrinterJob();
					job.setPrintService(printService);

					PageFormat pageFormat = job.defaultPage();
					Paper paper = pageFormat.getPaper();
					paper.setImageableArea(0, 0, paper.getWidth(),
							paper.getHeight());
					pageFormat.setPaper(paper);

					Printable printable = new OpenDrawerPrintable(printedString);
					job.setPrintable(printable);

					job.print();
					break;
				}
			}
		} catch (PrinterException e) {
			throw new AppException(e);
		}
	}

	public static void print(ReceiptPrinter receiptPrinter)
			throws PrintException {

		PrintService[] services = PrinterJob.lookupPrintServices();
		InputStream is = null;
		try {
			String receipt = receiptPrinter.buildReceipt();

			String printerName = (String) SystemSetting
					.get(GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT);

			is = new ByteArrayInputStream(receipt.getBytes());
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
					ReceiptPrinterUtils.openDrawer("");
					break;
				}
			}
		} catch (AppException e) {
			throw new PrintException(e);
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

	private static class OpenDrawerPrintable implements Printable {

		private String printedString;

		public OpenDrawerPrintable(String printedString) {
			this.printedString = printedString;
		}

		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
				throws PrinterException {
			return openDrawerOnly(graphics, pageFormat, pageIndex);
		}

		private int openDrawerOnly(Graphics g, PageFormat pf, int pageIndex) {
			if (pageIndex > 0) {
				return NO_SUCH_PAGE;
			}
			Graphics2D g2d = (Graphics2D) g;
			double imageableX = pf.getImageableX();
			double imageableY = pf.getImageableY();
			g2d.translate(imageableX, imageableY);
			g.drawString(printedString, 0, 0);
			return PAGE_EXISTS;
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
