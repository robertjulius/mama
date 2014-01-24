package com.ganesha.core.utils;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.ganesha.hibernate.HibernateUtils;

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

		Session session = HibernateUtils.openSession();
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
