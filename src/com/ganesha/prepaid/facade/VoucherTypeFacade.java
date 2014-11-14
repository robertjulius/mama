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
import com.ganesha.prepaid.model.Provider;
import com.ganesha.prepaid.model.VoucherType;

public class VoucherTypeFacade {

	private static VoucherTypeFacade instance;

	public static VoucherTypeFacade getInstance() {
		if (instance == null) {
			instance = new VoucherTypeFacade();
		}
		return instance;
	}

	private VoucherTypeFacade() {
	}

	public VoucherType addNewVoucherType(String name, Provider provider,
			Integer itemId, boolean disabled, boolean deleted, Session session)
			throws UserException {

		if (DBUtils.getInstance().isExists("name", name, VoucherType.class,
				session)) {
			throw new UserException("VoucherType dengan Nama " + name
					+ " sudah pernah didaftarkan");
		}

		VoucherType voucherType = new VoucherType();
		voucherType.setName(name);
		voucherType.setProvider(provider);

		Item item = ItemFacade.getInstance().getDetail(itemId, session);
		voucherType.setItem(item);

		voucherType.setDisabled(disabled);
		voucherType.setDeleted(deleted);
		voucherType.setLastUpdatedBy(Main.getUserLogin().getId());
		voucherType.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(voucherType);
		return voucherType;
	}

	public List<VoucherType> getAll(Session session) {
		Criteria criteria = session.createCriteria(VoucherType.class);
		criteria.add(Restrictions.eq("disabled", false));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<VoucherType> voucherTypes = criteria.list();
		return voucherTypes;
	}

	public VoucherType getDetail(Integer id, Session session) {
		VoucherType voucherType = (VoucherType) session.get(VoucherType.class,
				id);
		return voucherType;
	}

	public List<VoucherType> search(String name, Integer providerId,
			boolean disabled, Session session) {

		Criteria criteria = session.createCriteria(VoucherType.class);
		criteria.createAlias("provider", "provider");

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		if (providerId != null) {
			criteria.add(Restrictions.eq("provider.id", providerId));
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<VoucherType> voucherTypes = criteria.list();

		return voucherTypes;
	}

	public VoucherType updateExistingVoucherType(Integer id, String name,
			Provider provider, Integer itemId, boolean disabled,
			boolean deleted, Session session) throws UserException {

		VoucherType voucherType = getDetail(id, session);

		if (!voucherType.getName().equals(name)) {
			if (DBUtils.getInstance().isExists("name", name, VoucherType.class,
					session)) {
				throw new UserException("VoucherType dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				voucherType.setName(name);
			}
		}

		if (deleted) {
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus VoucherType yang masih dalam kondisi aktif");
			}
		}

		voucherType.setProvider(provider);

		Item item = ItemFacade.getInstance().getDetail(itemId, session);
		voucherType.setItem(item);

		voucherType.setDisabled(disabled);
		voucherType.setDeleted(deleted);
		voucherType.setLastUpdatedBy(Main.getUserLogin().getId());
		voucherType.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(voucherType);
		return voucherType;
	}
}
