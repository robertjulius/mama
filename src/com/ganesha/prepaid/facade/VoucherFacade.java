package com.ganesha.prepaid.facade;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.prepaid.model.Voucher;
import com.ganesha.prepaid.model.VoucherType;

public class VoucherFacade {

	private static VoucherFacade instance;

	public static VoucherFacade getInstance() {
		if (instance == null) {
			instance = new VoucherFacade();
		}
		return instance;
	}

	private VoucherFacade() {
	}

	public Voucher addNewVoucher(VoucherType voucherType, String packageName,
			Integer quantity, BigDecimal price, boolean disabled,
			boolean deleted, Session session) throws UserException {

		Voucher voucher = new Voucher();
		voucher.setVoucherType(voucherType);
		voucher.setPackageName(packageName);
		voucher.setQuantity(quantity);
		voucher.setPrice(price);
		voucher.setDisabled(disabled);
		voucher.setDeleted(deleted);
		voucher.setLastUpdatedBy(Main.getUserLogin().getId());
		voucher.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(voucher);
		return voucher;
	}

	public List<Voucher> getAllVoucherByType(VoucherType voucherType,
			Session session) {
		Criteria criteria = session.createCriteria(Voucher.class);
		criteria.createAlias("voucherType", "voucherType");

		criteria.add(Restrictions.eq("voucherType.id", voucherType.getId()));
		criteria.add(Restrictions.eq("disabled", false));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Voucher> vouchers = criteria.list();
		return vouchers;
	}

	public Voucher getDetail(int id, Session session) {
		Voucher voucher = (Voucher) session.get(Voucher.class, id);
		return voucher;
	}

	public List<Voucher> search(Integer voucherTypeId, boolean disabled,
			Session session) {

		Criteria criteria = session.createCriteria(Voucher.class);
		criteria.createAlias("voucherType", "voucherType");

		if (voucherTypeId != null) {
			criteria.add(Restrictions.eq("voucherType.id", voucherTypeId));
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Voucher> vouchers = criteria.list();

		return vouchers;
	}

	public Voucher updateExistingVoucher(Integer id, VoucherType voucherType,
			String packageName, Integer quantity, BigDecimal price,
			boolean disabled, boolean deleted, Session session)
			throws UserException {

		Voucher voucher = getDetail(id, session);

		if (deleted) {
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus Voucher yang masih dalam kondisi aktif");
			}
		}

		voucher.setVoucherType(voucherType);
		voucher.setPackageName(packageName);
		voucher.setQuantity(quantity);
		voucher.setPrice(price);

		voucher.setDisabled(disabled);
		voucher.setDeleted(deleted);
		voucher.setLastUpdatedBy(Main.getUserLogin().getId());
		voucher.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(voucher);
		return voucher;
	}
}
