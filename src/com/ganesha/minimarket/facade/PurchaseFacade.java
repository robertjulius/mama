package com.ganesha.minimarket.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.ganesha.accounting.constants.CoaCodeConstants;
import com.ganesha.accounting.constants.Enums.AccountAction;
import com.ganesha.accounting.constants.Enums.DebitCreditFlag;
import com.ganesha.accounting.facade.AccountFacade;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemStock;
import com.ganesha.minimarket.model.PurchaseDetail;
import com.ganesha.minimarket.model.PurchaseHeader;
import com.ganesha.minimarket.model.Supplier;

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

	public PurchaseDetail getDetail(Integer id, Session session) {
		PurchaseDetail purchaseDetail = (PurchaseDetail) session.get(
				PurchaseDetail.class, id);
		return purchaseDetail;
	}

	public void performPurchase(PurchaseHeader purchaseHeader,
			List<PurchaseDetail> purchaseDetails, Session session)
			throws AppException {

		ItemFacade itemFacade = ItemFacade.getInstance();
		session.saveOrUpdate(purchaseHeader);

		for (PurchaseDetail purchaseDetail : purchaseDetails) {

			purchaseDetail.setPurchaseHeader(purchaseHeader);
			session.saveOrUpdate(purchaseDetail);

			Item item = itemFacade.getDetail(purchaseDetail.getItemId(),
					session);
			ItemStock itemStock = new ItemStock();
			itemStock.setItem(item);
			itemStock.setPurchaseDetail(purchaseDetail);
			itemStock.setQuantity(purchaseDetail.getQuantity());
			session.saveOrUpdate(itemStock);
		}

		Timestamp currentTimestamp = CommonUtils.getCurrentTimestamp();

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.PEMBELIAN, purchaseHeader.getId(),
				currentTimestamp, "Pembelian", "", DebitCreditFlag.DEBIT,
				purchaseHeader.getTotalAmount(), session);

		if (purchaseHeader.getExpenses().doubleValue() != 0) {
			AccountFacade.getInstance().insertIntoAccount(
					CoaCodeConstants.BEBAN_PEMBELIAN_LAIN_LAIN,
					purchaseHeader.getId(), currentTimestamp,
					"Beban Pembelian Lain-Lain", "", DebitCreditFlag.DEBIT,
					purchaseHeader.getExpenses(), session);
		}

		if (purchaseHeader.getDiscount().doubleValue() != 0) {
			AccountFacade.getInstance().insertIntoAccount(
					CoaCodeConstants.DISKON_PEMBELIAN, purchaseHeader.getId(),
					currentTimestamp, "Diskon Pembelian", "",
					DebitCreditFlag.CREDIT, purchaseHeader.getDiscount(),
					session);
		}

		if (!purchaseHeader.getPaidInFullFlag()) {
			addToPayable(purchaseHeader, session);

			AccountFacade.getInstance().insertIntoAccount(
					CoaCodeConstants.HUTANG_USAHA, purchaseHeader.getId(),
					currentTimestamp, "Hutang Pembelian", "",
					DebitCreditFlag.CREDIT,
					purchaseHeader.getRemainingPayment(), session);
		}
	}

	@Override
	public List<Map<String, Object>> searchTransaction(
			String transactionNumber, Date beginDate, Date endDate,
			Session session) {

		String sqlString = "SELECT new Map("
				+ "header.transactionNumber AS transactionNumber"
				+ ", header.transactionTimestamp AS transactionTimestamp"
				+ ", detail.id AS transactionDetailId"
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
			Timestamp transactionTimestamp, Integer supplierId,
			Double subTotalAmount, Double expenses, Double discount,
			Double totalAmount, Double advancePayment, Double remainingPayment,
			Boolean paidInFullFlag, Session session) throws UserException {

		if (supplierId == null) {
			throw new UserException("Field Supplier dibutuhkan");
		}

		PurchaseHeader header = new PurchaseHeader();

		Supplier supplier = SupplierFacade.getInstance().getDetail(supplierId,
				session);

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
