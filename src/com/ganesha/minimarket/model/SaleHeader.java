package com.ganesha.minimarket.model;

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

import com.ganesha.model.LogableEntity;
import com.ganesha.model.Trackable;

@Entity
@Table(name = "SALE_HEADERS")
public class SaleHeader extends Trackable implements LogableEntity {
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

	@Column(name = "TOTAL_AMOUNT", nullable = false)
	private BigDecimal totalAmount;

	@Column(name = "PAY", nullable = false)
	private BigDecimal pay;

	@Column(name = "MONEY_CHANGE", nullable = false)
	private BigDecimal moneyChange;

	public Customer getCustomer() {
		return customer;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public BigDecimal getMoneyChange() {
		return moneyChange;
	}

	public BigDecimal getPay() {
		return pay;
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
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

	public void setMoneyChange(BigDecimal moneyChange) {
		this.moneyChange = moneyChange;
	}

	public void setPay(BigDecimal pay) {
		this.pay = pay;
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

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public void setTransactionTimestamp(Timestamp transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}
}
