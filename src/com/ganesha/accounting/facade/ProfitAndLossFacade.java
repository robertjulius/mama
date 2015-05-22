package com.ganesha.accounting.facade;

import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.constants.CoaCodeConstants;
import com.ganesha.accounting.constants.Enums.DebitCreditFlag;
import com.ganesha.accounting.model.ExpenseTransaction;
import com.ganesha.accounting.model.ProfitAndLossStatement;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.ItemFacade;
import com.ganesha.minimarket.ui.forms.reports.ReportViewerDialog;

public class ProfitAndLossFacade {

	private static final String REPORT_NAME = "Laporan Laba Rugi";
	private static final String REPORT_FILE = "com/ganesha/minimarket/reports/ProfitAndLossReport.jrxml";

	private Calendar calendarHelper;

	private static ProfitAndLossFacade instance;

	public static ProfitAndLossFacade getInstance() {
		if (instance == null) {
			instance = new ProfitAndLossFacade();
		}
		return instance;
	}

	private ProfitAndLossFacade() {
		calendarHelper = Calendar.getInstance();
	}

	public ProfitAndLossStatement getProfitAndSaveProfitAndLossStatement(
			Calendar period, Session session) throws AppException {

		ProfitAndLossStatement lastProfitAndLossStatement = getLastProfitAndLossStatement(session);

		Calendar lastStatementPeriod = Calendar.getInstance();
		DateUtils.setCalendarMonthAndYearOnly(lastStatementPeriod,
				lastProfitAndLossStatement.getPeriodMonth(),
				lastProfitAndLossStatement.getPeriodYear());

		if (lastStatementPeriod.before(period)) {
			ProfitAndLossStatement newProfitAndLossStatement = null;
			while (lastStatementPeriod.before(period)) {
				lastStatementPeriod.add(Calendar.MONTH, 1);

				newProfitAndLossStatement = create(lastStatementPeriod,
						lastProfitAndLossStatement, session);
				lastProfitAndLossStatement = newProfitAndLossStatement;

				newProfitAndLossStatement.setLastUpdatedBy(Main.getUserLogin()
						.getId());

				newProfitAndLossStatement.setLastUpdatedTimestamp(DateUtils
						.getCurrent(Timestamp.class));

				session.saveOrUpdate(newProfitAndLossStatement);
			}
			return newProfitAndLossStatement;
		} else {
			ProfitAndLossStatement profitAndLossStatement = getProfitAndLossStatementByPeriod(
					period, session);
			return profitAndLossStatement;
		}
	}

	public void previewReport(Window parent,
			ProfitAndLossStatement profitAndLossStatement) throws AppException {
		JasperPrint jasperPrint = prepareJasper(profitAndLossStatement);
		JRViewer viewer = new JRViewer(jasperPrint);
		ReportViewerDialog.viewReport(parent, REPORT_NAME, viewer);
	}

	private ProfitAndLossStatement create(Calendar period,
			ProfitAndLossStatement previousProfitAndLossStatement,
			Session session) throws AppException {
		ProfitAndLossStatement profitAndLossStatement = new ProfitAndLossStatement();

		profitAndLossStatement.setPeriodYear(period.get(Calendar.YEAR));
		profitAndLossStatement.setPeriodMonth(period.get(Calendar.MONTH));

		calendarHelper.setTime(period.getTime());

		calendarHelper.set(Calendar.DATE, 1);
		calendarHelper.set(Calendar.HOUR_OF_DAY, 0);
		calendarHelper.set(Calendar.MINUTE, 0);
		calendarHelper.set(Calendar.SECOND, 0);
		calendarHelper.set(Calendar.MILLISECOND, 0);

		Timestamp beginTimestamp = new Timestamp(
				calendarHelper.getTimeInMillis());

		calendarHelper.add(Calendar.MONTH, 1);
		calendarHelper.add(Calendar.DATE, -1);
		calendarHelper.set(Calendar.HOUR_OF_DAY, 23);
		calendarHelper.set(Calendar.MINUTE, 59);
		calendarHelper.set(Calendar.SECOND, 59);
		calendarHelper.set(Calendar.MILLISECOND, 999);

		Timestamp endTimestamp = new Timestamp(calendarHelper.getTimeInMillis());

		BigDecimal penjualan = AccountFacade.getInstance().getAccountSum(
				CoaCodeConstants.PENJUALAN, null, beginTimestamp, endTimestamp,
				DebitCreditFlag.CREDIT, session);
		profitAndLossStatement.setPenjualan(penjualan);

		BigDecimal potonganPenjualan = AccountFacade.getInstance()
				.getAccountSum(CoaCodeConstants.DISKON_PENJUALAN, null,
						beginTimestamp, endTimestamp, DebitCreditFlag.DEBIT,
						session);
		profitAndLossStatement.setPotonganPenjualan(potonganPenjualan);

		BigDecimal returPenjualan = AccountFacade.getInstance().getAccountSum(
				CoaCodeConstants.RETUR_PENJUALAN, null, beginTimestamp,
				endTimestamp, DebitCreditFlag.DEBIT, session);
		profitAndLossStatement.setReturPenjualan(returPenjualan);

		BigDecimal penjualanBersih = penjualan.subtract(potonganPenjualan)
				.subtract(returPenjualan);
		profitAndLossStatement.setPenjualanBersih(penjualanBersih);

		BigDecimal persediaanAwal = previousProfitAndLossStatement
				.getPersediaanAkhir();
		profitAndLossStatement.setPersediaanAwal(persediaanAwal);

		BigDecimal pembelian = AccountFacade.getInstance().getAccountSum(
				CoaCodeConstants.PEMBELIAN, null, beginTimestamp, endTimestamp,
				DebitCreditFlag.DEBIT, session);
		profitAndLossStatement.setPembelian(pembelian);

		BigDecimal potonganPembelian = AccountFacade.getInstance()
				.getAccountSum(CoaCodeConstants.DISKON_PEMBELIAN, null,
						beginTimestamp, endTimestamp, DebitCreditFlag.CREDIT,
						session);
		profitAndLossStatement.setPotonganPembelian(potonganPembelian);

		BigDecimal returPembelian = AccountFacade.getInstance().getAccountSum(
				CoaCodeConstants.RETUR_PEMBELIAN, null, beginTimestamp,
				endTimestamp, DebitCreditFlag.CREDIT, session);
		profitAndLossStatement.setReturPembelian(returPembelian);

		BigDecimal persediaanTotal = persediaanAwal.add(pembelian)
				.subtract(potonganPembelian).subtract(returPembelian);
		profitAndLossStatement.setPersediaanTotal(persediaanTotal);

		BigDecimal persediaanAkhir = ItemFacade.getInstance()
				.calculateAmountOfAllItem(session);
		profitAndLossStatement.setPersediaanAkhir(persediaanAkhir);

		BigDecimal hpp = persediaanTotal.subtract(persediaanAkhir);
		profitAndLossStatement.setHpp(hpp);

		BigDecimal labaKotor = penjualanBersih.subtract(hpp);
		profitAndLossStatement.setLabaKotor(labaKotor);

		List<ExpenseTransaction> expenseTransactions = ExpenseFacade
				.getInstance().getTransactionListByTimestamp(beginTimestamp,
						endTimestamp, session);
		BigDecimal bebanOperasi = BigDecimal.valueOf(0);
		for (ExpenseTransaction expenseTransaction : expenseTransactions) {
			bebanOperasi = bebanOperasi.add(expenseTransaction.getAmount());
		}
		profitAndLossStatement.setBebanOperasi(bebanOperasi);

		BigDecimal labaBersih = labaKotor.subtract(bebanOperasi);
		profitAndLossStatement.setLabaBersih(labaBersih);

		return profitAndLossStatement;
	}

