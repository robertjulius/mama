package com.ganesha.accounting.facade;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.model.Coa;

public class CoaFacade {

	private static CoaFacade instance;

	public static CoaFacade getInstance() {
		if (instance == null) {
			instance = new CoaFacade();
		}
		return instance;
	}

	private CoaFacade() {
	}

	public Coa getDetail(int id, Session session) {
		Criteria criteria = session.createCriteria(Coa.class);
		criteria.add(Restrictions.eq("id", id));

		Coa coa = (Coa) criteria.uniqueResult();
		return coa;
	}
}
