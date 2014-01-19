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
@Table(name = "PURCHASE_RETURN_HEADERS")
public class PurchaseReturnHeader extends Trackable {
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

	@Column(name = "DISCOUNT_RETURNED", nullable = false)
	private BigDecimal discountReturned;

	@Column(name = "TOTAL_RETURN_AMOUNT", nullable = false)
	private BigDecimal totalReturnAmount;

	@Column(name = "AMOUNT_RETURNED", nullable = false)
	private BigDecimal amountReturned;

	@Column(name = "DEBT_CUT", nullable = false)
	private BigDecimal debtCut;

	@Column(name = "REMAINING_RETURN_AMOUNT", nullable = false)
	private BigDecimal remainingReturnAmount;

	@Column(name = "RETURNED_IN_FULL_FLAG", nullable = false)
	private Boolean returnedInFullFlag;

	public BigDecimal getAmountReturned() {
		return amountReturned;
	}

	public BigDecimal getDebtCut() {
		return debtCut;
	}

	public BigDecimal getDiscountReturned() {
		return discountReturned;
	}

	public BigDecimal getExpenses() {
		return expenses;
	}

	public Integer getId() {
		return id;
	}

	public BigDecimal getRemainingReturnAmount() {
		return remainingReturnAmount;
	}

	public Boolean getReturnedInFullFlag() {
		return returnedInFullFlag;
	}

	public BigDecimal getSubTotalAmount() {
		return subTotalAmount;
	}

	public Supplier getSupplier() {
		return supplier;
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

	public void setAmountReturned(BigDecimal amountReturned) {
		this.amountReturned = amountReturned;
	}

	public void setDebtCut(BigDecimal debtCut) {
		this.debtCut = debtCut;
	}

	public void setDiscountReturned(BigDecimal discountReturned) {
		this.discountReturned = discountReturned;
	}

	public void setExpenses(BigDecimal expenses) {
		this.expenses = expenses;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setRemainingReturnAmount(BigDecimal remainingReturnAmount) {
		this.remainingReturnAmount = remainingReturnAmount;
	}

	public void setReturnedInFullFlag(Boolean returnedInFullFlag) {
		this.returnedInFullFlag = returnedInFullFlag;
	}

	public void setSubTotalAmount(BigDecimal subTotalAmount) {
		this.subTotalAmount = subTotalAmount;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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
