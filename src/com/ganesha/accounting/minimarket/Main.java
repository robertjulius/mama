package com.ganesha.accounting.minimarket;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.hibernate.Criteria;

import com.ganesha.accounting.minimarket.model.Company;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.accounting.minimarket.ui.forms.forms.login.LoginForm;
import com.ganesha.accounting.util.CoaConsistencyChecker;
import com.ganesha.accounting.util.CompanyConsistencyChecker;
import com.ganesha.core.exception.AppException;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.model.User;

public class Main {

	private static Company company;
	private static User userLogin;

	public static Company getCompany() {
		return company;
	}

	public static User getUserLogin() {
		return userLogin;
	}

	public static void main(String[] args) throws AppException {
		setLookAndFeel();

		CoaConsistencyChecker coaChecker = new CoaConsistencyChecker();
		coaChecker.check();

		CompanyConsistencyChecker companyChecker = new CompanyConsistencyChecker();
		companyChecker.check();
		company = companyChecker.getCompany();

		new LoginForm().setVisible(true);

		// testHibernate();
	}

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public static void setNimbusLookAndFeel() {
		try {

			UIManager.put("control", Color.BLACK);
			UIManager.put("text", Color.WHITE);
			UIManager.put("nimbusBlueGrey", new Color(0, 150, 0));
			UIManager.put("TextPane.background", Color.BLACK);
			UIManager.put("nimbusLightBackground", Color.DARK_GRAY);
			UIManager.put("Button.background", Color.DARK_GRAY);
			UIManager.put("MenuBar.background", Color.DARK_GRAY);

			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());

		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public static void setUserLogin(User userLogin) {
		Main.userLogin = userLogin;
	}

	public static void testHibernate() {
		Criteria criteria = HibernateUtils.openSession().createCriteria(
				ItemStock.class);

		Object o = criteria.list();
		System.out.println(o);

	}
}
