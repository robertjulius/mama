package com.ganesha.accounting.minimarket.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Trackable implements TableEntity {

	private static final long serialVersionUID = -490441473844263342L;

	@Column(name = "LAST_UPDATED_BY", nullable = false)
	private String lastUpdatedBy;

	@Column(name = "LAST_UPDATED_TIMESTAMP", nullable = false)
	private Timestamp lastUpdatedTimestamp;

	@Column(name = "DISABLED", nullable = false)
	private Boolean disabled;

	@Column(name = "DELETED", nullable = false)
	private Boolean deleted;

	public Boolean getDeleted() {
		return deleted;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public Timestamp getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public void setLastUpdatedTimestamp(Timestamp lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}
}
