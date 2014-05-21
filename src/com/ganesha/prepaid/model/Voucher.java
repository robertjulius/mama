package com.ganesha.prepaid.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ganesha.model.Identifiable;
import com.ganesha.model.Inactivable;
import com.ganesha.model.LogableEntity;

@Entity
@Table(name = "VOUCHERS")
public class Voucher extends Inactivable implements LogableEntity, Identifiable {
	private static final long serialVersionUID = -7780389008755790841L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "VOURCHER_TYPE_ID", nullable = false)
	private VoucherType voucherType;

	@Column(name = "PACKAGE_NAME", nullable = false)
	private String packageName;

	@Column(name = "QUANTITY", nullable = false)
	private Integer quantity;

	@Column(name = "PRICE", nullable = false)
	private BigDecimal price;

	@Override
	public Integer getId() {
		return id;
	}

	public String getPackageName() {
		return packageName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}
}