package com.ganesha.accounting.minimarket.barcodes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.ganesha.core.exception.AppException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BarcodeEAN;

public class BarcodeUtils {

	public static Image generateBarcode() throws AppException {
		return generateBarcode(System.currentTimeMillis());
	}

	public static Image generateBarcode(long code) throws AppException {
		String codeInString = String.valueOf(code);
		if (codeInString.length() != 13) {
			throw new AppException("Failed when generating barcode for code "
					+ codeInString);
		}

		BarcodeEAN codeEAN = new BarcodeEAN();
		codeEAN.setCodeType(BarcodeEAN.EAN13);
		codeEAN.setCode(codeInString);

		Image image = codeEAN.createAwtImage(Color.BLACK, Color.WHITE);
		return image;

	}

	public static void generateBarcodeToFile(long code, String fileName)
			throws AppException, IOException {

		Image rawImage = generateBarcode(code);

		int width = rawImage.getWidth(null) + 10;
		int height = rawImage.getHeight(null) + 10 + 20;
		BufferedImage outImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = outImage.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);
		g2d.drawImage(rawImage, 5, 5, null);
		java.awt.geom.Rectangle2D textBounds = g2d.getFontMetrics()
				.getStringBounds(String.valueOf(code), outImage.getGraphics());
		int textX = (width - (int) textBounds.getWidth()) / 2;
		g2d.setColor(Color.BLACK);
		g2d.drawString(String.valueOf(code), textX, height - 20 + 10);

		ImageIO.write(outImage, "jpg", new File(fileName));
	}

	public static void main(String[] args) throws AppException, IOException,
			DocumentException {
		String fileName = "C:/Users/Asus-020/git/mama/barcode.jpg";
		generateBarcodeToFile(System.currentTimeMillis(), fileName);

		// generateBarcode(System.currentTimeMillis());
	}

	// public static void writeToFile(Image image, String fileName)
	// throws AppException, IOException {
	// BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
	// image.getHeight(null), BufferedImage.TYPE_INT_RGB);
	//
	// Graphics2D graphics2d = bufferedImage.createGraphics();
	// graphics2d.drawImage(image, 0, 0, null);
	// graphics2d.dispose();

	// ImageIO.write(bufferedImage, "jpg", new File(fileName));
	// }
}