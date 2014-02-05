package com.ganesha.desktop.component.permissionutils;

import com.ganesha.core.exception.AppException;

public abstract class PermissionChecker {

	private static PermissionChecker permissionChecker;

	public static boolean checkPermission(PermissionControl permissionControl)
			throws AppException {

		if (isLoadByEclipse()) {
			return true;
		}

		if (permissionChecker == null) {
			throw new NullPointerException(
					"No implementation registered as permission checker");
		}
		return permissionChecker.check(permissionControl);
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

	private static boolean isLoadByEclipse() {
		String eclipseClassLoader = "org.eclipse.wb.internal.core.utils.reflect.ProjectClassLoader";
		ClassLoader classLoader = PermissionChecker.class.getClassLoader();
		String classLoaderName = classLoader.getClass().getName();
		return classLoaderName.equals(eclipseClassLoader);
	}

	public abstract boolean check(PermissionControl permissionControl)
			throws AppException;
}
