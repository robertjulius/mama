package com.ganesha.accounting.minimarket.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.accounting.minimarket.model.PurchaseReturnDetail;
import com.ganesha.accounting.minimarket.model.PurchaseReturnHeader;
import com.ganesha.accounting.minimarket.model.Supplier;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HqlParameter;

public class PurchaseReturnFacade implements TransactionFacade {

	private static PurchaseReturnFacade instance;

	public static PurchaseReturnFacade getInstance() {
		if (instance == null) {
			instance = new PurchaseReturnFacade();
		}
		return instance;
	}

	private PurchaseReturnFacade() {
	}

	public PurchaseReturnDetail getDetail(String transactionNumber,
			Integer orderNum, Session session) {
		Criteria criteria = session.createCriteria(PurchaseReturnDetail.class);
		criteria.createAlias("purchaseReturnHeader", "purchaseReturnHeader");
		criteria.add(Restrictions.eq("purchaseReturnHeader.transactionNumber",
				transactionNumber));
		criteria.add(Restrictions.eq("orderNum", orderNum));

		PurchaseReturnDetail purchaseReturnDetail = (PurchaseReturnDetail) criteria
				.uniqueResult();
		return purchaseReturnDetail;
	}

	public void performPurchase(PurchaseReturnHeader purchaseReturnHeader,
			List<PurchaseReturnDetail> purchaseReturnDetails, Session session) {

		StockFacade stockFacade = StockFacade.getInstance();
		session.save(purchaseReturnHeader);

		for (PurchaseReturnDetail purchaseReturnDetail : purchaseReturnDetails) {
			ItemStock itemStock = stockFacade.getDetail(
					purchaseReturnDetail.getItemCode(), session);

			int stock = itemStock.getStock()
					+ purchaseReturnDetail.getQuantity();
			itemStock.setStock(stock);

			BigDecimal lastPrice = purchaseReturnDetail.getPricePerUnit();
			itemStock.setBuyPrice(lastPrice);

			purchaseReturnDetail.setPurchaseReturnHeader(purchaseReturnHeader);
			session.save(purchaseReturnDetail);
			session.save(itemStock);

			session.save(purchaseReturnDetail);
		}
	}

	@Override
	public List<Map<String, Object>> searchTransaction(
			String transactionNumber, Date beginDate, Date endDate,
			Session session) {

		String sqlString = "SELECT new Map("
				+ "header.transactionNumber AS transactionNumber"
				+ ", header.transactionTimestamp AS transactionTimestamp"
				+ ", detail.orderNum AS orderNum"
				+ ", detail.itemCode AS itemCode"
				+ ", detail.itemName AS itemName"
				+ ", detail.quantity AS quantity"
				+ ", detail.unit AS unit"
				+ ", detail.pricePerUnit AS pricePerUnit"
				+ ", detail.totalPrice AS totalPrice"
				+ ") FROM PurchaseReturnDetail detail INNER JOIN detail.purchaseReturnHeader header WHERE 1=1";

		if (!transactionNumber.trim().equals("")) {
			sqlString += " AND header.transactionNumber LIKE :transactionNumber";
		}

		if (beginDate != null) {
			sqlString += " AND header.transactionTimestamp >= :beginDate";
		}

		if (endDate != null) {
			sqlString += " AND header.transactionTimestamp <= :endDate";
		}

		Query query = session.createQuery(sqlString);
		HqlParameter parameter = new HqlParameter(query);
		parameter.put("transactionNumber", "%" + transactionNumber + "%");
		parameter.put("beginDate", beginDate);
		parameter.put("endDate", endDate);
		parameter.validate();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = query.list();

		return list;

	}

	public PurchaseReturnHeader validateForm(String transactionNumber,
			Timestamp transactionTimestamp, String supplierCode,
			Double subTotalAmount, Double expenses, Double discountReturned,
			Double totalReturnAmount, Double amountReturned, Double debtCut,
			Double remainingReturnAmount, Boolean returnedInFullFlag,
			Session session) throws UserException {

		if (supplierCode == null || supplierCode.equals("")) {
			throw new UserException("Field Supplier dibutuhkan");
		}

		PurchaseReturnHeader header = new PurchaseReturnHeader();

		Supplier supplier = SupplierFacade.getInstance().getDetail(
				supplierCode, session);

		header.setTransactionNumber(transactionNumber);
		header.setTransactionTimestamp(transactionTimestamp);
		header.setSupplierId(supplier.getId());
		header.setSubTotalAmount(BigDecimal.valueOf(subTotalAmount));
		header.setExpenses(BigDecimal.valueOf(expenses));
		header.setDiscountReturned(BigDecimal.valueOf(discountReturned));
		header.setTotalReturnAmount(BigDecimal.valueOf(totalReturnAmount));
		header.setAmountReturned(BigDecimal.valueOf(amountReturned));
		header.setDebtCut(BigDecimal.valueOf(debtCut));
		header.setRemainingReturnAmount(BigDecimal
				.valueOf(remainingReturnAmount));
		header.setReturnedInFullFlag(returnedInFullFlag);

		header.setDisabled(false);
		header.setDeleted(false);
		header.setLastUpdatedBy(Main.getUserLogin().getId());
		header.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		return header;
	}
}
