package com.ganesha.minimarket.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Customer;

public class CustomerFacade {

	private static CustomerFacade instance;

	public static CustomerFacade getInstance() {
		if (instance == null) {
			instance = new CustomerFacade();
		}
		return instance;
	}

	private CustomerFacade() {
	}

	public Customer addNewCustomer(String address, String code,
			String description, String email, String name, String phone,
			boolean disabled, boolean deleted, Session session)
			throws UserException {

		if (DBUtils.getInstance().isExists("code", code, Customer.class,
				session)) {
			throw new UserException("Customer dengan ID " + code
					+ " sudah pernah didaftarkan");
		}

		Customer customer = new Customer();
		customer.setAddress(address);
		customer.setCode(code);
		customer.setDescription(description);
		customer.setEmail(email);
		customer.setName(name);
		customer.setPhone(phone);
		customer.setDisabled(disabled);
		customer.setDeleted(deleted);
		customer.setLastUpdatedBy(Main.getUserLogin().getId());
		customer.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(customer);
		return customer;
	}

	public Customer getDefaultCustomer(Session session) {
		return getDetail(0, session);
	}

	public Customer getDetail(int id, Session session) {
		Customer customer = (Customer) session.get(Customer.class, id);
		return customer;
	}

	public List<Customer> search(String code, String name, boolean disabled,
			Session session) {
		Criteria criteria = session.createCriteria(Customer.class);

		if (code != null && !code.trim().isEmpty()) {
			criteria.add(Restrictions.like("code", "%" + code + "%")
					.ignoreCase());
		}

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Customer> customer = criteria.list();

		return customer;
	}

	public Customer updateExistingCustomer(Integer id, String address,
			String description, String email, String name, String phone,
			boolean disabled, boolean deleted, Session session)
			throws UserException {

		Customer customer = getDetail(id, session);
		customer.setAddress(address);
		customer.setDescription(description);
		customer.setEmail(email);
		if (!customer.getName().equals(name)) {
			if (DBUtils.getInstance().isExists("name", name, Customer.class,
					session)) {
				throw new UserException("Customer dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				customer.setName(name);
			}
		}
		customer.setName(name);
		customer.setPhone(phone);
		if (deleted) {
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus Customer yang masih dalam kondisi aktif");
			}
		}
		customer.setDisabled(disabled);
		customer.setDeleted(deleted);
		customer.setLastUpdatedBy(Main.getUserLogin().getId());
		customer.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(customer);
		return customer;
	}
}
