package com.ganesha.prepaid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ganesha.minimarket.model.Item;
import com.ganesha.model.Identifiable;
import com.ganesha.model.Inactivable;
import com.ganesha.model.LogableEntity;

@Entity
@Table(name = "VOUCHER_TYPES")
public class VoucherType extends Inactivable implements LogableEntity,
		Identifiable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@ManyToOne
	@JoinColumn(name = "PROVIDER_ID", nullable = false)
	private Provider provider;

	@OneToOne
	@JoinColumn(name = "ITEM_ID", nullable = false)
	private Item item;

	@Override
	public Integer getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public String getName() {
		return name;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}
}