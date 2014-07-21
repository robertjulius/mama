package com.ganesha.minimarket.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ganesha.model.TableEntity;

public class ItemSellPricePK implements TableEntity {
	private static final long serialVersionUID = 274018399212050879L;

	@ManyToOne
	@JoinColumn(name = "ITEM_ID", nullable = false)
	private Item item;

	@Column(name = "SELL_PRICE", nullable = false)
	private BigDecimal sellPrice;

	public Item getItem() {
		return item;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
}
