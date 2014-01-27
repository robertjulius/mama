package com.ganesha.minimarket.utils;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.print.PrintService;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class BarcodeUtils {

	public static PrintService choosePrinter() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		if (printJob.printDialog()) {
			return printJob.getPrintService();
		} else {
			return null;
		}
	}

	public static File generateBarcode() throws AppException {
		return generateBarcode(System.currentTimeMillis());
	}

	public static File generateBarcode(long code) throws AppException {
		String codeInString = String.valueOf(code);
		if (codeInString.length() != 13) {
			throw new AppException("Failed when generating barcode for code "
					+ codeInString);
		}

		Document document = null;
		try {
			File tempFile = File.createTempFile(
					GeneralConstants.FILE_BARCODE_NAME, null);
			document = new Document(PageSize.A4, 0, 0, 0, 0);
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(tempFile));

			document.open();
			PdfContentByte cb = writer.getDirectContent();

			BarcodeEAN codeEAN = new BarcodeEAN();
			codeEAN.setCodeType(BarcodeEAN.EAN13);
			codeEAN.setCode(codeInString);
			codeEAN.setX(1.3f);
			codeEAN.setBarHeight(40f);

			Image image = codeEAN.createImageWithBarcode(cb, null, null);

			PdfPCell cell = new PdfPCell(image);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPaddingTop(10);
			cell.setPaddingBottom(10);
			cell.setBorder(Rectangle.NO_BORDER);

			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100);
			table.setWidths(new int[] { 1, 1, 1, 1 });
			table.addCell(cell);
			table.addCell(cell);
			table.addCell(cell);
			table.addCell(cell);

			document.add(table);
			document.add(table);
			document.add(table);
			document.add(table);
			document.add(table);
			document.add(table);
			document.add(table);
			document.add(table);
			document.add(table);
			document.add(table);
			document.add(table);

			return tempFile;

		} catch (FileNotFoundException e) {
			throw new AppException(e);
		} catch (DocumentException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		} finally {
			if (document != null && document.isOpen()) {
				document.close();
			}
		}
	}

	public static void main(String[] args) throws AppException,
			PrinterException {

		File file = generateBarcode();
		print(file);
	}

	public static void print(File file) throws AppException {
		PrintService printService = choosePrinter();
		printPDF(file, printService);
	}

	public static void printPDF(File file, PrintService printService)
			throws AppException {
		PDDocument doc = null;
		try {
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPrintService(printService);
			doc = PDDocument.load(file);
			doc.silentPrint(job);
		} catch (PrinterException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					throw new AppException(e);
				}
			}
		}
	}
}