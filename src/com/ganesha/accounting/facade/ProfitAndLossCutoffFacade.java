package com.ganesha.accounting.facade;

import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;

import com.ganesha.accounting.constants.CoaCodeConstants;
import com.ganesha.accounting.constants.Enums.DebitCreditFlag;
import com.ganesha.accounting.model.ExpenseTransaction;
import com.ganesha.accounting.model.ProfitAndLossCutoff;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.ItemFacade;
import com.ganesha.minimarket.facade.UserFacade;
import com.ganesha.minimarket.ui.forms.reports.ReportViewerDialog;
import com.ganesha.model.User;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

public class ProfitAndLossCutoffFacade {

	private static final String REPORT_NAME = "Laporan Laba Rugi";
	private static final String REPORT_FILE = "com/ganesha/minimarket/reports/ProfitAndLossCutoffReport.jrxml";

	private static ProfitAndLossCutoffFacade instance;

	public static ProfitAndLossCutoffFacade getInstance() {
		if (instance == null) {
			instance = new ProfitAndLossCutoffFacade();
		}
		return instance;
	}

	public ProfitAndLossCutoff getDetail(int id, Session session) {
		ProfitAndLossCutoff profitAndLossCutoff = (ProfitAndLossCutoff) session.get(ProfitAndLossCutoff.class, id);
		return profitAndLossCutoff;
	}

	public ProfitAndLossCutoff performProfitAndLossCutoff(int performedBy, Session session) throws AppException {

		ProfitAndLossCutoff previousProfitAndLossCutoff = getLastProfitAndLossCutoff(session);

		ProfitAndLossCutoff profitAndLossCutoff = create(DateUtils.getCurrent(Timestamp.class),
				previousProfitAndLossCutoff, session);

		profitAndLossCutoff.setLastUpdatedBy(performedBy);

		profitAndLossCutoff.setLastUpdatedTimestamp(DateUtils.getCurrent(Timestamp.class));

		session.saveOrUpdate(profitAndLossCutoff);

		return profitAndLossCutoff;
	}

	public void previewReport(Window parent, ProfitAndLossCutoff profitAndLossCutoff, Session session)
			throws AppException {
		JasperPrint jasperPrint = prepareJasper(profitAndLossCutoff, session);
		JRViewer viewer = new JRViewer(jasperPrint);
		ReportViewerDialog.viewReport(parent, REPORT_NAME, viewer);
	}

	public void previewReport(Window parent, List<ProfitAndLossCutoff> profitAndLossCutoffs)
			throws AppException, UserException {
		JasperPrint jasperPrint = prepareJasper(profitAndLossCutoffs);
		JRViewer viewer = new JRViewer(jasperPrint);
		ReportViewerDialog.viewReport(parent, REPORT_NAME, viewer);
	}

