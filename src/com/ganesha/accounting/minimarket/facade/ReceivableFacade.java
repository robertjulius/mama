package com.ganesha.accounting.minimarket.facade;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.model.ReceivableSummary;
import com.ganesha.accounting.minimarket.model.ReceivableTransaction;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.GeneralConstants.AccountAction;

public class ReceivableFacade {

	private static ReceivableFacade instance;

	public static ReceivableFacade getInstance() {
		if (instance == null) {
			instance = new ReceivableFacade();
		}
		return instance;
	}

	private ReceivableFacade() {
	}

	public void addSummary(int clientId, Session session) {
		ReceivableSummary receivableSummary = new ReceivableSummary();
		receivableSummary.setClientId(clientId);
		receivableSummary.setRemainingAmount(BigDecimal.valueOf(0));
		receivableSummary.setLastReceivableTransactionId(-1);
		receivableSummary.setDisabled(false);
		receivableSummary.setDeleted(false);
		receivableSummary.setLastUpdatedBy(Main.getUserLogin().getId());
		receivableSummary.setLastUpdatedTimestamp(CommonUtils
				.getCurrentTimestamp());

		session.save(receivableSummary);
	}

	public void addTransaction(int clientId, AccountAction accountAction,
			Date maturityDate, BigDecimal amount, String description,
			Session session) throws AppException {

		boolean summaryExists = GlobalFacade.getInstance().isExists("clientId",
				clientId, ReceivableSummary.class, session);

		if (!summaryExists) {
			addSummary(clientId, session);
		}

		ReceivableSummary receivableSummary = getSummary(clientId, session);

		ReceivableTransaction receivableTransaction = new ReceivableTransaction();
		receivableTransaction.setReceivableSummary(receivableSummary);
		receivableTransaction
				.setReffNumber(GeneralConstants.PREFIX_TRX_NUMBER_RECEIVABLE
						+ CommonUtils.getTimestampInString());
		receivableTransaction.setAccountAction(accountAction.name());
		receivableTransaction.setActionTimestamp(CommonUtils
				.getCurrentTimestamp());
		receivableTransaction.setMaturityDate(maturityDate);
		receivableTransaction.setAmount(amount);
		receivableTransaction.setDescription(description);
		receivableTransaction.setLastUpdatedBy(Main.getUserLogin().getId());
		receivableTransaction.setLastUpdatedTimestamp(CommonUtils
				.getCurrentTimestamp());

		session.save(receivableTransaction);

		if (accountAction == AccountAction.DECREASE) {

			BigDecimal lastRemainingAmount = receivableSummary
					.getRemainingAmount();
			lastRemainingAmount = lastRemainingAmount
					.subtract(receivableTransaction.getAmount());
			receivableSummary.setRemainingAmount(lastRemainingAmount);

		} else if (accountAction == AccountAction.INCREASE) {

			BigDecimal lastRemainingAmount = receivableSummary
					.getRemainingAmount();
			lastRemainingAmount = lastRemainingAmount.add(receivableTransaction
					.getAmount());
			receivableSummary.setRemainingAmount(lastRemainingAmount);

		} else {
			throw new AppException("Unknown AccountAction " + accountAction);
		}

		receivableSummary.setLastReceivableTransactionId(receivableTransaction
				.getId());
		session.save(receivableTransaction);
	}

	public ReceivableSummary getSummary(int clientId, Session session) {
		Criteria criteria = session.createCriteria(ReceivableSummary.class);
		criteria.add(Restrictions.eq("clientId", clientId));
		ReceivableSummary receivableSummary = (ReceivableSummary) criteria
				.uniqueResult();
		return receivableSummary;
	}
}
