package com.ganesha.accounting.minimarket;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.ganesha.accounting.minimarket.ui.login.LoginForm;

public class Main {

	public static void main(String[] args) {
		setLookAndFeel();

		new LoginForm().setVisible(true);
	}

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
