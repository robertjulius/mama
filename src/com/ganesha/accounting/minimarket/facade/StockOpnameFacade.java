package com.ganesha.accounting.minimarket.facade;

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

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.accounting.minimarket.model.StockOpnameDetail;
import com.ganesha.accounting.minimarket.model.StockOpnameHeader;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.CommonUtils;

public class StockOpnameFacade {

	private static final String REPORT_NAME = "Laporan Stok Opname";
	private static final String REPORT_FILE = "com/ganesha/accounting/minimarket/reports/StockOpnameReport.jrxml";

	private static StockOpnameFacade instance;

	public static StockOpnameFacade getInstance() {
		if (instance == null) {
			instance = new StockOpnameFacade();
		}
		return instance;
	}

	private StockOpnameFacade() {
	}

	public StockOpnameDetail createStockOpnameEntity(String itemCode,
			int quantityManual, int overCount, int lossCount, Session session)
			throws AppException {

		ItemStock itemStock = StockFacade.getInstance().getDetail(itemCode,
				session);

		BigDecimal hpp = countHpp();
		itemStock.setHpp(hpp);

		BigDecimal overAmount = hpp.multiply(BigDecimal.valueOf(overCount));
		BigDecimal lossAmount = hpp.multiply(BigDecimal.valueOf(lossCount));

		StockOpnameDetail stockOpname = new StockOpnameDetail();
		stockOpname.setItemStock(itemStock);
		stockOpname.setQuantitySystem(itemStock.getStock());
		stockOpname.setQuantityManual(quantityManual);
		stockOpname.setOverCount(overCount);
		stockOpname.setOverAmount(overAmount);
		stockOpname.setLossCount(lossCount);
		stockOpname.setLossAmount(lossAmount);

		return stockOpname;
	}

	public JasperPrint prepareJasper(List<StockOpnameDetail> stockOpnameDetails,
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

		int lastUpdatedBy = Main.getUserLogin().getId();
		Timestamp lastUpdatedTimestamp = CommonUtils.getCurrentTimestamp();

		StockOpnameHeader stockOpnameHeader = new StockOpnameHeader();
		stockOpnameHeader.setPerformedBy(Main.getUserLogin());
		stockOpnameHeader.setPerformedBeginTimestamp(performedBeginTimestamp);
		stockOpnameHeader.setPerformedEndTimestamp(performedEndTimestamp);
		stockOpnameHeader.setDisabled(false);
		stockOpnameHeader.setDeleted(false);
		stockOpnameHeader.setLastUpdatedBy(Main.getUserLogin().getId());
		stockOpnameHeader.setLastUpdatedTimestamp(CommonUtils
				.getCurrentTimestamp());

		session.saveOrUpdate(stockOpnameHeader);

		for (StockOpnameDetail stockOpname : stockOpnameDetails) {
			ItemStock itemStock = stockOpname.getItemStock();
			itemStock.setStock(stockOpname.getQuantityManual());
			session.saveOrUpdate(itemStock);

			stockOpname.setStockOpnameHeader(stockOpnameHeader);
			stockOpname.setLastUpdatedBy(lastUpdatedBy);
			stockOpname.setLastUpdatedTimestamp(lastUpdatedTimestamp);
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

	private BigDecimal countHpp() {
		/*
		 * TODO
		 */
		return BigDecimal.valueOf(0);
	}
}
