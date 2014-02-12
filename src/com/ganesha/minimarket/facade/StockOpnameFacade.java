package com.ganesha.minimarket.facade;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.PurchaseDetail;
import com.ganesha.minimarket.model.StockOpnameDetail;
import com.ganesha.minimarket.model.StockOpnameHeader;

public class StockOpnameFacade {

	private static final String REPORT_NAME = "Laporan Stok Opname";
	private static final String REPORT_FILE = "com/ganesha/minimarket/reports/StockOpnameReport.jrxml";

	private static StockOpnameFacade instance;

	public static StockOpnameFacade getInstance() {
		if (instance == null) {
			instance = new StockOpnameFacade();
		}
		return instance;
	}

	private StockOpnameFacade() {
	}

	public StockOpnameDetail createStockOpnameEntity(int itemId,
			int quantityManual, int overCount, int lossCount,
			StockQueueMethod stockQueueMethod, Session session)
			throws AppException {

		Item item = StockFacade.getInstance().getDetail(itemId, session);
		int quantityInThisStage = StockFacade.getInstance()
				.calculateStock(item);

		BigDecimal overAmount = null;
		BigDecimal lossAmount = null;

		if (stockQueueMethod == StockQueueMethod.FIFO) {

			overAmount = getFifoAmount(overCount, quantityInThisStage, itemId,
					session);
			lossAmount = getFifoAmount(lossCount, quantityInThisStage, itemId,
					session);
		} else if (stockQueueMethod == StockQueueMethod.LIFO) {
			overAmount = getLifoAmount(overCount, itemId, session);
			lossAmount = getLifoAmount(lossCount, itemId, session);
		} else {
			throw new AppException("Unsupported stock queue method: "
					+ stockQueueMethod);
		}

		BigDecimal lastStockAmount = countStockAmountEnd(item, overAmount,
				lossAmount, stockQueueMethod, session);
		item.setHpp(lastStockAmount);

		StockOpnameDetail stockOpname = new StockOpnameDetail();
		stockOpname.setItem(item);
		stockOpname.setQuantitySystem(quantityInThisStage);
		stockOpname.setQuantityManual(quantityManual);
		stockOpname.setOverCount(overCount);
		stockOpname.setOverAmount(overAmount);
		stockOpname.setLossCount(lossCount);
		stockOpname.setLossAmount(lossAmount);
		stockOpname.setLastStockAmount(lastStockAmount);

		return stockOpname;
	}

	public JasperPrint prepareJasper(
			List<StockOpnameDetail> stockOpnameDetails,
			Timestamp beginTimestamp, Timestamp endTimestamp)
			throws AppException {

		Map<String, Object> paramReport = new HashMap<String, Object>();
		paramReport.put("reportName", REPORT_NAME);
		paramReport.put("companyName", Main.getCompany().getName());
		paramReport.put("performedByUserName", Main.getUserLogin().getName());
		paramReport.put("performedBeginTimestamp", beginTimestamp);
		paramReport.put("performedEndTimestamp", endTimestamp);

		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getClassLoader()
					.getResourceAsStream(REPORT_FILE);

			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(jasperDesign);

			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, paramReport, new JRBeanCollectionDataSource(
							stockOpnameDetails));

