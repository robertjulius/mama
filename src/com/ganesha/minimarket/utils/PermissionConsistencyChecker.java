package com.ganesha.minimarket.utils;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.ganesha.core.utils.DBUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.model.Permission;

public class PermissionConsistencyChecker {

	private List<Permission> permissions;

	public PermissionConsistencyChecker() {
		permissions = new ArrayList<>();

		// Administrasi - Role
		permissions.add(createPermission(PermissionConstants.MN_ADMIN_ROLE,
				"/Administrasi/Role", 210));
		permissions.add(createPermission(PermissionConstants.ROLE_LIST,
				"Role List", 211));
		permissions.add(createPermission(PermissionConstants.ROLE_FORM,
				"Role Form", 212));

		// Administrasi - User
		permissions.add(createPermission(PermissionConstants.MN_ADMIN_USER,
				"/Administrasi/User", 220));
		permissions.add(createPermission(PermissionConstants.USER_LIST,
				"User List", 221));
		permissions.add(createPermission(PermissionConstants.USER_FORM,
				"User Form", 222));

		// Administrasi - Change Password
		permissions.add(createPermission(PermissionConstants.MN_ADMIN_CHGPWD,
				"/Administrasi/Ganti Password", 230));
		permissions.add(createPermission(PermissionConstants.CHGPWD_FORM,
				"Change Password Form", 231));

		// Master Date - Persediaan Barang
		permissions.add(createPermission(PermissionConstants.MN_MASTER_STOCK,
				"/Master Data/Persediaan Barang", 310));
		permissions.add(createPermission(PermissionConstants.STOCK_LIST,
				"Stock List", 311));
		permissions.add(createPermission(PermissionConstants.STOCK_FORM,
				"Stock Form", 312));

		// Master Date - Supplier
		permissions.add(createPermission(
				PermissionConstants.MN_MASTER_SUPPLIER,
				"/Master Data/Supplier", 320));
		permissions.add(createPermission(PermissionConstants.SUPPLIER_LIST,
				"Supplier List", 321));
		permissions.add(createPermission(PermissionConstants.SUPPLIER_FORM,
				"Supplier Form", 322));

		// Master Date - Customer
		permissions.add(createPermission(
				PermissionConstants.MN_MASTER_CUSTOMER,
				"/Master Data/Customer", 330));
		permissions.add(createPermission(PermissionConstants.CUST_LIST,
				"Customer List", 331));
		permissions.add(createPermission(PermissionConstants.CUST_FORM,
				"Customer Form", 332));

		// Master Date - Discount
		permissions.add(createPermission(
				PermissionConstants.MN_MASTER_DISCOUNT,
				"/Master Data/Discount", 340));
		permissions.add(createPermission(PermissionConstants.DISCOUNT_LIST,
				"Discount List", 341));
		permissions.add(createPermission(PermissionConstants.DISCOUNT_FORM,
				"Discount Form", 342));

		// Master Date - Circle
		permissions.add(createPermission(PermissionConstants.MN_MASTER_CIRCLE,
				"/Master Data/Circle", 350));
		permissions.add(createPermission(PermissionConstants.CIRCLE_LIST,
				"Circle List", 351));
		permissions.add(createPermission(PermissionConstants.CIRCLE_FORM,
				"Circle Form", 352));

		// Master Date - Expense
		permissions.add(createPermission(PermissionConstants.MN_MASTER_EXPENSE,
				"/Master Data/Expense", 360));
		permissions.add(createPermission(PermissionConstants.EXPENSE_LIST,
				"Expense List", 361));
		permissions.add(createPermission(PermissionConstants.EXPENSE_FORM,
				"Expense Form", 362));

		// Transaction - Pembelian
		permissions.add(createPermission(PermissionConstants.MN_TRX_PUR,
				"/Transaksi/Pembelian", 410));
		permissions.add(createPermission(PermissionConstants.PUR_FORM,
				"Pembelian Form", 411));

		// Transaction - Retur Pembelian
		permissions.add(createPermission(PermissionConstants.MN_TRX_PURRTN,
				"/Transaksi/Retur Pembelian", 420));
		permissions.add(createPermission(PermissionConstants.PURRTN_FORM,
				"Retur Pembelian Form", 421));

		// Transaction - Penjualan
		permissions.add(createPermission(PermissionConstants.MN_TRX_SAL,
				"/Transaksi/Penjualan", 430));
		permissions.add(createPermission(PermissionConstants.SAL_FORM,
				"Penjualan Form", 431));

		// Transaction - Retur Penjualan
		permissions.add(createPermission(PermissionConstants.MN_TRX_SALRTN,
				"/Transaksi/Retur Penjualan", 440));
		permissions.add(createPermission(PermissionConstants.SALRTN_FORM,
				"Retur Penjualan Form", 441));

		// Transaction - Payable
		permissions.add(createPermission(PermissionConstants.MN_TRX_PAYABLE,
				"/Transaksi/Hutang", 450));
		permissions.add(createPermission(PermissionConstants.PAYABLE_LIST,
				"Payable List", 451));
		permissions.add(createPermission(PermissionConstants.PAYABLE_FORM,
				"Payable Form", 452));

		// Transaction - Receivable
		permissions.add(createPermission(PermissionConstants.MN_TRX_RECEIVABLE,
				"/Transaksi/Piutang", 460));
		permissions.add(createPermission(PermissionConstants.RECEIVABLE_LIST,
				"Receivable List", 461));
		permissions.add(createPermission(PermissionConstants.RECEIVABLE_FORM,
				"Receivable Form", 462));

		// Transaction - Expense Transaction
		permissions.add(createPermission(PermissionConstants.MN_TRX_EXPENSE,
				"/Transaksi/Pembayaran Beban Lain-Lain", 470));
		permissions.add(createPermission(
				PermissionConstants.EXPENSE_TRANSACTION_FORM,
				"Expense Transaction Form", 471));

		// Transaction - Revenue Transaction
		permissions.add(createPermission(PermissionConstants.MN_TRX_REVENUE,
				"/Transaksi/Input Pendapatan Lain-Lain", 480));
		permissions.add(createPermission(
				PermissionConstants.REVENUE_TRANSACTION_FORM,
				"Revenue Transaction Form", 481));

		// Transaction - Constraint - Penjualan
		permissions.add(createPermission(
				PermissionConstants.MN_TRX_CONSTRAINT_SAL,
				"/Transaksi/Constraint/Transaksi Penjualan Constraint", 490));
		permissions.add(createPermission(
				PermissionConstants.CONSTRAINT_SAL_FORM,
				"Sale Constraint Form", 491));

		// Transaction - Constraint - Penjualan
		permissions.add(createPermission(
				PermissionConstants.MN_TRX_CONSTRAINT_SAL,
				"/Transaksi/Constraint/Transaksi Penjualan Constraint", 490));
		permissions.add(createPermission(
				PermissionConstants.CONSTRAINT_SAL_FORM,
				"Sale Constraint Form", 491));

		// Back Office - Laporan - Laporan Transaksi
		permissions.add(createPermission(PermissionConstants.MN_REPORT_TRX,
				"/Back Office/Laporan/Laporan Transaksi", 510));
		permissions.add(createPermission(PermissionConstants.REPORT_TRX_LIST,
				"Transaction Report List", 511));

		// Back Office - Laporan - Laporan Stok Barang
		permissions.add(createPermission(PermissionConstants.MN_REPORT_STOCK,
				"/Back Office/Laporan/Laporan Stok Barang", 520));
		permissions.add(createPermission(PermissionConstants.REPORT_STOCK_LIST,
				"Item Stock Report List", 521));

		// Back Office - Laporan - Laporan Stok Opname
		permissions.add(createPermission(
				PermissionConstants.MN_REPORT_STOCKOPNAME,
				"/Back Office/Laporan/Laporan Stok Opname", 530));
		permissions.add(createPermission(
				PermissionConstants.REPORT_STOCKOPNAME_LIST,
				"Stock Opname Report List", 531));

		// Back Office - Laporan - Laporan Profit and Loss
		permissions.add(createPermission(
				PermissionConstants.MN_REPORT_PROFITANDLOSS,
				"/Back Office/Laporan/Laporan Laba Rugi", 535));
		permissions.add(createPermission(
				PermissionConstants.REPORT_PROFITANDLOSS_LIST,
				"Profit and Lost Report List", 536));

		permissions.add(createPermission(PermissionConstants.REPORT_VIEWER,
				"Report Viewer", 539));

		// Back Office - Stock Opname
		permissions.add(createPermission(PermissionConstants.MN_BO_STOCKOPNAME,
				"/Back Office/Stock Opname", 540));
		permissions.add(createPermission(PermissionConstants.STOCKOPNAME_LIST,
				"Stock Opname List", 541));
		permissions.add(createPermission(
				PermissionConstants.STOCKOPNAME_CONFIRM,
				"Stock Opname Confirmation", 542));

		// Back Office - Laporan - Laporan Penjualan Constraint
		permissions.add(createPermission(
				PermissionConstants.MN_REPORT_CONSTRAINT_SALE,
				"/Back Office/Laporan/Laporan Penjualan Constraint", 550));
		permissions.add(createPermission(
				PermissionConstants.REPORT_CONSTRAINTSALE_LIST,
				"Sale Constraint Report List", 551));

		// Service Monitoring - Sale Constraint Posting Scheduler
		permissions.add(createPermission(
				PermissionConstants.MN_MON_SALCONSTRAINT_POSTING,
				"/Service Monitoring/Posting Penjualan Constraint", 590));
		permissions.add(createPermission(
				PermissionConstants.SALECONSTRAINT_POSTINGMONITORING_LIST,
				"Sale Constraint Posting Monitoring List", 591));
		permissions.add(createPermission(
				PermissionConstants.SALECONSTRAINT_POSTINGMONITORING_FORM,
				"Sale Constraint Posting Monitoring Form", 592));

		// Setting - Setting Database
		permissions.add(createPermission(PermissionConstants.MN_SETTING_DB,
				"/Setting/Database", 610));
		permissions.add(createPermission(PermissionConstants.SETTING_DB_FORM,
				"Setting Database Form", 611));

		// Setting - Setting Printer
		permissions
				.add(createPermission(PermissionConstants.MN_SETTING_PRINTER,
						"/Setting/Printer", 620));
		permissions.add(createPermission(
				PermissionConstants.SETTING_PRINTER_FORM,
				"Setting Printer Form", 621));

		// Setting - Problem Report Setting
		permissions.add(createPermission(
				PermissionConstants.MN_SETTING_PROBLEMREPORT,
				"/Setting/Problem Report", 630));
		permissions.add(createPermission(
				PermissionConstants.SETTING_PROBLEMREPORT_FORM,
				"Setting Problem Report Form", 631));

		// Setting - DB Consistency Checker
		permissions.add(createPermission(
				PermissionConstants.MN_SETTING_DBCONSISTENCY,
				"/Setting/Database Consistency", 640));
		permissions.add(createPermission(
				PermissionConstants.SETTING_DBCONSISTENCY,
				"Database Consistency Checker", 641));

		// Setting - Receipt Printer Status
		permissions.add(createPermission(
				PermissionConstants.MN_SETTING_RECEIPTPRINTERSTATUS,
				"/Setting/Receipt Printer Status", 650));
		permissions.add(createPermission(
				PermissionConstants.SETTING_RECEIPTPRINTERSTATUS,
				"Receipt Printer Status", 651));
	}

	public void initDB() {
		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			for (Permission permission : permissions) {

				boolean exists = DBUtils.getInstance().isExists("code",
						permission.getCode(), Permission.class, session);
				if (exists) {
					session.merge(permission);
				} else {
					session.saveOrUpdate(permission);
				}
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private Permission createPermission(String code, String name, int orderNum) {
		Permission permission = new Permission();
		permission.setCode(code);
		permission.setName(name);
		permission.setDescription(null);
		permission.setOrderNum(orderNum);
		return permission;
	}
}