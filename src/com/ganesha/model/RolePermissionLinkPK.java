package com.ganesha.model;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class RolePermissionLinkPK implements TableEntity {
	private static final long serialVersionUID = 274018399212050879L;

	@ManyToOne
	@JoinColumn(name = "ROLE_ID", nullable = false)
	private Role role;

	@ManyToOne
	@JoinColumn(name = "PERMISSION_CODE", nullable = false)
	private Permission permission;

	public Permission getPermission() {
		return permission;
	}

	public Role getRole() {
		return role;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
