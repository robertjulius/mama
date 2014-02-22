package com.ganesha.accounting.facade;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.constants.Enums.CircleUnit;
import com.ganesha.accounting.model.Circle;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.DBUtils;
import com.ganesha.minimarket.Main;

public class CircleFacade {

	private static CircleFacade instance;

	public static CircleFacade getInstance() {
		if (instance == null) {
			instance = new CircleFacade();
		}
		return instance;
	}

	private CircleFacade() {
	}

	public Circle addNewCircle(String name, CircleUnit unit, Integer duration,
			boolean disabled, boolean deleted, Session session)
			throws UserException {

		if (DBUtils.getInstance().isExists("name", name, Circle.class, session)) {
			throw new UserException("Circle dengan Nama " + name
					+ " sudah pernah didaftarkan");
		}

		Circle circle = new Circle();
		circle.setName(name);
		circle.setUnit(unit);
		circle.setDuration(duration);
		circle.setDisabled(disabled);
		circle.setDeleted(deleted);
		circle.setLastUpdatedBy(Main.getUserLogin().getId());
		circle.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(circle);
		return circle;
	}

	public Circle getDetail(Integer id, Session session) {
		Circle circle = (Circle) session.get(Circle.class, id);
		return circle;
	}

	public List<Circle> search(String name, CircleUnit unit, boolean disabled,
			Session session) {
		Criteria criteria = session.createCriteria(Circle.class);

		if (name != null && !name.trim().isEmpty()) {
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
		}

		if (unit != null) {
			criteria.add(Restrictions.eq("unit", unit));
		}

		criteria.add(Restrictions.eq("disabled", disabled));
		criteria.add(Restrictions.eq("deleted", false));

		@SuppressWarnings("unchecked")
		List<Circle> circles = criteria.list();

		return circles;
	}

	public Circle updateExistingCircle(Integer id, String name,
			CircleUnit unit, Integer duration, boolean disabled,
			boolean deleted, Session session) throws UserException {

		Circle circle = getDetail(id, session);
		if (!circle.getName().equals(name)) {
			if (DBUtils.getInstance().isExists("name", name, Circle.class,
					session)) {
				throw new UserException("Circle dengan Nama " + name
						+ " sudah pernah didaftarkan");
			} else {
				circle.setName(name);
			}
		}
		circle.setName(name);
		circle.setUnit(unit);
		circle.setDuration(duration);
		if (deleted) {
			if (!disabled) {
				throw new UserException(
						"Tidak dapat menghapus Circle yang masih dalam kondisi aktif");
			}
		}
		circle.setDisabled(disabled);
		circle.setDeleted(deleted);
		circle.setLastUpdatedBy(Main.getUserLogin().getId());
		circle.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		session.saveOrUpdate(circle);
		return circle;
	}
}
