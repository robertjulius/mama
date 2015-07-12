package com.ganesha.minimarket.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.ganesha.core.utils.DateUtils;
import com.ganesha.hibernate.HqlParameter;

public class SaleSummaryReportFacade {

	private static SaleSummaryReportFacade instance;

	public static SaleSummaryReportFacade getInstance() {
		if (instance == null) {
			instance = new SaleSummaryReportFacade();
		}
		return instance;
	}

	private SaleSummaryReportFacade() {
	}

	public List<Map<String, Object>> search(Date beginDate, Date endDate,
			Session session) {

		beginDate = DateUtils.validateDateBegin(beginDate);
		endDate = DateUtils.validateDateEnd(endDate);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT CAST(transaction_timestamp AS date) date, SUM(total_amount) AS amount").append("\n");
		stringBuilder.append("FROM (").append("\n");
		stringBuilder.append("	SELECT transaction_timestamp, total_amount").append("\n");
		stringBuilder.append("	FROM sale_headers").append("\n");
		stringBuilder.append("	UNION").append("\n");
		stringBuilder.append("	SELECT transaction_timestamp, total_amount").append("\n");
		stringBuilder.append("	FROM sale_constraint_headers").append("\n");
		stringBuilder.append("	UNION").append("\n");
		stringBuilder.append("	SELECT transaction_timestamp, -total_return_amount").append("\n");
		stringBuilder.append("	FROM sale_return_headers").append("\n");
		stringBuilder.append(") temp").append("\n");
		stringBuilder.append("WHERE 1=1");
		
		if (beginDate != null) {
			stringBuilder.append(" AND transaction_timestamp >= :beginDate");
		}

		if (endDate != null) {
			stringBuilder.append(" AND transaction_timestamp <= :endDate");
		}

		stringBuilder.append(" GROUP BY date");
		stringBuilder.append(" ORDER BY date DESC");

		SQLQuery sqlQuery = session.createSQLQuery(stringBuilder.toString());
		HqlParameter parameter = new HqlParameter(sqlQuery);
		parameter.put("beginDate", beginDate);
		parameter.put("endDate", endDate);
		parameter.validate();

		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();

		@SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();

		for (Object[] objects : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("date", objects[0]);
			map.put("amount", objects[1]);
			maps.add(map);
		}

		return maps;
	}
}