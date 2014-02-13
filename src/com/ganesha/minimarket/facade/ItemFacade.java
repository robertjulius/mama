package com.ganesha.minimarket.facade;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemStock;

public class ItemFacade {

	private static ItemFacade instance;

	public static ItemFacade getInstance() {
		if (instance == null) {
			instance = new ItemFacade();
		}
		return instance;
	}

	private ItemFacade() {
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

		Item item = getByBarcode(barcode, session);
		if (item != null) {
			throw new UserException("Barang dengan Barcode " + barcode
					+ " sudah pernah didaftarkan untuk barang ["
					+ item.getCode() + "] " + item.getName());
		}

		insertIntoItem(code, name, barcode, unit, buyPrice, hpp, sellPrice,
				minimumStock, disabled, deleted, session);
	}

	public int calculateStock(Item item) {
		int stock = 0;
		List<ItemStock> itemStocks = item.getItemStocks();
		for (ItemStock itemStock : itemStocks) {
			stock += itemStock.getQuantity();
		}
		return stock;
	}

	public Item getByBarcode(String barcode, Session session) {
		Criteria criteria = session.createCriteria(Item.class);
		criteria.add(Restrictions.eq("barcode", barcode));
		Item item = (Item) criteria.uniqueResult();
		return item;
	}

	public Item getDetail(int id, Session session) {
		Item item = (Item) session.get(Item.class, id);
		return item;
	}

	public BigDecimal getLastBuyPrice(Item item) {
		BigDecimal lastBuyPrice = null;
		List<ItemStock> itemStocks = item.getItemStocks();
		if (itemStocks.isEmpty()) {
			lastBuyPrice = BigDecimal.valueOf(0);
		} else {
			int lastIndex = itemStocks.size() - 1;
			lastBuyPrice = itemStocks.get(lastIndex).getBuyPrice();
		}
		return lastBuyPrice;
	}

	public void reAdjustStock(Item item, int stock, Session session) {
		/*
		 * TODO
		 */
	}

	public List<Item> search(String code, String barcode, String name,
			boolean disabled, String[] orderBy, Session session) {
		Criteria criteria = session.createCriteria(Item.class);

		if (code != null && !code.trim().isEmpty()) {
			criteria.add(Restrictions.like("code", "%" + code + "%")
					.ignoreCase());
		}

		if (barcode != null && !barcode.trim().isEmpty()) {
			criteria.add(Restrictions.like("barcode", "%" + barcode + "%")
					.ignoreCase());
		}

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
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
		List<Item> items = criteria.list();

		return items;
	}

	public void updateExistingItem(int id, String name, String barcode,
			String unit, BigDecimal buyPrice, BigDecimal hpp,
			BigDecimal sellPrice, int minimumStock, boolean disabled,
			boolean deleted, Session session) throws UserException {

		Item item = getDetail(id, session);
		item.setUnit(unit);
		item.setHpp(hpp);
		item.setSellPrice(sellPrice);
		item.setMinimumStock(minimumStock);
		item.setDisabled(disabled);
		item.setDeleted(deleted);
		item.setLastUpdatedBy(Main.getUserLogin().getId());
		item.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		if (!item.getName().equals(name)) {
			if (GlobalFacade.getInstance().isExists("name", name, Item.class,
					session)) {
				throw new UserException("Barang dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				item.setName(name);
			}
		}
		if (deleted) {
			item.setBarcode(null);
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus Barang yang masih dalam kondisi aktif");
			}
		} else {
			item.setBarcode(barcode);
		}
		item.setDisabled(disabled);
		item.setDeleted(deleted);
		item.setLastUpdatedBy(Main.getUserLogin().getId());
		item.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(item);
	}

	private void insertIntoItem(String code, String name, String barcode,
			String unit, BigDecimal buyPrice, BigDecimal hpp,
			BigDecimal sellPrice, int minimumStock, boolean disabled,
			boolean deleted, Session session) {

		Item item = new Item();

		item.setCode(code);
		item.setName(name);
		item.setBarcode(barcode);
		item.setUnit(unit);
		item.setHpp(hpp);
		item.setSellPrice(sellPrice);
		item.setMinimumStock(minimumStock);
		item.setDisabled(disabled);
		item.setDeleted(deleted);
		item.setLastUpdatedBy(Main.getUserLogin().getId());
		item.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(item);
	}
}
