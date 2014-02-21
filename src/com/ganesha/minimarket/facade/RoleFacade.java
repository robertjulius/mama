package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.model.Permission;
import com.ganesha.model.Role;
import com.ganesha.model.RolePermissionLink;

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

	public Role addNewRole(String name, String description,
			List<Permission> permissions, Session session) throws UserException {

		if (DBUtils.getInstance().isExists("name", name, Role.class, session)) {
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

		updateRolePermissionLink(role.getId(), permissions, session);

		return role;
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

	public List<RolePermissionLink> getRolePermissionLinks(int roleId,
			Session session) {
		Query query = session
				.createQuery("FROM RolePermissionLink link WHERE link.primaryKey.role.id = :roleId");

		HqlParameter parameter = new HqlParameter(query);
		parameter.put("roleId", roleId);
		parameter.validate();

		@SuppressWarnings("unchecked")
		List<RolePermissionLink> rolePermissionLinks = query.list();

		return rolePermissionLinks;
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

	public Role updateExistingRole(int id, String name, String description,
			List<Permission> permissions, Session session) throws UserException {

		Role role = getDetail(id, session);
		role.setName(name);
		role.setDescription(description);
		if (!role.getName().equals(name)) {
			if (DBUtils.getInstance().isExists("name", name, Role.class,
					session)) {
				throw new UserException("Role dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				role.setName(name);
			}
		}

		role.setLastUpdatedBy(Main.getUserLogin().getId());
		role.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(role);

		updateRolePermissionLink(role.getId(), permissions, session);

		return role;
	}

	private void deleteRolePermissionLinkByRoleId(int roleId, Session session) {
		String sql = "DELETE FROM role_permission_links WHERE role_id = :roleId";
		SQLQuery query = session.createSQLQuery(sql);

		HqlParameter parameter = new HqlParameter(query);
		parameter.put("roleId", roleId);
		parameter.validate();

		query.executeUpdate();
	}

	private void insertIntoRolePermissionLink(int roleId,
			List<Permission> permissions, Session session) {

		for (Permission permission : permissions) {
			String sql = "INSERT INTO role_permission_links (role_id, permission_code) VALUES (:roleId, :permissionCode)";
			SQLQuery query = session.createSQLQuery(sql);

			HqlParameter parameter = new HqlParameter(query);
			parameter.put("roleId", roleId);
			parameter.put("permissionCode", permission.getCode());
			parameter.validate();

			query.executeUpdate();
		}
	}

	private void updateRolePermissionLink(int roleId,
			List<Permission> permissions, Session session) {
		deleteRolePermissionLinkByRoleId(roleId, session);
		insertIntoRolePermissionLink(roleId, permissions, session);
	}
}
