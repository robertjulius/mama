package com.ganesha.minimarket.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;

import org.apache.commons.io.FileUtils;

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

			Image image = codeEAN.createImageWithBarcode(cb, null, null);

			PdfPCell cell = new PdfPCell(image);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPaddingTop(7);
			cell.setPaddingBottom(7);
			cell.setBorder(Rectangle.NO_BORDER);

			PdfPTable table = new PdfPTable(5);
			table.setWidthPercentage(100);
			table.setWidths(new int[] { 1, 1, 1, 1, 1 });
			table.addCell(cell);
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

	public static void main(String[] args) throws AppException {
		print(generateBarcode());
	}

	public static void print(File file) throws AppException {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(
				null, null);
		try {
			for (PrintService printService : printServices) {
				if (printService.getName()
						.equals(GeneralConstants.PRINTER_NAME)) {

					DocFlavor flavor = DocFlavor.INPUT_STREAM.POSTSCRIPT;

					PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
					attributes.add(MediaSizeName.ISO_A4);

					FileInputStream fis = new FileInputStream(file);
					Doc doc = new SimpleDoc(fis, flavor, null);

					DocPrintJob printerJob = printService.createPrintJob();
					printerJob.print(doc, attributes);

					FileUtils.forceDelete(file);
					break;
				}
			}
		} catch (FileNotFoundException e) {
			throw new AppException(e);
		} catch (PrintException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		}
	}
}