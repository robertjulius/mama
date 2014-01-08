package com.ganesha.accounting.util;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.ganesha.hibernate.HibernateUtil;

public class DBUtils {

	private static DBUtils instance;

	public static DBUtils getInstance() {
		if (instance == null) {
			instance = new DBUtils();
		}
		return instance;
	}

	private DBUtils() {
	}

	public <E> E getLastValue(String tableName, String columnName,
			Class<E> clazz) {

		Session session = HibernateUtil.openSession();
		try {

			String query = " SELECT " + columnName + " FROM " + tableName
					+ " WHERE id = (SELECT max(id) FROM " + tableName + ")";
			SQLQuery sqlQuery = session.createSQLQuery(query);

			@SuppressWarnings("unchecked")
			E e = (E) sqlQuery.uniqueResult();
			return e;

		} finally {
			session.close();
		}
	}
}
