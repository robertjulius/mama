package com.ganesha.desktop.component.permissionutils;

public interface PermissionControl {

	public String getPermissionCode();

	public boolean isPermissionRequired();

	public void setPermissionCode(String code);

	public void setPermissionRequired(boolean permissionRequired);
}
