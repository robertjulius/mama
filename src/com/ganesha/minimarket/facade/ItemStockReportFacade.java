package com.ganesha.minimarket.facade;

import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
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
import net.sf.jasperreports.swing.JRViewer;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.ui.forms.reports.ReportViewerDialog;

public class ItemStockReportFacade {

	private static final String REPORT_NAME = "Laporan Stok Barang";
	private static final String REPORT_FILE = "com/ganesha/minimarket/reports/StockReport.jrxml";

	private static ItemStockReportFacade instance;

	public static ItemStockReportFacade getInstance() {
		if (instance == null) {
			instance = new ItemStockReportFacade();
		}
		return instance;
	}

	private ItemStockReportFacade() {
	}

	public void previewReport(Window parent, String orderById,
			String orderByText, Session session) throws AppException,
			UserException {

		JasperPrint jasperPrint = prepareJasper(orderById, orderByText, session);

		JRViewer viewer = new JRViewer(jasperPrint);
		ReportViewerDialog.viewReport(parent, REPORT_NAME, viewer);
	}

	public List<Map<String, Object>> search(String orderBy, Session session) {

		String sql = "SELECT "
				+ " id "
				+ ", code "
				+ ", name "
				+ ", unit "
				+ ", IF(quantity IS NULL, 0, quantity) AS quantity "
				+ "FROM ( "
				+ "SELECT it.id, it.code, it.name, it.unit, SUM(ist.QUANTITY) AS quantity "
				+ "FROM items it "
				+ "LEFT JOIN item_stocks ist ON ist.ITEM_ID = it.ID "
				+ "WHERE it.DISABLED = false AND it.DELETED = false "
				+ "GROUP BY it.id, it.code, it.name, it.unit " + ") myTable "
				+ "ORDER BY " + orderBy;

		SQLQuery query = session.createSQLQuery(sql);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> itemMaps = query.list();

		return itemMaps;
	}

	private JasperPrint prepareJasper(String orderById, String orderByText,
			Session session) throws AppException {

		Map<String, Object> paramReport = new HashMap<String, Object>();
		paramReport.put("companyName", Main.getCompany().getName());
		paramReport.put("reportName", REPORT_NAME);
		paramReport.put("orderBy", orderByText);
		paramReport.put("reportBy", Main.getUserLogin().getName());
		paramReport.put("reportDate", DateUtils.getCurrentDate());

		List<Map<String, Object>> items = search(orderById, session);

		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getClassLoader()
					.getResourceAsStream(REPORT_FILE);

			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(jasperDesign);

			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, paramReport, new JRBeanCollectionDataSource(
							items));

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
}
