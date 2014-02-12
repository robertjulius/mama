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

import com.ganesha.model.TableEntity;

@Entity
@Table(name = "PURCHASE_RETURN_DETAILS")
public class PurchaseReturnDetail implements TableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "PURCHASE_RETURN_HEADER_ID", nullable = false)
	private PurchaseReturnHeader purchaseReturnHeader;

	@Column(name = "ORDER_NUM", nullable = false)
	private Integer orderNum;

	@ManyToOne
	@JoinColumn(name = "PURCHASE_DETAIL_ID", nullable = false)
	private PurchaseDetail purchaseDetail;

	@Column(name = "QUANTITY", nullable = false)
	private Integer quantity;

	@Column(name = "TOTAL_AMOUNT", nullable = false)
	private BigDecimal totalAmount;

	public Integer getId() {
		return id;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}

	public PurchaseReturnHeader getPurchaseReturnHeader() {
		return purchaseReturnHeader;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}

	public void setPurchaseReturnHeader(
			PurchaseReturnHeader purchaseReturnHeader) {
		this.purchaseReturnHeader = purchaseReturnHeader;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
}