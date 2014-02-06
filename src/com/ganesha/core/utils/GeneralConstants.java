package com.ganesha.core.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import com.ganesha.desktop.component.ComboBoxObject;

public class GeneralConstants {

	public static final String VERSION = "v2.0.0";

	public static final String USER_SESSION = "userSession";

	public static final String MODULE_SESSION = "moduleSession";
	public static final String REC_STATUS_ACTIVE = "A";

	public static final String REC_STATUS_NONACTIVE = "N";
	public static final String EMPTY_STRING = "";

	public static final String PREFIX_TRX_NUMBER_PURCHASE = "PUR";
	public static final String PREFIX_TRX_NUMBER_PURCHASE_RETURN = "PRT";
	public static final String PREFIX_TRX_NUMBER_SALES = "SAL";
	public static final String PREFIX_TRX_NUMBER_SALES_RETURN = "SRT";
	public static final String PREFIX_TRX_NUMBER_RECEIVABLE = "REC";
	public static final String PREFIX_TRX_NUMBER_PAYABLE = "PAY";

	public static final String TAX_CODE_PPN = "PPN";
	public static final String FILE_BARCODE_NAME = "barcodes"
			+ CommonUtils.getTimestampInString() + ".pdf";

	// public static final String PRINTER_NAME = "HP Deskjet Ink Adv 2010 K010";
	// public static final String PRINTER_NAME = "CutePDF Writer";
	public static final String PRINTER_NAME = "Canon iP2700 series";

	public static final ComboBoxObject[] CMB_BOX_TRX_TYPES;
	static {
		CMB_BOX_TRX_TYPES = new ComboBoxObject[4];
		CMB_BOX_TRX_TYPES[0] = new ComboBoxObject(TransactionType.PURCHASE,
				"Pembelian");
		CMB_BOX_TRX_TYPES[1] = new ComboBoxObject(
				TransactionType.PURCHASE_RETURN, "Retur Pembelian");
		CMB_BOX_TRX_TYPES[2] = new ComboBoxObject(TransactionType.SALES,
				"Penjualan");
		CMB_BOX_TRX_TYPES[3] = new ComboBoxObject(TransactionType.SALES_RETURN,
				"Retur Penjualan");
	}

	public static final AbstractFormatterFactory FORMATTER_FACTORY_NUMBER;
	static {
		NumberFormat format = new DecimalFormat("#,##0.##");
		NumberFormatter formatter = new NumberFormatter(format);
		FORMATTER_FACTORY_NUMBER = new DefaultFormatterFactory(formatter);
	}

	public static final String DECRIPTION_PAYABLE_PURCHASE = "Hutang untuk transaksi pembelian";
	public static final String DECRIPTION_PAYABLE_PURCHASE_RETURN = "Pemotongan hutang untuk retur pembelian";
	public static final String DECRIPTION_RECEIVABLE_PURCHASE_RETURN = "Piutang supplier untuk retur pembelian";

	public static enum AccountAction {
		INCREASE, DECREASE
	}

	public static enum ActionType {
		CREATE, READ, UPDATE, DELETE, OTHER
	}

	public static enum TransactionType {
		SALES, PURCHASE, SALES_RETURN, PURCHASE_RETURN
	}
}
