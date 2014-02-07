package com.ganesha.minimarket.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.ganesha.desktop.component.XJFrame;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.ui.forms.customer.CustomerListDialog;
import com.ganesha.minimarket.ui.forms.discount.DiscountListDialog;
import com.ganesha.minimarket.ui.forms.payable.PayableListDialog;
import com.ganesha.minimarket.ui.forms.purchase.PembelianForm;
import com.ganesha.minimarket.ui.forms.receivable.ReceivableListDialog;
import com.ganesha.minimarket.ui.forms.reports.ItemStockReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.StockOpnameReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.TransactionReportListDialog;
import com.ganesha.minimarket.ui.forms.returns.ReturPembelianForm;
import com.ganesha.minimarket.ui.forms.returns.ReturPenjualanForm;
import com.ganesha.minimarket.ui.forms.sale.PenjualanForm;
import com.ganesha.minimarket.ui.forms.stock.StockListDialog;
import com.ganesha.minimarket.ui.forms.stockopname.StockOpnameListDialog;
import com.ganesha.minimarket.ui.forms.supplier.SupplierListDialog;

public class MainFrame extends XJFrame {
	private static final long serialVersionUID = 5527217675003046133L;

	public MainFrame() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(Main.getCompany().getName());

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenu mnMasterData = new JMenu("Master Data");
		menuBar.add(mnMasterData);

		JMenuItem mntmPersediaan = new JMenuItem("Persediaan Barang");
		mntmPersediaan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new StockListDialog(MainFrame.this).setVisible(true);
			}
		});
		mnMasterData.add(mntmPersediaan);

		JMenuItem mntmSupplier = new JMenuItem("Supplier");
		mntmSupplier.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SupplierListDialog(MainFrame.this).setVisible(true);
			}
		});
		mnMasterData.add(mntmSupplier);

		JMenuItem mntmCustomer = new JMenuItem("Customer");
		mntmCustomer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CustomerListDialog(MainFrame.this).setVisible(true);
			}
		});
		mnMasterData.add(mntmCustomer);

		JMenuItem mntmDiskon = new JMenuItem("Diskon");
		mntmDiskon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new DiscountListDialog(MainFrame.this).setVisible(true);
			}
		});
		mnMasterData.add(mntmDiskon);

		JMenu mnTransaksi = new JMenu("Transaksi");
		menuBar.add(mnTransaksi);

		JMenuItem mntmPembelian = new JMenuItem("Pembelian");
		mntmPembelian.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PembelianForm(MainFrame.this).setVisible(true);
			}
		});
		mnTransaksi.add(mntmPembelian);

		JMenuItem mntmReturPembelian = new JMenuItem("Retur Pembelian");
		mntmReturPembelian.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ReturPembelianForm(MainFrame.this).setVisible(true);
			}
		});
		mnTransaksi.add(mntmReturPembelian);

		JSeparator separator = new JSeparator();
		mnTransaksi.add(separator);

		JMenuItem mntmPenjualan = new JMenuItem("Penjualan");
		mntmPenjualan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PenjualanForm(MainFrame.this).setVisible(true);
			}
		});
		mnTransaksi.add(mntmPenjualan);

		JMenuItem mntmReturPenjualan = new JMenuItem("Retur Penjualan");
		mntmReturPenjualan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ReturPenjualanForm(MainFrame.this).setVisible(true);
			}
		});
		mnTransaksi.add(mntmReturPenjualan);

		JSeparator separator_1 = new JSeparator();
		mnTransaksi.add(separator_1);

		JMenuItem mntmHutang = new JMenuItem("Hutang");
		mntmHutang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PayableListDialog(MainFrame.this).setVisible(true);
			}
		});
		mnTransaksi.add(mntmHutang);

		JMenuItem mntmPiutang = new JMenuItem("Piutang");
		mntmPiutang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ReceivableListDialog(MainFrame.this).setVisible(true);
			}
		});
		mnTransaksi.add(mntmPiutang);

		JMenu mnBackOffice = new JMenu("Back Office");
		menuBar.add(mnBackOffice);

		JMenu mnReport = new JMenu("Laporan");
		mnBackOffice.add(mnReport);

		JMenuItem mntmReportTransaksi = new JMenuItem("Laporan Transaksi");
		mntmReportTransaksi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new TransactionReportListDialog(MainFrame.this)
						.setVisible(true);
			}
		});
		mnReport.add(mntmReportTransaksi);

		JMenuItem mntmLaporanStokBarang = new JMenuItem("Laporan Stok Barang");
		mntmLaporanStokBarang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ItemStockReportListDialog(MainFrame.this).setVisible(true);
			}
		});
		mnReport.add(mntmLaporanStokBarang);

		JMenuItem mntmLaporanStockOpname = new JMenuItem("Laporan Stock Opname");
		mntmLaporanStockOpname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new StockOpnameReportListDialog(MainFrame.this)
						.setVisible(true);
			}
		});
		mnReport.add(mntmLaporanStockOpname);

		JMenuItem mntmStockOpname = new JMenuItem("Stock Opname");
		mntmStockOpname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new StockOpnameListDialog(MainFrame.this).setVisible(true);
			}
		});
		mnBackOffice.add(mntmStockOpname);

		JMenu mnLaporan = new JMenu("Laporan");
		menuBar.add(mnLaporan);

		// setExtendedState(JFrame.MAXIMIZED_BOTH);
		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		// TODO Auto-generated method stub
		default:
			break;
		}
	}
}
