package com.ganesha.accounting.minimarket.facade;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.minimarket.Main;
import com.ganesha.accounting.minimarket.model.SaleDetail;
import com.ganesha.accounting.minimarket.model.SaleReturnDetail;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.hibernate.HqlParameter;

public class SaleReturnReportFacade implements TransactionReportFacade {

	private static final String REPORT_NAME = "Laporan Retur Penjualan";
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

	public JasperPrint prepareJasper(String transactionNumber, Date beginDate,
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
			inputStream = this
					.getClass()
					.getClassLoader()
					.getResourceAsStream(
							"com/ganesha/accounting/minimarket/reports/mama.jasper");

			JasperReport jasperReport = (JasperReport) JRLoader
					.loadObject(inputStream);

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
	public void printReport(String transactionNumber, Date beginDate,
			Date endDate, Session session) throws AppException, UserException {
		JasperPrint jasperPrint = prepareJasper(transactionNumber, beginDate,
				endDate, session);
		JasperViewer.viewReport(jasperPrint);
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
