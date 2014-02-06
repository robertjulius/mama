package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.print.PrintService;
import javax.swing.JFileChooser;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJPasswordField;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.minimarket.utils.BackupDB;

public class SystemSettingForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	public static final String SYSTEM_SETTING_PRINTER_RECEIPT = "system.setting.printer.receipt";
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

	private XJButton btnSimpan;
	private XJPanel pnlKode;
	private XJButton btnBatal;
	private XJComboBox cmbReceiptPrinter;
	private XJPanel pnlProblemReporting;
	private XJLabel lblEmailTo;
	private XJTextField txtEmailTo;
	private XJLabel lblSmtpHost;
	private XJLabel lblSmtpPort;
	private XJLabel lblTimeout;
	private XJLabel lblAccountId;
	private XJLabel lblAccountLoginId;
	private XJLabel lblAccountPassword;
	private XJTextField txtSmtpHost;
	private XJTextField txtSmtpPort;
	private XJTextField txtTimeout;
	private XJTextField txtAccountId;
	private XJTextField txtLoginId;
	private XJPasswordField txtPassword;
	private XJPanel pnlBackupDB;
	private XJButton btnBackupDatabase;
	private XJLabel lblMysqlLocation;
	private XJTextField txtMySqlLocation;
	private XJLabel lblBackupLocation;
	private XJTextField txtBackupLocation;
	private XJButton btnBrowseMySqlLocation;
	private XJButton btnBrowseBackupLocation;
	private XJLabel lblBackupFileName;
	private XJTextField txtBackupFileName;

	public SystemSettingForm(Window parent) {
		super(parent);
		setCloseOnEsc(false);
		setTitle("System Setting");
		getContentPane().setLayout(new MigLayout("", "[grow][]", "[][][]"));

		pnlKode = new XJPanel();
		pnlKode.setBorder(new XEtchedBorder());
		getContentPane().add(pnlKode, "cell 0 0,grow");
		pnlKode.setLayout(new MigLayout("", "[grow][300]", "[]"));

		XJLabel lblReceiptPrinter = new XJLabel();
		pnlKode.add(lblReceiptPrinter, "cell 0 0,alignx left");
		lblReceiptPrinter.setText("Receipt Printer");

		cmbReceiptPrinter = new XJComboBox(getReceiptPrinterComboBoxList());
		pnlKode.add(cmbReceiptPrinter, "cell 1 0,growx");

		pnlProblemReporting = new XJPanel();
		pnlProblemReporting.setBorder(new XEtchedBorder());
		getContentPane().add(pnlProblemReporting, "cell 1 0 1 2,grow");
		pnlProblemReporting.setLayout(new MigLayout("", "[grow][300]",
				"[][][][][][][]"));

		lblSmtpHost = new XJLabel();
		lblSmtpHost.setText("SMTP Host");
		pnlProblemReporting.add(lblSmtpHost, "cell 0 0");

		txtSmtpHost = new XJTextField();
		pnlProblemReporting.add(txtSmtpHost, "cell 1 0,growx");

		lblSmtpPort = new XJLabel();
		lblSmtpPort.setText("SMTP Port");
		pnlProblemReporting.add(lblSmtpPort, "cell 0 1");

		txtSmtpPort = new XJTextField();
		pnlProblemReporting.add(txtSmtpPort, "cell 1 1,growx");

		lblTimeout = new XJLabel();
		lblTimeout.setText("Timeout");
		pnlProblemReporting.add(lblTimeout, "cell 0 2");

		txtTimeout = new XJTextField();
		pnlProblemReporting.add(txtTimeout, "cell 1 2,growx");

		lblAccountId = new XJLabel();
		lblAccountId.setText("Account ID");
		pnlProblemReporting.add(lblAccountId, "cell 0 3");

		txtAccountId = new XJTextField();
		pnlProblemReporting.add(txtAccountId, "cell 1 3,growx");

		lblAccountLoginId = new XJLabel();
		lblAccountLoginId.setText("Account Login ID");
		pnlProblemReporting.add(lblAccountLoginId, "cell 0 4");

		txtLoginId = new XJTextField();
		pnlProblemReporting.add(txtLoginId, "cell 1 4,growx");

		lblAccountPassword = new XJLabel();
		lblAccountPassword.setText("Account Password");
		pnlProblemReporting.add(lblAccountPassword, "cell 0 5");

		txtPassword = new XJPasswordField();
		pnlProblemReporting.add(txtPassword, "cell 1 5,growx");

		lblEmailTo = new XJLabel();
		lblEmailTo.setText("Email To");
		pnlProblemReporting.add(lblEmailTo, "cell 0 6");

		txtEmailTo = new XJTextField();
		pnlProblemReporting.add(txtEmailTo, "cell 1 6,growx");

		pnlBackupDB = new XJPanel();
		pnlBackupDB.setBorder(new XEtchedBorder());
		getContentPane().add(pnlBackupDB, "cell 0 1,grow");
		pnlBackupDB.setLayout(new MigLayout("", "[grow][300,grow]",
				"[][][][][][]"));

		lblMysqlLocation = new XJLabel();
		lblMysqlLocation.setText("MySQL Location");
		pnlBackupDB.add(lblMysqlLocation, "cell 0 0");

		txtMySqlLocation = new XJTextField();
		txtMySqlLocation.setEditable(false);
		pnlBackupDB.add(txtMySqlLocation, "cell 1 0,growx");

		btnBackupDatabase = new XJButton();
		btnBackupDatabase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					backupDb();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(SystemSettingForm.this, ex);
				}
			}
		});

		btnBrowseMySqlLocation = new XJButton();
		btnBrowseMySqlLocation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				browseMySqlLocation();
			}
		});
		btnBrowseMySqlLocation.setText("Browse");
		pnlBackupDB.add(btnBrowseMySqlLocation, "cell 1 1,alignx right");

		lblBackupLocation = new XJLabel();
		lblBackupLocation.setText("Backup To Location");
		pnlBackupDB.add(lblBackupLocation, "cell 0 2,alignx trailing");

		txtBackupLocation = new XJTextField();
		txtBackupLocation.setEditable(false);
		pnlBackupDB.add(txtBackupLocation, "cell 1 2,growx");

		btnBrowseBackupLocation = new XJButton();
		btnBrowseBackupLocation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				browseBackupLocation();
			}
		});
		btnBrowseBackupLocation.setText("Browse");
		pnlBackupDB.add(btnBrowseBackupLocation, "cell 1 3,alignx right");

		lblBackupFileName = new XJLabel();
		lblBackupFileName.setText("Backup File Name");
		pnlBackupDB.add(lblBackupFileName, "cell 0 4");

		txtBackupFileName = new XJTextField();
		pnlBackupDB.add(txtBackupFileName, "cell 1 4,growx");
		btnBackupDatabase.setText("Backup Database");
		pnlBackupDB.add(btnBackupDatabase, "cell 1 5,alignx trailing");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 2 2 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(SystemSettingForm.this, ex);
				}
			}
		});

		btnBatal = new XJButton();
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBatal.setMnemonic('Q');
		btnBatal.setText("<html><center>Batal<br/>[Alt+Q]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 1 0");

		try {
			initForm();
		} catch (AppException ex) {
			ExceptionHandler.handleException(this, ex);
		}

		pack();
		setLocationRelativeTo(null);
	}

	public void initForm() throws AppException {
		String selectedItem = (String) SystemSetting
				.get(SYSTEM_SETTING_PRINTER_RECEIPT);
		ComboBoxObject comboBoxObject = new ComboBoxObject(selectedItem,
				selectedItem);
		cmbReceiptPrinter.setSelectedItem(comboBoxObject);

		txtMySqlLocation.setText((String) SystemSetting
				.get(SYSTEM_SETTING_MYSQL_LOCATION));
		txtBackupLocation.setText((String) SystemSetting
				.get(SYSTEM_SETTING_BACKUP_LOCATION));
		txtBackupFileName.setText((String) SystemSetting
				.get(SYSTEM_SETTING_BACKUP_FILENAME));

		txtSmtpHost.setText((String) SystemSetting
				.get(SYSTEM_SETTING_SMTP_HOST));
		txtSmtpPort.setText((String) SystemSetting
				.get(SYSTEM_SETTING_SMTP_PORT));
		txtTimeout.setText((String) SystemSetting
				.get(SYSTEM_SETTING_SMTP_TIMEOUT));
		txtAccountId.setText((String) SystemSetting
				.get(SYSTEM_SETTING_SMTP_ACCOUNT_ID));
		txtLoginId.setText((String) SystemSetting
				.get(SYSTEM_SETTING_SMTP_ACCOUNT_LOGIN));
		txtPassword.setText((String) SystemSetting
				.get(SYSTEM_SETTING_SMTP_ACCOUNT_PASSWORD));
		txtEmailTo.setText((String) SystemSetting.get(SYSTEM_SETTING_EMAIL_TO));
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnSimpan.doClick();
			break;
		default:
			break;
		}
	}

	private void backupDb() throws AppException, UserException {
		File mysqldump = new File(txtMySqlLocation.getText(), "mysqldump.exe");
		File backupFile = new File(txtBackupLocation.getText(),
				txtBackupFileName.getText());
		BackupDB.backup(mysqldump, backupFile, "root", "root", "minimarketkk");
	}

	private void batal() {
		dispose();
	}

	private void browseBackupLocation() {
		JFileChooser fileChooser = new JFileChooser();
		if (!txtBackupLocation.getText().trim().equals("")) {
			fileChooser.setCurrentDirectory(new File(txtBackupLocation
					.getText()));
		}
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			txtBackupLocation.setText(file.getAbsolutePath());
		}
	}

	private void browseMySqlLocation() {
		JFileChooser fileChooser = new JFileChooser();
		if (!txtMySqlLocation.getText().trim().equals("")) {
			fileChooser
					.setCurrentDirectory(new File(txtMySqlLocation.getText()));
		}
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			txtMySqlLocation.setText(file.getAbsolutePath());
		}
	}

	private ComboBoxObject[] getReceiptPrinterComboBoxList() {
		PrintService[] printServices = PrinterJob.lookupPrintServices();
		ComboBoxObject[] comboBoxObjects = new ComboBoxObject[printServices.length + 1];
		comboBoxObjects[0] = new ComboBoxObject(null, null);
		for (int i = 0; i < printServices.length; ++i) {
			comboBoxObjects[i + 1] = new ComboBoxObject(
					printServices[i].getName(), printServices[i].getName());
		}
		return comboBoxObjects;
	}

	private void save() throws UserException, AppException {
		validateForm();
		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbReceiptPrinter
				.getSelectedItem();
		String printServiceName = (String) comboBoxObject.getId();
		SystemSetting.save(SYSTEM_SETTING_PRINTER_RECEIPT, printServiceName);

		SystemSetting.save(SYSTEM_SETTING_MYSQL_LOCATION,
				txtMySqlLocation.getText());
		SystemSetting.save(SYSTEM_SETTING_BACKUP_LOCATION,
				txtBackupLocation.getText());
		SystemSetting.save(SYSTEM_SETTING_BACKUP_FILENAME,
				txtBackupFileName.getText());

		SystemSetting.save(SYSTEM_SETTING_SMTP_HOST, txtSmtpHost.getText());
		SystemSetting.save(SYSTEM_SETTING_SMTP_PORT, txtSmtpPort.getText());
		SystemSetting.save(SYSTEM_SETTING_SMTP_TIMEOUT, txtTimeout.getText());
		SystemSetting.save(SYSTEM_SETTING_SMTP_ACCOUNT_ID,
				txtAccountId.getText());
		SystemSetting.save(SYSTEM_SETTING_SMTP_ACCOUNT_LOGIN,
				txtLoginId.getText());
		SystemSetting.save(SYSTEM_SETTING_SMTP_ACCOUNT_PASSWORD, new String(
				txtPassword.getPassword()));
		SystemSetting.save(SYSTEM_SETTING_EMAIL_TO, txtEmailTo.getText());

		dispose();
	}

	private void validateForm() throws UserException {
		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbReceiptPrinter
				.getSelectedItem();
		String printServiceName = (String) comboBoxObject.getId();
		if (printServiceName == null) {
			throw new UserException("Printer Receipt harus diisi");
		}
	}
}
