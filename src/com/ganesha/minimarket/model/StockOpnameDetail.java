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
@Table(name = "STOCK_OPNAME_DETAILS")
public class StockOpnameDetail implements TableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "STOCK_OPNAME_HEADER_ID", nullable = false)
	private StockOpnameHeader stockOpnameHeader;

	@ManyToOne
	@JoinColumn(name = "ITEM_STOCK_ID", nullable = false)
	private ItemStock itemStock;

	@Column(name = "QUANTITY_SYSTEM", nullable = false)
	private Integer quantitySystem;

	@Column(name = "QUANTITY_MANUAL", nullable = false)
	private Integer quantityManual;

	@Column(name = "OVER_COUNT", nullable = false)
	private Integer overCount;

	@Column(name = "OVER_AMOUNT", nullable = false)
	private BigDecimal overAmount;

	@Column(name = "LOSS_COUNT", nullable = false)
	private Integer lossCount;

	@Column(name = "LOSS_AMOUNT", nullable = false)
	private BigDecimal lossAmount;

	@Column(name = "LAST_STOCK_AMOUNT", nullable = false)
	private BigDecimal lastStockAmount;

	public Integer getId() {
		return id;
	}

	public ItemStock getItemStock() {
		return itemStock;
	}

	public BigDecimal getLastStockAmount() {
		return lastStockAmount;
	}

	public BigDecimal getLossAmount() {
		return lossAmount;
	}

	public Integer getLossCount() {
		return lossCount;
	}

	public BigDecimal getOverAmount() {
		return overAmount;
	}

	public Integer getOverCount() {
		return overCount;
	}

	public Integer getQuantityManual() {
		return quantityManual;
	}

	public Integer getQuantitySystem() {
		return quantitySystem;
	}

	public StockOpnameHeader getStockOpnameHeader() {
		return stockOpnameHeader;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setItemStock(ItemStock itemStock) {
		this.itemStock = itemStock;
	}

	public void setLastStockAmount(BigDecimal lastStockAmount) {
		this.lastStockAmount = lastStockAmount;
	}

	public void setLossAmount(BigDecimal lossAmount) {
		this.lossAmount = lossAmount;
	}

	public void setLossCount(Integer lossCount) {
		this.lossCount = lossCount;
	}

	public void setOverAmount(BigDecimal overAmount) {
		this.overAmount = overAmount;
	}

	public void setOverCount(Integer overCount) {
		this.overCount = overCount;
	}

	public void setQuantityManual(Integer quantityManual) {
		this.quantityManual = quantityManual;
	}

	public void setQuantitySystem(Integer quantitySystem) {
		this.quantitySystem = quantitySystem;
	}

	public void setStockOpnameHeader(StockOpnameHeader stockOpnameHeader) {
		this.stockOpnameHeader = stockOpnameHeader;
	}
}