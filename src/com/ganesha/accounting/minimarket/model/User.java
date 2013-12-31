package com.ganesha.accounting.minimarket.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class User extends Trackable {
	private static final long serialVersionUID = -7108231842533900375L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private int id;

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "LOGIN", nullable = false)
	private String login;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@OneToMany(mappedBy = "primaryKey.user")
	@JoinColumn(name = "USER_ID", nullable = false)
	private List<UserRoleLink> userRoleLinks;

}
