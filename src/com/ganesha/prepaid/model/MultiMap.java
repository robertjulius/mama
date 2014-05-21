package com.ganesha.prepaid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ganesha.minimarket.model.Item;
import com.ganesha.model.Identifiable;
import com.ganesha.model.LogableEntity;
import com.ganesha.model.Trackable;

@Entity
@Table(name = "MULTI_MAPS")
public class MultiMap extends Trackable implements LogableEntity, Identifiable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "ITEM_ID", nullable = true)
	private Item item;

	@Override
	public Integer getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}