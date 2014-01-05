package com.ganesha.accounting.minimarket.facade;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.model.Good;
import com.ganesha.accounting.minimarket.model.GoodStock;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HibernateUtil;

public class StockFacade {

	private static StockFacade instance;

	public static StockFacade getInstance() {
		if (instance == null) {
			instance = new StockFacade();
		}
		return instance;
	}

	private StockFacade() {
	}

	public void addNewGood(String code, String name, String barcode,
			String unit, BigDecimal buyPrice, BigDecimal hpp,
			BigDecimal sellPrice, int stock, int minimumStock) {

		Session session = HibernateUtil.getSession();
		try {
			session.beginTransaction();

			saveGood(code, name, barcode, session);
			saveGoodStock(code, unit, buyPrice, hpp, sellPrice, stock,
					minimumStock, session);

			session.getTransaction().commit();
		} finally {
			session.close();
		}
	}

	public GoodStock getDetail(String code) {
		Session session = HibernateUtil.getSession();
		try {
			return getDetail(code, session);
		} finally {
			session.close();
		}
	}

	public GoodStock getDetail(String code, Session session) {
		Criteria criteria = session.createCriteria(GoodStock.class);
		criteria.createAlias("good", "good");
		criteria.add(Restrictions.eq("good.code", code));

		GoodStock goodStock = (GoodStock) criteria.uniqueResult();
		return goodStock;
	}

	public List<GoodStock> search(String code, String name, boolean disabled) {
		Session session = HibernateUtil.getSession();
		try {
			Criteria criteria = session.createCriteria(GoodStock.class);
			criteria.createAlias("good", "good");

			if (code != null && !code.trim().isEmpty()) {
				criteria.add(Restrictions.like("good.code", "%" + code + "%"));
			}

			if (name != null && !name.trim().isEmpty()) {
				criteria.add(Restrictions.like("good.name", "%" + name + "%"));
			}

			criteria.add(Restrictions.eq("disabled", disabled));
			criteria.add(Restrictions.eq("deleted", false));

			@SuppressWarnings("unchecked")
			List<GoodStock> goodStocks = criteria.list();

			return goodStocks;
		} finally {
			session.close();
		}
	}

	public void updateExistingGood(String code, String name, String barcode,
			String unit, BigDecimal buyPrice, BigDecimal hpp,
			BigDecimal sellPrice, int stock, int minimumStock) {

		Session session = HibernateUtil.getSession();
		try {
			session.beginTransaction();

			GoodStock goodStock = getDetail(code, session);
			goodStock.setUnit(unit);
			goodStock.setBuyPrice(buyPrice);
			goodStock.setHpp(hpp);
			goodStock.setSellPrice(sellPrice);
			goodStock.setStock(stock);
			goodStock.setMinimumStock(minimumStock);
			goodStock.setLastUpdatedBy(Main.getUserLogin().getId());
			goodStock
					.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

			Good good = goodStock.getGood();
			good.setName(name);
			good.setBarcode(barcode);
			good.setLastUpdatedBy(Main.getUserLogin().getId());
			good.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

			session.saveOrUpdate(goodStock);
			session.getTransaction().commit();

		} finally {
			session.close();
		}
	}

	private void saveGood(String code, String name, String barcode,
			Session session) {

		Good good = new Good();
		good.setCode(code);
		good.setName(name);
		good.setBarcode(barcode);
		good.setLastUpdatedBy(Main.getUserLogin().getId());
		good.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(good);
	}

	private void saveGoodStock(String code, String unit, BigDecimal buyPrice,
			BigDecimal hpp, BigDecimal sellPrice, int stock, int minimumStock,
			Session session) {

		GoodStock goodStock = new GoodStock();
		goodStock.setBuyPrice(buyPrice);

		Criteria criteria = session.createCriteria(Good.class);
		criteria.add(Restrictions.eq("code", code));
		Good good = (Good) criteria.uniqueResult();

		goodStock.setGood(good);
		goodStock.setUnit(unit);
		goodStock.setBuyPrice(buyPrice);
		goodStock.setHpp(hpp);
		goodStock.setSellPrice(sellPrice);
		goodStock.setStock(stock);
		goodStock.setMinimumStock(minimumStock);
		goodStock.setLastUpdatedBy(Main.getUserLogin().getId());
		goodStock.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(goodStock);
	}
}
