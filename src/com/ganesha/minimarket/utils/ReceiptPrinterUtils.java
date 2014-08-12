package com.ganesha.minimarket.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants;

public class ReceiptPrinterUtils {

	public static void openDrawer() throws AppException {

		ReceiptPrinterSetting receiptPrinterSetting = (ReceiptPrinterSetting) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_PRINTER_SETTING);

		FileOutputStream os = null;
		try {
			String port = receiptPrinterSetting.getPort();

			int[] openDrawerCharaters = receiptPrinterSetting
					.getOpenDrawerCharaters();

			os = new FileOutputStream(port + ":");

			for (int openDrawerCharater : openDrawerCharaters) {
				os.write(openDrawerCharater);
			}

			os.flush();

		} catch (FileNotFoundException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					throw new AppException(e);
				}
			}
		}
	}
}
