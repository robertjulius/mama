package com.ganesha.hibernate;

import java.io.File;
import java.net.URISyntaxException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.LoggerFactory;

public class HibernateUtils {

	private static SessionFactory sessionFactory;

	static {
		try {
			File file = new File(HibernateUtils.class.getResource(
					"/configurations/hibernate.cfg.xml").toURI());
			sessionFactory = new AnnotationConfiguration().configure(file)
					.buildSessionFactory();
		} catch (RuntimeException e) {
			LoggerFactory.getLogger(HibernateUtils.class).error(
					"Initial SessionFactory creation failed:" + e.getMessage(),
					e);
			throw e;
		} catch (URISyntaxException e) {
			LoggerFactory.getLogger(HibernateUtils.class).error(
					"Initial SessionFactory creation failed:" + e.getMessage(),
					e);
			System.exit(1);
		}
	}

	public static Session openSession() throws HibernateException {
		return sessionFactory.openSession();
	}
}
