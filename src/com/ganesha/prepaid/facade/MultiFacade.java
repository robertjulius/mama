package com.ganesha.prepaid.facade;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.ItemFacade;
import com.ganesha.minimarket.model.Item;
import com.ganesha.prepaid.model.MultiMap;

public class MultiFacade {

	private static MultiFacade instance;

	public static MultiFacade getInstance() {
		if (instance == null) {
			instance = new MultiFacade();
		}
		return instance;
	}

	private MultiFacade() {
	}

	public MultiMap getTheOnlyOne(Session session) {
		Criteria criteria = session.createCriteria(MultiMap.class);
		MultiMap multiMap = (MultiMap) criteria.uniqueResult();
		return multiMap;
	}

	public MultiMap updateMultiMap(Integer multiMapId, Integer itemId,
			Session session) throws UserException {

		MultiMap voucherType = (MultiMap) session.get(MultiMap.class,
				multiMapId);

		Item item = ItemFacade.getInstance().getDetail(itemId, session);
		voucherType.setItem(item);
		voucherType.setLastUpdatedBy(Main.getUserLogin().getId());
		voucherType.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(voucherType);
		return voucherType;
	}
}
