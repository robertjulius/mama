package com.ganesha.accounting.minimarket.facade;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.model.SaleDetail;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.hibernate.HqlParameter;

public class SaleInquiryFacade implements TransactionInquiryFacade {

	private static SaleInquiryFacade instance;

	public static SaleInquiryFacade getInstance() {
		if (instance == null) {
			instance = new SaleInquiryFacade();
		}
		return instance;
	}

	private SaleInquiryFacade() {
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

	@Override
	public List<Map<String, Object>> searchTransaction(
			String transactionNumber, Date beginDate, Date endDate,
			Session session) {

		String sqlString = "SELECT new Map("
				+ "header.transactionNumber AS transactionNumber"
				+ ", header.transactionTimestamp AS transactionTimestamp"
				+ ", customer.code AS clientCode"
				+ ", customer.name AS clientName"
				+ ", header.totalAmount AS totalAmount"
				+ ") FROM SaleHeader header INNER JOIN header.customer customer WHERE 1=1";

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

	@Override
	public void showDetail(String transactionNumber) throws AppException,
			UserException {
		// TODO Auto-generated method stub
	}
}
