package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.model.Permission;

public class PermissionFacade {

	private static PermissionFacade instance;

	public static PermissionFacade getInstance() {
		if (instance == null) {
			instance = new PermissionFacade();
		}
		return instance;
	}

	private PermissionFacade() {
	}

	public List<Permission> getAll(Session session) {
		Query query = session
				.createQuery("from Permission p order by p.orderNum");

		@SuppressWarnings("unchecked")
		List<Permission> permissions = query.list();

		return permissions;
	}

	public Permission getDetail(String code, Session session) {
		Criteria criteria = session.createCriteria(Permission.class);
		criteria.add(Restrictions.eq("code", code));

		Permission permission = (Permission) criteria.uniqueResult();
		return permission;
	}
}
