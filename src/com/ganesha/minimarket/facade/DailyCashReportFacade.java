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

public class DailyCashReportFacade {

	private static DailyCashReportFacade instance;

	public static DailyCashReportFacade getInstance() {
		if (instance == null) {
			instance = new DailyCashReportFacade();
		}
		return instance;
	}

	private DailyCashReportFacade() {
	}

	public List<Map<String, Object>> search(Date beginDate, Date endDate, Session session) {

		String newLine = "\r\n";

		beginDate = DateUtils.validateDateBegin(beginDate);
		endDate = DateUtils.validateDateEnd(endDate);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT ").append(newLine);
		stringBuilder.append("     sh.date").append(newLine);
		stringBuilder.append("      , IF (sh.amount IS NULL, 0, sh.amount)").append(newLine);
		stringBuilder.append("           + IF (sch.amount IS NULL, 0, sch.amount)").append(newLine);
		stringBuilder.append("           - IF (srh.amount IS NULL, 0, srh.amount)").append(newLine);
		stringBuilder.append("           AS amount").append(newLine);
		stringBuilder.append(" FROM (").append(newLine);
		stringBuilder.append("      SELECT").append(newLine);
		stringBuilder.append("           CAST(transaction_timestamp AS DATE) AS DATE").append(newLine);
		stringBuilder.append("           , SUM(total_amount) AS amount").append(newLine);
		stringBuilder.append("      FROM").append(newLine);
		stringBuilder.append("           sale_headers").append(newLine);
		stringBuilder.append("      GROUP BY").append(newLine);
		stringBuilder.append("           date").append(newLine);
		stringBuilder.append(" ) AS sh").append(newLine);
		stringBuilder.append(" LEFT JOIN (").append(newLine);
		stringBuilder.append("      SELECT").append(newLine);
		stringBuilder.append("           CAST(transaction_timestamp AS DATE) AS DATE").append(newLine);
		stringBuilder.append("           , SUM(total_amount) AS amount").append(newLine);
		stringBuilder.append("      FROM").append(newLine);
		stringBuilder.append("           sale_constraint_headers").append(newLine);
		stringBuilder.append("      GROUP BY").append(newLine);
		stringBuilder.append("           date").append(newLine);
		stringBuilder.append(" ) AS sch ON sh.date = sch.date").append(newLine);
		stringBuilder.append(" LEFT JOIN (").append(newLine);
		stringBuilder.append("      SELECT").append(newLine);
		stringBuilder.append("           CAST(transaction_timestamp AS DATE) AS DATE").append(newLine);
		stringBuilder.append("           , SUM(total_return_amount) AS amount").append(newLine);
		stringBuilder.append("      FROM").append(newLine);
		stringBuilder.append("           sale_return_headers").append(newLine);
		stringBuilder.append("      GROUP BY").append(newLine);
		stringBuilder.append("           date").append(newLine);
		stringBuilder.append(" ) srh ON sh.date = srh.date").append(newLine);
		stringBuilder.append(" WHERE 1 = 1").append(newLine);

		if (beginDate != null) {
			stringBuilder.append(" AND sh.date >= :beginDate ").append(newLine);
		}

		if (endDate != null) {
			stringBuilder.append(" AND sh.date <= :endDate ").append(newLine);
		}

		stringBuilder.append(" ORDER BY").append(newLine);
		stringBuilder.append("      sh.date DESC;").append(newLine);
		stringBuilder.append(" ").append(newLine);

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