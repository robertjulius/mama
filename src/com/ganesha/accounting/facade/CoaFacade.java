package com.ganesha.accounting.facade;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

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

	public List<Coa> getAll(Session session) {
		Query query = session.createQuery("from Coa");

		@SuppressWarnings("unchecked")
		List<Coa> coaList = query.list();

		return coaList;
	}

	public Coa getDetail(Integer id, Session session) {
		Coa coa = (Coa) session.get(Coa.class, id);
		return coa;
	}
}
