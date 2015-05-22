package com.ganesha.minimarket.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.ui.forms.circle.CircleListDialog;
import com.ganesha.accounting.ui.forms.expense.ExpenseListDialog;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJFrame;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJMenu;
import com.ganesha.desktop.component.XJMenuItem;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.DailyCashReportFacade;
import com.ganesha.minimarket.ui.forms.customer.CustomerListDialog;
import com.ganesha.minimarket.ui.forms.discount.DiscountListDialog;
import com.ganesha.minimarket.ui.forms.expense.ExpenseTransactionForm;
import com.ganesha.minimarket.ui.forms.payable.PayableListDialog;
import com.ganesha.minimarket.ui.forms.purchase.PembelianForm;
import com.ganesha.minimarket.ui.forms.receivable.ReceivableListDialog;
import com.ganesha.minimarket.ui.forms.reports.ItemStockReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.ProfitAndLossCutoffListDialog;
import com.ganesha.minimarket.ui.forms.reports.SaleConstraintReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.StockOpnameReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.TransactionReportListDialog;
import com.ganesha.minimarket.ui.forms.returns.ReturPembelianForm;
import com.ganesha.minimarket.ui.forms.returns.ReturPenjualanForm;
import com.ganesha.minimarket.ui.forms.revenue.RevenueTransactionForm;
import com.ganesha.minimarket.ui.forms.role.RoleListDialog;
import com.ganesha.minimarket.ui.forms.sale.PenjualanForm;
import com.ganesha.minimarket.ui.forms.servicemonitoring.saleconstraintpostingmonitoring.SaleConstraintPostingMonitoringListDialog;
import com.ganesha.minimarket.ui.forms.stock.StockListDialog;
import com.ganesha.minimarket.ui.forms.stockopname.StockOpnameListDialog;
import com.ganesha.minimarket.ui.forms.supplier.SupplierListDialog;
import com.ganesha.minimarket.ui.forms.systemsetting.DbConsistencyChecker;
import com.ganesha.minimarket.ui.forms.systemsetting.DbSettingForm;
import com.ganesha.minimarket.ui.forms.systemsetting.PrinterSettingForm;
import com.ganesha.minimarket.ui.forms.systemsetting.ReceiptPrinterStatusForm;
import com.ganesha.minimarket.ui.forms.user.ChangePasswordForm;
import com.ganesha.minimarket.ui.forms.user.UserListDialog;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.minimarket.utils.ReceiptPrinterUtils;
import com.ganesha.prepaid.ui.forms.MultiMapListDialog;
import com.ganesha.prepaid.ui.forms.MultiSaleForm;
import com.ganesha.prepaid.ui.forms.PrepaidSaleForm;
import com.ganesha.prepaid.ui.forms.VoucherListDialog;
import com.ganesha.prepaid.ui.forms.VoucherTypeListDialog;

public class MainFrame extends XJFrame {
	private static final long serialVersionUID = 5527217675003046133L;
	private XJMenuItem mntmPenjualan;
	private XJButton btnSaleTransaction;
	private XJPanel pnlButton;
	private XJPanel pnlUserInfo;
	private XJPanel pnlCompanyInfo;
	private XJLabel lblTanggal;
	private XJLabel lblJam;
	private XJMenuItem mntmPenjualanPulsaIsiUlang;
	private XJMenuItem mntmPulsaMulti;
	private XJMenuItem mntmOpenDrawer;
	private XJButton btnPrepaidRegular;
	private XJButton btnPrepaidMulti;
	private XJButton btnOpenDrawer;

	public MainFrame() {
		getContentPane().setBackground(Color.BLACK);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(GeneralConstants.APPLICATION_NAME + " "
				+ GeneralConstants.VERSION);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		XJMenu mnFile = new XJMenu("File");
		menuBar.add(mnFile);

		XJMenu mnAdministrasi = new XJMenu("Administrasi");
		menuBar.add(mnAdministrasi);

		XJMenuItem mntmRole = new XJMenuItem("Role",
				PermissionConstants.MN_ADMIN_ROLE);
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

		XJMenuItem mntmUser = new XJMenuItem("User",
				PermissionConstants.MN_ADMIN_USER);
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
				PermissionConstants.MN_ADMIN_CHGPWD);
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

