package com.ganesha.hibernate;

import java.io.File;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.slf4j.LoggerFactory;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.ResourceUtils;

public class HibernateUtils {

	private static final String FILE_HIBERNATE_CONFIG_XML = "hibernate.cfg.xml";
	private static SessionFactory sessionFactory;

	static {
		try {
			File configBase = ResourceUtils.getConfigBase();
			File file = new File(configBase, FILE_HIBERNATE_CONFIG_XML);

			Configuration config = new AnnotationConfiguration();
			config.configure(file);

			String url = SystemSetting
					.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_URL);
			if (url != null) {
				config.setProperty("hibernate.connection.url", url);
			}

			String username = SystemSetting
					.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_USERNAME);
			if (username != null) {
				config.setProperty("hibernate.connection.username", username);
			}

			String password = SystemSetting
					.getProperty(GeneralConstants.SYSTEM_PROPERTY_DB_PASSWORD);
			if (password != null) {
				config.setProperty("hibernate.connection.password", password);
			}

			sessionFactory = config.buildSessionFactory();

		} catch (RuntimeException e) {
			LoggerFactory.getLogger(HibernateUtils.class).error(
					"Initial SessionFactory creation failed:" + e.getMessage(),
					e);
			throw e;
		}
	}

	public static Session openSession() throws HibernateException {
		Session session = sessionFactory.openSession();
		return session;
	}
}
