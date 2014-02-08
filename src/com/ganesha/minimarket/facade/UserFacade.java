package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.model.User;

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
			boolean disabled, boolean deleted, Session session)
			throws UserException {

		if (GlobalFacade.getInstance().isExists("login", login, User.class,
				session)) {
			throw new UserException("User dengan Login ID " + login
					+ " sudah pernah didaftarkan");
		}

		User user = new User();
		user.setLogin(login);
		user.setName(name);
		user.setPassword(password);
		user.setDisabled(disabled);
		user.setDeleted(deleted);
		user.setLastUpdatedBy(Main.getUserLogin().getId());
		user.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(user);
	}

	public User getDetail(int id, Session session) {
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("id", id));

		User user = (User) criteria.uniqueResult();
		return user;
	}

	public User getDetail(String login, Session session) {
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("login", login));

		User user = (User) criteria.uniqueResult();
		return user;
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

	public void updateExistingUser(String login, String password,
			boolean disabled, boolean deleted, Session session)
			throws UserException {

		User user = getDetail(login, session);
		if (password != null && !password.trim().equals("")) {
			user.setPassword(password);
		}
		user.setDisabled(disabled);
		user.setDeleted(deleted);
		user.setLastUpdatedBy(Main.getUserLogin().getId());
		user.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(user);
	}
}
