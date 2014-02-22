package com.ganesha.accounting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ganesha.accounting.model.Circle;
import com.ganesha.accounting.model.Coa;
import com.ganesha.model.Inactivable;
import com.ganesha.model.LogableEntity;

@Entity
@Table(name = "EXPENSES")
public class Expense extends Inactivable implements LogableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@ManyToOne
	@JoinColumn(name = "COA_ID", nullable = false)
	private Coa coa;

	@ManyToOne
	@JoinColumn(name = "CIRCLE_ID", nullable = false)
	private Circle circle;

	public Circle getCircle() {
		return circle;
	}

	public Coa getCoa() {
		return coa;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}

	public void setCoa(Coa coa) {
		this.coa = coa;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}