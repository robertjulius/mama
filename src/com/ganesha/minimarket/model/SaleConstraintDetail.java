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
@Table(name = "SALE_CONSTRAINT_DETAILS")
public class SaleConstraintDetail implements TableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "SALE_CONSTRAINT_HEADER_ID", nullable = false)
	private SaleConstraintHeader saleConstraintHeader;

	@Column(name = "ORDER_NUM", nullable = false)
	private Integer orderNum;

	@Column(name = "ITEM_ID", nullable = false)
	private Integer itemId;

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

	public static SaleConstraintDetail fromSaleDetail(SaleDetail saleDetail) {
		SaleConstraintHeader saleConstraintHeader = SaleConstraintHeader
				.fromSaleHeader(saleDetail.getSaleHeader());
		SaleConstraintDetail saleConstraintDetail = new SaleConstraintDetail();
		/*
		 * saleDetail.setId(id); Not used
		 */
		saleConstraintDetail.setSaleConstraintHeader(saleConstraintHeader);
		saleConstraintDetail.setOrderNum(saleDetail.getOrderNum());
		saleConstraintDetail.setItemId(saleDetail.getItemId());
		saleConstraintDetail.setItemCode(saleDetail.getItemCode());
		saleConstraintDetail.setItemName(saleDetail.getItemName());
		saleConstraintDetail.setQuantity(saleDetail.getQuantity());
		saleConstraintDetail.setUnit(saleDetail.getUnit());
		saleConstraintDetail.setPricePerUnit(saleDetail.getPricePerUnit());
		saleConstraintDetail
				.setDiscountPercent(saleDetail.getDiscountPercent());
		saleConstraintDetail.setTotalAmount(saleDetail.getTotalAmount());
		return saleConstraintDetail;
	}

	public BigDecimal getDiscountPercent() {
		return discountPercent;
	}

	public Integer getId() {
		return id;
	}

	public String getItemCode() {
		return itemCode;
	}

	public Integer getItemId() {
		return itemId;
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

	public SaleConstraintHeader getSaleConstraintHeader() {
		return saleConstraintHeader;
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

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
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

	public void setSaleConstraintHeader(
			SaleConstraintHeader saleConstraintHeader) {
		this.saleConstraintHeader = saleConstraintHeader;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public SaleConstraintLog toSaleConstraintLog() {
		SaleConstraintLog saleConstraintLog = new SaleConstraintLog();
		saleConstraintLog.setSaleConstraintHeaderId(saleConstraintHeader
				.getId());
		saleConstraintLog.setTransactionNumber(saleConstraintHeader
				.getTransactionNumber());
		saleConstraintLog.setTransactionTimestamp(saleConstraintHeader
				.getTransactionTimestamp());
		saleConstraintLog.setCustomerId(saleConstraintHeader.getCustomer()
				.getId());
		saleConstraintLog.setOrderNum(orderNum);
		saleConstraintLog.setItemId(itemId);
		saleConstraintLog.setItemCode(itemCode);
		saleConstraintLog.setItemName(itemName);
		saleConstraintLog.setQuantity(quantity);
		saleConstraintLog.setUnit(unit);
		saleConstraintLog.setPricePerUnit(pricePerUnit);
		saleConstraintLog.setDiscountPercent(discountPercent);
		saleConstraintLog.setTotalAmount(totalAmount);
		return saleConstraintLog;
	}

	public SaleDetail toSaleDetail() {
		SaleHeader saleHeader = saleConstraintHeader.toSaleHeader();
		SaleDetail saleDetail = new SaleDetail();
		/*
		 * saleDetail.setId(id); Not used
		 */
		saleDetail.setSaleHeader(saleHeader);
		saleDetail.setOrderNum(orderNum);
		saleDetail.setItemId(itemId);
		saleDetail.setItemCode(itemCode);
		saleDetail.setItemName(itemName);
		saleDetail.setQuantity(quantity);
		saleDetail.setUnit(unit);
		saleDetail.setPricePerUnit(pricePerUnit);
		saleDetail.setDiscountPercent(discountPercent);
		saleDetail.setTotalAmount(totalAmount);
		return saleDetail;
	}
}
