package com.ganesha.minimarket.utils;

import java.util.List;

public class ReceiptPrinter {

	private static final String NEW_LINE = "\r\n";

	private static final int LENGTH_PARAGRAPH = 40;
	private static final int LENGTH_COMPANY_NAME = LENGTH_PARAGRAPH;
	private static final int LENGTH_COMPANY_ADDRESS = LENGTH_PARAGRAPH;
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
	private String transactionTimestamp;
	private String cashier;
	private List<ItemBelanja> itemBelanjaList;
	private String totalBelanja;
	private String pay;
	private String moneyChange;

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

		builder.append(alignLeft(companyName, LENGTH_COMPANY_NAME)).append(
				NEW_LINE);
		builder.append(alignLeft(companyAddress, LENGTH_COMPANY_ADDRESS))
				.append(NEW_LINE);
		builder.append(
				alignLeft(transactionTimestamp, LENGTH_TRANSACTION_TIMESTAMP))
				.append(NEW_LINE);
		builder.append(alignLeft(cashier, LENGTH_CASHIER)).append(NEW_LINE);

		String separator = "";
		for (int i = 0; i < LENGTH_PARAGRAPH; ++i) {
			separator += "-";
		}
		builder.append(separator).append(NEW_LINE);
		builder.append(buildItemList());
		builder.append(separator).append(NEW_LINE);

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

		builder.append(NEW_LINE);
		builder.append(alignCenter("*** TERIMA KASIH ***", LENGTH_PARAGRAPH))
				.append(NEW_LINE);

		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);

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

	public String getTransactionTimestamp() {
		return transactionTimestamp;
	}

	public void setCashier(String cashier) {
		this.cashier = cashier;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setItemBelanjaList(List<ItemBelanja> itemBelanjaList) {
		this.itemBelanjaList = itemBelanjaList;
	}

	public void setMoneyChange(String moneyChange) {
		this.moneyChange = moneyChange;
	}

	public void setPay(String pay) {
		this.pay = pay;
	}

	public void setTotalBelanja(String totalBelanja) {
		this.totalBelanja = totalBelanja;
	}

	public void setTransactionTimestamp(String transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
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

		public ItemBelanja() {
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

		public void setDiscountPercent(String discountPercent) {
			this.discountPercent = discountPercent;
		}

		public void setItemName(String itemName) {
			this.itemName = itemName;
		}

		public void setPricePerUnit(String pricePerUnit) {
			this.pricePerUnit = pricePerUnit;
		}

		public void setQuantiy(String quantiy) {
			this.quantiy = quantiy;
		}

		public void setTotalAmount(String totalAmount) {
			this.totalAmount = totalAmount;
		}
	}
}
