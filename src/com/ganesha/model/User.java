package com.ganesha.model;

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

	@Column(name = "LOGIN", nullable = false, unique = true)
	private String login;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@OneToMany(mappedBy = "primaryKey.user")
	@JoinColumn(name = "USER_ID", nullable = false)
	private List<UserRoleLink> userRoleLinks;

	public int getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public List<UserRoleLink> getUserRoleLinks() {
		return userRoleLinks;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserRoleLinks(List<UserRoleLink> userRoleLinks) {
		this.userRoleLinks = userRoleLinks;
	}
}
