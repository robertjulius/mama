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
import com.ganesha.accounting.minimarket.model.PayableSummary;
import com.ganesha.accounting.minimarket.model.PurchaseReturnDetail;
import com.ganesha.accounting.minimarket.model.PurchaseReturnHeader;
import com.ganesha.accounting.minimarket.model.Supplier;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.GeneralConstants.AccountAction;
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
			List<PurchaseReturnDetail> purchaseReturnDetails, Session session)
			throws UserException, AppException {

		validatePayable(purchaseReturnHeader, session);
		validateReceivable(purchaseReturnHeader, session);

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

	private void addToPayable(PurchaseReturnHeader purchaseReturnHeader,
			Session session) throws AppException {
		int clientId = purchaseReturnHeader.getSupplierId();
		Date maturityDate = CommonUtils.getNextDate(1, Calendar.YEAR,
				CommonUtils.getCurrentDate());
		BigDecimal amount = purchaseReturnHeader.getDebtCut();
		String description = GeneralConstants.DECRIPTION_PAYABLE_PURCHASE_RETURN
				+ ": " + purchaseReturnHeader.getTransactionNumber();

		PayableFacade payableFacade = PayableFacade.getInstance();
		payableFacade.addTransaction(clientId, AccountAction.DECREASE,
				maturityDate, amount, description, session);
	}

	private void addToReceivable(PurchaseReturnHeader purchaseReturnHeader,
			Session session) throws AppException {
		int clientId = purchaseReturnHeader.getSupplierId();
		Date maturityDate = CommonUtils.getNextDate(1, Calendar.YEAR,
				CommonUtils.getCurrentDate());
		BigDecimal amount = purchaseReturnHeader.getRemainingReturnAmount();
		String description = GeneralConstants.DECRIPTION_RECEIVABLE_PURCHASE_RETURN
				+ ": " + purchaseReturnHeader.getTransactionNumber();

		ReceivableFacade receivableFacade = ReceivableFacade.getInstance();
		receivableFacade.addTransaction(clientId, AccountAction.INCREASE,
				maturityDate, amount, description, session);
	}

	private void validatePayable(PurchaseReturnHeader purchaseReturnHeader,
			Session session) throws UserException, AppException {

		double debtCut = purchaseReturnHeader.getDebtCut().doubleValue();
		if (debtCut <= 0) {
			return;
		}

		PayableSummary payableSummary = PayableFacade.getInstance().getSummary(
				purchaseReturnHeader.getSupplierId(), session);

		int supplierId = purchaseReturnHeader.getSupplierId();
		Supplier supplier = SupplierFacade.getInstance().getDetail(supplierId,
				session);

		if (payableSummary == null) {
			throw new UserException(
					"Tidak dapat memotong dari daftar hutang. Kita tidak memiliki hutang ke supplier "
							+ supplier.getName());
		}

		if (debtCut > payableSummary.getRemainingAmount().doubleValue()) {
			throw new UserException(
					"Tidak dapat memotong dari daftar hutang. Hutang kita ke supplier "
							+ supplier.getName() + " lebih kecil dari "
							+ debtCut);
		}

		addToPayable(purchaseReturnHeader, session);
	}

	private void validateReceivable(PurchaseReturnHeader purchaseReturnHeader,
			Session session) throws UserException, AppException {
		double remainingReturnAmount = purchaseReturnHeader
				.getRemainingReturnAmount().doubleValue();
		if (remainingReturnAmount <= 0) {
			return;
		}
		addToReceivable(purchaseReturnHeader, session);
	}
}
