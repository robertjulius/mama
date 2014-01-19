package com.ganesha.accounting.minimarket.facade;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.model.PurchaseDetail;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.hibernate.HqlParameter;

public class PurchaseInquiryFacade implements TransactionInquiryFacade {

	private static PurchaseInquiryFacade instance;

	public static PurchaseInquiryFacade getInstance() {
		if (instance == null) {
			instance = new PurchaseInquiryFacade();
		}
		return instance;
	}

	private PurchaseInquiryFacade() {
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

	@Override
	public List<Map<String, Object>> searchTransaction(
			String transactionNumber, Date beginDate, Date endDate,
			Session session) {

		String sqlString = "SELECT new Map("
				+ "header.transactionNumber AS transactionNumber"
				+ ", header.transactionTimestamp AS transactionTimestamp"
				+ ", supplier.code AS clientCode"
				+ ", supplier.name AS clientName"
				+ ", header.totalAmount AS totalAmount"
				+ ") FROM PurchaseHeader header INNER JOIN header.supplier supplier WHERE 1=1";

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
