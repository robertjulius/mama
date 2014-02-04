package com.ganesha.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "USER_ROLE_LINKS")
public class UserRoleLink implements TableEntity {
	private static final long serialVersionUID = 7322827690429228470L;

	@EmbeddedId
	private UserRoleLinkPK primaryKey;

	public UserRoleLinkPK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(UserRoleLinkPK primaryKey) {
		this.primaryKey = primaryKey;
	}
}
