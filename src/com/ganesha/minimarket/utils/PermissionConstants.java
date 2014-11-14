package com.ganesha.minimarket.utils;

public class PermissionConstants {

	public static final String MN_ADMIN_ROLE = "/administrasi/role";
	public static final String ROLE_LIST = "RoleList";
	public static final String ROLE_FORM = "RoleForm";

	// Administrasi - User
	public static final String MN_ADMIN_USER = "/administrasi/user";
	public static final String USER_LIST = "UserList";
	public static final String USER_FORM = "UserForm";

	// Administrasi - Change Password
	public static final String MN_ADMIN_CHGPWD = "/administrasi/changepassword";
	public static final String CHGPWD_FORM = "ChangePasswordForm";

	// Master Date - Persediaan Barang
	public static final String MN_MASTER_STOCK = "/master/stock";
	public static final String STOCK_LIST = "StockList";
	public static final String STOCK_FORM = "StockForm";

	// Master Date - Supplier
	public static final String MN_MASTER_SUPPLIER = "/master/supplier";
	public static final String SUPPLIER_LIST = "SupplierList";
	public static final String SUPPLIER_FORM = "SupplierForm";

	// Master Date - Customer
	public static final String MN_MASTER_CUSTOMER = "/master/customer";
	public static final String CUST_LIST = "CustomerList";
	public static final String CUST_FORM = "CustomerForm";

	// Master Date - Discount
	public static final String MN_MASTER_DISCOUNT = "/master/discount";
	public static final String DISCOUNT_LIST = "DiscountList";
	public static final String DISCOUNT_FORM = "DiscountForm";

	// Master Date - Circle
	public static final String MN_MASTER_CIRCLE = "/master/circle";
	public static final String CIRCLE_LIST = "CircleList";
	public static final String CIRCLE_FORM = "CircleForm";

	// Master Date - Expense
	public static final String MN_MASTER_EXPENSE = "/master/expense";
	public static final String EXPENSE_LIST = "ExpenseList";
	public static final String EXPENSE_FORM = "ExpenseForm";

	// Transaction - Pembelian
	public static final String MN_TRX_PUR = "/transaction/purchase";
	public static final String PUR_FORM = "PembelianForm";

	// Transaction - Retur Pembelian
	public static final String MN_TRX_PURRTN = "/transaction/purchasereturn";
	public static final String PURRTN_FORM = "ReturPembelianForm";

	// Transaction - Penjualan
	public static final String MN_TRX_SAL = "/transaction/sale";
	public static final String SAL_FORM = "PenjualanForm";

	// Transaction - Retur Penjualan
	public static final String MN_TRX_SALRTN = "/transaction/salereturn";
	public static final String SALRTN_FORM = "ReturPenjualanForm";

	// Transaction - Payable
	public static final String MN_TRX_PAYABLE = "/transaction/payable";
	public static final String PAYABLE_LIST = "PayableList";
	public static final String PAYABLE_FORM = "PayableForm";

	// Transaction - Receivable
	public static final String MN_TRX_RECEIVABLE = "/transaction/receivable";
	public static final String RECEIVABLE_LIST = "ReceivableList";
	public static final String RECEIVABLE_FORM = "ReceivableForm";

	// Transaction - Expense Transaction
	public static final String MN_TRX_EXPENSE = "/transaction/expense";
	public static final String EXPENSE_TRANSACTION_FORM = "ExpenseTransactionForm";

	// Transaction - Revenue Transaction
	public static final String MN_TRX_REVENUE = "/transaction/revenue";
	public static final String REVENUE_TRANSACTION_FORM = "RevenueTransactionForm";

	// Transaction - Constraint - Penjualan
	public static final String MN_TRX_CONSTRAINT_SAL = "/transaction/constraint/sale";
	public static final String CONSTRAINT_SAL_FORM = "SaleConstraintForm";

	// Back Office - Laporan - Laporan Transaksi
	public static final String MN_REPORT_TRX = "/backoffice/report/transaction";
	public static final String REPORT_TRX_LIST = "TransactionReportList";

