package com.ganesha.core.utils;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

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

	public boolean isExists(String columName, Object columnValue,
			Class<?> entityClass, Session session) {

		boolean exists = false;

		Criteria criteria = session.createCriteria(entityClass);
		criteria.add(Restrictions.eq(columName, columnValue));
		List<?> searchResults = criteria.list();

		if (searchResults.size() > 0) {
			exists = true;
		}
		return exists;
	}
}
