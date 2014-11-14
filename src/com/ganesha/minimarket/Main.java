package com.ganesha.minimarket;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.ganesha.core.exception.AppException;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.minimarket.db.DbInitializer;
import com.ganesha.minimarket.model.Company;
import com.ganesha.minimarket.ui.forms.login.LoginForm;
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

			// testLogger();
		} catch (Exception ex) {
			ExceptionHandler.handleException(null, ex);
		}
	}

	public static void runApp() throws AppException, SchedulerException {
		setDefaultUncaughtExceptionHandler();
		setLookAndFeel();

		DbInitializer.initial();

		new LoginForm().setVisible(true);
	}

	public static void runQuartz() throws SchedulerException {
		// Creating scheduler factory and scheduler
		SchedulerFactory factory = new StdSchedulerFactory("quartz.properties");
		Scheduler scheduler = factory.getScheduler();
		// Start scheduler
		scheduler.start();
	}

	public static void setCompany(Company company) {
		Main.company = company;
	}

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			ExceptionHandler.handleException(null, e);
		}
	}

	// public static void setNimbusLookAndFeel() {
	// try {
	// UIManager.put("control", Color.BLACK);
	// UIManager.put("text", Color.WHITE);
	// UIManager.put("nimbusBlueGrey", new Color(0, 150, 0));
	// UIManager.put("TextPane.background", Color.BLACK);
	// UIManager.put("nimbusLightBackground", Color.DARK_GRAY);
	// UIManager.put("Button.background", Color.DARK_GRAY);
	// UIManager.put("MenuBar.background", Color.DARK_GRAY);
	//
	// UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
	//
	// } catch (ClassNotFoundException | InstantiationException
	// | IllegalAccessException | UnsupportedLookAndFeelException e) {
	// ExceptionHandler.handleException(null, e);
	// }
	// }

	public static void setUserLogin(User userLogin) {
		Main.userLogin = userLogin;
	}

	// public static void testHibernate() {
	// Session session = HibernateUtils.openSession();
	//
	// try {
	// String sqlString = "SELECT new Map("
	// + "header.transactionNumber AS transactionNumber"
	// + ", detail.orderNum AS orderNum"
	// + ", detail.itemCode AS itemCode"
	// + ", detail.itemName AS itemName"
	// + ", header.transactionTimestamp AS transactionTimestamp"
	// +
	// ") FROM PurchaseDetail detail INNER JOIN detail.purchaseHeader header WHERE 1=1";
	//
	// sqlString += " AND header.transactionTimestamp >= :beginDate";
	//
	// Date date = new Date(System.currentTimeMillis());
	// date = CommonUtils.validateDateBegin(date);
	//
	// Query query = session.createQuery(sqlString);
	//
	// HqlParameter hqlParameter = new HqlParameter(query);
	// hqlParameter.put("beginDate", date);
	// hqlParameter.put("endDate", date);
	// hqlParameter.validate();
	//
	// List<?> map = query.list();
	//
	// System.out.println(map);
	// } finally {
	// session.close();
	// }
	// }

	// public static void testHibernate2() {
	// Session session = HibernateUtils.openSession();
	// try {
	// String sqlString = "SELECT new Map("
	// + "supplier.code AS code"
	// + ", supplier.name AS name"
	// + ", summary.remainingAmount AS remainingAmount"
	// +
	// ") FROM PayableSummary summary, Supplier supplier WHERE summary.clientId = supplier.id";
	//
	// Query query = session.createQuery(sqlString);
	// List<?> map = query.list();
	//
	// System.out.println(map);
	// } finally {
	// session.close();
	// }
	// }

	// public static void testLogger() {
	// System.out.println("TEST from SysOut");
	// LoggerFactory.getLogger(Loggers.APPLICATION).debug("Test from Logger");
	// LoggerFactory.getLogger(Loggers.APPLICATION).error("Test Error",
	// new Exception("Error coy"));
	// }

	private static void setDefaultUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				ExceptionHandler.handleException(null, e, "Uncaught Exception");
			}
		});
	}
}
