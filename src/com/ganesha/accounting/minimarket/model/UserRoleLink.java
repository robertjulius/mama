package com.ganesha.accounting.minimarket.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "USER_ROLE_LINKS")
public class UserRoleLink extends Trackable {
	private static final long serialVersionUID = 7322827690429228470L;

	@EmbeddedId
	private UserRoleLinkPK primaryKey;
}
