INSERT INTO profit_and_loss_statements (period_year, period_month, penjualan, potongan_penjualan, retur_penjualan, penjualan_bersih, persediaan_awal, pembelian, potongan_pembelian, retur_pembelian, persediaan_total, persediaan_akhir, hpp, laba_kotor, beban_operasi, laba_bersih, last_updated_by, last_updated_timestamp) VALUES ((SELECT YEAR(DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -1 MONTH))), (SELECT MONTH(DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL -1 MONTH))), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, CURRENT_TIMESTAMP());