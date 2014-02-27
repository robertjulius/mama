package com.ganesha.accounting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ganesha.accounting.constants.Enums.CircleUnit;
import com.ganesha.model.Identifiable;
import com.ganesha.model.Inactivable;
import com.ganesha.model.LogableEntity;

@Entity
@Table(name = "CIRCLES")
public class Circle extends Inactivable implements LogableEntity, Identifiable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "UNIT")
	private CircleUnit unit;

	@Column(name = "DURATION")
	private Integer duration;

	public Integer getDuration() {
		return duration;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public CircleUnit getUnit() {
		return unit;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUnit(CircleUnit unit) {
		this.unit = unit;
	}

}