package com.ganesha.minimarket.ui.forms.commons.alerts;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import java.awt.Color;

public class XCaptchaAlert extends XJDialog {

	public static final int YES_OPTION = JOptionPane.YES_OPTION;
	public static final int NO_OPTION = JOptionPane.NO_OPTION;

	private static final long serialVersionUID = -3986040446960213196L;

	private static final int CAPTCHA_LENGTH = 5;

	private XJButton btnYes;
	private XJLabel lblMessage;
	private XJPanel pnlInput;
	private XJLabel lblInputCaptcha;
	private XJTextField txtCaptcha;
	private XJLabel lblCaptcha;
	private XJButton btnNo;

	private int selectedValue;

	public static int showDialog(Window parent, String message, String title) {
		XCaptchaAlert alert = new XCaptchaAlert(parent);
		if (title == null) {
			title = "Peringatan";
		}
		alert.setTitle(title);
		alert.setMessage("<html><center>" + message + "</center></html>");
		alert.generateCaptcha();

		alert.pack();
		alert.setLocationRelativeTo(parent);
		alert.setVisible(true);

		return alert.selectedValue;
	}

	private XCaptchaAlert(Window parent) {
		super(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setPermissionRequired(false);

		selectedValue = NO_OPTION;

		getContentPane().setLayout(
				new MigLayout("", "[500,grow]", "[100px,grow][grow][]"));

		XJPanel pnlMessage = new XJPanel();
		getContentPane()
				.add(pnlMessage, "cell 0 0,alignx center,aligny center");
		pnlMessage.setLayout(new MigLayout("", "[]", "[]"));

		lblMessage = new XJLabel();
		lblMessage.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblMessage.setText("User Message");
		pnlMessage.add(lblMessage, "cell 0 0");

		pnlInput = new XJPanel();
		getContentPane().add(pnlInput, "cell 0 1,grow");
		pnlInput.setLayout(new MigLayout("", "[grow][][100px][][grow]",
				"[grow]"));

		lblInputCaptcha = new XJLabel();
		lblInputCaptcha.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblInputCaptcha.setText("Input Captcha");
		pnlInput.add(lblInputCaptcha, "cell 1 0,alignx trailing");

		txtCaptcha = new XJTextField();
		pnlInput.add(txtCaptcha, "cell 2 0,growx");

		lblCaptcha = new XJLabel();
		lblCaptcha.setForeground(Color.BLUE);
		lblCaptcha.setText("Captcha");
		pnlInput.add(lblCaptcha, "cell 3 0");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[100][100]", "[]"));

		btnYes = new XJButton();
		btnYes.setMnemonic('Y');
		btnYes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					yesAction();
				} catch (Exception ex) {
					ExceptionHandler.handleException(XCaptchaAlert.this, ex);
				}
			}
		});

		btnNo = new XJButton();
		btnNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				noAction();
			}
		});
		btnNo.setMnemonic('N');
		btnNo.setText("No");
		pnlButton.add(btnNo, "cell 0 0,growx");
		btnYes.setText("Yes");
		pnlButton.add(btnYes, "cell 1 0,growx");
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private void yesAction() throws UserException {
		if (!txtCaptcha.getText().equals(lblCaptcha.getText())) {
			ExceptionHandler.handleException(this, new UserException(
					"Captcha is not match"));
			generateCaptcha();
		} else {
			setSelectedValue(YES_OPTION);
		}
	}

	private void noAction() {
		setSelectedValue(NO_OPTION);
	}

	private void setSelectedValue(int selectedValue) {
		this.selectedValue = selectedValue;
		dispose();
	}

	private void setMessage(String message) {
		lblMessage.setText(message);
	}

	private void generateCaptcha() {
		char[] captcha = new char[CAPTCHA_LENGTH];
		for (int i = 0; i < captcha.length; ++i) {
			int firstLevelRandom = random(1, 3);
			char secondLevelRandom;
			if (firstLevelRandom == 1) {
				secondLevelRandom = (char) random(48, 57);
			} else if (firstLevelRandom == 2) {
				secondLevelRandom = (char) random(65, 90);
			} else if (firstLevelRandom == 3) {
				secondLevelRandom = (char) random(97, 122);
			} else {
				throw new RuntimeException(
						"Unexpected error while generate captcha");
			}
			captcha[i] = secondLevelRandom;
		}
		lblCaptcha.setText(new String(captcha));
	}

	private int random(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
