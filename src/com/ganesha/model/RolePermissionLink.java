package com.ganesha.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ROLE_PERMISSION_LINKS")
public class RolePermissionLink implements TableEntity {
	private static final long serialVersionUID = 7322827690429228470L;

	@EmbeddedId
	private RolePermissionLinkPK primaryKey;

	public RolePermissionLinkPK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(RolePermissionLinkPK primaryKey) {
		this.primaryKey = primaryKey;
	}
}
