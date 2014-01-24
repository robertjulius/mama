package com.ganesha.minimarket.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ganesha.model.Trackable;

@Entity
@Table(name = "RECEIVABLE_SUMMARIES")
public class ReceivableSummary extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "CLIENT_ID", nullable = false, unique = true)
	private Integer clientId;

	@Column(name = "REMAINING_AMOUNT", nullable = false)
	private BigDecimal remainingAmount;

	@Column(name = "LAST_RECEIVABLE_TRANSACTION_ID", nullable = false)
	private Integer lastReceivableTransactionId;

	public Integer getClientId() {
		return clientId;
	}

	public Integer getId() {
		return id;
	}

	public Integer getLastReceivableTransactionId() {
		return lastReceivableTransactionId;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLastReceivableTransactionId(
			Integer lastReceivableTransactionId) {
		this.lastReceivableTransactionId = lastReceivableTransactionId;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}
}