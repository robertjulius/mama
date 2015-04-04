package com.ganesha.minimarket.facade;

import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.model.Tax;

public class GlobalFacade {

	private static GlobalFacade instance;

	public static GlobalFacade getInstance() {
		if (instance == null) {
			instance = new GlobalFacade();
		}
		return instance;
	}

	private GlobalFacade() {
	}

	public Object getComboBoxSelectedItem(XJComboBox comboBox) {
		ComboBoxObject comboBoxObject = (ComboBoxObject) comboBox
				.getSelectedItem();
		Object id = null;
		if (comboBoxObject != null) {
			id = comboBoxObject.getObject();
		}
		return id;
	}

	public double getTaxPercent() {
		double taxPercent = 0;
		Session session = HibernateUtils.openSession();
		try {
			Criteria criteria = session.createCriteria(Tax.class);
			criteria.add(Restrictions.eq("code", GeneralConstants.TAX_CODE_PPN));
			Tax tax = (Tax) criteria.uniqueResult();
			if (tax != null) {
				taxPercent = tax.getTaxPercent().doubleValue();
			}
		} finally {
			session.close();
		}
		return taxPercent;
	}

	public Integer getIdByCode(String codeFieldName, String codeValue,
			String idFieldName, String tableName, Session session) {

		String sqlString = "SELECT " + idFieldName + " FROM " + tableName
				+ " WHERE " + codeFieldName + " =: codeValue";

		Query query = session.createQuery(sqlString);
		HqlParameter param = new HqlParameter(query);
		param.put("codeValue", codeValue);
		param.validate();

		@SuppressWarnings("unchecked")
		Map<String, Integer> list = (Map<String, Integer>) query.uniqueResult();
		Integer idValue = list.get(idFieldName);

		return idValue;
	}

	public String getCodeById(String idFieldName, int idValue,
			String codeFieldName, String tableName, Session session) {

		String sqlString = "SELECT " + codeFieldName + " FROM " + tableName
				+ " WHERE " + idFieldName + " =: idValue";

		Query query = session.createQuery(sqlString);
		HqlParameter param = new HqlParameter(query);
		param.put("idValue", idValue);
		param.validate();

		@SuppressWarnings("unchecked")
		Map<String, String> list = (Map<String, String>) query.uniqueResult();
		String codeFieldValue = list.get(codeFieldName);

		return codeFieldValue;
	}
}
