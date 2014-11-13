package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJPasswordField;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.minimarket.utils.BackupDB;
import com.ganesha.minimarket.utils.PermissionConstants;

public class DbSettingForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJPanel pnlAuth;
	private XJButton btnBatal;
	private XJTextField txtMySqlUsername;
	private XJPanel pnlBackupDB;
	private XJButton btnBackupDatabase;
	private XJLabel lblMysqlLocation;
	private XJTextField txtMySqlLocation;
	private XJLabel lblBackupLocation;
	private XJTextField txtBackupLocation;
	private XJButton btnBrowseBackupLocation;
	private XJLabel lblBackupFileName;
	private XJTextField txtBackupFileName;
	private XJLabel lblMySqlPassword;
	private XJPasswordField txtMySqlPassword;
	private XJLabel lblDbName;
	private XJTextField txtDbName;

	public DbSettingForm(Window parent) {
		super(parent);
		setTitle("Database Setting & Backup");
		setPermissionCode(PermissionConstants.SETTING_DB_FORM);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][]"));

		pnlAuth = new XJPanel();
		pnlAuth.setBorder(new XEtchedBorder());
		getContentPane().add(pnlAuth, "cell 0 0,grow");
		pnlAuth.setLayout(new MigLayout("", "[grow][300]", "[][][]"));

		XJLabel lblMySqlUsername = new XJLabel();
		pnlAuth.add(lblMySqlUsername, "cell 0 0");
		lblMySqlUsername.setText("MySQL Username");

		txtMySqlUsername = new XJTextField();
		pnlAuth.add(txtMySqlUsername, "cell 1 0,growx");

		lblMySqlPassword = new XJLabel();
		lblMySqlPassword.setText("MySQL Password");
		pnlAuth.add(lblMySqlPassword, "cell 0 1");

		txtMySqlPassword = new XJPasswordField();
		pnlAuth.add(txtMySqlPassword, "cell 1 1,growx");

		lblDbName = new XJLabel();
		lblDbName.setText("DB Name");
		pnlAuth.add(lblDbName, "cell 0 2");

		txtDbName = new XJTextField();
		pnlAuth.add(txtDbName, "cell 1 2,growx");

		pnlBackupDB = new XJPanel();
		pnlBackupDB.setBorder(new XEtchedBorder());
		getContentPane().add(pnlBackupDB, "cell 0 1,grow");
		pnlBackupDB.setLayout(new MigLayout("", "[grow][300]", "[][][][][]"));

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
					ExceptionHandler.handleException(DbSettingForm.this, ex);
				}
			}
		});

		lblBackupLocation = new XJLabel();
		lblBackupLocation.setText("Backup To Location");
		pnlBackupDB.add(lblBackupLocation, "cell 0 1,alignx trailing");

		txtBackupLocation = new XJTextField();
		txtBackupLocation.setEditable(false);
		pnlBackupDB.add(txtBackupLocation, "cell 1 1,growx");

		btnBrowseBackupLocation = new XJButton();
		btnBrowseBackupLocation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				browseBackupLocation();
			}
		});
		btnBrowseBackupLocation.setText("Browse");
		pnlBackupDB.add(btnBrowseBackupLocation, "cell 1 2,alignx right");

		lblBackupFileName = new XJLabel();
		lblBackupFileName.setText("Backup File Name");
		pnlBackupDB.add(lblBackupFileName, "cell 0 3");

		txtBackupFileName = new XJTextField();
		pnlBackupDB.add(txtBackupFileName, "cell 1 3,growx");
		btnBackupDatabase.setText("Backup Database");
		pnlBackupDB.add(btnBackupDatabase, "cell 1 4,alignx trailing");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(DbSettingForm.this, ex);
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
		setLocationRelativeTo(parent);
	}

	public void initForm() throws AppException {
		txtMySqlUsername.setText(SystemSetting
				.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_USERNAME));
		txtMySqlPassword.setText(SystemSetting
				.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_PASSWORD));
		txtDbName.setText(SystemSetting
				.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_SCHEMA));
		txtMySqlLocation
				.setText(SystemSetting
						.getProperty(GeneralConstants.SYSTEM_PROPERTY_MYSQL_LOCATION_EXE));
		txtBackupLocation.setText((String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_BACKUP_LOCATION));
		txtBackupFileName.setText((String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_BACKUP_FILENAME));
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
		BackupDB.backup(mysqldump, backupFile, txtMySqlUsername.getText(),
				new String(txtMySqlPassword.getPassword()), txtDbName.getText());
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

	private void save() throws UserException, AppException {
		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_BACKUP_LOCATION,
				txtBackupLocation.getText());
		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_BACKUP_FILENAME,
				txtBackupFileName.getText());

		dispose();
	}
}
