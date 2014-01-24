package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.model.Tax;

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

	public double getTaxPercent() {
		double taxPercent = 0;
		Session session = HibernateUtils.openSession();
		try {
			Criteria criteria = session.createCriteria(Tax.class);
			criteria.add(Restrictions.eq("code", GeneralConstants.TAX_CODE_PPN));
			Tax tax = (Tax) criteria.uniqueResult();
			if (tax != null) {
				taxPercent = tax.getTaxPercent().doubleValue();
			}
		} finally {
			session.close();
		}
		return taxPercent;
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
