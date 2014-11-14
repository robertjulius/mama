package com.ganesha.minimarket.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ganesha.model.TableEntity;

@Entity
@Table(name = "ITEM_SELL_PRICES")
public class ItemSellPrice implements TableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@EmbeddedId
	private ItemSellPricePK primaryKey;

	@Column(name = "SEQUENCE", nullable = false)
	private Integer sequence;

	public ItemSellPricePK getPrimaryKey() {
		return primaryKey;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setPrimaryKey(ItemSellPricePK primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
}