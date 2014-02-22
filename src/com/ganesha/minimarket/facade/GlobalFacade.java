package com.ganesha.minimarket.facade;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.hibernate.HibernateUtils;
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
			id = comboBoxObject.getId();
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
}
