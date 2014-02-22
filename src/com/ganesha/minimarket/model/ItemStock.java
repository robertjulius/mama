package com.ganesha.minimarket.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ganesha.model.TableEntity;

@Entity
@Table(name = "ITEM_STOCKS")
public class ItemStock implements TableEntity {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "ITEM_ID", nullable = false)
	private Item item;

	@OneToOne
	@JoinColumn(name = "PURCHASE_DETAIL_ID", nullable = false)
	private PurchaseDetail purchaseDetail;

	@Column(name = "QUANTITY", nullable = false)
	private Integer quantity;

	public Integer getId() {
		return id;
	}

	public Item getItem() {
		return item;
	}

	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}