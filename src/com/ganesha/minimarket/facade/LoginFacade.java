package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.SecurityUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.model.User;
import com.ganesha.model.UserRoleLink;

public class LoginFacade {

	private static LoginFacade instance;

	public static LoginFacade getInstance() {
		if (instance == null) {
			instance = new LoginFacade();
		}
		return instance;
	}

	private LoginFacade() {
	}

	public boolean login(String loginId, String password) throws UserException,
			AppException {
		Session session = HibernateUtils.openSession();
		try {

			Criteria criteria = session.createCriteria(User.class);
			criteria.add(Restrictions.eq("login", loginId).ignoreCase());

			User user = (User) criteria.uniqueResult();

			if (user != null) {
				if (!validatePassword(password, user.getPassword())) {
					throw new UserException("Login ID atau Password salah");
				}
				List<UserRoleLink> userRoleLinks = user.getUserRoleLinks();
				for (UserRoleLink userRoleLink : userRoleLinks) {
					Hibernate.initialize(userRoleLink.getPrimaryKey().getRole()
							.getRolePermissionLinks());
				}
				Main.setUserLogin(user);
				return true;
			} else {
				return false;
			}
		} finally {
			session.close();
		}
	}

	public boolean validatePassword(String passwordToBeCheck,
			String passwordInDb) throws AppException {
		String hashedPassword = SecurityUtils.hash(passwordToBeCheck);
		if (passwordInDb.equalsIgnoreCase(hashedPassword)) {
			return true;
		}
		return false;
	}
}
