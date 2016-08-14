package com.ganesha.minimarket.ui.forms.searchentity;

import java.awt.Window;
import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.model.Item;

public class SearchItemDialog extends SearchEntityDialog {

	private static final long serialVersionUID = 1452286313727721700L;

	private static final String COLUMN_PRICE = "_PRICE_";

	{
		getTableParameters().put(COLUMN_PRICE,
				new XTableParameter(3, 30, false, "HARGA", false, XTableConstants.CELL_RENDERER_RIGHT, Double.class));
	}

	public SearchItemDialog(String title, Window parent) {
		super(title, parent, Item.class);
		XTableUtils.initTable(table, getTableParameters());
		setSize(getWidth() + 200, getHeight());
		setLocationRelativeTo(parent);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String code = txtKode.getText();
			String name = txtNama.getText();

			Criteria criteria = session.createCriteria(entityClass);
			criteria.add(Restrictions.like("code", "%" + code + "%").ignoreCase());
			criteria.add(Restrictions.like("name", "%" + name + "%").ignoreCase());
			criteria.add(Restrictions.eq("disabled", false));
			criteria.add(Restrictions.eq("deleted", false));

			List<?> searchResults = criteria.list();

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(searchResults.size());

			for (int i = 0; i < searchResults.size(); ++i) {
				Item item = (Item) searchResults.get(i);

				int idValue = item.getId();
				String codeValue = item.getCode();
				String nameValue = item.getName();
				BigDecimal priceValue = item.getSellPrices().get(item.getSellPrices().size() - 1).getPrimaryKey().getSellPrice();

				tableModel.setValueAt(idValue, i, getTableParameters().get(COLUMN_ID).getColumnIndex());

				tableModel.setValueAt(codeValue, i, getTableParameters().get(COLUMN_CODE).getColumnIndex());

				tableModel.setValueAt(nameValue, i, getTableParameters().get(COLUMN_NAME).getColumnIndex());

				tableModel.setValueAt(Formatter.formatNumberToString(priceValue), i,
						getTableParameters().get(COLUMN_PRICE).getColumnIndex());
			}
		} finally {
			session.close();
		}
	}
}
