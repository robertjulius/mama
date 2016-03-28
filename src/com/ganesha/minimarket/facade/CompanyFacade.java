package com.ganesha.minimarket.facade;

import java.sql.Timestamp;

import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Company;

public class CompanyFacade {

	private static CompanyFacade instance;

	public static CompanyFacade getInstance() {
		if (instance == null) {
			instance = new CompanyFacade();
		}
		return instance;
	}

	private CompanyFacade() {
	}

	public Company getDetail(Session session) {
		Company company = (Company) session.createCriteria(Company.class).uniqueResult();
		return company;
	}

	public Company updateExistingCompany(String name, String address, String phone1, String phone2, String fax,
			Session session) throws UserException {

		Company company = getDetail(session);
		company.setName(name);
		company.setAddress(address);
		company.setPhone1(phone1);
		company.setPhone2(phone2);
		company.setFax(fax);
		company.setDisabled(false);
		company.setDeleted(false);
		company.setLastUpdatedBy(Main.getUserLogin().getId());
		company.setLastUpdatedTimestamp(DateUtils.getCurrent(Timestamp.class));

		session.saveOrUpdate(company);
		return company;
	}
}
