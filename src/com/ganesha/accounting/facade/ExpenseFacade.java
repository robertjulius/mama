package com.ganesha.accounting.facade;

import java.awt.Window;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.model.Circle;
import com.ganesha.accounting.model.Coa;
import com.ganesha.accounting.model.Expense;
import com.ganesha.accounting.model.ExpenseTransaction;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.core.utils.StringUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.TransactionReportFacade;

public class ExpenseFacade implements TransactionReportFacade {

	private static ExpenseFacade instance;

	public static ExpenseFacade getInstance() {
		if (instance == null) {
			instance = new ExpenseFacade();
		}
		return instance;
	}

	private ExpenseFacade() {
	}

	public Expense addNewExpense(String name, Coa coa, Circle circle,
			boolean disabled, boolean deleted, Session session)
			throws UserException {

		if (DBUtils.getInstance()
				.isExists("name", name, Expense.class, session)) {
			throw new UserException("Expense dengan Nama " + name
					+ " sudah pernah didaftarkan");
		}

		Expense expense = new Expense();
		expense.setName(name);
		expense.setCoa(coa);
		expense.setCircle(circle);
		expense.setDisabled(disabled);
		expense.setDeleted(deleted);
		expense.setLastUpdatedBy(Main.getUserLogin().getId());
		expense.setLastUpdatedTimestamp(DateUtils.getCurrent(Timestamp.class));

		session.saveOrUpdate(expense);
		return expense;
	}

	public Expense getDetail(Integer id, Session session) {
		Expense expense = (Expense) session.get(Expense.class, id);
		return expense;
	}

	public List<ExpenseTransaction> getTransactionListByTimestamp(
			Timestamp beginTimestamp, Timestamp endTimestamp, Session session) {

		Criteria criteria = session.createCriteria(ExpenseFacade.class);

		if (beginTimestamp != null) {
			criteria.add(Restrictions
					.ge("lastUpdatedTimestamp", beginTimestamp));
		}

		if (endTimestamp != null) {
			criteria.add(Restrictions.lt("lastUpdatedTimestamp", endTimestamp));
		}

		@SuppressWarnings("unchecked")
		List<ExpenseTransaction> expenseTransactions = criteria.list();

		return expenseTransactions;
	}

	public ExpenseTransaction performTransaction(Expense expense,
			BigDecimal amount, String notes, Session session)
			throws AppException {
		ExpenseTransaction expenseTransaction = new ExpenseTransaction();
		expenseTransaction.setExpense(expense);
		expenseTransaction.setAmount(amount);
		expenseTransaction.setNotes(notes);
		expenseTransaction.setLastUpdatedBy(Main.getUserLogin().getId());
		expenseTransaction.setLastUpdatedTimestamp(DateUtils
				.getCurrent(Timestamp.class));

		session.saveOrUpdate(expenseTransaction);

		AccountFacade.getInstance().handleExpenseTransaction(
				expense.getCoa().getId(), expenseTransaction.getId(),
				StringUtils.properCase(expense.getName()),
				expenseTransaction.getAmount(), session);

		return expenseTransaction;
	}

	@Override
	public void previewReport(Window parent, String transactionNumber,
			Date beginDate, Date endDate, Session session) throws AppException,
			UserException {
		// TODO Auto-generated method stub

	}

	public List<Expense> search(String name, Coa coa, Circle circle,
			String orderBy, boolean disabled, Session session) {
		Criteria criteria = session.createCriteria(Expense.class);
		criteria.createAlias("coa", "coa");
		criteria.createAlias("circle", "circle");

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		if (coa != null && coa.getId() != null) {
			criteria.add(Restrictions.eq("coa.id", coa.getId()));
		}

		if (circle != null && circle.getId() != null) {
			criteria.add(Restrictions.eq("circle.id", circle.getId()));
		}

		if (orderBy != null && !orderBy.trim().isEmpty()) {
			criteria.addOrder(Order.asc(orderBy));
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Expense> expenses = criteria.list();

		return expenses;
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

	public Expense updateExistingExpense(Integer id, String name, Coa coa,
			Circle circle, boolean disabled, boolean deleted, Session session)
			throws UserException {

		Expense expense = getDetail(id, session);
		if (!expense.getName().equals(name)) {
			if (DBUtils.getInstance().isExists("name", name, Expense.class,
					session)) {
				throw new UserException("Expense dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				expense.setName(name);
			}
		}
		expense.setName(name);
		expense.setCoa(coa);
		expense.setCircle(circle);
		if (deleted) {
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus Expense yang masih dalam kondisi aktif");
			}
		}
		expense.setDisabled(disabled);
		expense.setDeleted(deleted);
		expense.setLastUpdatedBy(Main.getUserLogin().getId());
		expense.setLastUpdatedTimestamp(DateUtils.getCurrent(Timestamp.class));

		session.saveOrUpdate(expense);
		return expense;
	}
}
