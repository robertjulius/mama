package com.ganesha.accounting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ganesha.model.Trackable;

@Entity
@Table(name = "COA_CONTRIBUTIONS")
public class CoaContribution extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private int id;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}