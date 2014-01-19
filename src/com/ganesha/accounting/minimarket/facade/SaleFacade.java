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
import com.ganesha.accounting.minimarket.model.Customer;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.accounting.minimarket.model.SaleDetail;
import com.ganesha.accounting.minimarket.model.SaleHeader;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HqlParameter;

public class SaleFacade implements TransactionFacade {

	private static SaleFacade instance;

	public static SaleFacade getInstance() {
		if (instance == null) {
			instance = new SaleFacade();
		}
		return instance;
	}

	private SaleFacade() {
	}

	public SaleDetail getDetail(String transactionNumber, Integer orderNum,
			Session session) {
		Criteria criteria = session.createCriteria(SaleDetail.class);
		criteria.createAlias("saleHeader", "saleHeader");
		criteria.add(Restrictions.eq("saleHeader.transactionNumber",
				transactionNumber));
		criteria.add(Restrictions.eq("orderNum", orderNum));

		SaleDetail saleDetail = (SaleDetail) criteria.uniqueResult();
		return saleDetail;
	}

	public double getDiscountPercent(String itemCode, int quantity,
			Session session) {
		/*
		 * TODO
		 */
		return 5;
	}

	public void performSale(SaleHeader saleHeader,
			List<SaleDetail> saleDetails, Session session) throws AppException {

		StockFacade stockFacade = StockFacade.getInstance();
		session.save(saleHeader);

		for (SaleDetail saleDetail : saleDetails) {
			ItemStock itemStock = stockFacade.getDetail(
					saleDetail.getItemCode(), session);

			int stock = itemStock.getStock() - saleDetail.getQuantity();
			itemStock.setStock(stock);

			saleDetail.setSaleHeader(saleHeader);
			session.save(saleDetail);
			session.save(itemStock);
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
				+ ") FROM SaleDetail detail INNER JOIN detail.saleHeader header WHERE 1=1";

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

	public SaleHeader validateForm(String transactionNumber,
			Timestamp transactionTimestamp, String customerCode,
			Double subTotalAmount, Double taxPercent, Double taxAmount,
			Double totalAmount, Double pay, Double moneyChange, Session session)
			throws UserException {

		if (customerCode == null || customerCode.equals("")) {
			throw new UserException("Field Customer dibutuhkan");
		}

		SaleHeader header = new SaleHeader();

		Customer customer = CustomerFacade.getInstance().getDetail(
				customerCode, session);

		header.setTransactionNumber(transactionNumber);
		header.setTransactionTimestamp(transactionTimestamp);
		header.setCustomer(customer);
		header.setSubTotalAmount(BigDecimal.valueOf(subTotalAmount));
		header.setTaxPercent(BigDecimal.valueOf(taxPercent));
		header.setTaxAmount(BigDecimal.valueOf(taxAmount));
		header.setTotalAmount(BigDecimal.valueOf(totalAmount));
		header.setPay(BigDecimal.valueOf(pay));
		header.setMoneyChange(BigDecimal.valueOf(moneyChange));

		header.setDisabled(false);
		header.setDeleted(false);
		header.setLastUpdatedBy(Main.getUserLogin().getId());
		header.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		return header;
	}
}