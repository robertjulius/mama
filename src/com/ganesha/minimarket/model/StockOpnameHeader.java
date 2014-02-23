package com.ganesha.minimarket.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ganesha.model.LogableEntity;
import com.ganesha.model.Trackable;
import com.ganesha.model.User;

@Entity
@Table(name = "STOCK_OPNAME_HEADERS")
public class StockOpnameHeader extends Trackable implements LogableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "PERFORMED_BY", nullable = false)
	private User performedBy;

	@Column(name = "PERFORMED_BEGIN_TIMESTAMP", nullable = false)
	private Timestamp performedBeginTimestamp;

	@Column(name = "PERFORMED_END_TIMESTAMP", nullable = false)
	private Timestamp performedEndTimestamp;

	@Override
	public Integer getId() {
		return id;
	}

	public Timestamp getPerformedBeginTimestamp() {
		return performedBeginTimestamp;
	}

	public User getPerformedBy() {
		return performedBy;
	}

	public Timestamp getPerformedEndTimestamp() {
		return performedEndTimestamp;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPerformedBeginTimestamp(Timestamp performedBeginTimestamp) {
		this.performedBeginTimestamp = performedBeginTimestamp;
	}

	public void setPerformedBy(User performedBy) {
		this.performedBy = performedBy;
	}

	public void setPerformedEndTimestamp(Timestamp performedEndTimestamp) {
		this.performedEndTimestamp = performedEndTimestamp;
	}
}
