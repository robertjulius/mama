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

	@Column(name = "NOMINAL", nullable = false)
	private Integer nominal;

	@Column(name = "PRICE", nullable = false)
	private BigDecimal price;

	@Override
	public Integer getId() {
		return id;
	}

	public Integer getNominal() {
		return nominal;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNominal(Integer nominal) {
		this.nominal = nominal;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}
}