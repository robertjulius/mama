package com.ganesha.minimarket.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ganesha.model.Inactivable;

@Entity
@Table(name = "SUPPLIERS")
public class Supplier extends Inactivable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "CODE", nullable = false, unique = true)
	private String code;

	@Column(name = "ADDRESS_1")
	private String address1;

	@Column(name = "PHONE_1")
	private String phone1;

	@Column(name = "EMAIL_1")
	private String email1;

	@Column(name = "ADDRESS_2")
	private String address2;

	@Column(name = "PHONE_2")
	private String phone2;

	@Column(name = "EMAIL_2")
	private String email2;

	@Column(name = "CONTACT_PERSON_1")
	private String contactPerson1;

	@Column(name = "CONTACT_PERSON_1_PHONE")
	private String contactPerson1Phone;

	@Column(name = "CONTACT_PERSON_1_EMAIL")
	private String contactPerson1Email;

	@Column(name = "CONTACT_PERSON_2")
	private String contactPerson2;

	@Column(name = "CONTACT_PERSON_2_PHONE")
	private String contactPerson2Phone;

	@Column(name = "CONTACT_PERSON_2_EMAIL")
	private String contactPerson2Email;

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getCode() {
		return code;
	}

	public String getContactPerson1() {
		return contactPerson1;
	}

	public String getContactPerson1Email() {
		return contactPerson1Email;
	}

	public String getContactPerson1Phone() {
		return contactPerson1Phone;
	}

	public String getContactPerson2() {
		return contactPerson2;
	}

	public String getContactPerson2Email() {
		return contactPerson2Email;
	}

	public String getContactPerson2Phone() {
		return contactPerson2Phone;
	}

	public String getDescription() {
		return description;
	}

	public String getEmail1() {
		return email1;
	}

	public String getEmail2() {
		return email2;
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

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setContactPerson1(String contactPerson1) {
		this.contactPerson1 = contactPerson1;
	}

	public void setContactPerson1Email(String contactPerson1Email) {
		this.contactPerson1Email = contactPerson1Email;
	}

	public void setContactPerson1Phone(String contactPerson1Phone) {
		this.contactPerson1Phone = contactPerson1Phone;
	}

	public void setContactPerson2(String contactPerson2) {
		this.contactPerson2 = contactPerson2;
	}

	public void setContactPerson2Email(String contactPerson2Email) {
		this.contactPerson2Email = contactPerson2Email;
	}

	public void setContactPerson2Phone(String contactPerson2Phone) {
		this.contactPerson2Phone = contactPerson2Phone;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
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