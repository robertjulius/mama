package com.ganesha.prepaid.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.DBUtils;
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

	public MultiMap addNewMultiMap(String name, Integer itemId,
			boolean disabled, boolean deleted, Session session)
			throws UserException {

		if (DBUtils.getInstance().isExists("name", name, MultiMap.class,
				session)) {
			throw new UserException("MultiMap dengan Nama " + name
					+ " sudah pernah didaftarkan");
		}

		MultiMap multiMap = new MultiMap();
		multiMap.setName(name);

		Item item = ItemFacade.getInstance().getDetail(itemId, session);
		multiMap.setItem(item);

		multiMap.setDisabled(disabled);
		multiMap.setDeleted(deleted);
		multiMap.setLastUpdatedBy(Main.getUserLogin().getId());
		multiMap.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(multiMap);
		return multiMap;
	}

	public List<MultiMap> getAll(Session session) {
		Criteria criteria = session.createCriteria(MultiMap.class);
		criteria.add(Restrictions.eq("disabled", false));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<MultiMap> multiMaps = criteria.list();
		return multiMaps;
	}

	public MultiMap getDetail(Integer id, Session session) {
		MultiMap multiMap = (MultiMap) session.get(MultiMap.class, id);
		return multiMap;
	}

	public List<MultiMap> search(String name, boolean disabled, Session session) {

		Criteria criteria = session.createCriteria(MultiMap.class);

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<MultiMap> multiMaps = criteria.list();

		return multiMaps;
	}

	public MultiMap updateExistingMultiMap(Integer id, String name,
			Integer itemId, boolean disabled, boolean deleted, Session session)
			throws UserException {

		MultiMap multiMap = getDetail(id, session);

		if (!multiMap.getName().equals(name)) {
			if (DBUtils.getInstance().isExists("name", name, MultiMap.class,
					session)) {
				throw new UserException("MultiMap dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				multiMap.setName(name);
			}
		}

		if (deleted) {
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus MultiMap yang masih dalam kondisi aktif");
			}
		}

		Item item = ItemFacade.getInstance().getDetail(itemId, session);
		multiMap.setItem(item);

		multiMap.setDisabled(disabled);
		multiMap.setDeleted(deleted);
		multiMap.setLastUpdatedBy(Main.getUserLogin().getId());
		multiMap.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(multiMap);
		return multiMap;
	}
}
