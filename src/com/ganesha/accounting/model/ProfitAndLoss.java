package com.ganesha.accounting.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProfitAndLoss {

	private BigDecimal penjualan;
	private BigDecimal hpp;
	private BigDecimal labaKotor;
	private BigDecimal persediaanAwal;
	private BigDecimal pembelian;
	private BigDecimal potonganPembelian;
	private BigDecimal persediaanTotal;
	private BigDecimal persediaanAkhir;
	private List<BigDecimal> bebanOperasi;
	private BigDecimal labaBersih;
	private Date startDate;
	private Date endDate;

	public ProfitAndLoss(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public List<BigDecimal> getBebanOperasi() {
		return bebanOperasi;
	}

	public Date getEndDate() {
		return endDate;
	}

	public BigDecimal getHpp() {
		return hpp;
	}

	public BigDecimal getLabaBersih() {
		return labaBersih;
	}

	public BigDecimal getLabaKotor() {
		return labaKotor;
	}

	public BigDecimal getPembelian() {
		return pembelian;
	}

	public BigDecimal getPenjualan() {
		return penjualan;
	}

	public BigDecimal getPersediaanAkhir() {
		return persediaanAkhir;
	}

	public BigDecimal getPersediaanAwal() {
		return persediaanAwal;
	}

	public BigDecimal getPersediaanTotal() {
		return persediaanTotal;
	}

	public BigDecimal getPotonganPembelian() {
		return potonganPembelian;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setBebanOperasi(List<BigDecimal> bebanOperasi) {
		this.bebanOperasi = bebanOperasi;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setHpp(BigDecimal hpp) {
		this.hpp = hpp;
	}

	public void setLabaBersih(BigDecimal labaBersih) {
		this.labaBersih = labaBersih;
	}

	public void setLabaKotor(BigDecimal labaKotor) {
		this.labaKotor = labaKotor;
	}

	public void setPembelian(BigDecimal pembelian) {
		this.pembelian = pembelian;
	}

	public void setPenjualan(BigDecimal penjualan) {
		this.penjualan = penjualan;
	}

	public void setPersediaanAkhir(BigDecimal persediaanAkhir) {
		this.persediaanAkhir = persediaanAkhir;
	}

	public void setPersediaanAwal(BigDecimal persediaanAwal) {
		this.persediaanAwal = persediaanAwal;
	}

	public void setPersediaanTotal(BigDecimal persediaanTotal) {
		this.persediaanTotal = persediaanTotal;
	}

	public void setPotonganPembelian(BigDecimal potonganPembelian) {
		this.potonganPembelian = potonganPembelian;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
