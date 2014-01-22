package com.ganesha.accounting.minimarket.facade;

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

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.model.PurchaseDetail;
import com.ganesha.accounting.minimarket.ui.forms.forms.reports.ReportViewerDialog;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HqlParameter;

public class PurchaseReportFacade implements TransactionReportFacade {

	private static final String REPORT_NAME = "Laporan Pembelian";
	private static PurchaseReportFacade instance;

	public static PurchaseReportFacade getInstance() {
		if (instance == null) {
			instance = new PurchaseReportFacade();
		}
		return instance;
	}

	private PurchaseReportFacade() {
	}

	public PurchaseDetail getDetail(String transactionNumber, Integer orderNum,
			Session session) {
		Criteria criteria = session.createCriteria(PurchaseDetail.class);
		criteria.createAlias("purchaseHeader", "purchaseHeader");
		criteria.add(Restrictions.eq("purchaseHeader.transactionNumber",
				transactionNumber));
		criteria.add(Restrictions.eq("orderNum", orderNum));

		PurchaseDetail purchaseDetail = (PurchaseDetail) criteria
				.uniqueResult();
		return purchaseDetail;
	}

	public JasperPrint prepareJasper(String transactionNumber, Date beginDate,
			Date endDate, Session session) throws AppException {

		Map<String, Object> paramReport = new HashMap<String, Object>();
		paramReport.put("companyName", Main.getCompany().getName());
		paramReport.put("reportName", REPORT_NAME);
		paramReport.put("reportPeriodBegin", beginDate);
		paramReport.put("reportPeriodEnd", endDate);
		paramReport.put("reportBy", Main.getUserLogin().getName());
		paramReport.put("reportDate", CommonUtils.getCurrentDate());

		List<PurchaseDetail> purchaseDetails = search(transactionNumber,
				beginDate, endDate, session);

		InputStream inputStream = null;
		try {
			inputStream = this
					.getClass()
					.getClassLoader()
					.getResourceAsStream(
							"com/ganesha/accounting/minimarket/reports/PurchaseReport.jrxml");

			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(jasperDesign);

			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, paramReport, new JRBeanCollectionDataSource(
							purchaseDetails));

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
	public void printReport(String transactionNumber, Date beginDate,
			Date endDate, Session session) throws AppException, UserException {

		JasperPrint jasperPrint = prepareJasper(transactionNumber, beginDate,
				endDate, session);

		JRViewer jrViewer = new JRViewer(jasperPrint);
		ReportViewerDialog dialog = new ReportViewerDialog(null, REPORT_NAME,
				jrViewer);
		dialog.setVisible(true);
	}

	public List<PurchaseDetail> search(String transactionNumber,
			Date beginDate, Date endDate, Session session) {

		Criteria criteria = session.createCriteria(PurchaseDetail.class);
		criteria.createAlias("purchaseHeader", "purchaseHeader");

		if (!transactionNumber.trim().equals("")) {
			criteria.add(Restrictions.like("transactionNumer",
					transactionNumber).ignoreCase());
		}

		if (beginDate != null) {
			criteria.add(Restrictions.ge("purchaseHeader.transactionTimestamp",
					beginDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le("purchaseHeader.transactionTimestamp",
					endDate));
		}

		@SuppressWarnings("unchecked")
		List<PurchaseDetail> purchaseDetails = criteria.list();

		return purchaseDetails;
	}

	@Override
	public List<Map<String, Object>> searchTransaction(
			String transactionNumber, Date beginDate, Date endDate,
			Session session) {

		String sqlString = "SELECT new Map("
				+ "header.transactionNumber AS transactionNumber"
				+ ", header.transactionTimestamp AS transactionTimestamp"
				+ ", supplier.code AS clientCode"
				+ ", supplier.name AS clientName"
				+ ", header.totalAmount AS totalAmount"
				+ ") FROM PurchaseHeader header INNER JOIN header.supplier supplier WHERE 1=1";

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
		/*
		 * TODO
		 */
	}
}
