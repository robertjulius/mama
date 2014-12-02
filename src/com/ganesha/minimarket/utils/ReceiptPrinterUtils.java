package com.ganesha.minimarket.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants;

public class ReceiptPrinterUtils {

	public static void openDrawer() throws AppException {
		String printerName = (String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT);
		PrintService[] services = PrinterJob.lookupPrintServices();
		try {
			for (PrintService printService : services) {
				if (printService.getName().equals(printerName)) {
					Printable printable = new OpenDrawerPrintable();
					PrinterJob job = PrinterJob.getPrinterJob();
					job.setPrintable(printable);
					job.setPrintService(printService);
					job.print();
					break;
				}
			}
		} catch (PrinterException e) {
			throw new AppException(e);
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
