package com.ganesha.accounting.minimarket.ui.login;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.model.User;
import com.ganesha.accounting.minimarket.ui.MainFrame;
import com.ganesha.hibernate.HibernateUtil;

public class LoginForm extends JFrame {
	private static final long serialVersionUID = -4959314146160473962L;
	private JTextField txtPassword;
	private JTextField txtLoginId;
	private JLabel lblLblImage;

	public LoginForm() {
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

		lblLblImage = new JLabel();
		pnlImage.add(lblLblImage, "cell 0 0,alignx left,aligny top");
		lblLblImage.setIcon(loadImageFromFile(
				LoginForm.class.getResource("/images/login.jpg"), 100, 75));

		JPanel pnlInput = new JPanel();
		getContentPane().add(pnlInput, "cell 1 2,grow");
		pnlInput.setLayout(new MigLayout("", "[][200px]", "[][][]"));

		JLabel lblLoginId = new JLabel("Login ID");
		pnlInput.add(lblLoginId, "cell 0 0,alignx right");

		txtLoginId = new JTextField();
		pnlInput.add(txtLoginId, "cell 1 0,growx");
		txtLoginId.setColumns(10);

		JLabel lblLblPassword = new JLabel("Password");
		pnlInput.add(lblLblPassword, "cell 0 1,alignx right");

		txtPassword = new JTextField();
		pnlInput.add(txtPassword, "cell 1 1,growx");
		txtPassword.setColumns(10);

		JSeparator separator_1 = new JSeparator();
		getContentPane().add(separator_1, "cell 1 3,grow");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 1 4,alignx right,growy");
		pnlButton.setLayout(new MigLayout("", "[][][]", "[][]"));

		JButton btnClear = new JButton("Clear");
		pnlButton.add(btnClear, "cell 0 0");

		JButton btnLogin = new JButton("Login");
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

		Session session = HibernateUtil.getCurrentSession();

		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("login", loginId).ignoreCase());

		User user = (User) criteria.uniqueResult();

		if (user != null) {
			LoginForm.this.setVisible(false);
			new MainFrame().setVisible(true);
		}
	}
}
