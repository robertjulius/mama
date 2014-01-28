package com.ganesha.hibernate;

import java.io.File;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.LoggerFactory;

import com.ganesha.core.utils.ResourceUtils;

public class HibernateUtils {

	private static final String FILE_HIBERNATE_CONFIG_XML = "hibernate.cfg.xml";
	private static SessionFactory sessionFactory;

	static {
		try {
			File configBase = ResourceUtils.getConfigBase();
			File file = new File(configBase, FILE_HIBERNATE_CONFIG_XML);
			sessionFactory = new AnnotationConfiguration().configure(file)
					.buildSessionFactory();
		} catch (RuntimeException e) {
			LoggerFactory.getLogger(HibernateUtils.class).error(
					"Initial SessionFactory creation failed:" + e.getMessage(),
					e);
			throw e;
		}
	}

	public static Session openSession() throws HibernateException {
		return sessionFactory.openSession();
	}
}
