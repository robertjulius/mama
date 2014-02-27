package com.ganesha.minimarket.facade;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemStock;
import com.ganesha.minimarket.model.StockOpnameDetail;
import com.ganesha.minimarket.model.StockOpnameHeader;

public class StockOpnameFacade {

	private static final String REPORT_NAME = "Laporan Stock Opname";
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
			int quantityManual, int overCount, int lossCount, Session session)
			throws AppException {

		Item item = ItemFacade.getInstance().getDetail(itemId, session);
		int quantityInThisStage = ItemFacade.getInstance().calculateStock(item);

		BigDecimal overAmount = calculateOverAmount(item, overCount);
		BigDecimal lossAmount = calculateLossAmount(item, lossCount);

		item.setLastUpdatedBy(Main.getUserLogin().getId());
		item.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		StockOpnameDetail stockOpname = new StockOpnameDetail();
		stockOpname.setItem(item);
		stockOpname.setQuantitySystem(quantityInThisStage);
		stockOpname.setQuantityManual(quantityManual);
		stockOpname.setOverCount(overCount);
		stockOpname.setOverAmount(overAmount);
		stockOpname.setLossCount(lossCount);
		stockOpname.setLossAmount(lossAmount);

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

	public StockOpnameHeader save(List<StockOpnameDetail> stockOpnameDetails,
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
			List<ItemStock> itemStocks = item.getItemStocks();
			for (ItemStock itemStock : itemStocks) {
				session.saveOrUpdate(itemStock);
			}
			session.saveOrUpdate(item);

			stockOpname.setStockOpnameHeader(stockOpnameHeader);
			session.saveOrUpdate(stockOpname);
		}

		return stockOpnameHeader;
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

	private BigDecimal calculateLossAmount(Item item, int lossStock) {
		BigDecimal amount = BigDecimal.valueOf(0);

		int requirement = lossStock;
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
			amount = amount.add(BigDecimal.valueOf(taken).multiply(
					itemStock.getPurchaseDetail().getPricePerUnit()));
		}
		return amount;
	}

	private BigDecimal calculateOverAmount(Item item, int overStock) {
		BigDecimal amount = BigDecimal.valueOf(0);

		int excess = overStock;
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
			excess = remain;
			amount = amount.add(BigDecimal.valueOf(taken).multiply(
					itemStock.getPurchaseDetail().getPricePerUnit()));
		}
		return amount;
	}
}