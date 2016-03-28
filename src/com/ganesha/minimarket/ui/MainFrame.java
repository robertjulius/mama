package com.ganesha.minimarket.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;

import com.ganesha.accounting.ui.forms.circle.CircleListDialog;
import com.ganesha.accounting.ui.forms.expense.ExpenseListDialog;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJFrame;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJMenu;
import com.ganesha.desktop.component.XJMenuItem;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.ui.forms.checkstock.CheckStockForm;
import com.ganesha.minimarket.ui.forms.company.CompanyForm;
import com.ganesha.minimarket.ui.forms.customer.CustomerListDialog;
import com.ganesha.minimarket.ui.forms.discount.DiscountListDialog;
import com.ganesha.minimarket.ui.forms.expense.ExpenseTransactionForm;
import com.ganesha.minimarket.ui.forms.payable.PayableListDialog;
import com.ganesha.minimarket.ui.forms.purchase.PembelianForm;
import com.ganesha.minimarket.ui.forms.receivable.ReceivableListDialog;
import com.ganesha.minimarket.ui.forms.reports.DailyCashReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.ItemStockReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.ProfitAndLossCutoffListDialog;
import com.ganesha.minimarket.ui.forms.reports.SaleConstraintReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.SaleSummaryReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.StockOpnameReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.TransactionReportListDialog;
import com.ganesha.minimarket.ui.forms.returns.ReturPembelianForm;
import com.ganesha.minimarket.ui.forms.returns.ReturPenjualanForm;
import com.ganesha.minimarket.ui.forms.revenue.RevenueTransactionForm;
import com.ganesha.minimarket.ui.forms.role.RoleListDialog;
import com.ganesha.minimarket.ui.forms.sale.PenjualanForm;
import com.ganesha.minimarket.ui.forms.sale.ReprintSaleReceiptListDialog;
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

