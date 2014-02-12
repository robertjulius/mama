package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.SecurityUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.model.Role;
import com.ganesha.model.User;
import com.ganesha.model.UserRoleLink;

public class UserFacade {

	private static UserFacade instance;

	public static UserFacade getInstance() {
		if (instance == null) {
			instance = new UserFacade();
		}
		return instance;
	}

	private UserFacade() {
	}

	public void addNewUser(String login, String name, String password,
			List<Role> roles, boolean disabled, boolean deleted, Session session)
			throws UserException, AppException {

		if (GlobalFacade.getInstance().isExists("login", login, User.class,
				session)) {
			throw new UserException("User dengan Login ID " + login
					+ " sudah pernah didaftarkan");
		}

		User user = new User();
		user.setLogin(login);
		user.setName(name);
		user.setPassword(SecurityUtils.hash(password));
		user.setDisabled(disabled);
		user.setDeleted(deleted);
		user.setLastUpdatedBy(Main.getUserLogin().getId());
		user.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(user);

		updateUserRoleLink(user.getId(), roles, session);
	}

	public void changePassword(int userId, String oldPassword,
			String newPassword, Session session) throws AppException,
			UserException {

		User user = getDetail(userId, session);
		boolean oldPasswordValid = LoginFacade.getInstance().validatePassword(
				oldPassword, user.getPassword());

		if (!oldPasswordValid) {
			throw new UserException("Password Lama tidak sesuai");
		}

		String hashedPassword = SecurityUtils.hash(newPassword);
		user.setPassword(hashedPassword);
		user.setLastUpdatedBy(Main.getUserLogin().getId());
		user.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(user);
	}

	public User getDetail(int id, Session session) {
		User user = (User) session.get(User.class, id);
		return user;
	}

	public User getDetail(String login, Session session) {
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("login", login));

		User user = (User) criteria.uniqueResult();
		return user;
	}

	public List<UserRoleLink> getUserRoleLinks(int userId, Session session) {
		Query query = session
				.createQuery("FROM UserRoleLink link WHERE link.primaryKey.user.id = :userId");

		HqlParameter parameter = new HqlParameter(query);
		parameter.put("userId", userId);
		parameter.validate();

		@SuppressWarnings("unchecked")
		List<UserRoleLink> userRoleLinks = query.list();

		return userRoleLinks;
	}

	public List<User> search(String login, String name, boolean disabled,
			Session session) {
		Criteria criteria = session.createCriteria(User.class);

		if (login != null && !login.trim().isEmpty()) {
			criteria.add(Restrictions.like("login", "%" + login + "%")
					.ignoreCase());
		}

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<User> user = criteria.list();

		return user;
	}

	public void updateExistingUser(String login, String name, String password,
			List<Role> roles, boolean disabled, boolean deleted, Session session)
			throws UserException, AppException {

		User user = getDetail(login, session);
		user.setName(name);
		if (password != null && !password.trim().equals("")) {
			user.setPassword(SecurityUtils.hash(password));
		}
		if (deleted) {
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus User yang masih dalam kondisi aktif");
			}
		}
		user.setDisabled(disabled);
		user.setDeleted(deleted);
		user.setLastUpdatedBy(Main.getUserLogin().getId());
		user.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(user);

		updateUserRoleLink(user.getId(), roles, session);
	}

	private void deleteUserRoleLinkByUserId(int userId, Session session) {
		String sql = "DELETE FROM user_role_links WHERE user_id = :userId";
		SQLQuery query = session.createSQLQuery(sql);

		HqlParameter parameter = new HqlParameter(query);
		parameter.put("userId", userId);
		parameter.validate();

		query.executeUpdate();
	}

	private void insertIntoUserRoleLink(int userId, List<Role> roles,
			Session session) {

		for (Role role : roles) {
			String sql = "INSERT INTO user_role_links (user_id, role_id) VALUES (:userId, :roleId)";
			SQLQuery query = session.createSQLQuery(sql);

			HqlParameter parameter = new HqlParameter(query);
			parameter.put("userId", userId);
			parameter.put("roleId", role.getId());
			parameter.validate();

			query.executeUpdate();
		}
	}

	private void updateUserRoleLink(int userId, List<Role> roles,
			Session session) {
		deleteUserRoleLinkByUserId(userId, session);
		insertIntoUserRoleLink(userId, roles, session);
	}
}
