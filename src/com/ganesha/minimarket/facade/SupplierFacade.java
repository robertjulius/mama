package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Supplier;

public class SupplierFacade {

	private static SupplierFacade instance;

	public static SupplierFacade getInstance() {
		if (instance == null) {
			instance = new SupplierFacade();
		}
		return instance;
	}

	private SupplierFacade() {
	}

	public void addNewSupplier(String address1, String address2, String code,
			String contackPerson1, String contackPerson1Email,
			String contackPerson1Phone, String contackPerson2,
			String contackPerson2Email, String contackPerson2Phone,
			String description, String email1, String email2, String name,
			String phone1, String phone2, boolean disabled, boolean deleted,
			Session session) throws UserException {

		if (GlobalFacade.getInstance().isExists("code", code, Supplier.class,
				session)) {
			throw new UserException("Supplier dengan ID " + code
					+ " sudah pernah didaftarkan");
		}

		if (GlobalFacade.getInstance().isExists("name", name, Supplier.class,
				session)) {
			throw new UserException("Supplier dengan Nama " + name
					+ " sudah pernah didaftarkan");
		}

		Supplier supplier = new Supplier();
		supplier.setAddress1(address1);
		supplier.setAddress2(address2);
		supplier.setCode(code);
		supplier.setContactPerson1(contackPerson1);
		supplier.setContactPerson1Email(contackPerson1Email);
		supplier.setContactPerson1Phone(contackPerson1Phone);
		supplier.setContactPerson2(contackPerson2);
		supplier.setContactPerson2Email(contackPerson2Email);
		supplier.setContactPerson2Phone(contackPerson2Phone);
		supplier.setDescription(description);
		supplier.setEmail1(email1);
		supplier.setEmail2(email2);
		supplier.setName(name);
		supplier.setPhone1(phone1);
		supplier.setPhone2(phone2);
		supplier.setDisabled(disabled);
		supplier.setDeleted(deleted);
		supplier.setLastUpdatedBy(Main.getUserLogin().getId());
		supplier.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(supplier);
	}

	public Supplier getDetail(int id, Session session) {
		Supplier supplier = (Supplier) session.get(Supplier.class, id);
		return supplier;
	}

	public List<Supplier> search(String code, String name,
			String contactPerson, boolean disabled, Session session) {
		Criteria criteria = session.createCriteria(Supplier.class);

		if (code != null && !code.trim().isEmpty()) {
			criteria.add(Restrictions.like("code", "%" + code + "%")
					.ignoreCase());
		}

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		if (contactPerson != null && !contactPerson.trim().isEmpty()) {
			Criterion criterion1 = Restrictions.like("contactPerson1",
					"%" + contactPerson + "%").ignoreCase();

			Criterion criterion2 = Restrictions.like("contactPerson2",
					"%" + contactPerson + "%").ignoreCase();

			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(criterion1);
			disjunction.add(criterion2);

			criteria.add(disjunction);
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Supplier> supplier = criteria.list();

		return supplier;
	}

	public void updateExistingSupplier(String address1, String address2,
			Integer id, String contackPerson1, String contackPerson1Email,
			String contackPerson1Phone, String contackPerson2,
			String contackPerson2Email, String contackPerson2Phone,
			String description, String email1, String email2, String name,
			String phone1, String phone2, boolean disabled, boolean deleted,
			Session session) throws UserException {

		Supplier supplier = getDetail(id, session);
		supplier.setAddress1(address1);
		supplier.setAddress2(address2);
		supplier.setContactPerson1(contackPerson1);
		supplier.setContactPerson1Email(contackPerson1Email);
		supplier.setContactPerson1Phone(contackPerson1Phone);
		supplier.setContactPerson2(contackPerson2);
		supplier.setContactPerson2Email(contackPerson2Email);
		supplier.setContactPerson2Phone(contackPerson2Phone);
		supplier.setDescription(description);
		supplier.setEmail1(email1);
		supplier.setEmail2(email2);
		if (!supplier.getName().equals(name)) {
			if (GlobalFacade.getInstance().isExists("name", name,
					Supplier.class, session)) {
				throw new UserException("Supplier dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				supplier.setName(name);
			}
		}
		supplier.setName(name);
		supplier.setPhone1(phone1);
		supplier.setPhone2(phone2);
		if (deleted) {
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus Supplier yang masih dalam kondisi aktif");
			}
		}
		supplier.setDisabled(disabled);
		supplier.setDeleted(deleted);
		supplier.setLastUpdatedBy(Main.getUserLogin().getId());
		supplier.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(supplier);
	}
}
