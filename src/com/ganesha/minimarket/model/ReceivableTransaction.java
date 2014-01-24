package com.ganesha.minimarket.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

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
@Table(name = "RECEIVABLE_TRANSACTIONS")
public class ReceivableTransaction extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "RECEIVABLE_SUMMARY_ID", nullable = false)
	private ReceivableSummary receivableSummary;

	@Column(name = "REFF_NUMBER", nullable = false, unique = true)
	private String reffNumber;

	@Column(name = "ACCOUNT_ACTION", nullable = false)
	private String accountAction;

	@Column(name = "ACTION_TIMESTAMP", nullable = false)
	private Timestamp actionTimestamp;

	@Column(name = "MATURITY_DATE", nullable = false)
	private Date maturityDate;

	@Column(name = "AMOUNT", nullable = false)
	private BigDecimal amount;

	@Column(name = "DESCRIPTION", nullable = false)
	private String description;

	public String getAccountAction() {
		return accountAction;
	}

	public Timestamp getActionTimestamp() {
		return actionTimestamp;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public Integer getId() {
		return id;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public ReceivableSummary getReceivableSummary() {
		return receivableSummary;
	}

	public String getReffNumber() {
		return reffNumber;
	}

	public void setAccountAction(String accountAction) {
		this.accountAction = accountAction;
	}

	public void setActionTimestamp(Timestamp actionTimestamp) {
		this.actionTimestamp = actionTimestamp;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public void setReceivableSummary(ReceivableSummary receivableSummary) {
		this.receivableSummary = receivableSummary;
	}

	public void setReffNumber(String reffNumber) {
		this.reffNumber = reffNumber;
	}
}