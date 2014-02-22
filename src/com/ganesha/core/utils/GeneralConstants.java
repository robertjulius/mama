package com.ganesha.core.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import com.ganesha.accounting.constants.Enums.CircleUnit;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.minimarket.constants.Enums.TransactionType;

public class GeneralConstants {

	public static final String VERSION = "v1.4.0";

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
		List<ComboBoxObject> cmbBoxes = new ArrayList<>();
		cmbBoxes.add(new ComboBoxObject(TransactionType.PURCHASE, "Pembelian"));
		cmbBoxes.add(new ComboBoxObject(TransactionType.PURCHASE_RETURN,
				"Retur Pembelian"));
		cmbBoxes.add(new ComboBoxObject(TransactionType.SALES, "Penjualan"));
		cmbBoxes.add(new ComboBoxObject(TransactionType.SALES_RETURN,
				"Retur Penjualan"));
		CMB_BOX_TRX_TYPES = new ComboBoxObject[cmbBoxes.size()];
	}

	public static final ComboBoxObject[] CMB_BOX_CIRCLE_UNITS;
	static {
		List<ComboBoxObject> cmbBoxes = new ArrayList<>();
		cmbBoxes.add(new ComboBoxObject(CircleUnit.HOUR, "Jam"));
		cmbBoxes.add(new ComboBoxObject(CircleUnit.DAY, "Harian"));
		cmbBoxes.add(new ComboBoxObject(CircleUnit.WEEK, "Mingguan"));
		cmbBoxes.add(new ComboBoxObject(CircleUnit.MONTH, "Bulanan"));
		cmbBoxes.add(new ComboBoxObject(CircleUnit.YEAR, "Tahunan"));
		CMB_BOX_CIRCLE_UNITS = new ComboBoxObject[cmbBoxes.size()];
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

	public static final String SYSTEM_SETTING_PRINTER_RECEIPT = "system.setting.printer.receipt";
	public static final String SYSTEM_SETTING_MYSQL_USERNAME = "system.setting.mysql.username";
	public static final String SYSTEM_SETTING_MYSQL_PASSWORD = "system.setting.mysql.password";
	public static final String SYSTEM_SETTING_DBNAME = "system.setting.dbname";
	public static final String SYSTEM_SETTING_MYSQL_LOCATION = "system.setting.mysql.location";
	public static final String SYSTEM_SETTING_BACKUP_LOCATION = "system.setting.backup.location";
	public static final String SYSTEM_SETTING_BACKUP_FILENAME = "system.setting.backup.filename";
	public static final String SYSTEM_SETTING_SMTP_HOST = "system.setting.smtp.host";
	public static final String SYSTEM_SETTING_SMTP_PORT = "system.setting.smtp.port";
	public static final String SYSTEM_SETTING_SMTP_TIMEOUT = "system.setting.smtp.timeout";
	public static final String SYSTEM_SETTING_SMTP_ACCOUNT_ID = "system.setting.smtp.account.id";
	public static final String SYSTEM_SETTING_SMTP_ACCOUNT_LOGIN = "system.setting.smtp.account.login";
	public static final String SYSTEM_SETTING_SMTP_ACCOUNT_PASSWORD = "system.setting.smtp.account.password";
	public static final String SYSTEM_SETTING_EMAIL_TO = "system.setting.email.to";
}
