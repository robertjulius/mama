package com.ganesha.prepaid.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.ganesha.core.utils.CommonUtils;
import com.ganesha.minimarket.Main;
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

	public List<SaleDetail> prepareSaleDetails(Customer customer,
			String transactionNumber, BigDecimal price, BigDecimal pay,
			BigDecimal moneyChange, Integer itemId, String itemCode,
			String itemName, Integer quantity, String unit, Session session) {

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
		saleDetail.setQuantity(quantity);
		saleDetail.setUnit(unit);
		saleDetail.setPricePerUnit(price.divide(
				BigDecimal.valueOf(saleDetail.getQuantity()), 10,
				RoundingMode.HALF_UP));
		saleDetail.setDiscountPercent(BigDecimal.valueOf(0));
		saleDetail.setTotalAmount(price);

		List<SaleDetail> saleDetails = new ArrayList<>();
		saleDetails.add(saleDetail);

		return saleDetails;
	}
}
