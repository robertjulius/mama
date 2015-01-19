package com.ganesha.minimarket.ui.forms.sale;

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.constants.Loggers;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.constants.Enums.SaleConstraintPostingStatus;
import com.ganesha.minimarket.facade.SaleConstraintFacade;
import com.ganesha.minimarket.model.SaleConstraintDetail;
import com.ganesha.minimarket.model.SaleConstraintHeader;
import com.ganesha.minimarket.model.SaleDetail;
import com.ganesha.minimarket.model.SaleHeader;
import com.ganesha.minimarket.utils.PermissionConstants;

public class SaleConstraintForm extends PenjualanForm {

	private static final long serialVersionUID = 1401014426195840845L;

	public SaleConstraintForm(Window parent) {
		super(parent);
		setTitle("Transaksi Penjualan Constraint");
		setPermissionCode(PermissionConstants.CONSTRAINT_SAL_FORM);
	}

	@Override
	protected void performSale(SaleHeader saleHeader,
			List<SaleDetail> saleDetails, Session session) throws AppException,
			UserException {

		SaleConstraintHeader saleConstraintHeader = SaleConstraintHeader
				.fromSaleHeader(saleHeader);
		saleConstraintHeader
				.setPostingStatus(SaleConstraintPostingStatus.WAITING);
		saleConstraintHeader.setPostingTriedCount(0);

		List<SaleConstraintDetail> saleConstraintDetails = new ArrayList<>();
		for (SaleDetail saleDetail : saleDetails) {
			SaleConstraintDetail saleConstraintDetail = SaleConstraintDetail
					.fromSaleDetail(saleDetail);
			saleConstraintDetails.add(saleConstraintDetail);
		}

		LoggerFactory.getLogger(Loggers.SALE).debug(
				"Starting to insert SaleConstraintHeader {"
						+ saleConstraintHeader.getTransactionNumber() + "|"
						+ saleConstraintHeader.getSubTotalAmount() + "|"
						+ saleConstraintHeader.getTaxAmount() + "|"
						+ saleConstraintHeader.getTotalAmount() + "|"
						+ saleConstraintHeader.getPay() + "|"
						+ saleConstraintHeader.getMoneyChange()
						+ "} into database");

		SaleConstraintFacade.getInstance().performSale(saleConstraintHeader,
				saleConstraintDetails, session);

		LoggerFactory.getLogger(Loggers.SALE).debug(
				"Finished inserting SaleConstraintHeader into database, the generated id is "
						+ saleHeader.getId());

		ActivityLogFacade.doLog(getPermissionCode(), ActionType.TRANSACTION,
				Main.getUserLogin(), saleConstraintHeader, session);

		LoggerFactory.getLogger(Loggers.SALE).debug(
				"Logging to ActivityLog is done");
	}
}
