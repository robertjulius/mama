package com.ganesha.minimarket.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.XJFrame;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJMenu;
import com.ganesha.desktop.component.XJMenuItem;
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
import com.ganesha.minimarket.ui.forms.role.RoleListDialog;
import com.ganesha.minimarket.ui.forms.sale.PenjualanForm;
import com.ganesha.minimarket.ui.forms.stock.StockListDialog;
import com.ganesha.minimarket.ui.forms.stockopname.StockOpnameListDialog;
import com.ganesha.minimarket.ui.forms.supplier.SupplierListDialog;
import com.ganesha.minimarket.ui.forms.systemsetting.SystemSettingForm;
import com.ganesha.minimarket.ui.forms.systemsetting.TestReceiptPrinter;
import com.ganesha.minimarket.ui.forms.user.ChangePasswordForm;
import com.ganesha.minimarket.ui.forms.user.UserListDialog;

public class MainFrame extends XJFrame {
	private static final long serialVersionUID = 5527217675003046133L;

	public MainFrame() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(Main.getCompany().getName() + " " + GeneralConstants.VERSION);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		XJMenu mnFile = new XJMenu("File");
		menuBar.add(mnFile);

		XJMenu mnAdministrasi = new XJMenu("Administrasi");
		menuBar.add(mnAdministrasi);

		XJMenuItem mntmRole = new XJMenuItem("Role", "/administrasi/role");
		mntmRole.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new RoleListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnAdministrasi.add(mntmRole);

		XJMenuItem mntmUser = new XJMenuItem("User", "/administrasi/user");
		mnAdministrasi.add(mntmUser);
		mntmUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new UserListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});

		XJMenuItem mntmGantiPassword = new XJMenuItem("Ganti Password",
				"/administrasi/changepassword");
		mntmGantiPassword.setPermissionRequired(false);
		mnAdministrasi.add(mntmGantiPassword);
		mntmGantiPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new ChangePasswordForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});

		XJMenu mnMasterData = new XJMenu("Master Data");
		menuBar.add(mnMasterData);

		XJMenuItem mntmPersediaan = new XJMenuItem("Persediaan Barang",
				"/master/stock");
		mntmPersediaan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new StockListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnMasterData.add(mntmPersediaan);

		XJMenuItem mntmSupplier = new XJMenuItem("Supplier", "/master/supplier");
		mntmSupplier.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new SupplierListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnMasterData.add(mntmSupplier);

		XJMenuItem mntmCustomer = new XJMenuItem("Customer", "/master/customer");
		mntmCustomer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new CustomerListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnMasterData.add(mntmCustomer);

		XJMenuItem mntmDiskon = new XJMenuItem("Diskon", "/master/discount");
		mntmDiskon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new DiscountListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnMasterData.add(mntmDiskon);

		XJMenu mnTransaksi = new XJMenu("Transaksi");
		menuBar.add(mnTransaksi);

		XJMenuItem mntmPembelian = new XJMenuItem("Pembelian",
				"/transaction/purchase");
		mntmPembelian.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new PembelianForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnTransaksi.add(mntmPembelian);

		XJMenuItem mntmReturPembelian = new XJMenuItem("Retur Pembelian",
				"/transaction/purchasereturn");
		mntmReturPembelian.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new ReturPembelianForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnTransaksi.add(mntmReturPembelian);

		JSeparator separator = new JSeparator();
		mnTransaksi.add(separator);

		XJMenuItem mntmPenjualan = new XJMenuItem("Penjualan",
				"/transaction/sale");
		mntmPenjualan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new PenjualanForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnTransaksi.add(mntmPenjualan);

		XJMenuItem mntmReturPenjualan = new XJMenuItem("Retur Penjualan",
				"/transaction/salereturn");
		mntmReturPenjualan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new ReturPenjualanForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnTransaksi.add(mntmReturPenjualan);

		JSeparator separator_1 = new JSeparator();
		mnTransaksi.add(separator_1);

		XJMenuItem mntmHutang = new XJMenuItem("Hutang", "/transaction/payable");
		mntmHutang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new PayableListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnTransaksi.add(mntmHutang);

		XJMenuItem mntmPiutang = new XJMenuItem("Piutang",
				"/transaction/receivable");
		mntmPiutang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new ReceivableListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnTransaksi.add(mntmPiutang);

		XJMenu mnBackOffice = new XJMenu("Back Office");
		menuBar.add(mnBackOffice);

		XJMenu mnReport = new XJMenu("Laporan");
		mnBackOffice.add(mnReport);

		XJMenuItem mntmReportTransaksi = new XJMenuItem("Laporan Transaksi",
				"/backoffice/report/transaction");
		mntmReportTransaksi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new TransactionReportListDialog(MainFrame.this)
							.setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnReport.add(mntmReportTransaksi);

		XJMenuItem mntmLaporanStokBarang = new XJMenuItem(
				"Laporan Stok Barang", "/backoffice/report/stock");
		mntmLaporanStokBarang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new ItemStockReportListDialog(MainFrame.this)
							.setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnReport.add(mntmLaporanStokBarang);

		XJMenuItem mntmLaporanStockOpname = new XJMenuItem(
				"Laporan Stock Opname", "/backoffice/report/stockopname");
		mntmLaporanStockOpname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new StockOpnameReportListDialog(MainFrame.this)
							.setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnReport.add(mntmLaporanStockOpname);

		XJMenuItem mntmStockOpname = new XJMenuItem("Stock Opname",
				"/backoffice/stockopname");
		mntmStockOpname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new StockOpnameListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnBackOffice.add(mntmStockOpname);

		XJMenu mnSetting = new XJMenu("Setting");
		menuBar.add(mnSetting);

		XJMenuItem mntmSettingAplikasi = new XJMenuItem("Setting Aplikasi",
				"/setting/systemsetting");
		mntmSettingAplikasi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new SystemSettingForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnSetting.add(mntmSettingAplikasi);

		XJMenuItem mntmReceiptPrinterTest = new XJMenuItem(
				"Receipt Printer Test", "/setting/receipttest");
		mntmReceiptPrinterTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TestReceiptPrinter.showDialog(MainFrame.this);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnSetting.add(mntmReceiptPrinterTest);

		setExtendedState(JFrame.MAXIMIZED_BOTH);
		pack();
		setLocationRelativeTo(null);
		getContentPane().setLayout(new MigLayout("", "[][]", "[][][]"));

		XJLabel lblCompanyName = new XJLabel();
		lblCompanyName.setText(Main.getCompany().getName());
		getContentPane().add(lblCompanyName, "cell 0 0 2 1");

		XJLabel lblUserId = new XJLabel();
		lblUserId.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblUserId.setText(Main.getUserLogin().getLogin());
		getContentPane().add(lblUserId, "cell 0 1,aligny bottom");

		XJLabel lblUserName = new XJLabel();
		lblUserName.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblUserName.setText(Main.getUserLogin().getName());
		getContentPane().add(lblUserName, "cell 1 1");
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
