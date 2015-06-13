package com.ganesha.quartz;

import java.util.Date;

import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

import com.ganesha.accounting.facade.ProfitAndLossCutoffFacade;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.coreapps.constants.Loggers;
import com.ganesha.hibernate.HibernateUtils;

public class ProfitAndLossCutoffDailyJob implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		LoggerFactory.getLogger(Loggers.SCHEDULER).debug(
				"Scheduler " + ProfitAndLossCutoffDailyJob.class.getName()
						+ " is started");

		execute();

		LoggerFactory.getLogger(Loggers.SCHEDULER).debug(
				"Scheduler " + ProfitAndLossCutoffDailyJob.class.getName()
						+ " is finished");
	}

	private void execute() throws JobExecutionException {

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			Date today = DateUtils.getCurrent(Date.class);
			Date beginDate = DateUtils.validateDateBegin(today);
			Date endDate = DateUtils.validateDateEnd(today);

			boolean exists = ProfitAndLossCutoffFacade.getInstance().isExists(
					beginDate, endDate, session);

			if (!exists) {
				ProfitAndLossCutoffFacade.getInstance()
						.performProfitAndLossCutoff(0, session);
			}

			session.getTransaction().commit();

		} catch (AppException e) {
			session.getTransaction().rollback();
			throw new JobExecutionException(e.getCause());
		} finally {
			session.close();
		}
	}
}