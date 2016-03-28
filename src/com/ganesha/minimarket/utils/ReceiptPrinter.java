package com.ganesha.minimarket.utils;

import java.util.List;

public class ReceiptPrinter {

	private static final String NEW_LINE = "\r\n";

	private String companyName;
	private String companyAddress;
	private String transactionNumber;
	private String transactionTimestamp;
	private String cashier;
	private List<ItemBelanja> itemBelanjaList;
	private String totalBelanja;
	private String pay;
	private String moneyChange;

	private ReceiptPrinterSetting receiptPrinterSetting = ReceiptPrinterUtils.PRINTER_SETTING;

	public ReceiptPrinter(String companyName, String companyAddress, String transactionNumber,
			String transactionTimestamp, String cashier, List<ItemBelanja> itemBelanjaList, String totalBelanja,
			String pay, String moneyChange) {

		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.transactionNumber = transactionNumber;
		this.transactionTimestamp = transactionTimestamp;
		this.cashier = cashier;
		this.itemBelanjaList = itemBelanjaList;
		this.totalBelanja = totalBelanja;
		this.pay = pay;
		this.moneyChange = moneyChange;
	}

	public String buildFooter() {
		StringBuilder builder = new StringBuilder();

		builder.append(alignCenter("KAMI TIDAK MELAYANI PENUKARAN BARANG", receiptPrinterSetting.getPaperWidth()))
				.append(NEW_LINE);

		builder.append(alignCenter("TANPA DISERTAI NOTA ASLI", receiptPrinterSetting.getPaperWidth())).append(NEW_LINE);

		builder.append(alignCenter("*** TERIMA KASIH ***", receiptPrinterSetting.getPaperWidth())).append(NEW_LINE);

		return builder.toString();
	}

	public String buildHeader() {
		StringBuilder builder = new StringBuilder();
		builder.append(alignLeft(companyName, receiptPrinterSetting.getPaperWidth())).append(NEW_LINE);
		builder.append(alignLeft(companyAddress, receiptPrinterSetting.getPaperWidth())).append(NEW_LINE);
		builder.append(alignLeft(transactionNumber, receiptPrinterSetting.getPaperWidth())).append(NEW_LINE);
		builder.append(alignLeft(transactionTimestamp, receiptPrinterSetting.getPaperWidth())).append(NEW_LINE);
		builder.append(alignLeft(cashier, receiptPrinterSetting.getPaperWidth())).append(NEW_LINE);
		return builder.toString();
	}

	public String buildItemList() {
		StringBuilder builder = new StringBuilder();
		for (ItemBelanja itemBelanja : itemBelanjaList) {
			builder.append(alignLeft(itemBelanja.getItemName(), receiptPrinterSetting.getPaperWidth()))
					.append(NEW_LINE);
			builder.append(alignRight(itemBelanja.getQuantiy(), receiptPrinterSetting.getQuantityLength()));
			builder.append(alignRight(itemBelanja.getPricePerUnit(), receiptPrinterSetting.getPriceLength()));
			builder.append(alignRight(itemBelanja.getDiscountPercent(), receiptPrinterSetting.getDiscountLength()));
			builder.append(alignRight(itemBelanja.getTotalAmount(), receiptPrinterSetting.getAmountLength()))
					.append(NEW_LINE);
		}
		return builder.toString();
	}

	public String buildReceipt() {
		StringBuilder builder = new StringBuilder();

		for (int i=0; i<receiptPrinterSetting.getRowCountBeforeHeader(); ++i) {
			builder.append(NEW_LINE);	
		}
		
		builder.append(buildHeader());
		builder.append(buildSeparator());
		builder.append(buildItemList());
		builder.append(buildSeparator());
		builder.append(buildSummary());
		builder.append(NEW_LINE);
		builder.append(buildFooter());
		
		for (int i=0; i<receiptPrinterSetting.getRowCountAfterFooter(); ++i) {
			builder.append(NEW_LINE);	
		}

		return builder.toString();
	}

	public String buildSeparator() {
		String separator = "";
		for (int i = 0; i < receiptPrinterSetting.getPaperWidth(); ++i) {
			separator += "-";
		}
		return separator + NEW_LINE;
	}

	public String buildSummary() {
		StringBuilder builder = new StringBuilder();
		builder.append(alignLeft("Total Belanja", receiptPrinterSetting.getSummaryLeftLength()));
		builder.append(alignRight(totalBelanja, receiptPrinterSetting.getSummaryRightLength())).append(NEW_LINE);
		builder.append(alignLeft("Bayar", receiptPrinterSetting.getSummaryLeftLength()));
		builder.append(alignRight(pay, receiptPrinterSetting.getSummaryRightLength())).append(NEW_LINE);
		builder.append(alignLeft("Kembali", receiptPrinterSetting.getSummaryLeftLength()));
		builder.append(alignRight(moneyChange, receiptPrinterSetting.getSummaryRightLength())).append(NEW_LINE);
		return builder.toString();
	}

	public String getCashier() {
		return cashier;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public String getCompanyName() {
		return companyName;
	}

	public List<ItemBelanja> getItemBelanjaList() {
		return itemBelanjaList;
	}

	public String getMoneyChange() {
		return moneyChange;
	}

	public String getPay() {
		return pay;
	}

	public String getTotalBelanja() {
		return totalBelanja;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public String getTransactionTimestamp() {
		return transactionTimestamp;
	}

	private String alignCenter(String string, int width) {
		int length = string.length();
		if (length > width) {
			return string.substring(0, width) + NEW_LINE + alignCenter(string.substring(width), width);
		} else {
			int emptyCount = width - length;
			int leftEmptyCount = emptyCount / 2;
			String leftEmptyString = "";
			for (int i = 0; i < leftEmptyCount; ++i) {
				leftEmptyString += " ";
			}
			return leftEmptyString + string;
		}
	}

	private String alignLeft(String string, int width) {
		int length = string.length();
		if (length > width) {
			return string.substring(0, width) + NEW_LINE + alignLeft(string.substring(width), width);
		} else {
			int emptyCount = width - length;
			String emptyString = "";
			for (int i = 0; i < emptyCount; ++i) {
				emptyString += " ";
			}
			return string + emptyString;
		}
	}

	private String alignRight(String string, int width) {
		int length = string.length();
		if (length > width) {
			return string.substring(0, width);
		} else {
			int emptyCount = width - length;
			String emptyString = "";
			for (int i = 0; i < emptyCount; ++i) {
				emptyString += " ";
			}
			return emptyString + string;
		}
	}

	public static class ItemBelanja {
		private String itemName;
		private String quantiy;
		private String pricePerUnit;
		private String discountPercent;
		private String totalAmount;

		public ItemBelanja(String itemName, String quantiy, String pricePerUnit, String discountPercent,
				String totalAmount) {
			this.itemName = itemName;
			this.quantiy = quantiy;
			this.pricePerUnit = pricePerUnit;
			this.discountPercent = discountPercent;
			this.totalAmount = totalAmount;
		}

		public String getDiscountPercent() {
			return discountPercent;
		}

		public String getItemName() {
			return itemName;
		}

		public String getPricePerUnit() {
			return pricePerUnit;
		}

		public String getQuantiy() {
			return quantiy;
		}

		public String getTotalAmount() {
			return totalAmount;
		}
	}
}
