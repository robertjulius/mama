package com.ganesha.accounting.minimarket.facade;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.model.Item;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;

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

	public void addNewItem(String code, String name, String barcode,
			String unit, BigDecimal buyPrice, BigDecimal hpp,
			BigDecimal sellPrice, int minimumStock, boolean disabled,
			boolean deleted, Session session) throws UserException {

		if (GlobalFacade.getInstance().isExists("code", code, Item.class,
				session)) {
			throw new UserException("Barang dengan ID " + code
					+ " sudah pernah didaftarkan");
		}

		if (GlobalFacade.getInstance().isExists("name", name, Item.class,
				session)) {
			throw new UserException("Barang dengan Nama " + name
					+ " sudah pernah didaftarkan");
		}

		insertIntoItem(code, name, barcode, disabled, deleted, session);
		insertIntoItemStock(code, unit, buyPrice, hpp, sellPrice, minimumStock,
				disabled, deleted, session);
	}

	public ItemStock getDetail(String code, Session session) {
		Criteria criteria = session.createCriteria(ItemStock.class);
		criteria.createAlias("item", "item");
		criteria.add(Restrictions.eq("item.code", code));

		ItemStock itemStock = (ItemStock) criteria.uniqueResult();
		return itemStock;
	}

	public List<ItemStock> search(String code, String name, boolean disabled,
			String[] orderBy, Session session) {
		Criteria criteria = session.createCriteria(ItemStock.class);
		criteria.createAlias("item", "item");

		if (code != null && !code.trim().isEmpty()) {
			criteria.add(Restrictions.like("item.code", "%" + code + "%")
					.ignoreCase());
		}

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("item.name", "%" + name + "%")
					.ignoreCase());
		}

		if (orderBy != null) {
			for (String order : orderBy) {
				if (order != null && !order.trim().isEmpty()) {
					criteria.addOrder(Order.asc(order));
				}
			}
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<ItemStock> itemStocks = criteria.list();

		return itemStocks;
	}

	public void updateExistingItem(String code, String name, String barcode,
			String unit, BigDecimal buyPrice, BigDecimal hpp,
			BigDecimal sellPrice, int minimumStock, boolean disabled,
			boolean deleted, Session session) throws UserException {

		ItemStock itemStock = getDetail(code, session);
		itemStock.setUnit(unit);
		itemStock.setBuyPrice(buyPrice);
		itemStock.setHpp(hpp);
		itemStock.setSellPrice(sellPrice);
		itemStock.setMinimumStock(minimumStock);
		itemStock.setDisabled(disabled);
		itemStock.setDeleted(deleted);
		itemStock.setLastUpdatedBy(Main.getUserLogin().getId());
		itemStock.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		Item item = itemStock.getItem();
		if (!item.getName().equals(name)) {
			if (GlobalFacade.getInstance().isExists("name", name, Item.class,
					session)) {
				throw new UserException("Barang dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				item.setName(name);
			}
		}
		item.setBarcode(barcode);
		item.setDisabled(disabled);
		item.setDeleted(deleted);
		item.setLastUpdatedBy(Main.getUserLogin().getId());
		item.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(itemStock);
	}

	private void insertIntoItem(String code, String name, String barcode,
			boolean disabled, boolean deleted, Session session) {

		Item item = new Item();
		item.setCode(code);
		item.setName(name);
		item.setBarcode(barcode);
		item.setDisabled(disabled);
		item.setDeleted(deleted);
		item.setLastUpdatedBy(Main.getUserLogin().getId());
		item.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(item);
	}

	private void insertIntoItemStock(String code, String unit,
			BigDecimal buyPrice, BigDecimal hpp, BigDecimal sellPrice,
			int minimumStock, boolean disabled, boolean deleted, Session session) {

		ItemStock itemStock = new ItemStock();
		itemStock.setBuyPrice(buyPrice);

		Criteria criteria = session.createCriteria(Item.class);
		criteria.add(Restrictions.eq("code", code));
		Item item = (Item) criteria.uniqueResult();

		itemStock.setItem(item);
		itemStock.setUnit(unit);
		itemStock.setBuyPrice(buyPrice);
		itemStock.setHpp(hpp);
		itemStock.setSellPrice(sellPrice);
		itemStock.setStock(0);
		itemStock.setMinimumStock(minimumStock);
		itemStock.setDisabled(disabled);
		itemStock.setDeleted(deleted);
		itemStock.setLastUpdatedBy(Main.getUserLogin().getId());
		itemStock.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(itemStock);
	}
}
