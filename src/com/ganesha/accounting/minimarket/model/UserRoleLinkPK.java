package com.ganesha.accounting.minimarket.model;

import java.io.Serializable;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class UserRoleLinkPK implements Serializable {
	private static final long serialVersionUID = 274018399212050879L;

	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "ROLE_ID", nullable = false)
	private Role role;

}
