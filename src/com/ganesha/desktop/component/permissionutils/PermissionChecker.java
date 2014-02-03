package com.ganesha.desktop.component.permissionutils;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;

public abstract class PermissionChecker {

	private static PermissionChecker permissionChecker;

	public static void checkPermission(PermissionControl permissionControl)
			throws UserException {
		if (permissionChecker == null) {
			throw new NullPointerException(
					"No implementation registered as permission checker");
		}
		permissionChecker.check(permissionControl);
	}

	public static void register(PermissionChecker permissionChecker)
			throws AppException {
		if (PermissionChecker.permissionChecker == null) {
			PermissionChecker.permissionChecker = permissionChecker;
		} else {
			throw new AppException(
					"An implementation already registered as permission checker");
		}
	}

	public abstract void check(PermissionControl permissionControl)
			throws UserException;
}
