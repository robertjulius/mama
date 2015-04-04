package com.ganesha.minimarket.ui.forms.reports;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.facade.ProfitAndLossFacade;
import com.ganesha.accounting.model.ProfitAndLossStatement;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJMonthChooser;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJYearChooser;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.utils.PermissionConstants;

public class ProfitAndLossReportListDialog extends XJDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJButton btnKeluar;
	private XJMonthChooser monthChooser;
	private XJYearChooser yearChooser;
	private XJButton btnGenerate;

	public ProfitAndLossReportListDialog(Window parent) {
		super(parent);
		getContentPane().setLayout(new MigLayout("", "[400]", "[][]"));

		setTitle("Laporan Laba Rugi");
		setPermissionCode(PermissionConstants.REPORT_PROFITANDLOSS_LIST);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,grow");
		pnlFilter.setLayout(new MigLayout("", "[][][grow][][100]", "[][]"));

		XJLabel lblPeriod = new XJLabel();
		pnlFilter.add(lblPeriod, "cell 0 0 2 1");
		lblPeriod.setText("Periode Laporan");

		XJLabel lblMonth = new XJLabel();
		pnlFilter.add(lblMonth, "cell 0 1");
		lblMonth.setText("Bulan");

		monthChooser = new XJMonthChooser();
		pnlFilter.add(monthChooser, "cell 1 1,growx");
		// dtChMonth.setMonth(Calendar.);
		// dtChMonth.getCalendarButton().setMnemonic('D');

		XJLabel lblYear = new XJLabel();
		pnlFilter.add(lblYear, "cell 3 1");
		lblYear.setText("Tahun");

		yearChooser = new XJYearChooser();
		pnlFilter.add(yearChooser, "cell 4 1,growx");
		// dtChEnd.setDate(CommonUtils.getCurrentDate());
		// dtChEnd.getCalendarButton().setMnemonic('S');

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 1,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][]", "[]"));

		btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
		panel.add(btnKeluar, "cell 0 0");

		btnGenerate = new XJButton();
		btnGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ProfitAndLossStatement profitAndLossStatement = generate();
					ProfitAndLossFacade.getInstance().previewReport(
							ProfitAndLossReportListDialog.this,
							profitAndLossStatement);
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ProfitAndLossReportListDialog.this, ex);
				}
			}
		});
		btnGenerate
				.setText("<html><center>Generate Laporan<br/>[F12]</center></html>");
		panel.add(btnGenerate, "cell 1 0");

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnGenerate.doClick();
			break;
		default:
			break;
		}
	}

	private ProfitAndLossStatement generate() throws AppException,
			UserException {
		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			int year = yearChooser.getYear();
			int month = monthChooser.getMonth();

			Calendar period = Calendar.getInstance();
			period.set(Calendar.YEAR, year);
			period.set(Calendar.MONTH, month);
			CommonUtils.setCalendarMonthAndYearOnly(period);

			validatePeriodInput(period);

			ProfitAndLossStatement profitAndLossStatement = ProfitAndLossFacade
					.getInstance().getProfitAndSaveProfitAndLossStatement(
							period, session);

			session.getTransaction().commit();
			return profitAndLossStatement;

		} finally {
			session.close();
		}
	}

	private void validatePeriodInput(Calendar period) throws UserException {
		Calendar now = Calendar.getInstance();
		CommonUtils.setCalendarMonthAndYearOnly(now);

		if (period.before(now)) {
			/*
			 * Mean that the request is for previous month
			 */
			return;
		} else {
			/*
			 * Mean that the request is for this month of for next month, we
			 * can't create such reports
			 */
			int month = period.get(Calendar.MONTH);
			DateFormatSymbols symbols = new DateFormatSymbols();
			throw new UserException("Laporan untuk bulan "
					+ symbols.getMonths()[month] + " belum terbentuk");
		}
	}
}
