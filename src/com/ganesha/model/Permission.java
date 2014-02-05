package com.ganesha.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PERMISSIONS")
public class Permission implements TableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@Column(name = "CODE", nullable = false, unique = true)
	private String code;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "orderNum", nullable = false, unique = true)
	private Integer orderNum;

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
}
