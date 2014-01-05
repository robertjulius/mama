package com.ganesha.core.desktop;

public class ExceptionHandler {

	public static void handleException(Exception ex) {
		// Session session = HibernateUtil.getCurrentSession();
		ex.printStackTrace();
	}
}
