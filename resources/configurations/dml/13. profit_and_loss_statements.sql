INSERT INTO profit_and_loss_statements (period_year, period_month, penjualan, potongan_penjualan, retur_penjualan, penjualan_bersih, persediaan_awal, pembelian, potongan_pembelian, retur_pembelian, persediaan_total, persediaan_akhir, hpp, laba_kotor, beban_operasi, laba_bersih, last_updated_by, last_updated_timestamp) VALUES ((SELECT YEAR(CURRENT_TIMESTAMP())), (SELECT MONTH(CURRENT_TIMESTAMP())), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, CURRENT_TIMESTAMP());