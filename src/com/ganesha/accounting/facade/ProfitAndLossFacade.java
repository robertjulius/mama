package com.ganesha.accounting.facade;

import java.math.BigDecimal;
import java.util.List;

import com.ganesha.accounting.model.ProfitAndLoss;

public class ProfitAndLossFacade {

	public static void bebanOperasi(ProfitAndLoss profitAndLoss) {
		List<BigDecimal> bebanOperasi = null;
		/*
		 * TODO Query beban operasi
		 */
		profitAndLoss.setBebanOperasi(bebanOperasi);
	}

	public static void labaKotor(ProfitAndLoss profitAndLoss) {
		BigDecimal penjualan = profitAndLoss.getPenjualan();
		BigDecimal hpp = profitAndLoss.getHpp();

		BigDecimal labaKotor = penjualan.subtract(hpp);
		profitAndLoss.setLabaKotor(labaKotor);
	}

	public static void profit(ProfitAndLoss profitAndLoss) {
		BigDecimal labaKotor = profitAndLoss.getLabaKotor();
		BigDecimal bebanOperasi = null;

		BigDecimal labaBersih = labaKotor.subtract(bebanOperasi);
		profitAndLoss.setLabaBersih(labaBersih);
	}
}
