package com.ganesha.accounting.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.constants.CoaCodeConstants;
import com.ganesha.accounting.constants.Enums.DebitCreditFlag;
import com.ganesha.accounting.model.Account;
import com.ganesha.accounting.model.Coa;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.SaleConstraintHeader;

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

	public List<SaleConstraintHeader> search(Integer entityId, Integer coaId,
			Timestamp timestampBegin, Timestamp timestampEnd, Session session) {
		Criteria criteria = session.createCriteria(SaleConstraintHeader.class);
		@SuppressWarnings("unchecked")
		List<SaleConstraintHeader> saleConstraintHeaders = criteria.list();
		return saleConstraintHeaders;
	}

	/*
	 * public List<Accounts> search(Integer coaId, Date date, Session session) {
	 * 
	 * }
	 */

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

	public BigDecimal getAccountSum(int coaId, Integer entityId,
			Timestamp beginTimestamp, Timestamp endTimestamp,
			DebitCreditFlag increaseOn, Session session) throws AppException {

		String sqlString = "SELECT new Map(SUM(a.debit) AS debit, SUM(a.credit) AS credit) FROM Account a WHERE a.coa.id = :coaId";

		if (entityId != null) {
			sqlString += " AND a.entityId = :entityId";
		}

		if (beginTimestamp != null) {
			sqlString += " AND a.timestamp >= :beginTimestamp";
		}

		if (endTimestamp != null) {
			sqlString += " AND a.timestamp < :endTimestamp";
		}

		Query query = session.createQuery(sqlString);
		HqlParameter param = new HqlParameter(query);
		param.put("coaId", coaId);
		param.put("entityId", entityId);
		param.put("beginTimestamp", beginTimestamp);
		param.put("endTimestamp", endTimestamp);
		param.validate();

		@SuppressWarnings("unchecked")
		Map<String, Object> list = (Map<String, Object>) query.uniqueResult();

		BigDecimal debit = (BigDecimal) list.get("debit");
		if (debit == null) {
			debit = BigDecimal.valueOf(0);
		}

		BigDecimal credit = (BigDecimal) list.get("credit");
		if (credit == null) {
			credit = BigDecimal.valueOf(0);
		}

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

	public void handleExpenseTransaction(Integer coaId, Integer entityId,
			String notes, BigDecimal amount, Session session)
			throws AppException {

		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();

		AccountFacade.getInstance().insertIntoAccount(coaId, entityId,
				currentTimestamp, notes, "", DebitCreditFlag.DEBIT, amount,
				session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.KAS_KECIL, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.CREDIT, amount, session);
	}

	public void handlePayableTransaction(Integer entityId, BigDecimal amount,
			Session session) throws AppException {

		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();
		String notes = "Pembayaran Hutang";

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.HUTANG_USAHA, entityId, currentTimestamp,
				notes, "", DebitCreditFlag.DEBIT, amount, session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.KAS_KECIL, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.CREDIT, amount, session);
	}

	public void handlePurchase(Integer entityId, BigDecimal totalAmount,
			BigDecimal advancePayment, BigDecimal remainingPayment,
			Session session) throws AppException {

		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();
		String notes = "Pembelian";

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.PEMBELIAN, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.DEBIT, totalAmount, session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.KAS_KECIL, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.CREDIT, advancePayment, session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.HUTANG_USAHA, entityId, currentTimestamp,
				notes, "", DebitCreditFlag.CREDIT, remainingPayment, session);
	}

	public void handlePurchaseReturn(Integer entityId,
			BigDecimal totalReturnAmount, BigDecimal amountReturned,
			BigDecimal remainingReturnAmount, BigDecimal debtCut,
			Session session) throws AppException {

		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();
		String notes = "Retur Pembelian";

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.KAS_KECIL, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.DEBIT, amountReturned, session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.PIUTANG_USAHA, entityId, currentTimestamp,
				notes, "", DebitCreditFlag.DEBIT, remainingReturnAmount,
				session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.HUTANG_USAHA, entityId, currentTimestamp,
				notes, "", DebitCreditFlag.DEBIT, debtCut, session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.RETUR_PEMBELIAN, entityId, currentTimestamp,
				notes, "", DebitCreditFlag.CREDIT, totalReturnAmount, session);
	}

	public void handleReceivableTransaction(Integer entityId,
			BigDecimal amount, Session session) throws AppException {

		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();
		String notes = "Penerimaan Piutang";

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.KAS_KECIL, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.DEBIT, amount, session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.PIUTANG_USAHA, entityId, currentTimestamp,
				notes, "", DebitCreditFlag.CREDIT, amount, session);
	}

	public void handleRevenueTransaction(Integer coaId, Integer entityId,
			String notes, BigDecimal amount, Session session)
			throws AppException {

		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.KAS_KECIL, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.DEBIT, amount, session);

		AccountFacade.getInstance().insertIntoAccount(coaId, entityId,
				currentTimestamp, notes, "", DebitCreditFlag.CREDIT, amount,
				session);

	}

	public void handleSale(Integer entityId, BigDecimal totalAmount,
			Session session) throws AppException {

		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();
		String notes = "Penjualan";

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.KAS_KECIL, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.DEBIT, totalAmount, session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.PENJUALAN, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.CREDIT, totalAmount, session);
	}

	public void handleSaleReturn(Integer entityId,
			BigDecimal totalReturnAmount, Session session) throws AppException {

		Timestamp currentTimestamp = DateUtils.getCurrentTimestamp();
		String notes = "Retur Penjualan";

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.RETUR_PENJUALAN, entityId, currentTimestamp,
				notes, "", DebitCreditFlag.DEBIT, totalReturnAmount, session);

		AccountFacade.getInstance().insertIntoAccount(
				CoaCodeConstants.KAS_KECIL, entityId, currentTimestamp, notes,
				"", DebitCreditFlag.CREDIT, totalReturnAmount, session);
	}

	private Account insertIntoAccount(int coaId, int entityId,
			Timestamp timestamp, String notes, String ref,
			DebitCreditFlag increaseOn, BigDecimal amount, Session session)
			throws AppException {

		if (amount.doubleValue() == 0) {
			return null;
		}

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
		} else {
			throw new AppException("Unsupported DebigCreditFlag " + increaseOn);
		}

		account.setLastUpdatedTimestamp(timestamp);
		account.setLastUpdatedBy(Main.getUserLogin().getId());

		session.saveOrUpdate(account);
		return account;
	}
}
