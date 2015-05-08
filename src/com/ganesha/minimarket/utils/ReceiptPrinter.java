package com.ganesha.minimarket.utils;

import java.util.List;

public class ReceiptPrinter {

	private static final String NEW_LINE = "\r\n";

	private static final int LENGTH_PARAGRAPH = 40;
	private static final int LENGTH_COMPANY_NAME = LENGTH_PARAGRAPH;
	private static final int LENGTH_COMPANY_ADDRESS = LENGTH_PARAGRAPH;
	private static final int LENGTH_TRANSACTION_NUMBER = LENGTH_PARAGRAPH;
	private static final int LENGTH_TRANSACTION_TIMESTAMP = LENGTH_PARAGRAPH;
	private static final int LENGTH_CASHIER = LENGTH_PARAGRAPH;
	private static final int LENGTH_ITEM_NAME = LENGTH_PARAGRAPH;
	private static final int LENGTH_EMPTY_FIELD = 4;
	private static final int LENGTH_QUANTITY = 3;
	private static final int LENGTH_PRICE_PER_UNIT = 14;
	private static final int LENGTH_DISCOUNT_PERCENT = 5;
	private static final int LENGTH_TOTAL_AMOUNT = 14;
	private static final int LENGTH_TOTAL_BELANJA = 25;
	private static final int LENGTH_PAY = 25;
	private static final int LENGTH_MONEY_CHANGE = 25;

	private String companyName;
	private String companyAddress;
	private String transactionNumber;
	private String transactionTimestamp;
	private String cashier;
	private List<ItemBelanja> itemBelanjaList;
	private String totalBelanja;
	private String pay;
	private String moneyChange;

	public ReceiptPrinter(String companyName, String companyAddress,
			String transactionNumber, String transactionTimestamp,
			String cashier, List<ItemBelanja> itemBelanjaList,
			String totalBelanja, String pay, String moneyChange) {
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

		builder.append(
				alignCenter("KAMI TIDAK MELAYANI PENUKARAN BARANG",
						LENGTH_PARAGRAPH)).append(NEW_LINE);

		builder.append(
				alignCenter("TANPA DISERTAI NOTA ASLI", LENGTH_PARAGRAPH))
				.append(NEW_LINE);

		builder.append(alignCenter("*** TERIMA KASIH ***", LENGTH_PARAGRAPH))
				.append(NEW_LINE);

		return builder.toString();
	}

	public String buildHeader() {
		StringBuilder builder = new StringBuilder();
		builder.append(alignLeft(companyName, LENGTH_COMPANY_NAME)).append(
				NEW_LINE);
		builder.append(alignLeft(companyAddress, LENGTH_COMPANY_ADDRESS))
				.append(NEW_LINE);
		builder.append(alignLeft(transactionNumber, LENGTH_TRANSACTION_NUMBER))
				.append(NEW_LINE);
		builder.append(
				alignLeft(transactionTimestamp, LENGTH_TRANSACTION_TIMESTAMP))
				.append(NEW_LINE);
		builder.append(alignLeft(cashier, LENGTH_CASHIER)).append(NEW_LINE);
		return builder.toString();
	}

	public String buildItemList() {
		StringBuilder builder = new StringBuilder();
		for (ItemBelanja itemBelanja : itemBelanjaList) {
			builder.append(
					alignLeft(itemBelanja.getItemName(), LENGTH_ITEM_NAME))
					.append(NEW_LINE);
			builder.append(alignRight("", LENGTH_EMPTY_FIELD));
			builder.append(alignRight(itemBelanja.getQuantiy(), LENGTH_QUANTITY));
			builder.append(alignRight(itemBelanja.getPricePerUnit(),
					LENGTH_PRICE_PER_UNIT));
			builder.append(alignRight(itemBelanja.getDiscountPercent(),
					LENGTH_DISCOUNT_PERCENT));
			builder.append(
					alignRight(itemBelanja.getTotalAmount(),
							LENGTH_TOTAL_AMOUNT)).append(NEW_LINE);
		}
		return builder.toString();
	}

	public String buildReceipt() {
		StringBuilder builder = new StringBuilder();

		builder.append(buildHeader());
		builder.append(buildSeparator());
		builder.append(buildItemList());
		builder.append(buildSeparator());
		builder.append(buildSummary());
		builder.append(NEW_LINE);
		builder.append(buildFooter());

		return builder.toString();
	}

	public String buildSeparator() {
		String separator = "";
		for (int i = 0; i < LENGTH_PARAGRAPH; ++i) {
			separator += "-";
		}
		return separator + NEW_LINE;
	}

	public String buildSummary() {
		StringBuilder builder = new StringBuilder();
		builder.append(alignLeft("Total Belanja", LENGTH_PARAGRAPH
				- LENGTH_TOTAL_BELANJA));
		builder.append(alignRight(totalBelanja, LENGTH_TOTAL_BELANJA)).append(
				NEW_LINE);
		builder.append(alignLeft("Bayar", LENGTH_PARAGRAPH - LENGTH_PAY));
		builder.append(alignRight(pay, LENGTH_PAY)).append(NEW_LINE);
		builder.append(alignLeft("Kembali", LENGTH_PARAGRAPH
				- LENGTH_MONEY_CHANGE));
		builder.append(alignRight(moneyChange, LENGTH_MONEY_CHANGE)).append(
				NEW_LINE);
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
			return string.substring(0, width) + NEW_LINE
					+ alignCenter(string.substring(width), width);
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
			return string.substring(0, width) + NEW_LINE
					+ alignLeft(string.substring(width), width);
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

		public ItemBelanja(String itemName, String quantiy,
				String pricePerUnit, String discountPercent, String totalAmount) {
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
