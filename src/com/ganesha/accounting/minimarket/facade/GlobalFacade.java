package com.ganesha.accounting.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class GlobalFacade {

	private static GlobalFacade instance;

	public static GlobalFacade getInstance() {
		if (instance == null) {
			instance = new GlobalFacade();
		}
		return instance;
	}

	private GlobalFacade() {
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
