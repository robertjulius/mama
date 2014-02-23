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
@Table(name = "PURCHASE_HEADERS")
public class PurchaseHeader extends Trackable implements LogableEntity {
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
	@JoinColumn(name = "SUPPLIER_ID", nullable = false)
	private Supplier supplier;

	@Column(name = "SUB_TOTAL_AMOUNT", nullable = false)
	private BigDecimal subTotalAmount;

	@Column(name = "EXPENSES", nullable = false)
	private BigDecimal expenses;

	@Column(name = "DISCOUNT", nullable = false)
	private BigDecimal discount;

	@Column(name = "TOTAL_AMOUNT", nullable = false)
	private BigDecimal totalAmount;

	@Column(name = "ADVANCE_PAYMENT", nullable = false)
	private BigDecimal advancePayment;

	@Column(name = "REMAINING_PAYMENT", nullable = false)
	private BigDecimal remainingPayment;

	@Column(name = "PAID_IN_FULL_FLAG", nullable = false)
	private Boolean paidInFullFlag;

	public BigDecimal getAdvancePayment() {
		return advancePayment;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public BigDecimal getExpenses() {
		return expenses;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public Boolean getPaidInFullFlag() {
		return paidInFullFlag;
	}

	public BigDecimal getRemainingPayment() {
		return remainingPayment;
	}

	public BigDecimal getSubTotalAmount() {
		return subTotalAmount;
	}

	public Supplier getSupplier() {
		return supplier;
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

	public void setAdvancePayment(BigDecimal advancePayment) {
		this.advancePayment = advancePayment;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public void setExpenses(BigDecimal expenses) {
		this.expenses = expenses;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPaidInFullFlag(Boolean paidInFullFlag) {
		this.paidInFullFlag = paidInFullFlag;
	}

	public void setRemainingPayment(BigDecimal remainingPayment) {
		this.remainingPayment = remainingPayment;
	}

	public void setSubTotalAmount(BigDecimal subTotalAmount) {
		this.subTotalAmount = subTotalAmount;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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
