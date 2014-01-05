package com.ganesha.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Trackable implements TableEntity {

	private static final long serialVersionUID = -490441473844263342L;

	@Column(name = "LAST_UPDATED_BY", nullable = false)
	private Integer lastUpdatedBy;

	@Column(name = "LAST_UPDATED_TIMESTAMP", nullable = false)
	private Timestamp lastUpdatedTimestamp;

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

	public Integer getLastUpdatedBy() {
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

	public void setLastUpdatedBy(Integer lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public void setLastUpdatedTimestamp(Timestamp lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}
}
