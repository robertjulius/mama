package com.ganesha.minimarket.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Discount;
import com.ganesha.minimarket.model.Item;

public class DiscountFacade {

	private static DiscountFacade instance;

	public static DiscountFacade getInstance() {
		if (instance == null) {
			instance = new DiscountFacade();
		}
		return instance;
	}

	private DiscountFacade() {
	}

	public Discount getDetail(Integer itemId, int quantity, Session session) {
		Criteria criteria = session.createCriteria(Discount.class);
		criteria.createAlias("item", "item");
		criteria.add(Restrictions.eq("item.id", itemId));
		criteria.add(Restrictions.eq("quantity", quantity));
		Discount discount = (Discount) criteria.uniqueResult();
		return discount;
	}

	public double getDiscountPercent(Integer itemId, int quantity,
			Session session) {
		double discountPercent = 0;
		Discount discount = getDetail(itemId, quantity, session);
		if (discount != null && !discount.getDisabled()) {
			discountPercent = discount.getDiscountPercent().doubleValue();
		}
		return discountPercent;
	}

	public Discount saveOrUpdate(Integer itemId, Integer quantity,
			BigDecimal discountPercent, boolean disabled, Session session)
			throws UserException {

		Discount discount = getDetail(itemId, quantity, session);
		if (discount == null) {
			discount = new Discount();
			Item item = ItemFacade.getInstance().getDetail(itemId, session);
			discount.setItem(item);
		}
		discount.setQuantity(quantity);
		discount.setDiscountPercent(discountPercent);
		discount.setDisabled(disabled);
		discount.setLastUpdatedBy(Main.getUserLogin().getId());
		discount.setLastUpdatedTimestamp(DateUtils.getCurrent(Timestamp.class));

		session.saveOrUpdate(discount);
		return discount;
	}

	public List<Discount> search(String code, String name, boolean disabled,
			Session session) {
		Criteria criteria = session.createCriteria(Discount.class);
		criteria.createAlias("item", "item");

		if (code != null && !code.trim().isEmpty()) {
			criteria.add(Restrictions.like("item.code", "%" + code + "%")
					.ignoreCase());
		}

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("item.name", "%" + name + "%")
					.ignoreCase());
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Discount> discounts = criteria.list();

		return discounts;
	}
}
