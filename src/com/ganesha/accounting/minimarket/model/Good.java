package com.ganesha.accounting.minimarket.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ganesha.model.Trackable;

@Entity
@Table(name = "GOODS")
public class Good extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private int id;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@Column(name = "CODE", nullable = false, unique = true)
	private String code;

	@Column(name = "BARCODE")
	private String barcode;

	public String getBarcode() {
		return barcode;
	}

	public String getCode() {
		return code;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}
