package com.ganesha.accounting.minimarket.ui.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.ui.forms.forms.purchase.PembelianForm;
import com.ganesha.accounting.minimarket.ui.forms.forms.stock.StockListDialog;
import com.ganesha.accounting.minimarket.ui.forms.forms.supplier.SupplierListDialog;
import com.ganesha.desktop.component.XJFrame;

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

		JMenu mnBackOffice = new JMenu("Back Office");
		menuBar.add(mnBackOffice);

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
