package com.ganesha.minimarket.utils;

import java.util.List;

import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.permissionutils.PermissionChecker;
import com.ganesha.desktop.component.permissionutils.PermissionControl;
import com.ganesha.minimarket.Main;
import com.ganesha.model.Permission;
import com.ganesha.model.RolePermissionLink;
import com.ganesha.model.UserRoleLink;

public class SimplePermissionChecker extends PermissionChecker {

	@Override
	public void check(PermissionControl permissionControl) throws UserException {
		String permissionCode = permissionControl.getPermissionCode();
		List<UserRoleLink> userRoleLinks = Main.getUserLogin()
				.getUserRoleLinks();
		for (UserRoleLink userRoleLink : userRoleLinks) {
			List<RolePermissionLink> rolePermissionLinks = userRoleLink
					.getPrimaryKey().getRole().getRolePermissionLinks();
			for (RolePermissionLink rolePermissionLink : rolePermissionLinks) {
				Permission permission = rolePermissionLink.getPrimaryKey()
						.getPermission();
				if (permission.getCode().equals(permissionCode)) {
					return;
				}
			}
		}
		throw new UserException(
				"Anda tidak mempunyai ijin untuk mengakses form ini");
	}
}
