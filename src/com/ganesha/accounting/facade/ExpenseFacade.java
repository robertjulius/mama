package com.ganesha.accounting.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.model.Circle;
import com.ganesha.accounting.model.Coa;
import com.ganesha.accounting.model.Expense;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.minimarket.Main;

public class ExpenseFacade {

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
		expense.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(expense);
		return expense;
	}

	public Expense getDetail(Integer id, Session session) {
		Expense expense = (Expense) session.get(Expense.class, id);
		return expense;
	}

	public List<Expense> search(String name, Coa coa, Circle circle,
			boolean disabled, Session session) {
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

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Expense> expenses = criteria.list();

		return expenses;
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
		expense.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(expense);
		return expense;
	}
}
