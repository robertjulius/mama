package com.ganesha.accounting.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.constants.Enums.DebitCreditFlag;
import com.ganesha.accounting.model.Account;
import com.ganesha.accounting.model.Coa;
import com.ganesha.core.exception.AppException;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;

public class AccountFacade {

	private static AccountFacade instance;

	public static AccountFacade getInstance() {
		if (instance == null) {
			instance = new AccountFacade();
		}
		return instance;
	}

	private AccountFacade() {
	}

	public BigDecimal getAccountSum(int coaId, Integer entityId,
			Timestamp afterThisTimestamp, DebitCreditFlag increaseOn,
			Session session) throws AppException {

		String sqlString = "SELECT new Map(SUM(a.debit) AS debit, SUM(a.credit) AS credit) FROM Account a WHERE a.coa.id = :coaId";

		if (entityId != null) {
			sqlString += " AND a.entityId = :entityId";
		}

		if (afterThisTimestamp != null) {
			sqlString += " AND a.timestamp > :afterThisTimestamp";
		}

		Query query = session.createQuery(sqlString);
		HqlParameter param = new HqlParameter(query);
		param.put("coaId", coaId);
		param.put("entityId", entityId);
		param.put("afterThisTimestamp", afterThisTimestamp);
		param.validate();

		@SuppressWarnings("unchecked")
		Map<String, Object> list = (Map<String, Object>) query.uniqueResult();
		BigDecimal debit = (BigDecimal) list.get("debit");
		BigDecimal credit = (BigDecimal) list.get("credit");

		BigDecimal amountSum = null;

		if (increaseOn == DebitCreditFlag.DEBIT) {
			amountSum = debit.subtract(credit);
		} else if (increaseOn == DebitCreditFlag.CREDIT) {
			amountSum = credit.subtract(debit);
		} else {
			throw new AppException("Unsupported DebitCreditCode: " + increaseOn);
		}

		return amountSum;
	}

	public Account getLastAccount(int coaId, Session session) {
		Criteria criteria = session.createCriteria(Account.class);
		criteria.add(Restrictions.eq("coaId", coaId));
		criteria.addOrder(Order.desc("id"));
		criteria.setMaxResults(1);

		Account account = (Account) criteria.uniqueResult();
		return account;
	}

	public void insertIntoAccount(int coaId, int entityId, Timestamp timestamp,
			String notes, String ref, DebitCreditFlag increaseOn,
			BigDecimal amount, Session session) throws AppException {

		Coa coa = CoaFacade.getInstance().getDetail(coaId, session);

		Account account = new Account();
		account.setCoa(coa);
		account.setEntityId(entityId);
		account.setTimestamp(timestamp);
		account.setNotes(notes);

		account.setRef(ref);

		if (increaseOn == DebitCreditFlag.DEBIT) {
			account.setDebit(amount);
			account.setCredit(BigDecimal.valueOf(0));
		} else if (increaseOn == DebitCreditFlag.CREDIT) {
			account.setCredit(amount);
			account.setDebit(BigDecimal.valueOf(0));
		}

		account.setDisabled(false);
		account.setDeleted(false);
		account.setLastUpdatedTimestamp(timestamp);
		account.setLastUpdatedBy(Main.getUserLogin().getId());

		session.saveOrUpdate(account);
	}
}
