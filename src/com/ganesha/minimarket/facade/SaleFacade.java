package com.ganesha.minimarket.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.constants.CoaCodeConstants;
import com.ganesha.accounting.constants.Enums.DebitCreditFlag;
import com.ganesha.accounting.facade.AccountFacade;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Customer;
import com.ganesha.minimarket.model.ItemStock;
import com.ganesha.minimarket.model.SaleDetail;
import com.ganesha.minimarket.model.SaleHeader;
import com.ganesha.minimarket.utils.ReceiptPrinter;
import com.ganesha.minimarket.utils.ReceiptPrinter.ItemBelanja;

public class SaleFacade implements TransactionFacade {

	private static SaleFacade instance;

	public static SaleFacade getInstance() {
		if (instance == null) {
			instance = new SaleFacade();
		}
		return instance;
	}

	private SaleFacade() {
	}

	public void cetakStruck(SaleHeader saleHeader, List<SaleDetail> saleDetails) {

		String companyName = Main.getCompany().getName();
		String companyAddress = Main.getCompany().getAddress();
		String transactionTimestamp = Formatter
				.formatTimestampToString(saleHeader.getTransactionTimestamp());
		String userInformation = "[" + Main.getUserLogin().getLogin() + "] "
				+ Main.getUserLogin().getName();
		String totalBelanja = Formatter.formatNumberToString(saleHeader
				.getTotalAmount());
		String pay = Formatter.formatNumberToString(saleHeader.getPay());
		String moneyChange = Formatter.formatNumberToString(saleHeader
				.getMoneyChange());

		ReceiptPrinter receiptPrinter = new ReceiptPrinter();
		receiptPrinter.setCompanyName(companyName);
		receiptPrinter.setCompanyAddress(companyAddress);
		receiptPrinter.setTransactionTimestamp(transactionTimestamp);
		receiptPrinter.setUserInformation(userInformation);
		receiptPrinter.setTotalBelanja(totalBelanja);
		receiptPrinter.setPay(pay);
		receiptPrinter.setMoneyChange(moneyChange);

		List<ItemBelanja> itemBelanjaList = new ArrayList<>();
		for (SaleDetail saleDetail : saleDetails) {
			String itemName = saleDetail.getItemName();
			String quantiy = Formatter.formatNumberToString(saleDetail
					.getQuantity());
			String pricePerUnit = Formatter.formatNumberToString(saleDetail
					.getPricePerUnit());
			String discountPercent = Formatter.formatNumberToString(saleDetail
					.getDiscountPercent()) + "%";
			String totalAmount = Formatter.formatNumberToString(saleDetail
					.getTotalAmount());

			ItemBelanja itemBelanja = new ItemBelanja();
			itemBelanja.setItemName(itemName);
			itemBelanja.setQuantiy(quantiy);
			itemBelanja.setPricePerUnit(pricePerUnit);
			itemBelanja.setDiscountPercent(discountPercent);
			itemBelanja.setTotalAmount(totalAmount);

			itemBelanjaList.add(itemBelanja);
		}

		receiptPrinter.setItemBelanjaList(itemBelanjaList);

		String struct = receiptPrinter.buildStruct();
		System.out.println(struct);
	}

	// public void cetakStruck(SaleHeader saleHeader, List<SaleDetail>
	// saleDetails)
	// throws AppException {
	// String reportFile = "com/ganesha/minimarket/reports/Struck.jrxml";
	//
	// Map<String, Object> paramReport = new HashMap<String, Object>();
	// paramReport.put("companyName", Main.getCompany().getName());
	// paramReport.put("companyAddress", Main.getCompany().getAddress());
	// paramReport.put("transactionTimestamp",
	// saleHeader.getTransactionTimestamp());
	// paramReport.put("userLoginId", Main.getUserLogin().getLogin());
	// paramReport.put("userLoginName", Main.getUserLogin().getName());
	// paramReport.put("pay", saleHeader.getPay());
	// paramReport.put("moneyChange", saleHeader.getMoneyChange());
	//
	// InputStream inputStream = null;
	// try {
	// inputStream = this.getClass().getClassLoader()
	// .getResourceAsStream(reportFile);
	//
	// JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
	//
	// JasperReport jasperReport = JasperCompileManager
	// .compileReport(jasperDesign);
	//
	// JasperPrint jasperPrint = JasperFillManager.fillReport(
	// jasperReport, paramReport, new JRBeanCollectionDataSource(
	// saleDetails));
	//
	// // JRViewer viewer = new JRViewer(jasperPrint);
	// // ReportViewerDialog.viewReport(null, "TEST", viewer);
	//
	// JRTextExporter exporter = new JRTextExporter();
	// exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, 50);
	// exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, 30);
	// exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
	// exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
	// "C:/Users/Asus-020/Desktop/test.txt");
	// exporter.exportReport();
	//
	// } catch (JRException e) {
	// throw new AppException(e);
	// } finally {
	// if (inputStream != null) {
	// try {
	// inputStream.close();
	// } catch (IOException e) {
	// throw new AppException(e);
	// }
	// }
	// }
	// }

