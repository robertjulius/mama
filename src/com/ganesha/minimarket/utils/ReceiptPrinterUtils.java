package com.ganesha.minimarket.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import com.ganesha.core.exception.AppException;

public class ReceiptPrinterUtils {

	public static void openDrawer() throws AppException {
		Printable printable = new OpenDrawerPrintable();
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(printable);
		boolean ok = job.printDialog();
		if (ok) {
			try {
				job.print();
			} catch (PrinterException e) {
				throw new AppException(e);
			}
		}
	}

	private static class OpenDrawerPrintable implements Printable {

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
			g.drawString("", 0, 0);
			return PAGE_EXISTS;
		}
	}
}
