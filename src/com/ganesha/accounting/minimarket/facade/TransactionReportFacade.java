package com.ganesha.accounting.minimarket.facade;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;

public interface TransactionReportFacade {

	public void printReport(String transactionNumber, Date beginDate,
			Date endDate, Session session) throws AppException, UserException;

	public List<Map<String, Object>> searchTransaction(
			String transactionNumber, Date beginDate, Date endDate,
			Session session) throws AppException, UserException;

	public void showDetail(String transactionNumber) throws AppException,
			UserException;
}