package com.ganesha.accounting.minimarket.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.accounting.minimarket.model.PurchaseDetail;
import com.ganesha.accounting.minimarket.model.PurchaseHeader;
import com.ganesha.accounting.minimarket.model.Supplier;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.GeneralConstants.AccountAction;
import com.ganesha.hibernate.HqlParameter;

public class PurchaseFacade implements TransactionFacade {

	private static PurchaseFacade instance;

	public static PurchaseFacade getInstance() {
		if (instance == null) {
			instance = new PurchaseFacade();
		}
		return instance;
	}

	private PurchaseFacade() {
	}

	public PurchaseDetail getDetail(String transactionNumber, Integer orderNum,
			Session session) {
		Criteria criteria = session.createCriteria(PurchaseDetail.class);
		criteria.createAlias("purchaseHeader", "purchaseHeader");
		criteria.add(Restrictions.eq("purchaseHeader.transactionNumber",
				transactionNumber));
		criteria.add(Restrictions.eq("orderNum", orderNum));

		PurchaseDetail purchaseDetail = (PurchaseDetail) criteria
				.uniqueResult();
		return purchaseDetail;
	}

	public void performPurchase(PurchaseHeader purchaseHeader,
			List<PurchaseDetail> purchaseDetails, Session session)
			throws AppException {

		StockFacade stockFacade = StockFacade.getInstance();
		session.saveOrUpdate(purchaseHeader);

		for (PurchaseDetail purchaseDetail : purchaseDetails) {
			ItemStock itemStock = stockFacade.getDetail(
					purchaseDetail.getItemCode(), session);

			int stock = itemStock.getStock() + purchaseDetail.getQuantity();
			itemStock.setStock(stock);

			BigDecimal lastPrice = purchaseDetail.getPricePerUnit();
			itemStock.setBuyPrice(lastPrice);

			purchaseDetail.setPurchaseHeader(purchaseHeader);
			session.saveOrUpdate(purchaseDetail);
			session.saveOrUpdate(itemStock);
		}

		if (!purchaseHeader.getPaidInFullFlag()) {
			addToPayable(purchaseHeader, session);
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
				+ ", detail.totalAmount AS totalAmount"
				+ ") FROM PurchaseDetail detail INNER JOIN detail.purchaseHeader header WHERE 1=1";

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
		header.setSupplier(supplier);
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

	private void addToPayable(PurchaseHeader purchaseHeader, Session session)
			throws AppException {
		int clientId = purchaseHeader.getSupplier().getId();
		Date maturityDate = CommonUtils.getNextDate(1, Calendar.YEAR,
				CommonUtils.getCurrentDate());
		BigDecimal amount = purchaseHeader.getRemainingPayment();
		String description = GeneralConstants.DECRIPTION_PAYABLE_PURCHASE
				+ ": " + purchaseHeader.getTransactionNumber();

		PayableFacade payableFacade = PayableFacade.getInstance();
		payableFacade.addTransaction(clientId, AccountAction.INCREASE,
				maturityDate, amount, description, session);
	}
}
