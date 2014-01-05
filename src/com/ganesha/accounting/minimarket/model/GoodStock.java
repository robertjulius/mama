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
@Table(name = "GOOD_STOCKS")
public class GoodStock extends Trackable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "GOOD_ID", nullable = false)
	private Good good;

	@Column(name = "BUY_PRICE", nullable = false)
	private BigDecimal buyPrice;

	@Column(name = "HPP", nullable = false)
	private BigDecimal hpp;

	@Column(name = "SELL_PRICE", nullable = false)
	private BigDecimal sellPrice;

	@Column(name = "STOCK", nullable = false)
	private Integer stock;

	@Column(name = "MINIMUM_STOCK", nullable = false)
	private Integer minimumStock;

	@Column(name = "UNIT", nullable = false)
	private String unit;

	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public Good getGood() {
		return good;
	}

	public BigDecimal getHpp() {
		return hpp;
	}

	public Integer getId() {
		return id;
	}

	public Integer getMinimumStock() {
		return minimumStock;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public Integer getStock() {
		return stock;
	}

	public String getUnit() {
		return unit;
	}

	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}

	public void setGood(Good good) {
		this.good = good;
	}

	public void setHpp(BigDecimal hpp) {
		this.hpp = hpp;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMinimumStock(Integer minimumStock) {
		this.minimumStock = minimumStock;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}