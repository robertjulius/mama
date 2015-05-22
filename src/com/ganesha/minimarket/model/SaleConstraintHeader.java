package com.ganesha.minimarket.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ganesha.minimarket.constants.Enums.SaleConstraintPostingStatus;
import com.ganesha.model.LogableEntity;
import com.ganesha.model.Trackable;

@Entity
@Table(name = "SALE_CONSTRAINT_HEADERS")
public class SaleConstraintHeader extends Trackable implements LogableEntity {
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

	@Enumerated(EnumType.STRING)
	@Column(name = "POSTING_STATUS", nullable = false)
	private SaleConstraintPostingStatus postingStatus;

	@Column(name = "POSTING_TRIED_COUNT", nullable = false)
	private Integer postingTriedCount;

	@Column(name = "POSTING_MESSAGE", nullable = true, length = 4096)
	private String postingMessage;

	public static SaleConstraintHeader fromSaleHeader(SaleHeader saleHeader) {
		SaleConstraintHeader saleConstraintHeader = new SaleConstraintHeader();
		/*
		 * saleConstraintHeader.setId(id); Not used
		 */
		saleConstraintHeader.setTransactionNumber(saleHeader
				.getTransactionNumber());
		saleConstraintHeader.setTransactionTimestamp(saleHeader
				.getTransactionTimestamp());
		saleConstraintHeader.setCustomer(saleHeader.getCustomer());
		saleConstraintHeader.setSubTotalAmount(saleHeader.getSubTotalAmount());
		saleConstraintHeader.setTaxPercent(saleHeader.getTaxPercent());
		saleConstraintHeader.setTaxAmount(saleHeader.getTaxAmount());
		saleConstraintHeader.setTotalAmount(saleHeader.getTotalAmount());
		saleConstraintHeader.setPay(saleHeader.getPay());
		saleConstraintHeader.setMoneyChange(saleHeader.getMoneyChange());
		saleConstraintHeader.setLastUpdatedBy(saleHeader.getLastUpdatedBy());
		saleConstraintHeader.setLastUpdatedTimestamp(saleHeader
				.getLastUpdatedTimestamp());
		return saleConstraintHeader;
	}

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

	public String getPostingMessage() {
		return postingMessage;
	}

	public SaleConstraintPostingStatus getPostingStatus() {
		return postingStatus;
	}

	public Integer getPostingTriedCount() {
		return postingTriedCount;
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

	public void setPostingMessage(String postingMessage) {
		this.postingMessage = postingMessage;
	}

	public void setPostingStatus(SaleConstraintPostingStatus postingStatus) {
		this.postingStatus = postingStatus;
	}

	public void setPostingTriedCount(Integer postingTriedCount) {
		this.postingTriedCount = postingTriedCount;
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

	public SaleHeader toSaleHeader() {
		SaleHeader saleHeader = new SaleHeader();
		/*
		 * saleHeader.setId(id); Not used
		 */
		saleHeader.setTransactionNumber(transactionNumber);
		saleHeader.setTransactionTimestamp(transactionTimestamp);
		saleHeader.setCustomer(customer);
		saleHeader.setSubTotalAmount(subTotalAmount);
		saleHeader.setTaxPercent(taxPercent);
		saleHeader.setTaxAmount(taxAmount);
		saleHeader.setTotalAmount(totalAmount);
		saleHeader.setPay(pay);
		saleHeader.setMoneyChange(moneyChange);
		saleHeader.setLastUpdatedBy(getLastUpdatedBy());
		saleHeader.setLastUpdatedTimestamp(getLastUpdatedTimestamp());
		return saleHeader;
	}
}
