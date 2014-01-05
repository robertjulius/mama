package com.ganesha.hibernate;

import java.io.File;
import java.net.URISyntaxException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	private static Session session;

	static {
		try {
			File file = new File(HibernateUtil.class.getResource(
					"/configurations/hibernate.cfg.xml").toURI());
			sessionFactory = new AnnotationConfiguration().configure(file)
					.buildSessionFactory();
		} catch (RuntimeException e) {
			LoggerFactory.getLogger(HibernateUtil.class).error(
					"Initial SessionFactory creation failed:" + e.getMessage(),
					e);
			throw e;
		} catch (URISyntaxException e) {
			LoggerFactory.getLogger(HibernateUtil.class).error(
					"Initial SessionFactory creation failed:" + e.getMessage(),
					e);
			System.exit(1);
		}
	}

	public static Session forceCreateNewSession() throws HibernateException {
		return sessionFactory.openSession();
	}

	public static Session getSession() throws HibernateException {
		if (session == null || !session.isOpen()) {
			session = sessionFactory.openSession();
		}
		return session;
	}
}
