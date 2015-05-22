package com.ganesha.prepaid.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.prepaid.model.Provider;

public class ProviderFacade {

	private static ProviderFacade instance;

	public static ProviderFacade getInstance() {
		if (instance == null) {
			instance = new ProviderFacade();
		}
		return instance;
	}

	private ProviderFacade() {
	}

	public Provider addNewProvider(String name, boolean disabled,
			boolean deleted, Session session) throws UserException {

		if (DBUtils.getInstance().isExists("name", name, Provider.class,
				session)) {
			throw new UserException("Provider dengan Nama " + name
					+ " sudah pernah didaftarkan");
		}

		Provider provider = new Provider();
		provider.setName(name);
		provider.setDisabled(disabled);
		provider.setDeleted(deleted);
		provider.setLastUpdatedBy(Main.getUserLogin().getId());
		provider.setLastUpdatedTimestamp(DateUtils.getCurrentTimestamp());

		session.saveOrUpdate(provider);
		return provider;
	}

	public List<Provider> getAll(Session session) {
		Query query = session
				.createQuery("from Provider where disabled = :disabled AND deleted = :deleted");

		HqlParameter parameter = new HqlParameter(query);
		parameter.put("disabled", false);
		parameter.put("deleted", false);
		parameter.validate();

		@SuppressWarnings("unchecked")
		List<Provider> providers = query.list();

		return providers;
	}

	public Provider getDetail(Integer id, Session session) {
		Provider provider = (Provider) session.get(Provider.class, id);
		return provider;
	}

	public List<Provider> search(String name, boolean disabled, Session session) {

		Criteria criteria = session.createCriteria(Provider.class);

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Provider> providers = criteria.list();

		return providers;
	}

	public Provider updateExistingProvider(Integer id, String name,
			boolean disabled, boolean deleted, Session session)
			throws UserException {

		Provider provider = getDetail(id, session);

		if (!provider.getName().equals(name)) {
			if (DBUtils.getInstance().isExists("name", name, Provider.class,
					session)) {
				throw new UserException("Provider dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				provider.setName(name);
			}
		}

		if (deleted) {
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus Provider yang masih dalam kondisi aktif");
			}
		}

		provider.setDisabled(disabled);
		provider.setDeleted(deleted);
		provider.setLastUpdatedBy(Main.getUserLogin().getId());
		provider.setLastUpdatedTimestamp(DateUtils.getCurrentTimestamp());

		session.saveOrUpdate(provider);
		return provider;
	}
}
