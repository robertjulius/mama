package com.ganesha.accounting.minimarket.model;

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
@Table(name = "SALE_RETURN_HEADERS")
public class SaleReturnHeader extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "TRANSACTION_NUMBER", nullable = false, unique = true)
	private String transactionNumber;

	@Column(name = "TRANSACTION_TIMESTAMP", nullable = false)
	private Timestamp transactionTimestamp;

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID", nullable = false)
	private Customer customer;

	@Column(name = "SUB_TOTAL_AMOUNT", nullable = false)
	private BigDecimal subTotalAmount;

	@Column(name = "TAX_PERCENT", nullable = false)
	private BigDecimal taxPercent;

	@Column(name = "TAX_AMOUNT", nullable = false)
	private BigDecimal taxAmount;

	@Column(name = "TOTAL_RETURN_AMOUNT", nullable = false)
	private BigDecimal totalReturnAmount;

	public Customer getCustomer() {
		return customer;
	}

	public Integer getId() {
		return id;
	}

	public BigDecimal getSubTotalAmount() {
		return subTotalAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public BigDecimal getTaxPercent() {
		return taxPercent;
	}

	public BigDecimal getTotalReturnAmount() {
		return totalReturnAmount;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public Timestamp getTransactionTimestamp() {
		return transactionTimestamp;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setSubTotalAmount(BigDecimal subTotalAmount) {
		this.subTotalAmount = subTotalAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public void setTaxPercent(BigDecimal taxPercent) {
		this.taxPercent = taxPercent;
	}

	public void setTotalReturnAmount(BigDecimal totalReturnAmount) {
		this.totalReturnAmount = totalReturnAmount;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public void setTransactionTimestamp(Timestamp transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}
}
