package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.SystemSetting;
import com.ganesha.desktop.exeptions.ExceptionHandler;
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
import com.ganesha.minimarket.utils.PermissionConstants;

public class ProblemReportSettingForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJButton btnBatal;
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

	public ProblemReportSettingForm(Window parent) {
		super(parent);
		setTitle("Problem Report Setting");
		setPermissionCode(PermissionConstants.SETTING_PROBLEMREPORT_FORM);
		getContentPane().setLayout(new MigLayout("", "[]", "[][]"));

		pnlProblemReporting = new XJPanel();
		pnlProblemReporting.setBorder(new XEtchedBorder());
		getContentPane().add(pnlProblemReporting, "cell 0 0,grow");
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

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ProblemReportSettingForm.this, ex);
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
		txtSmtpHost.setText((String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_SMTP_HOST));
		txtSmtpPort.setText((String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_SMTP_PORT));
		txtTimeout.setText((String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_SMTP_TIMEOUT));
		txtAccountId.setText((String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_SMTP_ACCOUNT_ID));
		txtLoginId.setText((String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_SMTP_ACCOUNT_LOGIN));
		txtPassword.setText((String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_SMTP_ACCOUNT_PASSWORD));
		txtEmailTo.setText((String) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_EMAIL_TO));
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

	private void batal() {
		dispose();
	}

	private void save() throws UserException, AppException {
		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_SMTP_HOST,
				txtSmtpHost.getText());
		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_SMTP_PORT,
				txtSmtpPort.getText());
		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_SMTP_TIMEOUT,
				txtTimeout.getText());
		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_SMTP_ACCOUNT_ID,
				txtAccountId.getText());
		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_SMTP_ACCOUNT_LOGIN,
				txtLoginId.getText());
		SystemSetting.save(
				GeneralConstants.SYSTEM_SETTING_SMTP_ACCOUNT_PASSWORD,
				new String(txtPassword.getPassword()));
		SystemSetting.save(GeneralConstants.SYSTEM_SETTING_EMAIL_TO,
				txtEmailTo.getText());
		dispose();
	}
}
