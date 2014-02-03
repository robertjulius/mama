package com.ganesha.minimarket;

import java.awt.Color;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.hibernate.Query;
import org.hibernate.Session;

import com.ganesha.accounting.utils.CoaConsistencyChecker;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.model.Company;
import com.ganesha.minimarket.ui.forms.login.LoginForm;
import com.ganesha.minimarket.utils.CompanyConsistencyChecker;
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

	public static void main(String[] args) {
		try {
			// testHibernate2();
			// testResource();
			runApp();
		} catch (Exception ex) {
			ExceptionHandler.handleException(null, ex);
		}
	}

	public static void runApp() throws AppException {
		setLookAndFeel();

		CoaConsistencyChecker coaChecker = new CoaConsistencyChecker();
		coaChecker.check();

		CompanyConsistencyChecker companyChecker = new CompanyConsistencyChecker();
		companyChecker.check();
		company = companyChecker.getCompany();

		new LoginForm().setVisible(true);
	}

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			ExceptionHandler.handleException(null, e);
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
			ExceptionHandler.handleException(null, e);
		}
	}

	public static void setUserLogin(User userLogin) {
		Main.userLogin = userLogin;
	}

	public static void testHibernate() {
		Session session = HibernateUtils.openSession();

		try {
			String sqlString = "SELECT new Map("
					+ "header.transactionNumber AS transactionNumber"
					+ ", detail.orderNum AS orderNum"
					+ ", detail.itemCode AS itemCode"
					+ ", detail.itemName AS itemName"
					+ ", header.transactionTimestamp AS transactionTimestamp"
					+ ") FROM PurchaseDetail detail INNER JOIN detail.purchaseHeader header WHERE 1=1";

			sqlString += " AND header.transactionTimestamp >= :beginDate";

			Date date = new Date(System.currentTimeMillis());
			date = CommonUtils.validateDateBegin(date);

			Query query = session.createQuery(sqlString);

			HqlParameter hqlParameter = new HqlParameter(query);
			hqlParameter.put("beginDate", date);
			hqlParameter.put("endDate", date);
			hqlParameter.validate();

			List<?> map = query.list();

			System.out.println(map);
		} finally {
			session.close();
		}
	}

	public static void testHibernate2() {
		Session session = HibernateUtils.openSession();
		try {
			String sqlString = "SELECT new Map("
					+ "supplier.code AS code"
					+ ", supplier.name AS name"
					+ ", summary.remainingAmount AS remainingAmount"
					+ ") FROM PayableSummary summary, Supplier supplier WHERE summary.clientId = supplier.id";

			Query query = session.createQuery(sqlString);
			List<?> map = query.list();

			System.out.println(map);
		} finally {
			session.close();
		}
	}

	public static void testResource() {
		URL url = Main.class.getResource("");
		System.out.println(url);
	}
}
