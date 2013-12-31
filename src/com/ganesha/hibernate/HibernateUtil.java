package com.ganesha.hibernate;

import java.io.File;
import java.net.URISyntaxException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	public static final ThreadLocal<Session> threadSession = new ThreadLocal<Session>();
	public static final ThreadLocal<Transaction> threadTransaction = new ThreadLocal<Transaction>();

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

	public static void beginTransaction() {
		Transaction tx = threadTransaction.get();
		if (tx == null) {
			tx = getCurrentSession().beginTransaction();
			threadTransaction.set(tx);
		}
	}

	public static void closeSession() throws HibernateException {
		Session s = threadSession.get();
		threadSession.set(null);
		if (s != null && !s.isOpen()) {
			s.close();
		}
	}

	public static void commitTransaction() {
		Transaction tx = threadTransaction.get();
		try {
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				tx.commit();
			}
			threadTransaction.set(null);
		} catch (HibernateException ex) {
			rollbackTransaction();
			throw ex;
		}
	}

	public static Session getCurrentSession() throws HibernateException {
		return getCurrentSession(false);
	}

	public static Session getCurrentSession(Boolean forceNewConnection)
			throws HibernateException {
		Session s = null;
		if (!forceNewConnection) {
			s = threadSession.get();
		}

		// Open a new Session, if this Thread has none yet
		if (s == null) {
			s = sessionFactory.openSession();
			threadSession.set(s);
		}
		return s;
	}

	public static SessionFactory getSessionFactory() throws HibernateException {
		return sessionFactory;
	}

	public static Transaction getTransaction() {
		return threadTransaction.get();
	}

	public static void rollbackTransaction() {
		Transaction tx = threadTransaction.get();
		threadTransaction.set(null);
		try {
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				tx.rollback();
			}
		} finally {
			closeSession();
		}

	}
}
