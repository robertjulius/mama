package com.ganesha.accounting.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ganesha.model.Trackable;

@Entity
@Table(name = "PROFIT_AND_LOSS_CUTOFF")
public class ProfitAndLossCutoff extends Trackable {

	private static final long serialVersionUID = 1007802195951324758L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "PREVIOUS_CUTOFF_ID", nullable = true)
	private ProfitAndLossCutoff previousProfitAndLossCutoff;

	@Column(name = "CUTOFF_TIMESTAMP", nullable = false)
	private Timestamp cutoffTimestamp;

	@Column(name = "PENJUALAN", nullable = false)
	private BigDecimal penjualan;

	@Column(name = "POTONGAN_PENJUALAN", nullable = false)
	private BigDecimal potonganPenjualan;

	@Column(name = "RETUR_PENJUALAN", nullable = false)
	private BigDecimal returPenjualan;

	@Column(name = "PENJUALAN_BERSIH", nullable = false)
	private BigDecimal penjualanBersih;

	@Column(name = "PERSEDIAAN_AWAL", nullable = false)
	private BigDecimal persediaanAwal;

	@Column(name = "PEMBELIAN", nullable = false)
	private BigDecimal pembelian;

	@Column(name = "POTONGAN_PEMBELIAN", nullable = false)
	private BigDecimal potonganPembelian;

	@Column(name = "RETUR_PEMBELIAN", nullable = false)
	private BigDecimal returPembelian;

	@Column(name = "PERSEDIAAN_TOTAL", nullable = false)
	private BigDecimal persediaanTotal;

	@Column(name = "PERSEDIAAN_AKHIR", nullable = false)
	private BigDecimal persediaanAkhir;

	@Column(name = "HPP", nullable = false)
	private BigDecimal hpp;

	@Column(name = "LABA_KOTOR", nullable = false)
	private BigDecimal labaKotor;

	@Column(name = "BEBAN_OPERASI", nullable = false)
	private BigDecimal bebanOperasi;

	@Column(name = "LABA_BERSIH", nullable = false)
	private BigDecimal labaBersih;

	public BigDecimal getBebanOperasi() {
		return bebanOperasi;
	}

	public Timestamp getCutoffTimestamp() {
		return cutoffTimestamp;
	}

	public BigDecimal getHpp() {
		return hpp;
	}

	public Integer getId() {
		return id;
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

	public BigDecimal getPenjualanBersih() {
		return penjualanBersih;
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

	public BigDecimal getPotonganPenjualan() {
		return potonganPenjualan;
	}

	public ProfitAndLossCutoff getPreviousProfitAndLossCutoff() {
		return previousProfitAndLossCutoff;
	}

	public BigDecimal getReturPembelian() {
		return returPembelian;
	}

	public BigDecimal getReturPenjualan() {
		return returPenjualan;
	}

	public void setBebanOperasi(BigDecimal bebanOperasi) {
		this.bebanOperasi = bebanOperasi;
	}

	public void setCutoffTimestamp(Timestamp cutoffTimestamp) {
		this.cutoffTimestamp = cutoffTimestamp;
	}

	public void setHpp(BigDecimal hpp) {
		this.hpp = hpp;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public void setPenjualanBersih(BigDecimal penjualanBersih) {
		this.penjualanBersih = penjualanBersih;
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

	public void setPotonganPenjualan(BigDecimal potonganPenjualan) {
		this.potonganPenjualan = potonganPenjualan;
	}

	public void setPreviousProfitAndLossCutoff(
			ProfitAndLossCutoff previousProfitAndLossCutoff) {
		this.previousProfitAndLossCutoff = previousProfitAndLossCutoff;
	}

	public void setReturPembelian(BigDecimal returPembelian) {
		this.returPembelian = returPembelian;
	}

	public void setReturPenjualan(BigDecimal returPenjualan) {
		this.returPenjualan = returPenjualan;
	}
}
