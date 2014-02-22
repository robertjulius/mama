package com.ganesha.accounting.facade;

import java.util.List;

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

	public Coa getDetail(Integer id, Session session) {
		Coa coa = (Coa) session.get(Coa.class, id);
		return coa;
	}

	public List<Coa> search(String name, Integer coaGroupId, boolean disabled,
			Session session) {
		Criteria criteria = session.createCriteria(Coa.class);
		criteria.createAlias("coaGroup", "coaGroup");

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		if (coaGroupId != null) {
			criteria.add(Restrictions.eq("coaGroup.id", coaGroupId));
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Coa> coaList = criteria.list();

		return coaList;
	}
}
