package com.ganesha.accounting.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ganesha.model.Trackable;

@Entity
@Table(name = "ACCOUNTS")
public class Account extends Trackable {

	private static final long serialVersionUID = -5372047327396271313L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "COA_ID", nullable = false)
	private Coa coa;

	@Column(name = "ENTITY_ID", nullable = false)
	private Integer entityId;

	@Column(name = "TIMESTAMP", nullable = false)
	private Timestamp timestamp;

	@Column(name = "NOTES", nullable = false)
	private String notes;

	@Column(name = "REF", nullable = false)
	private String ref;

	@Column(name = "DEBIT", nullable = false)
	private BigDecimal debit;

	@Column(name = "CREDIT", nullable = false)
	private BigDecimal credit;

	public Coa getCoa() {
		return coa;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public BigDecimal getDebit() {
		return debit;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public Integer getId() {
		return id;
	}

	public String getNotes() {
		return notes;
	}

	public String getRef() {
		return ref;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setCoa(Coa coa) {
		this.coa = coa;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public void setDebit(BigDecimal debit) {
		this.debit = debit;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}
