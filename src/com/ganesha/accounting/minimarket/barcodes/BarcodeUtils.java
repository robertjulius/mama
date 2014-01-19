package com.ganesha.accounting.minimarket.barcodes;

import java.awt.Image;

import com.ganesha.core.exception.AppException;
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

		Image image = codeEAN.createAwtImage(null, null);
		return image;
	}
}