	// Back Office - Laporan - Laporan Stok Barang
	public static final String MN_REPORT_STOCK = "/backoffice/report/stock";
	public static final String REPORT_STOCK_LIST = "ItemStockReportList";

	// Back Office - Laporan - Laporan Stok Opname
	public static final String MN_REPORT_STOCKOPNAME = "/backoffice/report/stockopname";
	public static final String REPORT_STOCKOPNAME_LIST = "StockOpnameReportList";

	// Back Office - Laporan - Laporan Profit and Loss
	public static final String MN_REPORT_PROFITANDLOSS = "/backoffice/report/lossandprofit";
	public static final String REPORT_PROFITANDLOSS_LIST = "ProfitAndLossReportList";

	public static final String REPORT_VIEWER = "ReportViewer";

	// Back Office - Stock Opname
	public static final String MN_BO_STOCKOPNAME = "/backoffice/stockopname";
	public static final String STOCKOPNAME_LIST = "StockOpnameList";
	public static final String STOCKOPNAME_CONFIRM = "StockOpnameConfirmation";

	// Back Office - Laporan - Laporan Penjualan Constraint
	public static final String MN_REPORT_CONSTRAINT_SALE = "/backoffice/report/saleconstraint";
	public static final String REPORT_CONSTRAINTSALE_LIST = "SaleConstraintReportList";

	// Service Monitoring - Sale Constraint Posting Scheduler
	public static final String MN_MON_SALCONSTRAINT_POSTING = "/servicemonitoring/saleconstraintposting";
	public static final String SALECONSTRAINT_POSTINGMONITORING_LIST = "SaleConstraintPostingMonitoringList";
	public static final String SALECONSTRAINT_POSTINGMONITORING_FORM = "SaleConstraintPostingMonitoringForm";

	// Setting - DB Setting
	public static final String MN_SETTING_DB = "/setting/database";
	public static final String SETTING_DB_FORM = "DbSettingForm";

	// Setting - Printer Setting
	public static final String MN_SETTING_PRINTER = "/setting/printer";
	public static final String SETTING_PRINTER_FORM = "PrinterSettingForm";

	// Setting - Problem Report Setting
	public static final String MN_SETTING_PROBLEMREPORT = "/setting/problemreport";
	public static final String SETTING_PROBLEMREPORT_FORM = "ProblemReportSettingForm";

	// Setting - DB Consistency Checker
	public static final String MN_SETTING_DBCONSISTENCY = "/setting/dbconsistency";
	public static final String SETTING_DBCONSISTENCY = "DbConsistencyChecker";

	// Setting - Receipt Printer Status
	public static final String MN_SETTING_RECEIPTPRINTERSTATUS = "/setting/receiptprinterstatus";
	public static final String SETTING_RECEIPTPRINTERSTATUS = "ReceiptPrinterStatus";

	// Prepaid - Maintenance - Voucher Type
	public static final String MN_PREPAID_MAINTENANCE_VOUCHERTYPE = "/prepaid/maintenance/vouchertype";
	public static final String VOUCHER_TYPE_LIST = "VoucherTypeList";
	public static final String VOUCHER_TYPE_FORM = "VoucherTypeForm";

	// Prepaid - Maintenance - Voucher
	public static final String MN_PREPAID_MAINTENANCE_VOUCHER = "/prepaid/maintenance/voucher";
	public static final String VOUCHER_LIST = "VoucherList";
	public static final String VOUCHER_FORM = "VoucherForm";

	// Prepaid - Sale
	public static final String MN_PREPAID_SALE = "/prepaid/sale";
	public static final String SALE_PREPAID_FORM = "SalePrepaidForm";

	// Prepaid - Maintenance - Multi Map
	public static final String MN_PREPAID_MAINTENANCE_MULTI = "/prepaid/maintenance/multi";
	public static final String MULTI_LIST = "Multi List";
	public static final String MULTI_FORM = "Multi Form";

	// Prepaid - Multi Map Sale
	public static final String MN_MULTI_SALE = "/prepaid/multisale";
	public static final String MULTI_SALE_FORM = "MultiSaleForm";
}
