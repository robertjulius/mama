package com.ganesha.coreapps.facade;

import java.sql.Timestamp;

import org.hibernate.Session;

import com.ganesha.core.utils.DateUtils;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.minimarket.facade.PermissionFacade;
import com.ganesha.model.ActivityLog;
import com.ganesha.model.LogableEntity;
import com.ganesha.model.Permission;
import com.ganesha.model.User;

public class ActivityLogFacade {

	private static ActivityLogFacade instance;

	public static void doLog(String permissionCode, ActionType actionType,
			User actionBy, LogableEntity logableEntity, Session session) {
		getInstance().log(permissionCode, actionType, actionBy, logableEntity,
				session);
	}

	public static ActivityLogFacade getInstance() {
		if (instance == null) {
			instance = new ActivityLogFacade();
		}
		return instance;
	}

	private ActivityLogFacade() {
	}

	private ActivityLog log(String permissionCode, ActionType actionType,
			User actionBy, LogableEntity logableEntity, Session session) {

		Permission permission = PermissionFacade.getInstance().getDetail(
				permissionCode, session);

		ActivityLog activityLog = new ActivityLog();
		activityLog.setUserId(actionBy.getId());
		activityLog.setUserLoginId(actionBy.getLogin());
		activityLog.setUserName(actionBy.getName());
		activityLog.setPermissionCode(permission.getCode());
		activityLog.setPermissionName(permission.getName());
		activityLog.setActionType(actionType);
		activityLog.setEntityClass(logableEntity.getClass().getName());
		activityLog.setEntityId(logableEntity.getId());
		activityLog.setActionTimestamp(DateUtils.getCurrent(Timestamp.class));

		session.saveOrUpdate(activityLog);
		return activityLog;
	}
}
