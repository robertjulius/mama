package com.ganesha.minimarket.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.constants.Enums.SaleConstraintPostingStatus;
import com.ganesha.minimarket.model.SaleConstraintDetail;
import com.ganesha.minimarket.model.SaleConstraintHeader;
import com.ganesha.minimarket.model.SaleConstraintLog;
import com.ganesha.minimarket.model.SaleDetail;
import com.ganesha.minimarket.model.SaleHeader;

public class SaleConstraintFacade {

	private static SaleConstraintFacade instance;

	public static SaleConstraintFacade getInstance() {
		if (instance == null) {
			instance = new SaleConstraintFacade();
		}
		return instance;
	}

	private SaleConstraintFacade() {
	}

	public List<SaleConstraintHeader> getAllHeaders(Session session) {
		Criteria criteria = session.createCriteria(SaleConstraintHeader.class);
		@SuppressWarnings("unchecked")
		List<SaleConstraintHeader> saleConstraintHeaders = criteria.list();
		return saleConstraintHeaders;
	}

	public SaleConstraintDetail getDetail(Integer id, Session session) {
		SaleConstraintDetail saleConstraintDetail = (SaleConstraintDetail) session
				.get(SaleConstraintDetail.class, id);
		return saleConstraintDetail;
	}

	public SaleConstraintHeader getHeader(Integer id, Session session) {
		SaleConstraintHeader saleConstraintHeader = (SaleConstraintHeader) session
				.get(SaleConstraintHeader.class, id);
		return saleConstraintHeader;
	}

	public void performPosting() {
		List<SaleConstraintHeader> saleConstraintHeaders = null;

		{ // Prepare list of SaleConstraintHeader
			Session session = null;
			try {
				session = HibernateUtils.openSession();
				saleConstraintHeaders = getSaleConstraintHeaders(session);
			} finally {
				session.close();
			}
		}

		{
			for (SaleConstraintHeader saleConstraintHeader : saleConstraintHeaders) {
				List<SaleConstraintDetail> saleConstraintDetails = null;
				{ // Prepare list of SaleConstraintDetail
					Session session = HibernateUtils.openSession();
					try {
						saleConstraintDetails = getSaleConstraintDetails(
								saleConstraintHeader.getId(), session);
					} finally {
						session.close();
					}
				}

				{
					Session session = HibernateUtils.openSession();
					try {
						session.beginTransaction();

						SaleHeader saleHeader = saleConstraintHeader
								.toSaleHeader();
						List<SaleDetail> saleDetails = new ArrayList<>();

						for (SaleConstraintDetail saleConstraintDetail : saleConstraintDetails) {
							SaleDetail saleDetail = saleConstraintDetail
									.toSaleDetail();
							saleDetail.setSaleHeader(saleHeader);
							saleDetails.add(saleDetail);
						}

						SaleFacade.getInstance().performSale(saleHeader,
								saleDetails, session);

						for (SaleConstraintDetail saleConstraintDetail : saleConstraintDetails) {
							session.delete(saleConstraintDetail);
						}
						session.delete(saleConstraintHeader);

						session.getTransaction().commit();
						session.close();

					} catch (Exception e) {

						session.getTransaction().rollback();
						session.close();

						try {
							session = HibernateUtils.openSession();
							session.beginTransaction();

							if (e instanceof UserException) {
								saleConstraintHeader
										.setPostingStatus(SaleConstraintPostingStatus.USER_EXCEPTION);
								saleConstraintHeader.setPostingMessage(e
										.getMessage());
							} else {
								saleConstraintHeader
										.setPostingStatus(SaleConstraintPostingStatus.APP_EXCEPTION);
								if (e instanceof AppException) {
									saleConstraintHeader.setPostingMessage(e
											.getCause().getMessage());
								} else {
									e.printStackTrace();
									saleConstraintHeader.setPostingMessage(e
											.getMessage());
								}
							}
							saleConstraintHeader
									.setPostingTriedCount(saleConstraintHeader
											.getPostingTriedCount() + 1);
							session.saveOrUpdate(saleConstraintHeader);

							session.getTransaction().commit();
						} finally {
							session.close();
						}
					}
				}
			}
		}
	}

	public void performSale(SaleConstraintHeader saleConstraintHeader,
			List<SaleConstraintDetail> saleConstraintDetails, Session session)
			throws AppException {

		session.saveOrUpdate(saleConstraintHeader);
		for (SaleConstraintDetail saleConstraintDetail : saleConstraintDetails) {
			saleConstraintDetail.setSaleConstraintHeader(saleConstraintHeader);
			session.saveOrUpdate(saleConstraintDetail);
			insertIntoLogTable(saleConstraintDetail, session);
		}
	}

	public List<SaleConstraintLog> searchSaleConstraintLog(Date beginDate,
			Date endDate, Session session) {
		Criteria criteria = session.createCriteria(SaleConstraintLog.class);

		if (beginDate != null) {
			criteria.add(Restrictions.ge("transactionTimestamp", beginDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le("transactionTimestamp", endDate));
		}

		@SuppressWarnings("unchecked")
		List<SaleConstraintLog> saleConstraintLogs = criteria.list();

		return saleConstraintLogs;
	}

	public List<SaleConstraintDetail> getSaleConstraintDetails(
			Integer saleConstraintHeaderId, Session session) {
		Criteria criteria = session.createCriteria(SaleConstraintDetail.class);
		criteria.createAlias("saleConstraintHeader", "saleConstraintHeader");

		criteria.add(Restrictions.eq("saleConstraintHeader.id",
				saleConstraintHeaderId));

		@SuppressWarnings("unchecked")
		List<SaleConstraintDetail> saleConstraintDetails = criteria.list();
		return saleConstraintDetails;
	}

	public List<SaleConstraintHeader> getSaleConstraintHeaders(Session session) {
		Criteria criteria = session.createCriteria(SaleConstraintHeader.class);
		@SuppressWarnings("unchecked")
		List<SaleConstraintHeader> saleConstraintHeaders = criteria.list();
		return saleConstraintHeaders;
	}

	private void insertIntoLogTable(SaleConstraintDetail saleConstraintDetail,
			Session session) {
		SaleConstraintLog saleConstraintLog = saleConstraintDetail
				.toSaleConstraintLog();
		session.saveOrUpdate(saleConstraintLog);
	}
}