package com.ganesha.minimarket.utils;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.ganesha.desktop.component.permissionutils.PermissionControl;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.GlobalFacade;
import com.ganesha.minimarket.ui.forms.customer.CustomerForm;
import com.ganesha.minimarket.ui.forms.customer.CustomerListDialog;
import com.ganesha.minimarket.ui.forms.discount.DiscountForm;
import com.ganesha.minimarket.ui.forms.discount.DiscountListDialog;
import com.ganesha.minimarket.ui.forms.payable.PayableForm;
import com.ganesha.minimarket.ui.forms.purchase.PembelianForm;
import com.ganesha.minimarket.ui.forms.receivable.ReceivableForm;
import com.ganesha.minimarket.ui.forms.reports.ItemStockReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.ReportViewerDialog;
import com.ganesha.minimarket.ui.forms.reports.StockOpnameReportListDialog;
import com.ganesha.minimarket.ui.forms.reports.TransactionReportListDialog;
import com.ganesha.minimarket.ui.forms.returns.ReturPembelianForm;
import com.ganesha.minimarket.ui.forms.returns.ReturPenjualanForm;
import com.ganesha.minimarket.ui.forms.role.RoleForm;
import com.ganesha.minimarket.ui.forms.role.RoleListDialog;
import com.ganesha.minimarket.ui.forms.sale.PenjualanForm;
import com.ganesha.minimarket.ui.forms.stock.StockForm;
import com.ganesha.minimarket.ui.forms.stock.StockListDialog;
import com.ganesha.minimarket.ui.forms.stockopname.StockOpnameConfirmationDialog;
import com.ganesha.minimarket.ui.forms.stockopname.StockOpnameListDialog;
import com.ganesha.minimarket.ui.forms.supplier.SupplierForm;
import com.ganesha.minimarket.ui.forms.supplier.SupplierListDialog;
import com.ganesha.minimarket.ui.forms.systemsetting.SystemSettingForm;
import com.ganesha.minimarket.ui.forms.user.UserForm;
import com.ganesha.minimarket.ui.forms.user.UserListDialog;
import com.ganesha.model.Permission;

public class PermissionConsistencyChecker {

	private List<Permission> permissions;

	public PermissionConsistencyChecker() {
		permissions = new ArrayList<>();

		// Administrasi - Role
		permissions.add(createPermission("/administrasi/role",
				"/Administrasi/Role", 210));
		permissions.add(createPermission(RoleListDialog.class,
				"Role List Dialog", 211));
		permissions.add(createPermission(RoleForm.class, "Role Form", 212));

		// Administrasi - User
		permissions.add(createPermission("/administrasi/user",
				"/Administrasi/User", 220));
		permissions.add(createPermission(UserListDialog.class,
				"User List Dialog", 221));
		permissions.add(createPermission(UserForm.class, "User Form", 222));

		// Master Date - Persediaan Barang
		permissions.add(createPermission("/master/stock",
				"/Master Data/Persediaan Barang", 310));
		permissions.add(createPermission(StockListDialog.class,
				"Stock List Dialog", 311));
		permissions.add(createPermission(StockForm.class, "Stock Form", 312));

		// Master Date - Supplier
		permissions.add(createPermission("/master/supplier",
				"/Master Data/Supplier", 320));
		permissions.add(createPermission(SupplierListDialog.class,
				"Supplier List Dialog", 321));
		permissions.add(createPermission(SupplierForm.class, "Supplier Form",
				322));

		// Master Date - Customer
		permissions.add(createPermission("/master/customer",
				"/Master Data/Customer", 330));
		permissions.add(createPermission(CustomerListDialog.class,
				"Customer List Dialog", 331));
		permissions.add(createPermission(CustomerForm.class, "Customer Form",
				332));

		// Master Date - Discount
		permissions.add(createPermission("/master/discount",
				"/Master Data/Discount", 340));
		permissions.add(createPermission(DiscountListDialog.class,
				"Discount List Dialog", 341));
		permissions.add(createPermission(DiscountForm.class, "Discount Form",
				342));

		// Transaction - Pembelian
		permissions.add(createPermission("/transaction/purchase",
				"/Transaksi/Pembelian", 410));
		permissions.add(createPermission(PembelianForm.class, "Pembelian Form",
				411));

		// Transaction - Retur Pembelian
		permissions.add(createPermission("/transaction/purchasereturn",
				"/Transaksi/Retur Pembelian", 420));
		permissions.add(createPermission(ReturPembelianForm.class,
				"Retur Pembelian Form", 421));

		// Transaction - Penjualan
		permissions.add(createPermission("/transaction/sale",
				"/Transaksi/Penjualan", 430));
		permissions.add(createPermission(PenjualanForm.class, "Penjualan Form",
				431));

		// Transaction - Retur Penjualan
		permissions.add(createPermission("/transaction/salereturn",
				"/Transaksi/Retur Penjualan", 440));
		permissions.add(createPermission(ReturPenjualanForm.class,
				"Retur Penjualan Form", 441));

		// Transaction - Payable
		permissions.add(createPermission("/transaction/payable",
				"/Transaksi/Hutang", 450));
		permissions
				.add(createPermission(PayableForm.class, "Payable Form", 451));

		// Transaction - Receivable
		permissions.add(createPermission("/transaction/receivable",
				"/Transaksi/Piutang", 460));
		permissions.add(createPermission(ReceivableForm.class,
				"Receivable Form", 461));

		// Back Office - Laporan - Laporan Transaksi
		permissions.add(createPermission("/backoffice/report/transaction",
				"/Back Office/Laporan/Laporan Transaksi", 510));
		permissions.add(createPermission(TransactionReportListDialog.class,
				"Transaction Report List Dialog", 511));

		// Back Office - Laporan - Laporan Stok Barang
		permissions.add(createPermission("/backoffice/report/stock",
				"/Back Office/Laporan/Laporan Stok Barang", 520));
		permissions.add(createPermission(ItemStockReportListDialog.class,
				"Item Stock Report List Dialog", 521));

		// Back Office - Laporan - Laporan Stok Opname
		permissions.add(createPermission("/backoffice/report/stockopname",
				"/Back Office/Laporan/Laporan Stok Opname", 530));
		permissions.add(createPermission(StockOpnameReportListDialog.class,
				"Stock Opname Report List Dialog", 531));

		permissions.add(createPermission(ReportViewerDialog.class,
				"Report Viewer Dialog", 539));

		// Back Office - Stock Opname
		permissions.add(createPermission("/backoffice/stockopname",
				"/Back Office/Stock Opname", 540));
		permissions.add(createPermission(StockOpnameListDialog.class,
				"Stock Opname List Dialog", 541));
		permissions.add(createPermission(StockOpnameConfirmationDialog.class,
				"Stock Opname Confirmation Dialog", 542));

		// Setting - Setting Aplikasi
		permissions.add(createPermission("/setting/systemsetting",
				"/Setting/Setting Aplikasi", 610));
		permissions.add(createPermission(SystemSettingForm.class,
				"System Setting Form", 611));

		// Setting - Receipt Printer Test
		permissions.add(createPermission("/setting/receipttest",
				"/Setting/Receipt Printer Test", 620));
	}

	public void initDB() {
		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			for (Permission permission : permissions) {

				boolean exists = GlobalFacade.getInstance().isExists("code",
						permission.getCode(), Permission.class, session);
				if (!exists) {
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

	private Permission createPermission(
			Class<? extends PermissionControl> clazz, String name, int orderNum) {
		return createPermission(clazz.getName(), name, orderNum);
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