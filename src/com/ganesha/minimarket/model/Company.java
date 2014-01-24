package com.ganesha.minimarket.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ganesha.model.Trackable;

@Entity
@Table(name = "COMPANIES")
public class Company extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@Column(name = "ADDRESS", nullable = false)
	private String address;

	@Column(name = "PHONE_1")
	private String phone1;

	@Column(name = "PHONE_2")
	private String phone2;

	@Column(name = "FAX")
	private String fax;

	public String getAddress() {
		return address;
	}

	public String getFax() {
		return fax;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhone1() {
		return phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
}