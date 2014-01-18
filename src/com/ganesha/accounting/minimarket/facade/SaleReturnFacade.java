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
import com.ganesha.accounting.minimarket.model.SaleReturnDetail;
import com.ganesha.accounting.minimarket.model.SaleReturnHeader;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HqlParameter;

public class SaleReturnFacade implements TransactionFacade {

	private static SaleReturnFacade instance;

	public static SaleReturnFacade getInstance() {
		if (instance == null) {
			instance = new SaleReturnFacade();
		}
		return instance;
	}

	private SaleReturnFacade() {
	}

	public SaleReturnDetail getDetail(String transactionNumber,
			Integer orderNum, Session session) {
		Criteria criteria = session.createCriteria(SaleReturnDetail.class);
		criteria.createAlias("saleReturnHeader", "saleReturnHeader");
		criteria.add(Restrictions.eq("saleReturnHeader.transactionNumber",
				transactionNumber));
		criteria.add(Restrictions.eq("orderNum", orderNum));

		SaleReturnDetail saleReturnDetail = (SaleReturnDetail) criteria
				.uniqueResult();
		return saleReturnDetail;
	}

	public void performSaleReturn(SaleReturnHeader saleReturnHeader,
			List<SaleReturnDetail> saleReturnDetails, Session session)
			throws UserException, AppException {

		StockFacade stockFacade = StockFacade.getInstance();
		session.save(saleReturnHeader);

		for (SaleReturnDetail saleReturnDetail : saleReturnDetails) {
			ItemStock itemStock = stockFacade.getDetail(
					saleReturnDetail.getItemCode(), session);

			int stock = itemStock.getStock() + saleReturnDetail.getQuantity();
			itemStock.setStock(stock);

			BigDecimal lastPrice = saleReturnDetail.getPricePerUnit();
			itemStock.setBuyPrice(lastPrice);

			saleReturnDetail.setSaleReturnHeader(saleReturnHeader);
			session.save(saleReturnDetail);
			session.save(itemStock);

			session.save(saleReturnDetail);
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
				+ ") FROM SaleReturnDetail detail INNER JOIN detail.saleReturnHeader header WHERE 1=1";

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

	public SaleReturnHeader validateForm(String transactionNumber,
			Timestamp transactionTimestamp, String customerCode,
			Double subTotalAmount, Double taxPercent, Double taxAmount,
			Double totalReturnAmount, Session session) throws UserException {

		if (customerCode == null || customerCode.equals("")) {
			throw new UserException("Field Customer dibutuhkan");
		}

		SaleReturnHeader header = new SaleReturnHeader();

		Customer customer = CustomerFacade.getInstance().getDetail(
				customerCode, session);

		header.setTransactionNumber(transactionNumber);
		header.setTransactionTimestamp(transactionTimestamp);
		header.setCustomerId(customer.getId());
		header.setSubTotalAmount(BigDecimal.valueOf(subTotalAmount));
		header.setTaxPercent(BigDecimal.valueOf(taxPercent));
		header.setTaxAmount(BigDecimal.valueOf(taxAmount));
		header.setTotalReturnAmount(BigDecimal.valueOf(totalReturnAmount));
		header.setDisabled(false);
		header.setDeleted(false);
		header.setLastUpdatedBy(Main.getUserLogin().getId());
		header.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		return header;
	}
}
