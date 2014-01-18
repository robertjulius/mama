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
@Table(name = "SALE_RETURN_DETAILS")
public class SaleReturnDetail extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "SALE_RETURN_HEADER_ID", nullable = false)
	private SaleReturnHeader saleReturnHeader;

	@Column(name = "ORDER_NUM", nullable = false)
	private Integer orderNum;

	@Column(name = "SALE_TRANSACTION_NUMBER", nullable = false)
	private String saleTransactionNumber;

	@Column(name = "SALE_TRANSACTION_TIMESTAMP", nullable = false)
	private Timestamp saleTransactionTimestamp;

	@Column(name = "ITEM_CODE", nullable = false)
	private String itemCode;

	@Column(name = "ITEM_NAME", nullable = false)
	private String itemName;

	@Column(name = "QUANTITY", nullable = false)
	private Integer quantity;

	@Column(name = "UNIT", nullable = false)
	private String unit;

	@Column(name = "PRICE_PER_UNIT", nullable = false)
	private BigDecimal pricePerUnit;

	@Column(name = "DISCOUNT_PERCENT", nullable = false)
	private BigDecimal discountPercent;

	@Column(name = "TOTAL_AMOUNT", nullable = false)
	private BigDecimal totalAmount;

	public BigDecimal getDiscountPercent() {
		return discountPercent;
	}

	public Integer getId() {
		return id;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public SaleReturnHeader getSaleReturnHeader() {
		return saleReturnHeader;
	}

	public String getSaleTransactionNumber() {
		return saleTransactionNumber;
	}

	public Timestamp getSaleTransactionTimestamp() {
		return saleTransactionTimestamp;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public String getUnit() {
		return unit;
	}

	public void setDiscountPercent(BigDecimal discountPercent) {
		this.discountPercent = discountPercent;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setSaleReturnHeader(SaleReturnHeader saleReturnHeader) {
		this.saleReturnHeader = saleReturnHeader;
	}

	public void setSaleTransactionNumber(String saleTransactionNumber) {
		this.saleTransactionNumber = saleTransactionNumber;
	}

	public void setSaleTransactionTimestamp(Timestamp saleTransactionTimestamp) {
		this.saleTransactionTimestamp = saleTransactionTimestamp;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}