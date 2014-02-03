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
@Table(name = "ROLES")
public class Role extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@OneToMany(mappedBy = "primaryKey.role")
	@JoinColumn(name = "ROLE_ID", nullable = false)
	private List<RolePermissionLink> rolePermissionLinks;

	public String getDescription() {
		return description;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<RolePermissionLink> getRolePermissionLinks() {
		return rolePermissionLinks;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRolePermissionLinks(
			List<RolePermissionLink> rolePermissionLinks) {
		this.rolePermissionLinks = rolePermissionLinks;
	}
}
