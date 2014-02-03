package com.ganesha.minimarket.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.model.Permission;

public class PermissionConsistencyChecker {

	private Map<Integer, Permission> permissionsFromConstants;
	private List<Permission> permissionsFromDB;

	public PermissionConsistencyChecker() {
	}

	public void check() throws AppException {
		loadListFromConstants();
		loadListFromDB();

		if (permissionsFromConstants.size() != permissionsFromDB.size()) {
			String additionalMessage = "Size in DB: "
					+ permissionsFromDB.size() + " , size in Constants: "
					+ permissionsFromConstants.size();
			throwException(additionalMessage);
		}

		for (Permission permissionDb : permissionsFromDB) {
			int id = permissionDb.getId();
			Permission permissionConstant = permissionsFromConstants
					.get(permissionDb.getId());

			if (permissionConstant == null) {
				String additionalMessage = "Permission " + id
						+ " in Constant is not set";
				throwException(additionalMessage);
			}
		}
	}

	private void loadListFromConstants() throws AppException {
		permissionsFromConstants = new HashMap<>();
		try {
			Class<PermissionConstants> clazz = PermissionConstants.class;
			Field[] fields = clazz.getFields();

			for (Field field : fields) {
				String permissionName = field.getName();
				int permissionId = field.getInt(null);
				Permission permission = new Permission();
				permission.setId(permissionId);
				permission.setName(permissionName);
				permissionsFromConstants.put(permissionId, permission);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new AppException(e);
		}
	}

	private void loadListFromDB() {
		Session session = HibernateUtils.openSession();
		try {
			Query query = session.createQuery("from Permission");

			@SuppressWarnings("unchecked")
			List<Permission> permissions = query.list();

			permissionsFromDB = permissions;
		} finally {
			session.close();
		}
	}

	private void throwException(String additionalMessage) throws AppException {
		throw new AppException("Inconsistent Permission in DB. "
				+ additionalMessage);
	}
}