	public List<ProfitAndLossCutoff> search(Date beginDate, Date endDate, Session session) {

		final StringBuilder builder = new StringBuilder();
		builder.append(
				"SELECT curr.ID, curr.LAST_UPDATED_BY, curr.LAST_UPDATED_TIMESTAMP, curr.BEBAN_OPERASI, curr.CUTOFF_TIMESTAMP, curr.HPP, curr.LABA_BERSIH, curr.LABA_KOTOR, curr.PEMBELIAN, curr.PENJUALAN, curr.PENJUALAN_BERSIH, curr.PERSEDIAAN_AKHIR, curr.PERSEDIAAN_AWAL, curr.PERSEDIAAN_TOTAL, curr.POTONGAN_PEMBELIAN, curr.POTONGAN_PENJUALAN, curr.RETUR_PEMBELIAN, curr.RETUR_PENJUALAN, curr.PREVIOUS_CUTOFF_ID, prev.ID as PREVIOUS_ID, prev.CUTOFF_TIMESTAMP PREVIOUS_CUTOFF_TIMESTAMP");
		builder.append(
				" FROM profit_and_loss_cutoff prev INNER JOIN profit_and_loss_cutoff curr ON curr.previous_cutoff_id = prev.id WHERE 1=1");

		final List<Object> parameters = new ArrayList<>();
		if (beginDate != null) {
			builder.append(" AND curr.cutoff_timestamp >= ?");
			parameters.add(beginDate);
		}

		if (endDate != null) {
			builder.append(" AND prev.cutoff_timestamp <= ?");
			parameters.add(endDate);
		}

		final List<ProfitAndLossCutoff> cutoffs = new ArrayList<>();

		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				PreparedStatement stmt = connection.prepareStatement(builder.toString());
				for (int i = 0; i < parameters.size(); ++i) {
					stmt.setObject(i + 1, parameters.get(i));
				}
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					ProfitAndLossCutoff cutoff = new ProfitAndLossCutoff();
					cutoff.setId((Integer) rs.getObject(1));
					cutoff.setLastUpdatedBy((Integer) rs.getObject(2));
					cutoff.setLastUpdatedTimestamp((Timestamp) rs.getObject(3));
					cutoff.setBebanOperasi((BigDecimal) rs.getObject(4));
					cutoff.setCutoffTimestamp((Timestamp) rs.getObject(5));
					cutoff.setHpp((BigDecimal) rs.getObject(6));
					cutoff.setLabaBersih((BigDecimal) rs.getObject(7));
					cutoff.setLabaKotor((BigDecimal) rs.getObject(8));
					cutoff.setPembelian((BigDecimal) rs.getObject(9));
					cutoff.setPenjualan((BigDecimal) rs.getObject(10));
					cutoff.setPenjualanBersih((BigDecimal) rs.getObject(11));
					cutoff.setPersediaanAkhir((BigDecimal) rs.getObject(12));
					cutoff.setPersediaanAwal((BigDecimal) rs.getObject(13));
					cutoff.setPersediaanTotal((BigDecimal) rs.getObject(14));
					cutoff.setPotonganPembelian((BigDecimal) rs.getObject(15));
					cutoff.setPotonganPenjualan((BigDecimal) rs.getObject(16));
					cutoff.setReturPembelian((BigDecimal) rs.getObject(17));
					cutoff.setReturPenjualan((BigDecimal) rs.getObject(18));

					ProfitAndLossCutoff prevCutoff = new ProfitAndLossCutoff();
					prevCutoff.setId((Integer) rs.getObject(20));
					prevCutoff.setCutoffTimestamp((Timestamp) rs.getObject(21));

					cutoff.setPreviousProfitAndLossCutoff(prevCutoff);

					cutoffs.add(cutoff);
				}
			}
		});

		return cutoffs;
	}

	public List<ProfitAndLossCutoff> getListById(List<Integer> ids, Session session) {

		Criteria criteria = session.createCriteria(ProfitAndLossCutoff.class);
		criteria.add(Restrictions.isNotNull("previousProfitAndLossCutoff"));
		criteria.add(Restrictions.in("id", ids));

		@SuppressWarnings("unchecked")
		List<ProfitAndLossCutoff> profitAndLossCutoffs = criteria.list();

		return profitAndLossCutoffs;
	}

	public boolean isExists(Date beginDate, Date endDate, Session session) {

		StringBuilder sqlString = new StringBuilder();
		sqlString.append("SELECT COUNT(1) AS count FROM profit_and_loss_cutoff WHERE 1=1");

		if (beginDate != null) {
			sqlString.append(" AND cutoff_timestamp >= :beginDate");
		}

		if (endDate != null) {
			sqlString.append(" AND cutoff_timestamp <= :endDate");
		}

		SQLQuery sqlQuery = session.createSQLQuery(sqlString.toString());

		HqlParameter parameter = new HqlParameter(sqlQuery);
		parameter.put("beginDate", beginDate);
		parameter.put("endDate", endDate);
		parameter.validate();

		int count = ((BigInteger) sqlQuery.uniqueResult()).intValue();
		return count > 0;
	}

	private ProfitAndLossCutoff create(Timestamp cutoffTImestamp, ProfitAndLossCutoff previousProfitAndLossCutoff,
			Session session) throws AppException {

		ProfitAndLossCutoff profitAndLossCutoff = new ProfitAndLossCutoff();
		profitAndLossCutoff.setPreviousProfitAndLossCutoff(previousProfitAndLossCutoff);

		profitAndLossCutoff.setCutoffTimestamp(cutoffTImestamp);

		Timestamp beginTimestamp = previousProfitAndLossCutoff.getCutoffTimestamp();

		Timestamp endTimestamp = profitAndLossCutoff.getCutoffTimestamp();

		BigDecimal penjualan = AccountFacade.getInstance().getAccountSum(CoaCodeConstants.PENJUALAN, null,
				beginTimestamp, endTimestamp, DebitCreditFlag.CREDIT, session);
		profitAndLossCutoff.setPenjualan(penjualan);

		BigDecimal potonganPenjualan = AccountFacade.getInstance().getAccountSum(CoaCodeConstants.DISKON_PENJUALAN,
				null, beginTimestamp, endTimestamp, DebitCreditFlag.DEBIT, session);
		profitAndLossCutoff.setPotonganPenjualan(potonganPenjualan);

		BigDecimal returPenjualan = AccountFacade.getInstance().getAccountSum(CoaCodeConstants.RETUR_PENJUALAN, null,
				beginTimestamp, endTimestamp, DebitCreditFlag.DEBIT, session);
		profitAndLossCutoff.setReturPenjualan(returPenjualan);

		BigDecimal penjualanBersih = penjualan.subtract(potonganPenjualan).subtract(returPenjualan);
		profitAndLossCutoff.setPenjualanBersih(penjualanBersih);

		BigDecimal persediaanAwal = previousProfitAndLossCutoff.getPersediaanAkhir();
		profitAndLossCutoff.setPersediaanAwal(persediaanAwal);

		BigDecimal pembelian = AccountFacade.getInstance().getAccountSum(CoaCodeConstants.PEMBELIAN, null,
				beginTimestamp, endTimestamp, DebitCreditFlag.DEBIT, session);
		profitAndLossCutoff.setPembelian(pembelian);

		BigDecimal potonganPembelian = AccountFacade.getInstance().getAccountSum(CoaCodeConstants.DISKON_PEMBELIAN,
				null, beginTimestamp, endTimestamp, DebitCreditFlag.CREDIT, session);
		profitAndLossCutoff.setPotonganPembelian(potonganPembelian);

		BigDecimal returPembelian = AccountFacade.getInstance().getAccountSum(CoaCodeConstants.RETUR_PEMBELIAN, null,
				beginTimestamp, endTimestamp, DebitCreditFlag.CREDIT, session);
		profitAndLossCutoff.setReturPembelian(returPembelian);

		BigDecimal persediaanTotal = persediaanAwal.add(pembelian).subtract(potonganPembelian).subtract(returPembelian);
		profitAndLossCutoff.setPersediaanTotal(persediaanTotal);

		BigDecimal persediaanAkhir = ItemFacade.getInstance().calculateAmountOfAllItem(session);
		profitAndLossCutoff.setPersediaanAkhir(persediaanAkhir);

		BigDecimal hpp = persediaanTotal.subtract(persediaanAkhir);
		profitAndLossCutoff.setHpp(hpp);

		BigDecimal labaKotor = penjualanBersih.subtract(hpp);
		profitAndLossCutoff.setLabaKotor(labaKotor);

		List<ExpenseTransaction> expenseTransactions = ExpenseFacade.getInstance()
				.getTransactionListByTimestamp(beginTimestamp, endTimestamp, session);
		BigDecimal bebanOperasi = BigDecimal.valueOf(0);
		for (ExpenseTransaction expenseTransaction : expenseTransactions) {
			bebanOperasi = bebanOperasi.add(expenseTransaction.getAmount());
		}
		profitAndLossCutoff.setBebanOperasi(bebanOperasi);

		BigDecimal labaBersih = labaKotor.subtract(bebanOperasi);
		profitAndLossCutoff.setLabaBersih(labaBersih);

		return profitAndLossCutoff;
	}

	private ProfitAndLossCutoff getLastProfitAndLossCutoff(Session session) {

		Criteria criteria = session.createCriteria(ProfitAndLossCutoff.class);

		@SuppressWarnings("unchecked")
		List<ProfitAndLossCutoff> profitAndLossCutoffs = criteria.list();

		ProfitAndLossCutoff profitAndLossCutoff = profitAndLossCutoffs.get(profitAndLossCutoffs.size() - 1);

		return profitAndLossCutoff;
	}

	private JasperPrint prepareJasper(ProfitAndLossCutoff profitAndLossCutoff, Session session) throws AppException {

		Map<String, Object> paramReport = new HashMap<String, Object>();
		paramReport.put("companyName", Main.getCompany().getName());
		paramReport.put("reportName", REPORT_NAME);
		paramReport.put("periodBegin", profitAndLossCutoff.getPreviousProfitAndLossCutoff().getCutoffTimestamp());
		paramReport.put("periodEnd", profitAndLossCutoff.getCutoffTimestamp());

		User cutoffBy = UserFacade.getInstance().getDetail(profitAndLossCutoff.getLastUpdatedBy(), session);

		paramReport.put("cutoffBy", cutoffBy.getLogin() + " [" + cutoffBy.getName() + "]");

		paramReport.put("penjualan", profitAndLossCutoff.getPenjualan());
		paramReport.put("potonganPenjualan", profitAndLossCutoff.getPotonganPenjualan());
		paramReport.put("returPenjualan", profitAndLossCutoff.getReturPenjualan());
		paramReport.put("penjualanBersih", profitAndLossCutoff.getPenjualanBersih());

		paramReport.put("persediaanAwal", profitAndLossCutoff.getPersediaanAwal());
		paramReport.put("pembelian", profitAndLossCutoff.getPembelian());
		paramReport.put("potonganPembelian", profitAndLossCutoff.getPotonganPembelian());
		paramReport.put("returPembelian", profitAndLossCutoff.getReturPembelian());
		paramReport.put("persediaanTotal", profitAndLossCutoff.getPersediaanTotal());
		paramReport.put("persediaanAkhir", profitAndLossCutoff.getPersediaanAkhir());
		paramReport.put("hpp", profitAndLossCutoff.getHpp());

		paramReport.put("labaKotor", profitAndLossCutoff.getLabaKotor());
		paramReport.put("bebanOperasi", profitAndLossCutoff.getBebanOperasi());
		paramReport.put("labaBersih", profitAndLossCutoff.getLabaBersih());

		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(REPORT_FILE);

			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, paramReport,
					new JRBeanCollectionDataSource(new ArrayList<Object>()));

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

	private JasperPrint prepareJasper(List<ProfitAndLossCutoff> profitAndLossCutoffs)
			throws AppException, UserException {

		if (profitAndLossCutoffs.isEmpty()) {
			throw new UserException("No Profit and Loss Statement selected");
		}

		ProfitAndLossCutoff firstCutoff = profitAndLossCutoffs.get(0);
		ProfitAndLossCutoff lastCutoff = profitAndLossCutoffs.get(profitAndLossCutoffs.size() - 1);

		Map<String, Object> paramReport = new HashMap<String, Object>();
		paramReport.put("companyName", Main.getCompany().getName());
		paramReport.put("reportName", REPORT_NAME);
		paramReport.put("periodBegin", firstCutoff.getPreviousProfitAndLossCutoff().getCutoffTimestamp());
		paramReport.put("periodEnd", lastCutoff.getCutoffTimestamp());

		paramReport.put("cutoffBy", "[COMBINED]");

		BigDecimal penjualan = getTotalOfMethod("getPenjualan", profitAndLossCutoffs);
		paramReport.put("penjualan", penjualan);

		BigDecimal potonganPenjualan = getTotalOfMethod("getPotonganPenjualan", profitAndLossCutoffs);
		paramReport.put("potonganPenjualan", potonganPenjualan);

		BigDecimal returPenjualan = getTotalOfMethod("getReturPenjualan", profitAndLossCutoffs);
		paramReport.put("returPenjualan", returPenjualan);

		BigDecimal penjualanBersih = penjualan.subtract(potonganPenjualan).subtract(returPenjualan);
		paramReport.put("penjualanBersih", penjualanBersih);

		BigDecimal persediaanAwal = firstCutoff.getPersediaanAwal();
		paramReport.put("persediaanAwal", persediaanAwal);

		BigDecimal pembelian = getTotalOfMethod("getPembelian", profitAndLossCutoffs);
		paramReport.put("pembelian", pembelian);

		BigDecimal potonganPembelian = getTotalOfMethod("getPotonganPembelian", profitAndLossCutoffs);
		paramReport.put("potonganPembelian", potonganPembelian);

		BigDecimal returPembelian = getTotalOfMethod("getReturPembelian", profitAndLossCutoffs);
		paramReport.put("returPembelian", returPembelian);

		BigDecimal persediaanTotal = persediaanAwal.add(pembelian).subtract(potonganPembelian).subtract(returPembelian);
		paramReport.put("persediaanTotal", persediaanTotal);

		BigDecimal persediaanAkhir = lastCutoff.getPersediaanAkhir();
		paramReport.put("persediaanAkhir", persediaanAkhir);

		BigDecimal hpp = persediaanTotal.subtract(persediaanAkhir);
		paramReport.put("hpp", hpp);

		BigDecimal labaKotor = penjualanBersih.subtract(hpp);
		paramReport.put("labaKotor", labaKotor);

		BigDecimal bebanOperasi = getTotalOfMethod("getBebanOperasi", profitAndLossCutoffs);
		paramReport.put("bebanOperasi", bebanOperasi);

		BigDecimal labaBersih = labaKotor.subtract(bebanOperasi);
		paramReport.put("labaBersih", labaBersih);

		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(REPORT_FILE);

			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, paramReport,
					new JRBeanCollectionDataSource(new ArrayList<Object>()));

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

	private BigDecimal getTotalOfMethod(String methodName, List<ProfitAndLossCutoff> profitAndLossCutoffs)
			throws AppException {
		try {
			Method method = ProfitAndLossCutoff.class.getMethod(methodName);
			BigDecimal total = BigDecimal.valueOf(0);
			for (ProfitAndLossCutoff profitAndLossCutoff : profitAndLossCutoffs) {
				BigDecimal value = (BigDecimal) method.invoke(profitAndLossCutoff);
				total = total.add(value);
			}
			return total;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new AppException(e);
		}
	}
}
