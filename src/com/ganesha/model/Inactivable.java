package com.ganesha.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Inactivable extends Trackable {

	private static final long serialVersionUID = -490441473844263342L;

	@Column(name = "DISABLED", nullable = false)
	private Boolean disabled = false;

	@Column(name = "DELETED", nullable = false)
	private Boolean deleted = false;

	public Boolean getDeleted() {
		return deleted;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
}
