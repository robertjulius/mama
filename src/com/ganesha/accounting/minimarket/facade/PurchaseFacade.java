package com.ganesha.accounting.minimarket.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Session;

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.accounting.minimarket.model.PurchaseDetail;
import com.ganesha.accounting.minimarket.model.PurchaseHeader;
import com.ganesha.accounting.minimarket.model.Supplier;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;

public class PurchaseFacade {

	private static PurchaseFacade instance;

	public static PurchaseFacade getInstance() {
		if (instance == null) {
			instance = new PurchaseFacade();
		}
		return instance;
	}

	private PurchaseFacade() {
	}

	public void performPurchase(PurchaseHeader purchaseHeader,
			List<PurchaseDetail> purchaseDetails, Session session) {

		StockFacade stockFacade = StockFacade.getInstance();
		session.save(purchaseHeader);

		for (PurchaseDetail purchaseDetail : purchaseDetails) {
			ItemStock itemStock = stockFacade.getDetail(
					purchaseDetail.getItemCode(), session);

			int stock = itemStock.getStock() + purchaseDetail.getQuantity();
			itemStock.setStock(stock);

			BigDecimal lastPrice = purchaseDetail.getPricePerUnit();
			itemStock.setBuyPrice(lastPrice);

			purchaseDetail.setPurchaseHeader(purchaseHeader);
			session.save(purchaseDetail);
			session.save(itemStock);

			session.save(purchaseDetail);
		}
	}

	public PurchaseHeader validateForm(String transactionNumber,
			Timestamp transactionTimestamp, String supplierCode,
			Double subTotalAmount, Double expenses, Double discount,
			Double totalAmount, Double advancePayment, Double remainingPayment,
			Boolean paidInFullFlag, Session session) throws UserException {

		if (supplierCode == null || supplierCode.equals("")) {
			throw new UserException("Field Supplier dibutuhkan");
		}

		PurchaseHeader header = new PurchaseHeader();

		Supplier supplier = SupplierFacade.getInstance().getDetail(
				supplierCode, session);

		header.setTransactionNumber(transactionNumber);
		header.setTransactionTimestamp(transactionTimestamp);
		header.setSupplierId(supplier.getId());
		header.setSubTotalAmount(BigDecimal.valueOf(subTotalAmount));
		header.setExpenses(BigDecimal.valueOf(expenses));
		header.setDiscount(BigDecimal.valueOf(discount));
		header.setTotalAmount(BigDecimal.valueOf(totalAmount));
		header.setAdvancePayment(BigDecimal.valueOf(advancePayment));
		header.setRemainingPayment(BigDecimal.valueOf(remainingPayment));
		header.setPaidInFullFlag(paidInFullFlag);

		header.setDisabled(false);
		header.setDeleted(false);
		header.setLastUpdatedBy(Main.getUserLogin().getId());
		header.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		return header;
	}
}