			return jasperPrint;
		} catch (JRException e) {
			throw new AppException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new AppException(e);
				}
			}
		}
	}

	public void save(List<StockOpnameDetail> stockOpnameDetails,
			Timestamp performedBeginTimestamp, Timestamp performedEndTimestamp,
			Session session) {

		StockOpnameHeader stockOpnameHeader = new StockOpnameHeader();
		stockOpnameHeader.setPerformedBy(Main.getUserLogin());
		stockOpnameHeader.setPerformedBeginTimestamp(performedBeginTimestamp);
		stockOpnameHeader.setPerformedEndTimestamp(performedEndTimestamp);
		stockOpnameHeader.setLastUpdatedBy(Main.getUserLogin().getId());
		stockOpnameHeader.setLastUpdatedTimestamp(CommonUtils
				.getCurrentTimestamp());

		session.saveOrUpdate(stockOpnameHeader);

		for (StockOpnameDetail stockOpname : stockOpnameDetails) {
			Item item = stockOpname.getItem();

			StockFacade.getInstance().reAdjustStock(item,
					stockOpname.getQuantityManual(), session);
			session.saveOrUpdate(item);

			stockOpname.setStockOpnameHeader(stockOpnameHeader);
			session.saveOrUpdate(stockOpname);
		}
	}

	public List<StockOpnameHeader> search(Date beginDate, Date endDate,
			Session session) {

		Criteria criteria = session.createCriteria(StockOpnameHeader.class);

		if (beginDate != null) {
			criteria.add(Restrictions.ge("lastUpdatedTimestamp", beginDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le("lastUpdatedTimestamp", endDate));
		}

		@SuppressWarnings("unchecked")
		List<StockOpnameHeader> stockOpnameHeaders = criteria.list();

		return stockOpnameHeaders;
	}

	private BigDecimal countStockAmountEnd(Item item, BigDecimal overAmount,
			BigDecimal lossAmount, StockQueueMethod stockQueueMethod,
			Session session) throws AppException {

		int itemId = item.getId();
		int stock = StockFacade.getInstance().calculateStock(item);

		StockOpnameHeader lastStockOpnameHeader = getLastStockOpnameHeader(session);
		StockOpnameDetail lastStockOpnameDetail = null;
		if (lastStockOpnameHeader != null) {
			lastStockOpnameDetail = getStockOpnameDetail(
					lastStockOpnameHeader.getId(), itemId, session);
		}

		BigDecimal stockAmountBegin = null;
		if (lastStockOpnameDetail == null) {
			stockAmountBegin = BigDecimal.valueOf(0);
		} else if (lastStockOpnameDetail.getLastStockAmount() == null) {
			stockAmountBegin = BigDecimal.valueOf(0);
		} else {
			stockAmountBegin = lastStockOpnameDetail.getLastStockAmount();
		}

		BigDecimal lastStockAmountBySystem = null;
		if (stockQueueMethod == StockQueueMethod.FIFO) {
			lastStockAmountBySystem = getFifoAmount(stock, stock, itemId,
					session);
		} else if (stockQueueMethod == StockQueueMethod.LIFO) {
			lastStockAmountBySystem = getLifoAmount(stock, itemId, session);
		} else {
			throw new AppException("Unsupported stock queue method: "
					+ stockQueueMethod);
		}

		BigDecimal stockAmountEnd = stockAmountBegin
				.add(lastStockAmountBySystem).add(overAmount)
				.subtract(lossAmount);

		return stockAmountEnd;
	}

	private BigDecimal getFifoAmount(int required, int quantityInThisStage,
			int itemId, Session session) {

		List<BigDecimal> amountContainer = new ArrayList<BigDecimal>();

		Integer belowThisPurchaseDetailId = null;
		getFifoAmountRecursive(required, quantityInThisStage, itemId,
				amountContainer, belowThisPurchaseDetailId, session);

		BigDecimal fifoAmount = BigDecimal.valueOf(0);
		for (BigDecimal amount : amountContainer) {
			fifoAmount.add(amount);
		}
		return fifoAmount;
	}

	private Integer getFifoAmountRecursive(int required,
			int quantityInThisStage, int itemId,
			List<BigDecimal> amountContainer,
			Integer belowThisPurchaseDetailId, Session session) {

		PurchaseDetail purchaseDetail = getLastPurchaseDetail(itemId,
				belowThisPurchaseDetailId, session);

		Integer available;
		if (quantityInThisStage > purchaseDetail.getQuantity()) {
			int quantityInPreviousStage = quantityInThisStage
					- purchaseDetail.getQuantity();
			int needMore = getFifoAmountRecursive(required,
					quantityInPreviousStage, itemId, amountContainer,
					purchaseDetail.getId(), session);
			required = needMore;
			available = purchaseDetail.getQuantity();

		} else {
			available = quantityInThisStage;
		}

		Integer taken;
		Integer needMore;
		if (required > available) {
			taken = available;
			needMore = required - taken;
		} else {
			taken = required;
			needMore = 0;
		}
		BigDecimal amount = BigDecimal.valueOf(taken).multiply(
				purchaseDetail.getPricePerUnit());
		amountContainer.add(amount);

		return needMore;
	}

	private PurchaseDetail getLastPurchaseDetail(int itemId,
			Integer belowThisPurchaseDetailId, Session session) {
		Criteria criteria = session.createCriteria(PurchaseDetail.class);
		if (belowThisPurchaseDetailId != null) {
			criteria.add(Restrictions.lt("id", belowThisPurchaseDetailId));
		}
		criteria.add(Restrictions.eq("itemId", itemId));
		criteria.addOrder(Order.desc("id"));
		criteria.setMaxResults(1);

		PurchaseDetail purchaseDetail = (PurchaseDetail) criteria
				.uniqueResult();
		return purchaseDetail;
	}

	private StockOpnameHeader getLastStockOpnameHeader(Session session) {
		Criteria criteria = session.createCriteria(StockOpnameHeader.class);
		criteria.addOrder(Order.desc("id"));
		criteria.setMaxResults(1);
		StockOpnameHeader stockOpnameHeader = (StockOpnameHeader) criteria
				.uniqueResult();
		return stockOpnameHeader;
	}

	private BigDecimal getLifoAmount(int quantity, int itemId, Session session) {

		Integer belowThisPurchaseDetailId = null;
		BigDecimal amount = BigDecimal.valueOf(0);

		while (quantity > 0) {
			PurchaseDetail purchaseDetail = getLastPurchaseDetail(itemId,
					belowThisPurchaseDetailId, session);

			if (quantity > purchaseDetail.getQuantity()) {
				amount = amount.add(purchaseDetail.getPricePerUnit().multiply(
						BigDecimal.valueOf(purchaseDetail.getQuantity())));
				quantity -= purchaseDetail.getQuantity();
			} else {
				amount = amount.add(purchaseDetail.getPricePerUnit().multiply(
						BigDecimal.valueOf(quantity)));
				quantity = 0;
			}

			belowThisPurchaseDetailId = purchaseDetail.getId();
		}

		return amount;
	}

	private StockOpnameDetail getStockOpnameDetail(int stockOpnameHeaderId,
			int itemStockId, Session session) {

		Criteria criteria = session.createCriteria(StockOpnameDetail.class);
		criteria.createAlias("stockOpnameHeader", "stockOpnameHeader");
		criteria.createAlias("itemStock", "itemStock");

		criteria.add(Restrictions.eq("stockOpnameHeader.id",
				stockOpnameHeaderId));
		criteria.add(Restrictions.eq("itemStock.id", itemStockId));

		StockOpnameDetail stockOpnameDetail = (StockOpnameDetail) criteria
				.uniqueResult();
		return stockOpnameDetail;
	}

	public static enum StockQueueMethod {
		FIFO, LIFO;
	}
}
