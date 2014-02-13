package com.ganesha.minimarket.facade;

import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.accounting.constants.CoaCodeConstants;
import com.ganesha.accounting.constants.Enums.DebitCreditFlag;
import com.ganesha.accounting.facade.AccountFacade;
import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.hibernate.HqlParameter;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.model.Customer;
import com.ganesha.minimarket.model.Item;
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

	public void cetakReceipt(SaleHeader saleHeader, List<SaleDetail> saleDetails)
			throws PrintException {

		String companyName = Main.getCompany().getName();
		String companyAddress = Main.getCompany().getAddress();
		String transactionNumber = "No      : "
				+ saleHeader.getTransactionNumber();
		String transactionTimestamp = "Tanggal : "
				+ Formatter.formatTimestampToString(saleHeader
						.getTransactionTimestamp());
		String cashier = "Kasir   : " + Main.getUserLogin().getName();
		String totalBelanja = Formatter.formatNumberToString(saleHeader
				.getTotalAmount());
		String pay = Formatter.formatNumberToString(saleHeader.getPay());
		String moneyChange = Formatter.formatNumberToString(saleHeader
				.getMoneyChange());

		List<ItemBelanja> itemBelanjaList = new ArrayList<>();
		for (SaleDetail saleDetail : saleDetails) {
			String itemName = saleDetail.getItemName();
			String quantiy = Formatter.formatNumberToString(saleDetail
					.getQuantity()) + "x";
			String pricePerUnit = Formatter.formatNumberToString(saleDetail
					.getPricePerUnit());
			String discountPercent = saleDetail.getDiscountPercent()
					.doubleValue() > 0 ? Formatter
					.formatNumberToString(saleDetail.getDiscountPercent())
					+ "%" : "";
			String totalAmount = Formatter.formatNumberToString(saleDetail
					.getTotalAmount());

			ItemBelanja itemBelanja = new ItemBelanja(itemName, quantiy,
					pricePerUnit, discountPercent, totalAmount);

			itemBelanjaList.add(itemBelanja);
		}

		ReceiptPrinter receiptPrinter = new ReceiptPrinter(companyName,
				companyAddress, transactionNumber, transactionTimestamp,
				cashier, itemBelanjaList, totalBelanja, pay, moneyChange);

		String receipt = receiptPrinter.buildReceipt();

		PrintService[] services = PrinterJob.lookupPrintServices();
		InputStream is = null;
		try {
			String printerName = (String) SystemSetting
					.get(GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT);
			is = new ByteArrayInputStream(receipt.getBytes());
			for (PrintService printService : services) {
				if (printService.getName().equals(printerName)) {
					DocFlavor flavor = DocFlavor.STRING.INPUT_STREAM.AUTOSENSE;
					Doc doc = new SimpleDoc(is, flavor, null);
					DocPrintJob printJob = printService.createPrintJob();
					PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
					pras.add(new Copies(1));

					PrintJobWatcher pjw = new PrintJobWatcher(printJob);
					printJob.print(doc, pras);

					pjw.waitForDone();
				}
			}
		} catch (AppException e) {
			throw new PrintException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new PrintException(e);
				}
			}
		}
	}

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

		ItemFacade stockFacade = ItemFacade.getInstance();
		session.saveOrUpdate(saleHeader);

		for (SaleDetail saleDetail : saleDetails) {
			Item item = stockFacade.getDetail(saleDetail.getItemId(), session);

			int stock = stockFacade.calculateStock(item)
					- saleDetail.getQuantity();
			stockFacade.reAdjustStock(item, stock, session);

			saleDetail.setSaleHeader(saleHeader);
			session.saveOrUpdate(saleDetail);
			session.saveOrUpdate(item);

			AccountFacade.getInstance().insertIntoAccount(
					CoaCodeConstants.PENJUALAN, saleDetail.getId(),
					CommonUtils.getCurrentTimestamp(), "Penjualan", "",
					DebitCreditFlag.CREDIT, saleDetail.getTotalAmount(),
					session);
		}
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
			Timestamp transactionTimestamp, Integer customerId,
			Double subTotalAmount, Double taxPercent, Double taxAmount,
			Double totalAmount, Double pay, Double moneyChange, Session session)
			throws UserException {

		if (customerId == null) {
			throw new UserException("Field Customer dibutuhkan");
		}

		SaleHeader header = new SaleHeader();

		Customer customer = CustomerFacade.getInstance().getDetail(customerId,
				session);

		header.setTransactionNumber(transactionNumber);
		header.setTransactionTimestamp(transactionTimestamp);
		header.setCustomer(customer);
		header.setSubTotalAmount(BigDecimal.valueOf(subTotalAmount));
		header.setTaxPercent(BigDecimal.valueOf(taxPercent));
		header.setTaxAmount(BigDecimal.valueOf(taxAmount));
		header.setTotalAmount(BigDecimal.valueOf(totalAmount));
		header.setPay(BigDecimal.valueOf(pay));
		header.setMoneyChange(BigDecimal.valueOf(moneyChange));

		header.setLastUpdatedBy(Main.getUserLogin().getId());
		header.setLastUpdatedTimestamp(CommonUtils.getCurrentTimestamp());

		return header;
	}

	class PrintJobWatcher {
		boolean done = false;

		PrintJobWatcher(DocPrintJob job) {
			job.addPrintJobListener(new PrintJobAdapter() {
				@Override
				public void printJobCanceled(PrintJobEvent pje) {
					allDone();
				}

				@Override
				public void printJobCompleted(PrintJobEvent pje) {
					allDone();
				}

				@Override
				public void printJobFailed(PrintJobEvent pje) {
					allDone();
				}

				@Override
				public void printJobNoMoreEvents(PrintJobEvent pje) {
					allDone();
				}

				void allDone() {
					synchronized (PrintJobWatcher.this) {
						done = true;
						System.out.println("Printing done ...");
						PrintJobWatcher.this.notify();
					}
				}
			});
		}

		public synchronized void waitForDone() {
			try {
				while (!done) {
					wait();
				}
			} catch (InterruptedException e) {
			}
		}
	}
}