	private ProfitAndLossStatement getLastProfitAndLossStatement(Session session) {

		Criteria criteria = session
				.createCriteria(ProfitAndLossStatement.class);

		@SuppressWarnings("unchecked")
		List<ProfitAndLossStatement> profitAndLossStatements = criteria.list();

		ProfitAndLossStatement profitAndLossStatement = profitAndLossStatements
				.get(profitAndLossStatements.size() - 1);

		return profitAndLossStatement;
	}

	private ProfitAndLossStatement getProfitAndLossStatementByPeriod(
			Calendar period, Session session) {

		int periodYear = period.get(Calendar.YEAR);
		int periodMonth = period.get(Calendar.MONTH);

		Criteria criteria = session
				.createCriteria(ProfitAndLossStatement.class);

		criteria.add(Restrictions.eq("periodYear", periodYear));
		criteria.add(Restrictions.eq("periodMonth", periodMonth));
		ProfitAndLossStatement profitAndLossStatement = (ProfitAndLossStatement) criteria
				.uniqueResult();

		return profitAndLossStatement;
	}

	private JasperPrint prepareJasper(
			ProfitAndLossStatement profitAndLossStatement) throws AppException {

		calendarHelper.set(Calendar.MONTH,
				profitAndLossStatement.getPeriodMonth());
		calendarHelper.set(Calendar.YEAR,
				profitAndLossStatement.getPeriodYear());

		Map<String, Object> paramReport = new HashMap<String, Object>();
		paramReport.put("companyName", Main.getCompany().getName());
		paramReport.put("reportName", REPORT_NAME);
		paramReport.put("month", calendarHelper.getDisplayName(Calendar.MONTH,
				Calendar.LONG, Locale.getDefault()));
		paramReport.put("year", profitAndLossStatement.getPeriodYear());
		paramReport.put("reportBy", Main.getUserLogin().getName());
		paramReport.put("reportDate", DateUtils.getCurrent(Date.class));

		paramReport.put("penjualan", profitAndLossStatement.getPenjualan());
		paramReport.put("potonganPenjualan",
				profitAndLossStatement.getPotonganPenjualan());
		paramReport.put("returPenjualan",
				profitAndLossStatement.getReturPenjualan());
		paramReport.put("penjualanBersih",
				profitAndLossStatement.getPenjualanBersih());

		paramReport.put("persediaanAwal",
				profitAndLossStatement.getPersediaanAwal());
		paramReport.put("pembelian", profitAndLossStatement.getPembelian());
		paramReport.put("potonganPembelian",
				profitAndLossStatement.getPotonganPembelian());
		paramReport.put("returPembelian",
				profitAndLossStatement.getReturPembelian());
		paramReport.put("persediaanTotal",
				profitAndLossStatement.getPersediaanTotal());
		paramReport.put("persediaanAkhir",
				profitAndLossStatement.getPersediaanAkhir());
		paramReport.put("hpp", profitAndLossStatement.getHpp());

		paramReport.put("labaKotor", profitAndLossStatement.getLabaKotor());
		paramReport.put("bebanOperasi",
				profitAndLossStatement.getBebanOperasi());
		paramReport.put("labaBersih", profitAndLossStatement.getLabaBersih());

		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getClassLoader()
					.getResourceAsStream(REPORT_FILE);

			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(jasperDesign);

			JasperPrint jasperPrint = JasperFillManager.fillReport(
					jasperReport, paramReport, new JRBeanCollectionDataSource(
							new ArrayList<Object>()));

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
