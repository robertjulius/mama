package com.ganesha.minimarket;

import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.ResourceUtils;
import com.ganesha.coreapps.constants.Loggers;
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
			runApp();
		} catch (Exception ex) {
			ExceptionHandler.handleException(null, ex);
		}
	}

	public static void runApp() throws AppException, SchedulerException {
		setDefaultUncaughtExceptionHandler();
		loadLoggingCofiguration();
		setLookAndFeel();

		DbInitializer.initial();
		runQuartz();

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
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			ExceptionHandler.handleException(null, e);
		}
	}

	public static void setUserLogin(User userLogin) {
		Main.userLogin = userLogin;
	}

	private static void setDefaultUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				ExceptionHandler.handleException(null, e, "Uncaught Exception");
			}
		});
	}

	private static void loadLoggingCofiguration() {
		String loggingConfigFileName = SystemSetting.getProperty(GeneralConstants.SYSTEM_PROPERTY_LOGGIN_FILE);
		File loggingConfigFile = new File(ResourceUtils.getConfigBase(), loggingConfigFileName);
		System.setProperty("logback.configurationFile", loggingConfigFile.getAbsolutePath());
		LoggerFactory.getLogger(Loggers.BUTTON).trace("====== Starting logging capability =====");
	}
}
