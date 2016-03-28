package com.ganesha.minimarket.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemSellPrice;
import com.ganesha.minimarket.model.ItemStock;
import com.ganesha.minimarket.model.PurchaseDetail;

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

	public Item addNewItem(String code, String name, String barcode,
			String unit, BigDecimal buyPrice, List<ItemSellPrice> sellPrices,
			int minimumStock, boolean disabled, boolean deleted, Session session)
			throws UserException {

		if (DBUtils.getInstance().isExists("code", code, Item.class, session)) {
			throw new UserException("Barang dengan ID " + code
					+ " sudah pernah didaftarkan");
		}

		if (DBUtils.getInstance().isExists("name", name, Item.class, session)) {
			throw new UserException("Barang dengan Nama " + name
					+ " sudah pernah didaftarkan");
		}

		Item item = getByBarcode(barcode, session);
		if (item != null) {
			throw new UserException("Barang dengan Barcode " + barcode
					+ " sudah pernah didaftarkan untuk barang ["
					+ item.getCode() + "] " + item.getName());
		}

		return insertIntoItem(code, name, barcode, unit, buyPrice, sellPrices,
				minimumStock, disabled, deleted, session);
	}

	public BigDecimal calculateAmount(Item item) {
		BigDecimal amount = BigDecimal.valueOf(0);
		List<ItemStock> itemStocks = item.getItemStocks();
		for (ItemStock itemStock : itemStocks) {
			amount = amount.add(itemStock.getPurchaseDetail().getPricePerUnit()
					.multiply(BigDecimal.valueOf(itemStock.getQuantity())));
		}
		return amount;
	}

	public BigDecimal calculateAmountOfAllItem(Session session) {
		BigDecimal amountOfAllItem = BigDecimal.valueOf(0);
		List<Item> allItem = search(null, null, null, false, session);
		for (Item item : allItem) {
			amountOfAllItem = amountOfAllItem.add(calculateAmount(item));
		}
		return amountOfAllItem;
	}

	public int calculateMaxStock(Item item) {
		int maxStock = 0;
		List<ItemStock> itemStocks = item.getItemStocks();
		for (ItemStock itemStock : itemStocks) {
			PurchaseDetail purchaseDetail = itemStock.getPurchaseDetail();
			maxStock += purchaseDetail.getQuantity();
		}
		return maxStock;
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

	public BigDecimal getFirstSellPrice(Item item) {
		BigDecimal sellPrice = null;
		if (item.getSellPrices().size() > 0) {
			sellPrice = item.getSellPrices().get(0).getPrimaryKey()
					.getSellPrice();
		}
		return sellPrice;
	}

	public BigDecimal getHigherBuyPrice(Item item) {
		BigDecimal higherBuyPrice = BigDecimal.valueOf(0);
		List<ItemStock> itemStocks = item.getItemStocks();
		for (ItemStock itemStock : itemStocks) {
			if (itemStock.getPurchaseDetail().getPricePerUnit()
					.compareTo(higherBuyPrice) > 0) {
				higherBuyPrice = itemStock.getPurchaseDetail()
						.getPricePerUnit();
			}
		}
		return higherBuyPrice;
	}

	public BigDecimal getLastBuyPrice(Item item) {
		BigDecimal lastBuyPrice = null;
		List<ItemStock> itemStocks = item.getItemStocks();
		if (itemStocks.isEmpty()) {
			lastBuyPrice = BigDecimal.valueOf(0);
		} else {
			int lastIndex = itemStocks.size() - 1;
			lastBuyPrice = itemStocks.get(lastIndex).getPurchaseDetail()
					.getPricePerUnit();
		}
		return lastBuyPrice;
	}

	public BigDecimal getSecondToLastBuyPrice(Item item) {
		BigDecimal lastBuyPrice = null;
		List<ItemStock> itemStocks = item.getItemStocks();
		if (itemStocks.isEmpty()) {
			lastBuyPrice = BigDecimal.valueOf(0);
		} else {
			int secondToLastIndex = itemStocks.size() - 2;
			if (secondToLastIndex < 0) {
				lastBuyPrice = BigDecimal.valueOf(0);
			} else {
				lastBuyPrice = itemStocks.get(secondToLastIndex).getPurchaseDetail().getPricePerUnit();
			}
		}
		return lastBuyPrice;
	}

	public void reAdjustStock(Item item, int newStock, Session session) {
		int totalStock = calculateStock(item);
		if (newStock < totalStock) {
			reAdjustStockReduce(item, newStock, session);
		} else if (newStock > totalStock) {
			reAdjustStockIncrease(item, newStock, session);
		} else {
			/*
			 * Do nothing
			 */
		}
	}

	public List<Item> search(String code, String barcode, String name,
			boolean disabled, Session session) {
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

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Item> items = criteria.list();

		return items;
	}

	public Item updateExistingItem(int id, String name, String barcode,
			String unit, BigDecimal buyPrice, List<ItemSellPrice> sellPrices,
			int minimumStock, boolean disabled, boolean deleted, Session session)
			throws UserException {

		Item item = getDetail(id, session);
		item.setUnit(unit);
		item.setMinimumStock(minimumStock);
		item.setDisabled(disabled);
		item.setDeleted(deleted);
		item.setLastUpdatedBy(Main.getUserLogin().getId());
		item.setLastUpdatedTimestamp(DateUtils.getCurrent(Timestamp.class));

		if (!item.getName().equals(name)) {
			if (DBUtils.getInstance().isExists("name", name, Item.class,
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
		item.setLastUpdatedTimestamp(DateUtils.getCurrent(Timestamp.class));

		session.saveOrUpdate(item);
		deleteInsertSalePrices(sellPrices, item.getId(), session);

		return item;
	}

	private void deleteInsertSalePrices(List<ItemSellPrice> sellPrices,
			Integer itemId, Session session) {
		deleteItemSalePricesByItemId(itemId, session);
		insertIntoItemSalePrices(itemId, sellPrices, session);
	}

	private void deleteItemSalePricesByItemId(Integer itemId, Session session) {
		String sql = "DELETE FROM item_sell_prices WHERE item_id = :itemId";
		SQLQuery query = session.createSQLQuery(sql);

		HqlParameter parameter = new HqlParameter(query);
		parameter.put("itemId", itemId);
		parameter.validate();

		query.executeUpdate();
	}

	private Item insertIntoItem(String code, String name, String barcode,
			String unit, BigDecimal buyPrice, List<ItemSellPrice> sellPrices,
			int minimumStock, boolean disabled, boolean deleted, Session session) {

		Item item = new Item();

		item.setCode(code);
		item.setName(name);
		item.setBarcode(barcode);
		item.setUnit(unit);
		item.setMinimumStock(minimumStock);
		item.setDisabled(disabled);
		item.setDeleted(deleted);
		item.setLastUpdatedBy(Main.getUserLogin().getId());
		item.setLastUpdatedTimestamp(DateUtils.getCurrent(Timestamp.class));

		session.saveOrUpdate(item);
		deleteInsertSalePrices(sellPrices, item.getId(), session);

		return item;
	}

	private void insertIntoItemSalePrices(Integer itemId,
			List<ItemSellPrice> sellPrices, Session session) {

		for (ItemSellPrice sellPrice : sellPrices) {
			String sql = "INSERT INTO item_sell_prices (item_id, sell_price, sequence) VALUES (:itemId, :sellPrice, :sequence)";
			SQLQuery query = session.createSQLQuery(sql);

			HqlParameter parameter = new HqlParameter(query);
			parameter.put("itemId", itemId);
			parameter
					.put("sellPrice", sellPrice.getPrimaryKey().getSellPrice());
			parameter.put("sequence", sellPrice.getSequence());
			parameter.validate();

			query.executeUpdate();
		}
	}

	private void reAdjustStockIncrease(Item item, int greaterStock,
			Session session) {
		int totalStock = calculateStock(item);
		int excess = greaterStock - totalStock;
		List<ItemStock> itemStocks = item.getItemStocks();
		for (int i = itemStocks.size() - 1; i >= 0 && excess > 0; --i) {
			ItemStock itemStock = itemStocks.get(i);
			int maxStock = itemStock.getPurchaseDetail().getQuantity();
			int stock = itemStock.getQuantity();
			int taken;
			if ((excess + stock) > maxStock) {
				taken = maxStock - stock;
			} else {
				taken = excess;
			}
			int remain = excess - taken;
			int newStock = stock + taken;

			itemStock.setQuantity(newStock);
			session.saveOrUpdate(itemStock);

			excess = remain;
		}
	}

	private void reAdjustStockReduce(Item item, int lesserStock, Session session) {
		int totalStock = calculateStock(item);
		int requirement = totalStock - lesserStock;
		List<ItemStock> itemStocks = item.getItemStocks();
		for (int i = 0; i < itemStocks.size() && requirement > 0; ++i) {
			ItemStock itemStock = itemStocks.get(i);
			int stock = itemStock.getQuantity();
			int taken;
			if (requirement > stock) {
				taken = stock;
				requirement -= stock;
			} else {
				taken = requirement;
				requirement = 0;
			}
			int remain = stock - taken;
			itemStock.setQuantity(remain);
			session.saveOrUpdate(itemStock);
		}
	}
}
