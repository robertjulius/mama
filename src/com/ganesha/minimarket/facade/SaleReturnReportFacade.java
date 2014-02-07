package com.ganesha.minimarket.facade;

import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
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
import net.sf.jasperreports.swing.JRViewer;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.SaleDetail;
import com.ganesha.minimarket.model.SaleReturnDetail;
import com.ganesha.minimarket.ui.forms.reports.ReportViewerDialog;

public class SaleReturnReportFacade implements TransactionReportFacade {

	private static final String REPORT_NAME = "Laporan Retur Penjualan";
	private static final String REPORT_FILE = "com/ganesha/minimarket/reports/SaleReturnReport.jrxml";

	private static SaleReturnReportFacade instance;

	public static SaleReturnReportFacade getInstance() {
		if (instance == null) {
			instance = new SaleReturnReportFacade();
		}
		return instance;
	}

	private SaleReturnReportFacade() {
	}

	public SaleDetail getDetail(String transactionNumber, Integer orderNum,
			Session session) {
		Criteria criteria = session.createCriteria(SaleDetail.class);
		criteria.createAlias("saleReturnHeader", "saleReturnHeader");
		criteria.add(Restrictions.eq("saleReturnHeader.transactionNumber",
				transactionNumber));
		criteria.add(Restrictions.eq("orderNum", orderNum));

		SaleDetail saleDetail = (SaleDetail) criteria.uniqueResult();
		return saleDetail;
	}

	private JasperPrint prepareJasper(String transactionNumber, Date beginDate,
			Date endDate, Session session) throws AppException {

		Map<String, Object> paramReport = new HashMap<String, Object>();
		paramReport.put("companyName", Main.getCompany().getName());
		paramReport.put("reportName", REPORT_NAME);
		paramReport.put("reportPeriodBegin", beginDate);
		paramReport.put("reportPeriodEnd", endDate);
		paramReport.put("reportBy", Main.getUserLogin().getName());
		paramReport.put("reportDate", CommonUtils.getCurrentDate());

		List<SaleReturnDetail> saleReturnDetails = search(transactionNumber,
				beginDate, endDate, session);

		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getClassLoader()
					.getResourceAsStream(REPORT_FILE);

			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(jasperDesign);

			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, paramReport, new JRBeanCollectionDataSource(
							saleReturnDetails));

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

	@Override
	public void previewReport(Window parent, String transactionNumber,
			Date beginDate, Date endDate, Session session) throws AppException,
			UserException {

		JasperPrint jasperPrint = prepareJasper(transactionNumber, beginDate,
				endDate, session);

		JRViewer viewer = new JRViewer(jasperPrint);
		ReportViewerDialog.viewReport(parent, REPORT_NAME, viewer);
	}

	public List<SaleReturnDetail> search(String transactionNumber,
			Date beginDate, Date endDate, Session session) {

		Criteria criteria = session.createCriteria(SaleReturnDetail.class);
		criteria.createAlias("saleReturnHeader", "saleReturnHeader");

		if (!transactionNumber.trim().equals("")) {
			criteria.add(Restrictions.like("transactionNumer",
					transactionNumber).ignoreCase());
		}

		if (beginDate != null) {
			criteria.add(Restrictions.ge(
					"saleReturnHeader.transactionTimestamp", beginDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le(
					"saleReturnHeader.transactionTimestamp", endDate));
		}

		@SuppressWarnings("unchecked")
		List<SaleReturnDetail> saleReturnDetails = criteria.list();

		return saleReturnDetails;
	}

	@Override
	public List<Map<String, Object>> searchTransaction(
			String transactionNumber, Date beginDate, Date endDate,
			Session session) {

		String sqlString = "SELECT new Map("
				+ "header.transactionNumber AS transactionNumber"
				+ ", header.transactionTimestamp AS transactionTimestamp"
				+ ", customer.code AS clientCode"
				+ ", customer.name AS clientName"
				+ ", header.totalReturnAmount AS totalAmount"
				+ ") FROM SaleReturnHeader header INNER JOIN header.customer customer WHERE 1=1";

		if (!transactionNumber.trim().equals("")) {
			sqlString += " AND header.transactionNumber LIKE :transactionNumber";
		}

		if (beginDate != null) {
			sqlString += " AND header.transactionTimestamp >= :beginDate";
		}

		if (endDate != null) {
			sqlString += " AND header.transactionTimestamp <= :endDate";
		}

		Query query = session.createQuery(sqlString);
		HqlParameter parameter = new HqlParameter(query);
		parameter.put("transactionNumber", "%" + transactionNumber + "%");
		parameter.put("beginDate", beginDate);
		parameter.put("endDate", endDate);
		parameter.validate();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = query.list();

		return list;
	}

	@Override
	public void showDetail(String transactionNumber) throws AppException,
			UserException {
		// TODO Auto-generated method stub
	}
}
