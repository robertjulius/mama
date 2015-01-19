package com.ganesha.prepaid.ui.forms;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.constants.Loggers;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.constants.Enums.SaleConstraintPostingStatus;
import com.ganesha.minimarket.facade.CustomerFacade;
import com.ganesha.minimarket.facade.ItemFacade;
import com.ganesha.minimarket.facade.SaleConstraintFacade;
import com.ganesha.minimarket.facade.SaleFacade;
import com.ganesha.minimarket.model.Customer;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.SaleConstraintDetail;
import com.ganesha.minimarket.model.SaleConstraintHeader;
import com.ganesha.minimarket.model.SaleDetail;
import com.ganesha.minimarket.model.SaleHeader;
import com.ganesha.minimarket.ui.forms.searchentity.SearchEntityDialog;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.prepaid.facade.PrepaidSaleFacade;
import com.ganesha.prepaid.facade.VoucherFacade;
import com.ganesha.prepaid.facade.VoucherTypeFacade;
import com.ganesha.prepaid.model.Voucher;
import com.ganesha.prepaid.model.VoucherType;

public class PrepaidSaleForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJPanel pnlAuth;
	private XJButton btnBatal;
	private XJComboBox cmbVoucherTypes;
	private XJPanel pnlBackupDB;
	private XJLabel lblPrice;
	private XJTextField txtPrice;
	private XJLabel lblPay;
	private XJTextField txtPay;
	private XJLabel lblChange;
	private XJTextField txtChange;
	private XJLabel lblCreditStock;
	private XJLabel lblPackageName;
	private XJComboBox cmbPackageName;
	private XJLabel lblCustomer;
	private XJTextField txtCustomer;
	private XJButton btnSearchCustomer;

	private Integer customerId;
	private String transactionNumber = GeneralConstants.PREFIX_TRX_NUMBER_SALES
			+ CommonUtils.getTimestampInString();
	private XJPanel panel;
	private XJLabel lblCreditStockValue;
	private XJLabel lblQuantity;
	private XJLabel lblQuantityValue;

	public PrepaidSaleForm(Window parent) {
		super(parent);
		setTitle("Penjualan Voucher Pulsa");
		setPermissionCode(PermissionConstants.SALE_PREPAID_FORM);
		setCloseOnEsc(false);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][]"));

		pnlAuth = new XJPanel();
		pnlAuth.setBorder(new XEtchedBorder());
		getContentPane().add(pnlAuth, "cell 0 0,grow");
		pnlAuth.setLayout(new MigLayout("", "[200][300,grow][grow]",
				"[][][][grow]"));

		lblCustomer = new XJLabel();
		lblCustomer.setText("Customer");
		pnlAuth.add(lblCustomer, "cell 0 0");

		txtCustomer = new XJTextField();
		txtCustomer.setEditable(false);
		pnlAuth.add(txtCustomer, "cell 1 0,growx");

		btnSearchCustomer = new XJButton();
		btnSearchCustomer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cariCustomer();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrepaidSaleForm.this, ex);
				}
			}
		});
		btnSearchCustomer.setText("Cari Customer [F5]");
		pnlAuth.add(btnSearchCustomer, "cell 2 0");

		XJLabel lblVoucherType = new XJLabel();
		pnlAuth.add(lblVoucherType, "cell 0 1");
		lblVoucherType.setText("Tipe Voucher");

		cmbVoucherTypes = new XJComboBox();
		cmbVoucherTypes.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					cmbVoucherTypesSelected();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrepaidSaleForm.this, ex);
				}
			}
		});
		pnlAuth.add(cmbVoucherTypes, "cell 1 1,growx");

		lblPackageName = new XJLabel();
		lblPackageName.setText("Nama Paket");
		pnlAuth.add(lblPackageName, "cell 0 2");

		cmbPackageName = new XJComboBox();
		cmbPackageName.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					cmbPackageNameSelected();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrepaidSaleForm.this, ex);
				}
			}
		});
		pnlAuth.add(cmbPackageName, "cell 1 2,growx");

		panel = new XJPanel();
		pnlAuth.add(panel, "cell 1 3,alignx right,growy");
		panel.setLayout(new MigLayout("", "[][]", "[][]"));

		lblQuantity = new XJLabel();
		lblQuantity.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblQuantity.setText("Quantity (Modal): Rp");
		panel.add(lblQuantity, "cell 0 0,alignx trailing");
		lblQuantity.setVisible(false);

		lblQuantityValue = new XJLabel("-");
		lblQuantityValue.setFont(new Font("Tahoma", Font.BOLD,
				FONT_SIZE_SMALLEST));
		panel.add(lblQuantityValue, "cell 1 0,alignx trailing");
		lblQuantityValue.setVisible(false);

		lblCreditStock = new XJLabel();
		panel.add(lblCreditStock, "cell 0 1,alignx trailing");
		lblCreditStock
				.setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_SMALLEST));
		lblCreditStock.setText("Sisa Stok Pulsa: Rp");

		lblCreditStockValue = new XJLabel("-");
		lblCreditStockValue.setFont(new Font("Tahoma", Font.BOLD,
				FONT_SIZE_SMALLEST));
		panel.add(lblCreditStockValue, "cell 1 1,alignx trailing");

		pnlBackupDB = new XJPanel();
		pnlBackupDB.setBackground(Color.BLACK);
		pnlBackupDB.setBorder(new XEtchedBorder());
		getContentPane().add(pnlBackupDB, "cell 0 1,grow");
		pnlBackupDB.setLayout(new MigLayout("", "[300][grow]", "[][][]"));

		lblPrice = new XJLabel();
		lblPrice.setForeground(Color.WHITE);
		lblPrice.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblPrice.setText("Harga [F10]");
		pnlBackupDB.add(lblPrice, "cell 0 0");

		txtPrice = new XJTextField();
		txtPrice.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		txtPrice.setFont(new Font("Tahoma", Font.BOLD, 40));
		txtPrice.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlBackupDB.add(txtPrice, "cell 1 0,growx");

		lblPay = new XJLabel();
		lblPay.setForeground(Color.WHITE);
		lblPay.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblPay.setText("Bayar [F11]");
		pnlBackupDB.add(lblPay, "cell 0 1");

		txtPay = new XJTextField();
		txtPay.setFont(new Font("Tahoma", Font.BOLD, 40));
		txtPay.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					calculate();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrepaidSaleForm.this, ex);
				}
			}
		});
		txtPay.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		txtPay.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlBackupDB.add(txtPay, "cell 1 1,growx");

		lblChange = new XJLabel();
		lblChange.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblChange.setForeground(Color.WHITE);
		lblChange.setText("Kembalian");
		pnlBackupDB.add(lblChange, "cell 0 2");

		txtChange = new XJTextField();
		txtChange.setEditable(false);
		txtChange.setBackground(Color.PINK);
		txtChange.setFont(new Font("Tahoma", Font.BOLD, 40));
		txtChange.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlBackupDB.add(txtChange, "cell 1 2,growx");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.setEnabled(false);
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					selesaiDanSimpan();
				} catch (Exception ex) {
					ExceptionHandler.handleException(PrepaidSaleForm.this, ex);
				}
			}
		});

		btnBatal = new XJButton();
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBatal.setMnemonic('Q');
		btnBatal.setText("<html><center>Batal<br/>[Alt+Q]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0");
		btnSimpan
				.setText("<html><center>Selesai & Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 1 0");

		loadComboVoucherTypes();
		cmbVoucherTypesSelected();
		cmbPackageNameSelected();

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnSearchCustomer.doClick();
			break;
		case KeyEvent.VK_F10:
			setFocusToFieldHarga();
			break;
		case KeyEvent.VK_F11:
			setFocusToFieldBayar();
			break;
		case KeyEvent.VK_F12:
			btnSimpan.doClick();
			break;
		default:
			break;
		}
	}

	private void batal() {
		dispose();
	}

	private void calculate() {
		double price = Formatter.formatStringToNumber(txtPrice.getText())
				.doubleValue();
		double pay = Formatter.formatStringToNumber(txtPay.getText())
				.doubleValue();

		double change = 0;
		if (pay < price) {
			btnSimpan.setEnabled(false);
		} else {
			change = pay - price;
			btnSimpan.setEnabled(true);
		}
		txtChange.setText(Formatter.formatNumberToString(change));
	}

	private void cariCustomer() {
		SearchEntityDialog searchEntityDialog = new SearchEntityDialog(
				"Cari Customer", this, Customer.class);
		searchEntityDialog.setVisible(true);

		customerId = searchEntityDialog.getSelectedId();
		if (customerId != null) {
			String nama = searchEntityDialog.getSelectedName();
			txtCustomer.setText(nama);
		}
	}

	private void cmbPackageNameSelected() {
		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbPackageName
				.getSelectedItem();
		if (comboBoxObject == null) {
			txtPrice.setText("");
			txtPay.setText("");
			lblQuantityValue.setText("0");
			return;
		}

		Voucher voucher = (Voucher) comboBoxObject.getObject();
		if (voucher == null) {
			txtPrice.setText("");
			txtPay.setText("");
			lblQuantityValue.setText("0");
			return;
		}
		lblQuantityValue.setText(Formatter.formatNumberToString(voucher
				.getQuantity()));
		txtPrice.setText(Formatter.formatNumberToString(voucher.getPrice()));
	}

	private void cmbVoucherTypesSelected() {
		loadComboNominal();

		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbVoucherTypes
				.getSelectedItem();
		if (comboBoxObject == null) {
			return;
		}

		VoucherType voucher = (VoucherType) comboBoxObject.getObject();
		if (voucher == null) {
			lblCreditStock.setVisible(false);
			lblCreditStockValue.setVisible(false);
			return;
		} else {
			lblCreditStock.setVisible(true);
			lblCreditStockValue.setVisible(true);
		}

		Integer itemId = voucher.getItem().getId();
		Session session = HibernateUtils.openSession();
		try {
			Item item = ItemFacade.getInstance().getDetail(itemId, session);
			Integer stock = ItemFacade.getInstance().calculateStock(item);
			lblCreditStockValue.setText(Formatter.formatNumberToString(stock));
		} finally {
			session.close();
		}
	}

	private void loadComboNominal() {
		VoucherType selectedVoucherType = (VoucherType) ((ComboBoxObject) cmbVoucherTypes
				.getSelectedItem()).getObject();
		if (selectedVoucherType == null) {
			cmbPackageName.removeAllItems();
			return;
		}

		Session session = HibernateUtils.openSession();
		try {
			List<ComboBoxObject> comboBoxObjects = new ArrayList<ComboBoxObject>();
			List<Voucher> vouchers = VoucherFacade.getInstance()
					.getAllVoucherByType(selectedVoucherType, session);
			for (Voucher voucher : vouchers) {
				ComboBoxObject comboBoxObject = new ComboBoxObject(voucher,
						voucher.getPackageName());
				comboBoxObjects.add(comboBoxObject);
			}
			cmbPackageName.setModel(new DefaultComboBoxModel<ComboBoxObject>(
					comboBoxObjects.toArray(new ComboBoxObject[] {})));
		} finally {
			session.close();
		}
	}

	private void loadComboVoucherTypes() {
		Session session = HibernateUtils.openSession();
		try {
			List<ComboBoxObject> comboBoxObjects = new ArrayList<ComboBoxObject>();
			List<VoucherType> voucherTypes = VoucherTypeFacade.getInstance()
					.getAll(session);
			for (VoucherType voucherType : voucherTypes) {
				ComboBoxObject comboBoxObject = new ComboBoxObject(voucherType,
						voucherType.getName());
				comboBoxObjects.add(comboBoxObject);
			}
			cmbVoucherTypes.setModel(new DefaultComboBoxModel<ComboBoxObject>(
					comboBoxObjects.toArray(new ComboBoxObject[] {})));
		} finally {
			session.close();
		}
	}

	private void selesaiDanSimpan() throws UserException, AppException {

		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			if (customerId == null) {
				throw new UserException("Field Customer dibutuhkan");
			}

			Customer customer = CustomerFacade.getInstance().getDetail(
					customerId, session);

			ComboBoxObject comboBoxObject = (ComboBoxObject) cmbPackageName
					.getSelectedItem();
			Voucher voucher = (Voucher) comboBoxObject.getObject();
			Item item = voucher.getVoucherType().getItem();

			List<SaleDetail> saleDetails = PrepaidSaleFacade.getInstance()
					.prepareSaleDetails(
							customer,
							transactionNumber,
							BigDecimal.valueOf(Formatter.formatStringToNumber(
									txtPrice.getText()).doubleValue()),
							BigDecimal.valueOf(Formatter.formatStringToNumber(
									txtPay.getText()).doubleValue()),
							BigDecimal.valueOf(Formatter.formatStringToNumber(
									txtChange.getText()).doubleValue()),
							item.getId(), item.getCode(), item.getName(),
							voucher.getQuantity(), item.getUnit(), session);

			SaleHeader saleHeader = saleDetails.get(0).getSaleHeader();

			performSale(saleHeader, saleDetails, session);
			session.getTransaction().commit();

			try {
				SaleFacade.getInstance().cetakReceipt(saleHeader, saleDetails);
			} catch (PrintException ex) {
				ExceptionHandler.handleException(this, ex);
			}

			dispose();
			LoggerFactory.getLogger(Loggers.SALE).debug(
					"Sale Specific Prepaid finished. The window is closed.");
		} finally {
			session.close();
		}
	}

	private void setFocusToFieldBayar() {
		txtPay.selectAll();
		txtPay.requestFocus();
	}

	private void setFocusToFieldHarga() {
		txtPrice.selectAll();
		txtPrice.requestFocus();
	}

	private void validateForm() throws UserException {

		int quantity = Formatter.formatStringToNumber(
				lblQuantityValue.getText()).intValue();

		double price = Formatter.formatStringToNumber(txtPrice.getText())
				.doubleValue();

		if (price < quantity) {
			throw new UserException(
					"Harga jual tidak boleh di bawah harga modal");
		}
	}

	protected void performSale(SaleHeader saleHeader,
			List<SaleDetail> saleDetails, Session session) throws AppException,
			UserException {

		try {
			performSaleNormal(saleHeader, saleDetails, session);
		} catch (UserException e) {
			if (e.getMessage().contains(
					"Tidak dapat melakukan penjualan barang")
					&& e.getMessage().contains(
							"karena stock di sistem hanya ada")) {
				performSaleConstraint(saleHeader, saleDetails, session);
			} else {
				throw e;
			}
		}

		ActivityLogFacade.doLog(getPermissionCode(), ActionType.TRANSACTION,
				Main.getUserLogin(), saleHeader, session);

		LoggerFactory.getLogger(Loggers.SALE).debug(
				"Logging to ActivityLog is done");
	}

	private void performSaleConstraint(SaleHeader saleHeader,
			List<SaleDetail> saleDetails, Session session) throws AppException {

		SaleConstraintHeader saleConstraintHeader = SaleConstraintHeader
				.fromSaleHeader(saleHeader);
		saleConstraintHeader
				.setPostingStatus(SaleConstraintPostingStatus.WAITING);
		saleConstraintHeader.setPostingTriedCount(0);

		List<SaleConstraintDetail> saleConstraintDetails = new ArrayList<>();
		for (SaleDetail saleDetail : saleDetails) {
			SaleConstraintDetail saleConstraintDetail = SaleConstraintDetail
					.fromSaleDetail(saleDetail);
			saleConstraintDetails.add(saleConstraintDetail);
		}

		LoggerFactory.getLogger(Loggers.SALE).debug(
				"Starting to insert SaleConstraintHeader {"
						+ saleConstraintHeader.getTransactionNumber() + "|"
						+ saleConstraintHeader.getSubTotalAmount() + "|"
						+ saleConstraintHeader.getTaxAmount() + "|"
						+ saleConstraintHeader.getTotalAmount() + "|"
						+ saleConstraintHeader.getPay() + "|"
						+ saleConstraintHeader.getMoneyChange()
						+ "} into database");

		SaleConstraintFacade.getInstance().performSale(saleConstraintHeader,
				saleConstraintDetails, session);

		LoggerFactory.getLogger(Loggers.SALE).debug(
				"Finished inserting SaleConstraintHeader into database, the generated id is "
						+ saleHeader.getId());
	}

	private void performSaleNormal(SaleHeader saleHeader,
			List<SaleDetail> saleDetails, Session session) throws AppException,
			UserException {

		LoggerFactory.getLogger(Loggers.SALE).debug(
				"Starting to insert SaleHeader {"
						+ saleHeader.getTransactionNumber() + "|"
						+ saleHeader.getSubTotalAmount() + "|"
						+ saleHeader.getTaxAmount() + "|"
						+ saleHeader.getTotalAmount() + "|"
						+ saleHeader.getPay() + "|"
						+ saleHeader.getMoneyChange() + "} into database");

		SaleFacade.getInstance().performSale(saleHeader, saleDetails, session);

		LoggerFactory.getLogger(Loggers.SALE).debug(
				"Finished inserting SaleHeader into database, the generated id is "
						+ saleHeader.getId());
	}
}
