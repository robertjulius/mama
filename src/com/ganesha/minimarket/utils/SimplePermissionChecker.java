package com.ganesha.minimarket.utils;

import java.util.List;

import com.ganesha.core.exception.AppException;
import com.ganesha.desktop.component.permissionutils.PermissionChecker;
import com.ganesha.desktop.component.permissionutils.PermissionControl;
import com.ganesha.minimarket.Main;
import com.ganesha.model.Permission;
import com.ganesha.model.RolePermissionLink;
import com.ganesha.model.UserRoleLink;

public class SimplePermissionChecker extends PermissionChecker {

	@Override
	public boolean check(PermissionControl permissionControl)
			throws AppException {
		String permissionCode = permissionControl.getPermissionCode();
		if (permissionCode == null) {
			throw new AppException("Permission Code for "
					+ permissionControl.getClass().getCanonicalName()
					+ " is not set");
		}
		List<UserRoleLink> userRoleLinks = Main.getUserLogin()
				.getUserRoleLinks();
		for (UserRoleLink userRoleLink : userRoleLinks) {
			List<RolePermissionLink> rolePermissionLinks = userRoleLink
					.getPrimaryKey().getRole().getRolePermissionLinks();
			for (RolePermissionLink rolePermissionLink : rolePermissionLinks) {
				Permission permission = rolePermissionLink.getPrimaryKey()
						.getPermission();
				if (permission.getCode().equals(permissionCode)) {
					return true;
				}
			}
		}
		return false;
	}
}
