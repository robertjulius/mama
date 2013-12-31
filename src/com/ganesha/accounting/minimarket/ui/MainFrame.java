package com.ganesha.accounting.minimarket.ui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 5527217675003046133L;

	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenu mnMasterData = new JMenu("Master Data");
		menuBar.add(mnMasterData);

		JMenu mnTransaksi = new JMenu("Transaksi");
		menuBar.add(mnTransaksi);

		JMenu mnBackOffice = new JMenu("Back Office");
		menuBar.add(mnBackOffice);

		JMenu mnLaporan = new JMenu("Laporan");
		menuBar.add(mnLaporan);

		setExtendedState(JFrame.MAXIMIZED_BOTH);
		pack();
		setLocationRelativeTo(null);
	}

}
