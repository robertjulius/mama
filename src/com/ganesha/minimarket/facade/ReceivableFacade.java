package com.ganesha.minimarket.facade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.constants.Enums.AccountAction;
import com.ganesha.accounting.facade.AccountFacade;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.ReceivableSummary;
import com.ganesha.minimarket.model.ReceivableTransaction;

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

	public ReceivableSummary addReceivableForPurchaseReturn(int clientId,
			Date maturityDate, BigDecimal amount, String description,
			Session session) throws AppException {

		boolean summaryExists = DBUtils.getInstance().isExists("clientId",
				clientId, ReceivableSummary.class, session);

		if (!summaryExists) {
			addSummary(clientId, session);
		}

		ReceivableSummary receivableSummary = getSummary(clientId, session);

		ReceivableTransaction receivableTransaction = new ReceivableTransaction();
		receivableTransaction.setReceivableSummary(receivableSummary);
		receivableTransaction
				.setReffNumber(GeneralConstants.PREFIX_TRX_NUMBER_RECEIVABLE
						+ DateUtils.getTimestampInString());
		receivableTransaction.setAccountAction(AccountAction.INCREASE);
		receivableTransaction.setActionTimestamp(DateUtils
				.getCurrentTimestamp());
		receivableTransaction.setMaturityDate(maturityDate);
		receivableTransaction.setAmount(amount);
		receivableTransaction.setDescription(description);
		receivableTransaction.setLastUpdatedBy(Main.getUserLogin().getId());
		receivableTransaction.setLastUpdatedTimestamp(DateUtils
				.getCurrentTimestamp());

		session.saveOrUpdate(receivableTransaction);

		BigDecimal lastRemainingAmount = receivableSummary.getRemainingAmount();
		lastRemainingAmount = lastRemainingAmount.add(receivableTransaction
				.getAmount());
		receivableSummary.setRemainingAmount(lastRemainingAmount);

		receivableSummary.setLastReceivableTransactionId(receivableTransaction
				.getId());
		receivableSummary.setLastUpdatedBy(Main.getUserLogin().getId());
		receivableSummary.setLastUpdatedTimestamp(DateUtils
				.getCurrentTimestamp());
		session.saveOrUpdate(receivableTransaction);

		return receivableSummary;
	}

	public ReceivableSummary getSummary(int clientId, Session session) {
		Criteria criteria = session.createCriteria(ReceivableSummary.class);
		criteria.add(Restrictions.eq("clientId", clientId));
		ReceivableSummary receivableSummary = (ReceivableSummary) criteria
				.uniqueResult();
		return receivableSummary;
	}

	public ReceivableSummary receiveAndStoreToCash(int clientId,
			Date maturityDate, BigDecimal amount, String description,
			Session session) throws AppException {

		boolean summaryExists = DBUtils.getInstance().isExists("clientId",
				clientId, ReceivableSummary.class, session);

		if (!summaryExists) {
			addSummary(clientId, session);
		}

		ReceivableSummary receivableSummary = getSummary(clientId, session);

		ReceivableTransaction receivableTransaction = new ReceivableTransaction();
		receivableTransaction.setReceivableSummary(receivableSummary);
		receivableTransaction
				.setReffNumber(GeneralConstants.PREFIX_TRX_NUMBER_RECEIVABLE
						+ DateUtils.getTimestampInString());
		receivableTransaction.setAccountAction(AccountAction.DECREASE);
		receivableTransaction.setActionTimestamp(DateUtils
				.getCurrentTimestamp());
		receivableTransaction.setMaturityDate(maturityDate);
		receivableTransaction.setAmount(amount);
		receivableTransaction.setDescription(description);
		receivableTransaction.setLastUpdatedBy(Main.getUserLogin().getId());
		receivableTransaction.setLastUpdatedTimestamp(DateUtils
				.getCurrentTimestamp());

		session.saveOrUpdate(receivableTransaction);

		BigDecimal lastRemainingAmount = receivableSummary.getRemainingAmount();
		lastRemainingAmount = lastRemainingAmount
				.subtract(receivableTransaction.getAmount());
		receivableSummary.setRemainingAmount(lastRemainingAmount);

		receivableSummary.setLastReceivableTransactionId(receivableTransaction
				.getId());
		receivableSummary.setLastUpdatedBy(Main.getUserLogin().getId());
		receivableSummary.setLastUpdatedTimestamp(DateUtils
				.getCurrentTimestamp());
		session.saveOrUpdate(receivableTransaction);

		AccountFacade.getInstance().handleReceivableTransaction(
				receivableTransaction.getId(),
				receivableTransaction.getAmount(), session);

		return receivableSummary;
	}

	public List<Map<String, Object>> search(String clientCode,
			String clientName, Session session) {

		String sqlString = "SELECT new Map("
				+ "supplier.id AS id"
				+ ", supplier.code AS code"
				+ ", supplier.name AS name"
				+ ", summary.remainingAmount AS remainingAmount"
				+ ") FROM ReceivableSummary summary, Supplier supplier WHERE summary.clientId = supplier.id";

		if (!clientCode.trim().equals("")) {
			sqlString += " AND supplier.code LIKE :clientCode";
		}

		if (!clientName.trim().equals("")) {
			sqlString += " AND supplier.name LIKE :clientName";
		}

		Query query = session.createQuery(sqlString);
		HqlParameter parameter = new HqlParameter(query);
		parameter.put("clientCode", "%" + clientCode + "%");
		parameter.put("clientName", "%" + clientName + "%");
		parameter.validate();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = query.list();

		return list;
	}

	private ReceivableSummary addSummary(int clientId, Session session) {
		ReceivableSummary receivableSummary = new ReceivableSummary();
		receivableSummary.setClientId(clientId);
		receivableSummary.setRemainingAmount(BigDecimal.valueOf(0));
		receivableSummary.setLastReceivableTransactionId(-1);
		receivableSummary.setLastUpdatedBy(Main.getUserLogin().getId());
		receivableSummary.setLastUpdatedTimestamp(DateUtils
				.getCurrentTimestamp());

		session.saveOrUpdate(receivableSummary);
		return receivableSummary;
	}
}
