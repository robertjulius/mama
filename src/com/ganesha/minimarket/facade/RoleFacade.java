package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.model.Permission;
import com.ganesha.model.Role;
import com.ganesha.model.RolePermissionLink;
import com.ganesha.model.RolePermissionLinkPK;

public class RoleFacade {

	private static RoleFacade instance;

	public static RoleFacade getInstance() {
		if (instance == null) {
			instance = new RoleFacade();
		}
		return instance;
	}

	private RoleFacade() {
	}

	public void addNewRole(String name, String description,
			List<Permission> permissions, Session session) throws UserException {

		if (GlobalFacade.getInstance().isExists("name", name, Role.class,
				session)) {
			throw new UserException("Role dengan Nama " + name
					+ " sudah pernah didaftarkan");
		}

		Role role = new Role();
		role.setName(name);
		role.setDescription(description);

		role.setDisabled(false);
		role.setDeleted(false);
		role.setLastUpdatedBy(Main.getUserLogin().getId());
		role.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(role);

		for (Permission permission : permissions) {
			RolePermissionLinkPK primaryKey = new RolePermissionLinkPK();
			primaryKey.setRole(role);
			primaryKey.setPermission(permission);

			RolePermissionLink rolePermissionLink = new RolePermissionLink();
			rolePermissionLink.setPrimaryKey(primaryKey);
			rolePermissionLink.setDisabled(false);
			rolePermissionLink.setDeleted(false);
			rolePermissionLink.setLastUpdatedBy(Main.getUserLogin().getId());
			rolePermissionLink.setLastUpdatedTimestamp(CommonUtils
					.getCurrentTimestamp());

			session.saveOrUpdate(rolePermissionLink);
		}
	}

	public List<Role> getAll(Session session) {
		Query query = session.createQuery("from Role");

		@SuppressWarnings("unchecked")
		List<Role> roles = query.list();

		return roles;
	}

	public Role getDetail(int id, Session session) {
		Criteria criteria = session.createCriteria(Role.class);
		criteria.add(Restrictions.eq("id", id));

		Role role = (Role) criteria.uniqueResult();
		return role;
	}

	public Role getDetail(String name) {
		StatelessSession session = HibernateUtils.openStatelessSession();
		try {
			Criteria criteria = session.createCriteria(Role.class);
			criteria.add(Restrictions.eq("name", name));

			Role role = (Role) criteria.uniqueResult();
			Hibernate.initialize(role.getRolePermissionLinks());
			return role;
		} finally {
			session.close();
		}
	}

	public Role getDetail(String name, Session session) {
		Criteria criteria = session.createCriteria(Role.class);
		criteria.add(Restrictions.eq("name", name));

		Role role = (Role) criteria.uniqueResult();
		return role;
	}

	public List<Role> search(String name, boolean disabled, Session session) {
		Criteria criteria = session.createCriteria(Role.class);

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Role> role = criteria.list();

		return role;
	}

	public void updateExistingRole(String name, String description,
			List<Permission> permissions, Session session) throws UserException {

		Role role = getDetail(name, session);
		role.setName(name);
		role.setDescription(description);
		if (!role.getName().equals(name)) {
			if (GlobalFacade.getInstance().isExists("name", name, Role.class,
					session)) {
				throw new UserException("Role dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				role.setName(name);
			}
		}
		List<RolePermissionLink> rolePermissionLinks = role
				.getRolePermissionLinks();
		for (RolePermissionLink rolePermissionLink : rolePermissionLinks) {
			session.delete(rolePermissionLink);
		}

		for (Permission permission : permissions) {
			RolePermissionLinkPK primaryKey = new RolePermissionLinkPK();
			primaryKey.setRole(role);
			primaryKey.setPermission(permission);

			RolePermissionLink rolePermissionLink = (RolePermissionLink) session
					.get(RolePermissionLink.class, primaryKey);
			if (rolePermissionLink == null) {
				rolePermissionLink = new RolePermissionLink();
			}

			rolePermissionLink.setPrimaryKey(primaryKey);
			rolePermissionLink.setDisabled(false);
			rolePermissionLink.setDeleted(false);
			rolePermissionLink.setLastUpdatedBy(Main.getUserLogin().getId());
			rolePermissionLink.setLastUpdatedTimestamp(CommonUtils
					.getCurrentTimestamp());

			session.merge(rolePermissionLink);
		}

		role.setLastUpdatedBy(Main.getUserLogin().getId());
		role.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(role);
	}
}