	public SaleDetail getDetail(String transactionNumber, Integer orderNum,
			Session session) {
		Criteria criteria = session.createCriteria(SaleDetail.class);
		criteria.createAlias("saleHeader", "saleHeader");
		criteria.add(Restrictions.eq("saleHeader.transactionNumber",
				transactionNumber));
		criteria.add(Restrictions.eq("orderNum", orderNum));

		SaleDetail saleDetail = (SaleDetail) criteria.uniqueResult();
		return saleDetail;
	}

	public void performSale(SaleHeader saleHeader,
			List<SaleDetail> saleDetails, Session session) throws AppException {

		StockFacade stockFacade = StockFacade.getInstance();
		session.saveOrUpdate(saleHeader);

		for (SaleDetail saleDetail : saleDetails) {
			ItemStock itemStock = stockFacade.getDetail(
					saleDetail.getItemCode(), session);

			int stock = itemStock.getStock() - saleDetail.getQuantity();
			itemStock.setStock(stock);

			saleDetail.setSaleHeader(saleHeader);
			session.saveOrUpdate(saleDetail);
			session.saveOrUpdate(itemStock);

			AccountFacade.getInstance().insertIntoAccount(
					CoaCodeConstants.PENJUALAN, saleDetail.getId(),
					CommonUtils.getCurrentTimestamp(), "Penjualan", "",
					DebitCreditFlag.CREDIT, saleDetail.getTotalAmount(),
					session);
		}

		// cetakStruck(saleHeader, saleDetails);
	}

	@Override
	public List<Map<String, Object>> searchTransaction(
			String transactionNumber, Date beginDate, Date endDate,
			Session session) {

		String sqlString = "SELECT new Map("
				+ "header.transactionNumber AS transactionNumber"
				+ ", header.transactionTimestamp AS transactionTimestamp"
				+ ", detail.orderNum AS orderNum"
				+ ", detail.itemCode AS itemCode"
				+ ", detail.itemName AS itemName"
				+ ", detail.quantity AS quantity"
				+ ", detail.unit AS unit"
				+ ", detail.pricePerUnit AS pricePerUnit"
				+ ", detail.totalAmount AS totalAmount"
				+ ") FROM SaleDetail detail INNER JOIN detail.saleHeader header WHERE 1=1";

		if (!transactionNumber.trim().equals("")) {
			sqlString += " AND header.transactionNumber LIKE :transactionNumber";
		}

		if (beginDate != null) {
			sqlString += " AND header.transactionTimestamp >= :beginDate";
		}

		if (endDate != null) {
			sqlString += " AND header.transactionTimestamp <= :endDate";
		}

		Query query = session.createQuery(sqlString);
		HqlParameter parameter = new HqlParameter(query);
		parameter.put("transactionNumber", "%" + transactionNumber + "%");
		parameter.put("beginDate", beginDate);
		parameter.put("endDate", endDate);
		parameter.validate();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = query.list();

		return list;
	}

	public SaleHeader validateForm(String transactionNumber,
			Timestamp transactionTimestamp, String customerCode,
			Double subTotalAmount, Double taxPercent, Double taxAmount,
			Double totalAmount, Double pay, Double moneyChange, Session session)
			throws UserException {

		if (customerCode == null || customerCode.equals("")) {
			throw new UserException("Field Customer dibutuhkan");
		}

		SaleHeader header = new SaleHeader();

		Customer customer = CustomerFacade.getInstance().getDetail(
				customerCode, session);

		header.setTransactionNumber(transactionNumber);
		header.setTransactionTimestamp(transactionTimestamp);
		header.setCustomer(customer);
		header.setSubTotalAmount(BigDecimal.valueOf(subTotalAmount));
		header.setTaxPercent(BigDecimal.valueOf(taxPercent));
		header.setTaxAmount(BigDecimal.valueOf(taxAmount));
		header.setTotalAmount(BigDecimal.valueOf(totalAmount));
		header.setPay(BigDecimal.valueOf(pay));
		header.setMoneyChange(BigDecimal.valueOf(moneyChange));

		header.setDisabled(false);
		header.setDeleted(false);
		header.setLastUpdatedBy(Main.getUserLogin().getId());
		header.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		return header;
	}
}