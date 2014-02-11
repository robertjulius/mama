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

	public Integer getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public Timestamp getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}

	public void setLastUpdatedBy(Integer lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public void setLastUpdatedTimestamp(Timestamp lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}
}
