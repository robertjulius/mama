package com.ganesha.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.LoggerFactory;

import com.ganesha.coreapps.constants.Loggers;
import com.ganesha.minimarket.facade.SaleConstraintFacade;

public class PostingConstraintJob implements Job {

	@Override
	public void execute(JobExecutionContext context) {
		
		LoggerFactory.getLogger(Loggers.SCHEDULER).debug(
				"Scheduler " + PostingConstraintJob.class.getName()
						+ " is started");

		SaleConstraintFacade.getInstance().performPosting();

		LoggerFactory.getLogger(Loggers.SCHEDULER).debug(
				"Scheduler " + PostingConstraintJob.class.getName()
						+ " is finished");
	}
}