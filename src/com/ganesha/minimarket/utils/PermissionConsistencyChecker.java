package com.ganesha.minimarket.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.ganesha.core.utils.CommonUtils;
import com.ganesha.desktop.component.permissionutils.PermissionControl;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.GlobalFacade;
import com.ganesha.minimarket.ui.forms.role.RoleForm;
import com.ganesha.minimarket.ui.forms.role.RoleListDialog;
import com.ganesha.minimarket.ui.forms.user.UserForm;
import com.ganesha.minimarket.ui.forms.user.UserListDialog;
import com.ganesha.model.Permission;

public class PermissionConsistencyChecker {

	private List<Class<? extends PermissionControl>> classes;

	public PermissionConsistencyChecker() {
		classes = new ArrayList<>();
		classes.add(RoleListDialog.class);
		classes.add(RoleForm.class);
		classes.add(UserListDialog.class);
		classes.add(UserForm.class);
	}

	public void initDB() {
		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			int userId = 0;
			Timestamp currentTimestamp = CommonUtils.getCurrentTimestamp();

			for (Class<? extends PermissionControl> clazz : classes) {
				String code = clazz.getName();
				String name = clazz.getSimpleName();
				String description = null;

				boolean exists = GlobalFacade.getInstance().isExists("code",
						code, Permission.class, session);
				if (!exists) {
					Permission permission = createPermission(code, name,
							description, userId, currentTimestamp);
					session.saveOrUpdate(permission);
				}
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private Permission createPermission(String code, String name,
			String description, int userId, Timestamp currentTimestamp) {

		Permission permission = new Permission();
		permission.setCode(code);
		permission.setName(name);
		permission.setDescription(description);
		permission.setLastUpdatedBy(userId);
		permission.setLastUpdatedTimestamp(currentTimestamp);

		return permission;
	}
}