package com.ganesha.minimarket.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.constants.CoaCodeConstants;
import com.ganesha.accounting.constants.Enums.DebitCreditFlag;
import com.ganesha.accounting.facade.AccountFacade;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Customer;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.SaleReturnDetail;
import com.ganesha.minimarket.model.SaleReturnHeader;

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

		ItemFacade itemFacade = ItemFacade.getInstance();
		session.saveOrUpdate(saleReturnHeader);

		for (SaleReturnDetail saleReturnDetail : saleReturnDetails) {

			saleReturnDetail.setSaleReturnHeader(saleReturnHeader);
			session.saveOrUpdate(saleReturnDetail);

			Item item = itemFacade.getDetail(saleReturnDetail.getSaleDetail()
					.getItemId(), session);

			int stockBeforeReturn = itemFacade.calculateStock(item);
			int stockAfterReturn = stockBeforeReturn
					+ saleReturnDetail.getQuantity();
			int maxStock = itemFacade.calculateMaxStock(item);
			if (stockAfterReturn > maxStock) {
				throw new UserException("Tidak dapat melakukan retur barang "
						+ saleReturnDetail.getSaleDetail().getItemCode()
						+ " sebanyak " + saleReturnDetail.getQuantity() + " "
						+ item.getUnit() + ". Stock saat ini ada sebanyak "
						+ stockBeforeReturn
						+ ". Total modal untuk barang ini hanya sebanyak "
						+ maxStock + " " + item.getUnit());
			}
			itemFacade.reAdjustStock(item, stockAfterReturn, session);

			AccountFacade.getInstance().insertIntoAccount(
					CoaCodeConstants.RETUR_PENJUALAN, saleReturnDetail.getId(),
					CommonUtils.getCurrentTimestamp(), "Retur Penjualan", "",
					DebitCreditFlag.DEBIT, saleReturnDetail.getTotalAmount(),
					session);
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
			Timestamp transactionTimestamp, Integer customerId,
			Double subTotalAmount, Double taxPercent, Double taxAmount,
			Double totalReturnAmount, Session session) throws UserException {

		if (customerId == null) {
			throw new UserException("Field Customer dibutuhkan");
		}

		SaleReturnHeader header = new SaleReturnHeader();

		Customer customer = CustomerFacade.getInstance().getDetail(customerId,
				session);

		header.setTransactionNumber(transactionNumber);
		header.setTransactionTimestamp(transactionTimestamp);
		header.setCustomer(customer);
		header.setSubTotalAmount(BigDecimal.valueOf(subTotalAmount));
		header.setTaxPercent(BigDecimal.valueOf(taxPercent));
		header.setTaxAmount(BigDecimal.valueOf(taxAmount));
		header.setTotalReturnAmount(BigDecimal.valueOf(totalReturnAmount));
		header.setLastUpdatedBy(Main.getUserLogin().getId());
		header.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		return header;
	}
}
