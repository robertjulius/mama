package com.ganesha.minimarket.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ganesha.accounting.model.Coa;
import com.ganesha.model.Trackable;

@Entity
@Table(name = "REVENUE_TRANSACTIONS")
public class RevenueTransaction extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "COA_ID", nullable = false)
	private Coa coa;

	@Column(name = "AMOUNT", nullable = false)
	private BigDecimal amount;

	@Column(name = "NOTES", nullable = false)
	private String notes;

	public BigDecimal getAmount() {
		return amount;
	}

	public Coa getCoa() {
		return coa;
	}

	public Integer getId() {
		return id;
	}

	public String getNotes() {
		return notes;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setCoa(Coa coa) {
		this.coa = coa;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
