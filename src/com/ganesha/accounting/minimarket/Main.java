package com.ganesha.accounting.minimarket;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.hibernate.Criteria;

import com.ganesha.accounting.minimarket.model.Company;
import com.ganesha.accounting.minimarket.model.GoodStock;
import com.ganesha.accounting.minimarket.ui.forms.forms.login.LoginForm;
import com.ganesha.accounting.util.CoaConsistencyChecker;
import com.ganesha.accounting.util.CompanyConsistencyChecker;
import com.ganesha.core.exception.AppException;
import com.ganesha.hibernate.HibernateUtil;
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

	public static void setUserLogin(User userLogin) {
		Main.userLogin = userLogin;
	}

	public static void testHibernate() {
		Criteria criteria = HibernateUtil.openSession().createCriteria(
				GoodStock.class);

		Object o = criteria.list();
		System.out.println(o);

	}
}