import net.miginfocom.swing.MigLayout;

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
	private JMenuBar menuBar;
	
	public static MainFrame INSTANCE;
	
	public static MainFrame reCreateInstance() {
		INSTANCE = new MainFrame();
		return INSTANCE;
	}

	private MainFrame() {
		getContentPane().setBackground(Color.BLACK);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(GeneralConstants.APPLICATION_NAME + " " + GeneralConstants.VERSION);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		XJMenu mnFile = createMenu("File", null);

		XJMenu mnAdministrasi = createMenu("Administrasi", null);
		createMenuItem(mnAdministrasi, CompanyForm.class, "Company", true, PermissionConstants.MN_ADMIN_COMPANY);
		createMenuItem(mnAdministrasi, RoleListDialog.class, "Role", true, PermissionConstants.MN_ADMIN_ROLE);
		createMenuItem(mnAdministrasi, UserListDialog.class, "User", true, PermissionConstants.MN_ADMIN_USER);
		createMenuItem(mnAdministrasi, ChangePasswordForm.class, "Ganti Password", false,
				PermissionConstants.MN_ADMIN_CHGPWD);

		XJMenu mnMasterData = createMenu("Master Data", null);

		createMenuItem(mnMasterData, SupplierListDialog.class, "Supplier", true,
				PermissionConstants.MN_MASTER_SUPPLIER);
		createMenuItem(mnMasterData, CustomerListDialog.class, "Customer", true,
				PermissionConstants.MN_MASTER_CUSTOMER);
		createMenuItem(mnMasterData, StockListDialog.class, "Persediaan Barang", true,
				PermissionConstants.MN_MASTER_STOCK);

		addSeparator(mnMasterData);
		createMenuItem(mnMasterData, DiscountListDialog.class, "Diskon", true, PermissionConstants.MN_MASTER_DISCOUNT);

		addSeparator(mnMasterData);
		createMenuItem(mnMasterData, CircleListDialog.class, "Siklus", true, PermissionConstants.MN_MASTER_CIRCLE);
		createMenuItem(mnMasterData, ExpenseListDialog.class, "Beban", true, PermissionConstants.MN_MASTER_EXPENSE);
		createMenuItem(mnMasterData, ExpenseListDialog.class, "Beban", true, PermissionConstants.MN_MASTER_EXPENSE);

		XJMenu mnTransaksi = createMenu("Transaksi", null);

		createMenuItem(mnTransaksi, PembelianForm.class, "Pembelian", true, PermissionConstants.MN_TRX_PUR);
		createMenuItem(mnTransaksi, ReturPembelianForm.class, "Retur Pembelian", true,
				PermissionConstants.MN_TRX_PURRTN);

		addSeparator(mnTransaksi);
		mntmPenjualan = createMenuItem(mnTransaksi, PenjualanForm.class, "Penjualan", true,
				PermissionConstants.MN_TRX_SAL);
		createMenuItem(mnTransaksi, ReturPenjualanForm.class, "Retur Penjualan", true,
				PermissionConstants.MN_TRX_SALRTN);
		createMenuItem(mnTransaksi, ReprintSaleReceiptListDialog.class, "Cetak Ulang Struk Penjualan", true,
				PermissionConstants.MN_REPRINT_SALERECEIPT);
		createMenuItem(mnTransaksi, CheckStockForm.class, "Cek Stock & Harga", true,
				PermissionConstants.MN_CHECK_STOCK);

		addSeparator(mnTransaksi);
		createMenuItem(mnTransaksi, PayableListDialog.class, "Hutang", true, PermissionConstants.MN_TRX_PAYABLE);
		createMenuItem(mnTransaksi, ReceivableListDialog.class, "Piutang", true, PermissionConstants.MN_TRX_RECEIVABLE);

		addSeparator(mnTransaksi);
		createMenuItem(mnTransaksi, ExpenseTransactionForm.class, "Pembayaran Beban Lain-Lain", true,
				PermissionConstants.MN_TRX_EXPENSE);
		createMenuItem(mnTransaksi, RevenueTransactionForm.class, "Input Pendapatan Lain-Lain", true,
				PermissionConstants.MN_TRX_REVENUE);

		addSeparator(mnTransaksi);
		// XJMenu mnConstraint = createMenu("Constraint", mnTransaksi);

		XJMenu mnPrepaid = createMenu("Prepaid", null);

		mntmPenjualanPulsaIsiUlang = createMenuItem(mnPrepaid, PrepaidSaleForm.class, "Penjualan Voucher Pulsa", true,
				PermissionConstants.MN_PREPAID_SALE);
		mntmPulsaMulti = createMenuItem(mnPrepaid, MultiSaleForm.class, "Penjualan Pulsa Multi", true,
				PermissionConstants.MN_MULTI_SALE);

		XJMenu mnMaintenance = createMenu("Maintenance", mnPrepaid);

		createMenuItem(mnMaintenance, VoucherTypeListDialog.class, "Tipe Voucher", true,
				PermissionConstants.VOUCHER_TYPE_LIST);
		createMenuItem(mnMaintenance, VoucherListDialog.class, "Voucher", true, PermissionConstants.VOUCHER_LIST);
		createMenuItem(mnMaintenance, MultiMapListDialog.class, "Multi", true, PermissionConstants.MULTI_LIST);

		XJMenu mnReport = createMenu("Laporan", null);

		createMenuItem(mnReport, TransactionReportListDialog.class, "Laporan Transaksi", true,
				PermissionConstants.MN_REPORT_TRX);
		createMenuItem(mnReport, ItemStockReportListDialog.class, "Laporan Stok Barang", true,
				PermissionConstants.MN_REPORT_STOCK);
		createMenuItem(mnReport, StockOpnameReportListDialog.class, "Laporan Stok Opname", true,
				PermissionConstants.MN_REPORT_STOCKOPNAME);
		createMenuItem(mnReport, ProfitAndLossCutoffListDialog.class, "Laporan Laba Rugi", true,
				PermissionConstants.MN_REPORT_PROFITANDLOSS);
		createMenuItem(mnReport, SaleConstraintReportListDialog.class, "Laporan Penjualan Constraint", true,
				PermissionConstants.MN_REPORT_CONSTRAINT_SALE);
		createMenuItem(mnReport, DailyCashReportListDialog.class, "Laporan Kas Harian", true,
				PermissionConstants.MN_REPORT_DAILYCASH);
		createMenuItem(mnReport, SaleSummaryReportListDialog.class, "Laporan Ringkasan Penjualan", true,
				PermissionConstants.MN_REPORT_SALESUMMARY);

		XJMenu mnBackOffice = createMenu("Back Office", null);
		createMenuItem(mnBackOffice, StockOpnameListDialog.class, "Stock Opname", true,
				PermissionConstants.MN_BO_STOCKOPNAME);

		XJMenu mnMonitoring = createMenu("Monitoring", null);
		createMenuItem(mnMonitoring, SaleConstraintPostingMonitoringListDialog.class, "Posting Penjualan Constraint",
				true, PermissionConstants.MN_MON_SALCONSTRAINT_POSTING);

		XJMenu mnSetting = createMenu("Setting", null);
		createMenuItem(mnSetting, DbSettingForm.class, "Database", true, PermissionConstants.MN_SETTING_DB);
		createMenuItem(mnSetting, PrinterSettingForm.class, "Printer", true, PermissionConstants.MN_SETTING_PRINTER);
		createMenuItem(mnSetting, DbConsistencyChecker.class, "DB Consistency", true,
				PermissionConstants.MN_SETTING_DBCONSISTENCY);
		createMenuItem(mnSetting, ReceiptPrinterStatusForm.class, "Receipt Printer Status", true,
				PermissionConstants.MN_SETTING_RECEIPTPRINTERSTATUS);

		mntmOpenDrawer = createMenuItem(mnSetting, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ReceiptPrinterUtils.openDrawer();
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		}, "Open Drawer", true, PermissionConstants.MN_SETTING_OPENDRAWER);

		setExtendedState(JFrame.MAXIMIZED_BOTH);
		pack();
		setLocationRelativeTo(null);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][grow][]"));

		XJPanel pnlRunningClock = new XJPanel();
		pnlRunningClock.setOpaque(false);
		getContentPane().add(pnlRunningClock, "cell 0 0,alignx center");
		pnlRunningClock.setLayout(new MigLayout("", "[]", "[][]"));

		lblTanggal = new XJLabel();
		lblTanggal.setForeground(Color.WHITE);
		lblTanggal.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTanggal.setText(Formatter.formatDateToString(DateUtils.getCurrent(Date.class)));
		pnlRunningClock.add(lblTanggal, "cell 0 0,alignx center");

		lblJam = new XJLabel();
		lblJam.setForeground(Color.WHITE);
		lblJam.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJam.setText(Formatter.formatClockToString(DateUtils.getCurrent(Date.class)));
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
			lblRoleValue.setText(Main.getUserLogin().getUserRoleLinks().get(0).getPrimaryKey().getRole().getName());
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
		pnlButton.setLayout(new MigLayout("", "[grow][150px][150px][150px][150px][150px][grow]", "[]"));

		btnSaleTransaction = new XJButton("<html><center>Transaksi Penjualan<br/>[F5]</center></html>");
		btnSaleTransaction.setText("<html><center>Penjualan<br/><br/>[F5]</center></html>");
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
		btnPrepaidRegular.setText("<html><center>Voucher Pulsa<br/>(Reguler)<br/>[F6]</center></html>");
		pnlButton.add(btnPrepaidRegular, "cell 2 0,growx");

		btnPrepaidMulti = new XJButton();
		btnPrepaidMulti.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mntmPulsaMulti.doClick();
			}
		});
		btnPrepaidMulti.setText("<html><center>Voucher Pulsa<br/>(Multi)<br/>[F7]</center></html>");
		pnlButton.add(btnPrepaidMulti, "cell 3 0,growx");

		btnOpenDrawer = new XJButton();
		btnOpenDrawer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openDrawer();
			}
		});
		btnOpenDrawer.setText("<html><center>Open Drawer<br/><br/>[F10]</center></html>");
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
	
	public void openDrawer() {
		mntmOpenDrawer.doClick();
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

	private XJMenu createMenu(String label, XJMenu parent) {
		XJMenu menu = new XJMenu(label);
		if (parent == null) {
			menuBar.add(menu);
		} else {
			parent.add(menu);
		}
		return menu;
	}

	private XJMenuItem createMenuItem(XJMenu parent, final Class<? extends XJDialog> clazz, String label,
			final boolean permissionRequired, String permissionCode) {

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					XJDialog dialog = newDialog(clazz);
					dialog.setPermissionRequired(permissionRequired);
					dialog.setVisible(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MainFrame.this, ex);
				}
			}
		};

		return createMenuItem(parent, actionListener, label, permissionRequired, permissionCode);
	}

	private XJMenuItem createMenuItem(XJMenu parent, ActionListener actionListener, String label,
			final boolean permissionRequired, String permissionCode) {

		XJMenuItem menuItem = new XJMenuItem(label, permissionCode);
		menuItem.addActionListener(actionListener);
		parent.add(menuItem);

		return menuItem;
	}

	private XJDialog newDialog(Class<? extends XJDialog> clazz) throws AppException, UserException {
		try {
			Constructor<? extends XJDialog> constructor = clazz.getConstructor(Window.class);
			XJDialog dialog = constructor.newInstance(this);
			return dialog;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException e) {
			throw new AppException(e);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof AppException) {
				throw (AppException) e.getTargetException();
			} else if (e.getTargetException() instanceof UserException) {
				throw (UserException) e.getTargetException();
			} else {
				throw new AppException(e.getTargetException());
			}
		}
	}

	private void addSeparator(XJMenu menu) {
		JSeparator separator = new JSeparator();
		menu.add(separator);
	}
}
