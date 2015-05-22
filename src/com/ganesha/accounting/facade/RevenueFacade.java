package com.ganesha.accounting.facade;

import java.awt.Window;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.ganesha.accounting.model.Coa;
import com.ganesha.accounting.model.RevenueTransaction;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.core.utils.StringUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.TransactionReportFacade;

public class RevenueFacade implements TransactionReportFacade {

	private static RevenueFacade instance;

	public static RevenueFacade getInstance() {
		if (instance == null) {
			instance = new RevenueFacade();
		}
		return instance;
	}

	private RevenueFacade() {
	}

	public RevenueTransaction performTransaction(Coa coa, BigDecimal amount,
			String notes, Session session) throws AppException {
		RevenueTransaction revenueTransaction = new RevenueTransaction();
		revenueTransaction.setCoa(coa);
		revenueTransaction.setAmount(amount);
		revenueTransaction.setNotes(notes);
		revenueTransaction.setLastUpdatedBy(Main.getUserLogin().getId());
		revenueTransaction.setLastUpdatedTimestamp(DateUtils
				.getCurrent(Timestamp.class));

		session.saveOrUpdate(revenueTransaction);

		AccountFacade.getInstance().handleRevenueTransaction(coa.getId(),
				revenueTransaction.getId(),
				StringUtils.properCase(coa.getName()),
				revenueTransaction.getAmount(), session);

		return revenueTransaction;
	}

	@Override
	public void previewReport(Window parent, String transactionNumber,
			Date beginDate, Date endDate, Session session) throws AppException,
			UserException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Map<String, Object>> searchTransaction(
			String transactionNumber, Date beginDate, Date endDate,
			Session session) throws AppException, UserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showDetail(String transactionNumber) throws AppException,
			UserException {
		// TODO Auto-generated method stub

	}
}