		XJMenuItem mntmSupplier = new XJMenuItem("Supplier",
				PermissionConstants.MN_MASTER_SUPPLIER);
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

		XJMenuItem mntmCustomer = new XJMenuItem("Customer",
				PermissionConstants.MN_MASTER_CUSTOMER);
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

		XJMenuItem mntmPersediaan = new XJMenuItem("Persediaan Barang",
				PermissionConstants.MN_MASTER_STOCK);
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

		JSeparator separator_2 = new JSeparator();
		mnMasterData.add(separator_2);
		mnMasterData.add(mntmPersediaan);

		XJMenuItem mntmDiskon = new XJMenuItem("Diskon",
				PermissionConstants.MN_MASTER_DISCOUNT);
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

		XJMenuItem mntmSiklus = new XJMenuItem("Siklus",
				PermissionConstants.MN_MASTER_CIRCLE);
		mntmSiklus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new CircleListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});

		JSeparator separator_3 = new JSeparator();
		mnMasterData.add(separator_3);
		mnMasterData.add(mntmSiklus);

		XJMenuItem mntmBeban = new XJMenuItem("Beban",
				PermissionConstants.MN_MASTER_EXPENSE);
		mntmBeban.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new ExpenseListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnMasterData.add(mntmBeban);

		XJMenu mnTransaksi = new XJMenu("Transaksi");
		menuBar.add(mnTransaksi);

		XJMenuItem mntmPembelian = new XJMenuItem("Pembelian",
				PermissionConstants.MN_TRX_PUR);
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
				PermissionConstants.MN_TRX_PURRTN);
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

		mntmPenjualan = new XJMenuItem("Penjualan",
				PermissionConstants.MN_TRX_SAL);
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
				PermissionConstants.MN_TRX_SALRTN);
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

		XJMenuItem mntmHutang = new XJMenuItem("Hutang",
				PermissionConstants.MN_TRX_PAYABLE);
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
				PermissionConstants.MN_TRX_RECEIVABLE);
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

		JSeparator separator_4 = new JSeparator();
		mnTransaksi.add(separator_4);

		XJMenuItem mntmTrxExpense = new XJMenuItem(
				"Pembayaran Beban Lain-Lain",
				PermissionConstants.MN_TRX_EXPENSE);
		mntmTrxExpense.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new ExpenseTransactionForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnTransaksi.add(mntmTrxExpense);

		XJMenuItem mntmTrxRevenue = new XJMenuItem(
				"Input Pendapatan Lain-Lain",
				PermissionConstants.MN_TRX_REVENUE);
		mntmTrxRevenue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new RevenueTransactionForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnTransaksi.add(mntmTrxRevenue);

		JSeparator separator_5 = new JSeparator();
		mnTransaksi.add(separator_5);

		XJMenu mnConstraint = new XJMenu((String) null);
		mnConstraint.setText("Constraint");
		mnTransaksi.add(mnConstraint);

		XJMenu mnPrepaid = new XJMenu("Prepaid");
		menuBar.add(mnPrepaid);

		mntmPenjualanPulsaIsiUlang = new XJMenuItem("Penjualan Voucher Pulsa",
				PermissionConstants.MN_PREPAID_SALE);
		mntmPenjualanPulsaIsiUlang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new PrepaidSaleForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnPrepaid.add(mntmPenjualanPulsaIsiUlang);

		mntmPulsaMulti = new XJMenuItem("Penjualan Pulsa Multi",
				PermissionConstants.MN_MULTI_SALE);
		mntmPulsaMulti.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new MultiSaleForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnPrepaid.add(mntmPulsaMulti);

		XJMenu mnMaintenance = new XJMenu((String) null);
		mnMaintenance.setText("Maintenance");
		mnPrepaid.add(mnMaintenance);

		XJMenuItem mntmTipeVoucher = new XJMenuItem("Tipe Voucher",
				PermissionConstants.VOUCHER_TYPE_LIST);
		mntmTipeVoucher.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new VoucherTypeListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnMaintenance.add(mntmTipeVoucher);

		XJMenuItem mntmVoucher = new XJMenuItem("Voucher",
				PermissionConstants.VOUCHER_LIST);
		mntmVoucher.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new VoucherListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnMaintenance.add(mntmVoucher);

		XJMenuItem mntmMulti = new XJMenuItem("Multi",
				PermissionConstants.MULTI_LIST);
		mntmMulti.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new MultiMapListDialog(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnMaintenance.add(mntmMulti);

		XJMenu mnBackOffice = new XJMenu("Back Office");
		menuBar.add(mnBackOffice);

		XJMenu mnReport = new XJMenu("Laporan");
		mnBackOffice.add(mnReport);

		XJMenuItem mntmReportTransaksi = new XJMenuItem("Laporan Transaksi",
				PermissionConstants.MN_REPORT_TRX);
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
				"Laporan Stok Barang", PermissionConstants.MN_REPORT_STOCK);
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
				"Laporan Stock Opname",
				PermissionConstants.MN_REPORT_STOCKOPNAME);
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

		XJMenuItem mntmLaporanLabaRugi = new XJMenuItem("Laporan Laba Rugi",
				PermissionConstants.MN_REPORT_PROFITANDLOSS);
		mntmLaporanLabaRugi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// new ProfitAndLossReportListDialog(MainFrame.this)
					// .setVisible(true);
					new ProfitAndLossCutoffListDialog(MainFrame.this)
							.setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnReport.add(mntmLaporanLabaRugi);

		XJMenuItem mntmSaleConstraintReport = new XJMenuItem(
				"Laporan Penjualan Constraint",
				PermissionConstants.MN_REPORT_CONSTRAINT_SALE);
		mntmSaleConstraintReport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new SaleConstraintReportListDialog(MainFrame.this)
							.setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnReport.add(mntmSaleConstraintReport);

		XJMenuItem mntmDailyCashReport = new XJMenuItem("Laporan Kas Harian",
				PermissionConstants.MN_REPORT_CONSTRAINT_SALE);
		mntmDailyCashReport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Session session = HibernateUtils.openSession();
				try {
					DailyCashReportFacade.getInstance().search(
							DateUtils.getCurrent(Date.class),
							DateUtils.getCurrent(Date.class), session);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				} finally {
					session.close();
				}
			}
		});
		mnReport.add(mntmDailyCashReport);

		XJMenuItem mntmStockOpname = new XJMenuItem("Stock Opname",
				PermissionConstants.MN_BO_STOCKOPNAME);
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

		XJMenu mnMonitoring = new XJMenu((String) null);
		mnMonitoring.setText("Monitoring");
		menuBar.add(mnMonitoring);

		XJMenuItem mntmMonitoringPostingSaleConstraint = new XJMenuItem(
				"Posting Penjualan Constraint",
				PermissionConstants.MN_MON_SALCONSTRAINT_POSTING);
		mnMonitoring.add(mntmMonitoringPostingSaleConstraint);
		mntmMonitoringPostingSaleConstraint
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							new SaleConstraintPostingMonitoringListDialog(
									MainFrame.this).setVisible(true);
						} catch (Exception ex) {
							ExceptionHandler
									.handleException(MainFrame.this, ex);
						}
					}
				});

		XJMenu mnSetting = new XJMenu("Setting");
		menuBar.add(mnSetting);

		XJMenuItem mntmDbSetting = new XJMenuItem("Database",
				PermissionConstants.MN_SETTING_DB);
		mntmDbSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new DbSettingForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnSetting.add(mntmDbSetting);

		XJMenuItem mntmPrinterSetting = new XJMenuItem("Printer",
				PermissionConstants.MN_SETTING_PRINTER);
		mntmPrinterSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new PrinterSettingForm(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnSetting.add(mntmPrinterSetting);

		XJMenuItem mntmDbConsistencyCheckerSetting = new XJMenuItem(
				"DB Consistency", PermissionConstants.MN_SETTING_DBCONSISTENCY);
		mntmDbConsistencyCheckerSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new DbConsistencyChecker(MainFrame.this).setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnSetting.add(mntmDbConsistencyCheckerSetting);

		XJMenuItem mntmReceiptPrinterStatus = new XJMenuItem(
				"Receipt Printer Status",
				PermissionConstants.MN_SETTING_RECEIPTPRINTERSTATUS);
		mntmReceiptPrinterStatus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new ReceiptPrinterStatusForm(MainFrame.this)
							.setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnSetting.add(mntmReceiptPrinterStatus);

		mntmOpenDrawer = new XJMenuItem("Open Drawer",
				PermissionConstants.MN_SETTING_OPENDRAWER);
		mntmOpenDrawer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ReceiptPrinterUtils.openDrawer("");
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		});
		mnSetting.add(mntmOpenDrawer);

		setExtendedState(JFrame.MAXIMIZED_BOTH);
		pack();
		setLocationRelativeTo(null);
		getContentPane().setLayout(
				new MigLayout("", "[grow]", "[][][][grow][]"));

		XJPanel pnlRunningClock = new XJPanel();
		pnlRunningClock.setOpaque(false);
		getContentPane().add(pnlRunningClock, "cell 0 0,alignx center");
		pnlRunningClock.setLayout(new MigLayout("", "[]", "[][]"));

		lblTanggal = new XJLabel();
		lblTanggal.setForeground(Color.WHITE);
		lblTanggal.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTanggal.setText(Formatter.formatDateToString(DateUtils
				.getCurrent(Date.class)));
		pnlRunningClock.add(lblTanggal, "cell 0 0,alignx center");

		lblJam = new XJLabel();
		lblJam.setForeground(Color.WHITE);
		lblJam.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJam.setText(Formatter.formatClockToString(DateUtils
				.getCurrent(Date.class)));
		pnlRunningClock.add(lblJam, "cell 0 1,alignx center");

		pnlUserInfo = new XJPanel();
		pnlUserInfo.setOpaque(false);
		getContentPane().add(pnlUserInfo, "cell 0 1,alignx center,growy");
		pnlUserInfo.setLayout(new MigLayout("", "[100][50][]", "[][][]"));

		XJLabel lblLogin = new XJLabel();
		lblLogin.setForeground(Color.WHITE);
		lblLogin.setText("Login ID");
		pnlUserInfo.add(lblLogin, "cell 0 0");

		XJLabel lblTitikDua1 = new XJLabel();
		lblTitikDua1.setForeground(Color.WHITE);
		lblTitikDua1.setText(":");
		pnlUserInfo.add(lblTitikDua1, "cell 1 0,alignx right");

		XJLabel lblLoginValue = new XJLabel();
		lblLoginValue.setForeground(Color.WHITE);
		pnlUserInfo.add(lblLoginValue, "cell 2 0");
		lblLoginValue.setText(Main.getUserLogin().getLogin());

		XJLabel lblNama = new XJLabel();
		lblNama.setForeground(Color.WHITE);
		lblNama.setText("Nama User");
		pnlUserInfo.add(lblNama, "cell 0 1");

		XJLabel lblTitikDua2 = new XJLabel();
		lblTitikDua2.setForeground(Color.WHITE);
		lblTitikDua2.setText(":");
		pnlUserInfo.add(lblTitikDua2, "cell 1 1,alignx right");

		XJLabel lblNameValue = new XJLabel();
		lblNameValue.setForeground(Color.WHITE);
		pnlUserInfo.add(lblNameValue, "cell 2 1");
		lblNameValue.setText(Main.getUserLogin().getName());

		XJLabel lblRole = new XJLabel();
		lblRole.setForeground(Color.WHITE);
		lblRole.setText("Role");
		pnlUserInfo.add(lblRole, "cell 0 2");

		XJLabel lblTitikDua3 = new XJLabel();
		lblTitikDua3.setForeground(Color.WHITE);
		lblTitikDua3.setText(":");
		pnlUserInfo.add(lblTitikDua3, "cell 1 2,alignx right");

		XJLabel lblRoleValue = new XJLabel();
		lblRoleValue.setForeground(Color.WHITE);
		lblRoleValue.setText("");
		if (Main.getUserLogin().getUserRoleLinks().isEmpty()) {
			lblRoleValue.setText("-");
		} else {
			lblRoleValue.setText(Main.getUserLogin().getUserRoleLinks().get(0)
					.getPrimaryKey().getRole().getName());
		}

		pnlUserInfo.add(lblRoleValue, "cell 2 2");

		pnlCompanyInfo = new XJPanel();
		pnlCompanyInfo.setOpaque(false);
		getContentPane().add(pnlCompanyInfo, "cell 0 2,alignx center,growy");
		pnlCompanyInfo.setLayout(new MigLayout("", "[]", "[]"));

		XJLabel lblCompanyName = new XJLabel();
		lblCompanyName.setForeground(Color.WHITE);
		lblCompanyName.setFont(new Font("Tahoma", Font.BOLD, 80));
		pnlCompanyInfo.add(lblCompanyName, "cell 0 0");
		lblCompanyName.setText(Main.getCompany().getName());

		pnlButton = new XJPanel();
		pnlButton.setOpaque(false);
		getContentPane().add(pnlButton, "cell 0 4,growx");
		pnlButton.setLayout(new MigLayout("",
				"[grow][150px][150px][150px][150px][150px][grow]", "[]"));

		btnSaleTransaction = new XJButton(
				"<html><center>Transaksi Penjualan<br/>[F5]</center></html>");
		btnSaleTransaction
				.setText("<html><center>Penjualan<br/><br/>[F5]</center></html>");
		btnSaleTransaction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mntmPenjualan.doClick();
			}
		});
		pnlButton.add(btnSaleTransaction, "cell 1 0,growx");

		btnPrepaidRegular = new XJButton();
		btnPrepaidRegular.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mntmPenjualanPulsaIsiUlang.doClick();
			}
		});
		btnPrepaidRegular
				.setText("<html><center>Voucher Pulsa<br/>(Reguler)<br/>[F6]</center></html>");
		pnlButton.add(btnPrepaidRegular, "cell 2 0,growx");

		btnPrepaidMulti = new XJButton();
		btnPrepaidMulti.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mntmPulsaMulti.doClick();
			}
		});
		btnPrepaidMulti
				.setText("<html><center>Voucher Pulsa<br/>(Multi)<br/>[F7]</center></html>");
		pnlButton.add(btnPrepaidMulti, "cell 3 0,growx");

		btnOpenDrawer = new XJButton();
		btnOpenDrawer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mntmOpenDrawer.doClick();
			}
		});
		btnOpenDrawer
				.setText("<html><center>Open Drawer<br/><br/>[F10]</center></html>");
		pnlButton.add(btnOpenDrawer, "cell 5 0");

		runClock();
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnSaleTransaction.doClick();
			break;
		case KeyEvent.VK_F6:
			btnPrepaidRegular.doClick();
			break;
		case KeyEvent.VK_F7:
			btnPrepaidMulti.doClick();
			break;
		case KeyEvent.VK_F10:
			btnOpenDrawer.doClick();
			break;
		default:
			break;
		}
	}

	private void runClock() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				while (true) {
					Date date = DateUtils.getCurrent(Date.class);
					String tanggal = Formatter.formatDateToString(date);
					String jam = Formatter.formatClockToString(date);
					lblTanggal.setText(tanggal);
					lblJam.setText(jam);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						/*
						 * Do Nothing
						 */
					}
				}
			};
		};
		thread.start();
	}
}
