package com.ganesha.prepaid.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.SaleFacade;
import com.ganesha.minimarket.model.Customer;
import com.ganesha.minimarket.model.SaleDetail;
import com.ganesha.minimarket.model.SaleHeader;

public class PrepaidSaleFacade {

	private static PrepaidSaleFacade instance;

	public static PrepaidSaleFacade getInstance() {
		if (instance == null) {
			instance = new PrepaidSaleFacade();
		}
		return instance;
	}

	private PrepaidSaleFacade() {
	}

	public List<SaleDetail> performSale(Customer customer,
			String transactionNumber, BigDecimal price, BigDecimal pay,
			BigDecimal moneyChange, Integer itemId, String itemCode,
			String itemName, Integer nominal, String unit, Session session)
			throws AppException, UserException {

		SaleHeader saleHeader = new SaleHeader();
		saleHeader.setTransactionNumber(transactionNumber);
		saleHeader.setTransactionTimestamp(CommonUtils.getCurrentTimestamp());
		saleHeader.setCustomer(customer);
		saleHeader.setSubTotalAmount(price);
		saleHeader.setTaxPercent(BigDecimal.valueOf(0));
		saleHeader.setTaxAmount(saleHeader.getSubTotalAmount().multiply(
				saleHeader.getTaxPercent()));
		saleHeader.setTotalAmount(saleHeader.getSubTotalAmount().add(
				saleHeader.getTaxAmount()));
		saleHeader.setPay(pay);
		saleHeader.setMoneyChange(moneyChange);

		saleHeader.setLastUpdatedBy(Main.getUserLogin().getId());
		saleHeader.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		SaleDetail saleDetail = new SaleDetail();
		saleDetail.setSaleHeader(saleHeader);
		saleDetail.setOrderNum(0);
		saleDetail.setItemId(itemId);
		saleDetail.setItemCode(itemCode);
		saleDetail.setItemName(itemName);
		saleDetail.setQuantity(nominal);
		saleDetail.setUnit(unit);
		saleDetail.setPricePerUnit(price.divide(BigDecimal.valueOf(saleDetail
				.getQuantity())));
		saleDetail.setDiscountPercent(BigDecimal.valueOf(0));
		saleDetail.setTotalAmount(price);

		List<SaleDetail> saleDetails = new ArrayList<>();
		saleDetails.add(saleDetail);

		SaleFacade.getInstance().performSale(saleHeader, saleDetails, session);

		return saleDetails;
	}
}
