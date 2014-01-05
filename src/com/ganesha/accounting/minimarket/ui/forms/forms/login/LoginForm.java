package com.ganesha.accounting.minimarket.ui.forms.forms.login;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.facade.LoginFacade;
import com.ganesha.accounting.minimarket.ui.forms.MainFrame;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJFrame;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTextField;

public class LoginForm extends XJFrame {
	private static final long serialVersionUID = -4959314146160473962L;
	private XJTextField txtPassword;
	private XJTextField txtLoginId;
	private XJLabel lblLblImage;
	private XJButton btnClear;
	private XJButton btnLogin;

	public LoginForm() {

		setTitle(Main.getCompany().getName());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(
				new MigLayout("", "[grow][]", "[grow][][][][]"));

		JPanel pnlHeader = new JPanel();
		getContentPane().add(pnlHeader, "cell 0 0 2 1,grow");
		pnlHeader.setLayout(new MigLayout("", "[]", "[]"));

		JLabel lblLogin = new JLabel("Login");
		lblLogin.setFont(new Font("Tahoma", Font.BOLD, 30));
		pnlHeader.add(lblLogin, "cell 0 0");

		JSeparator separator = new JSeparator();
		getContentPane().add(separator, "cell 0 1 2 1,grow");

		JPanel pnlImage = new JPanel();
		getContentPane().add(pnlImage, "flowx,cell 0 2 1 3,growy");
		pnlImage.setLayout(new MigLayout("", "[100px]", "[75px]"));

		lblLblImage = new XJLabel();
		pnlImage.add(lblLblImage, "cell 0 0,alignx left,aligny top");
		lblLblImage.setIcon(loadImageFromFile(
				LoginForm.class.getResource("/images/login.jpg"), 100, 75));

		JPanel pnlInput = new JPanel();
		getContentPane().add(pnlInput, "cell 1 2,grow");
		pnlInput.setLayout(new MigLayout("", "[][200px]", "[][][]"));

		XJLabel lblLoginId = new XJLabel("Login ID");
		pnlInput.add(lblLoginId, "cell 0 0,alignx right");

		txtLoginId = new XJTextField();
		pnlInput.add(txtLoginId, "cell 1 0,growx");
		txtLoginId.setColumns(10);

		XJLabel lblLblPassword = new XJLabel("Password");
		pnlInput.add(lblLblPassword, "cell 0 1,alignx right");

		txtPassword = new XJTextField();
		pnlInput.add(txtPassword, "cell 1 1,growx");
		txtPassword.setColumns(10);

		JSeparator separator_1 = new JSeparator();
		getContentPane().add(separator_1, "cell 1 3,grow");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 1 4,alignx right,growy");
		pnlButton.setLayout(new MigLayout("", "[][][]", "[][]"));

		btnClear = new XJButton("Clear");
		btnClear.setMnemonic('C');
		pnlButton.add(btnClear, "cell 0 0");

		btnLogin = new XJButton("Login");
		btnLogin.setMnemonic('L');
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				login(txtLoginId.getText(), txtPassword.getText());
			}
		});
		pnlButton.add(btnLogin, "cell 1 0");

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			btnLogin.doClick();
			break;
		default:
			break;
		}
	}

	private ImageIcon loadImageFromFile(URL fileUrl, int width, int height) {
		ImageIcon imageIcon = null;
		try {
			BufferedImage bufferedImage = ImageIO
					.read(new File(fileUrl.toURI()));
			Image scaledImage = bufferedImage.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(scaledImage);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return imageIcon;
	}

	private void login(String loginId, String password) {

		LoginFacade facade = LoginFacade.getInstance();
		boolean success = facade.login(loginId);
		if (success) {
			setVisible(false);
			new MainFrame().setVisible(true);
		} else {
			/*
			 * TODO
			 */
		}
	}
}
