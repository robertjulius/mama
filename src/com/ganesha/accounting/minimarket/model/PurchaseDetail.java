package com.ganesha.accounting.minimarket.model;

import java.math.BigDecimal;

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
@Table(name = "PURCHASE_DETAILS")
public class PurchaseDetail extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "PURCHASE_HEADER_ID", nullable = false)
	private PurchaseHeader purchaseHeader;

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

	@Column(name = "TOTAL_PRICE", nullable = false)
	private BigDecimal totalPrice;

	public Integer getId() {
		return id;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public PurchaseHeader getPurchaseHeader() {
		return purchaseHeader;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public String getUnit() {
		return unit;
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

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public void setPurchaseHeader(PurchaseHeader purchaseHeader) {
		this.purchaseHeader = purchaseHeader;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
