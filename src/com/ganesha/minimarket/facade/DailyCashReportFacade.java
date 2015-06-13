package com.ganesha.minimarket.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.ganesha.accounting.constants.CoaCodeConstants;
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

	public List<Map<String, Object>> search(Date beginDate, Date endDate,
			Session session) {

		beginDate = DateUtils.validateDateBegin(beginDate);
		endDate = DateUtils.validateDateEnd(endDate);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT CAST(timestamp AS DATE) AS DATE");
		stringBuilder.append(", (SUM(debit) - SUM(credit)) AS amount");
		stringBuilder.append(" FROM accounts");
		stringBuilder.append(" WHERE 1=1");
		stringBuilder.append(" AND coa_id = :coaId");

		if (beginDate != null) {
			stringBuilder.append(" AND timestamp >= :beginDate");
		}

		if (endDate != null) {
			stringBuilder.append(" AND timestamp <= :endDate");
		}

		stringBuilder.append(" GROUP BY date");
		stringBuilder.append(" ORDER BY date DESC");

		SQLQuery sqlQuery = session.createSQLQuery(stringBuilder.toString());
		HqlParameter parameter = new HqlParameter(sqlQuery);
		parameter.put("coaId", CoaCodeConstants.KAS_KECIL);
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