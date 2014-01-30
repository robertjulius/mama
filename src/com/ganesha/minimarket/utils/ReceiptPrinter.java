package com.ganesha.minimarket.utils;

import java.util.List;

public class ReceiptPrinter {

	private static final String NEW_LINE = "\r\n";

	private static final int MAX_LENGTH_PARAGRAPH = 40;
	private static final int MAX_LENGTH_COMPANY_NAME = MAX_LENGTH_PARAGRAPH;
	private static final int MAX_LENGTH_COMPANY_ADDRESS = MAX_LENGTH_PARAGRAPH;
	private static final int MAX_LENGTH_TRANSACTION_TIMESTAMP = MAX_LENGTH_PARAGRAPH;
	private static final int MAX_LENGTH_USER_INFORMATION = MAX_LENGTH_PARAGRAPH;
	private static final int MAX_LENGTH_ITEM_NAME = MAX_LENGTH_PARAGRAPH;
	private static final int MAX_LENGTH_QUANTITY = 3;
	private static final int MAX_LENGTH_PRICE_PER_UNIT = 16;
	private static final int MAX_LENGTH_DISCOUNT_PERCENT = 5;
	private static final int MAX_LENGTH_TOTAL_AMOUNT = 16;
	private static final int MAX_LENGTH_TOTAL_BELANJA = 15;
	private static final int MAX_LENGTH_PAY = 15;
	private static final int MAX_LENGTH_MONEY_CHANGE = 15;

	private String companyName;
	private String companyAddress;
	private String transactionTimestamp;
	private String userInformation;
	private List<ItemBelanja> itemBelanjaList;
	private String totalBelanja;
	private String pay;
	private String moneyChange;

	public String buildItemList() {
		StringBuilder builder = new StringBuilder();
		for (ItemBelanja itemBelanja : itemBelanjaList) {
			builder.append(
					alignLeft(itemBelanja.getItemName(), MAX_LENGTH_ITEM_NAME))
					.append(NEW_LINE);
			builder.append(alignRight(itemBelanja.getQuantiy(),
					MAX_LENGTH_QUANTITY));
			builder.append(alignRight(itemBelanja.getPricePerUnit(),
					MAX_LENGTH_PRICE_PER_UNIT));
			builder.append(alignRight(itemBelanja.getDiscountPercent(),
					MAX_LENGTH_DISCOUNT_PERCENT));
			builder.append(
					alignRight(itemBelanja.getTotalAmount(),
							MAX_LENGTH_TOTAL_AMOUNT)).append(NEW_LINE);
		}
		return builder.toString();
	}

	public String buildReceipt() {
		StringBuilder builder = new StringBuilder();

		builder.append(NEW_LINE);
		builder.append(alignLeft(companyName, MAX_LENGTH_COMPANY_NAME)).append(
				NEW_LINE);
		builder.append(alignLeft(companyAddress, MAX_LENGTH_COMPANY_ADDRESS))
				.append(NEW_LINE);
		builder.append(
				alignLeft(transactionTimestamp,
						MAX_LENGTH_TRANSACTION_TIMESTAMP)).append(NEW_LINE);
		builder.append(alignLeft(userInformation, MAX_LENGTH_USER_INFORMATION))
				.append(NEW_LINE);

		String separator = "";
		for (int i = 0; i < MAX_LENGTH_PARAGRAPH; ++i) {
			separator += "-";
		}
		builder.append(separator).append(NEW_LINE);
		builder.append(buildItemList());
		builder.append(separator).append(NEW_LINE);

		builder.append(alignLeft("Total Belanja", 15));
		builder.append(alignRight(totalBelanja, MAX_LENGTH_TOTAL_BELANJA))
				.append(NEW_LINE);
		builder.append(alignLeft("Bayar", 15));
		builder.append(alignRight(pay, MAX_LENGTH_PAY)).append(NEW_LINE);
		builder.append(alignLeft("Kembali", 15));
		builder.append(alignRight(moneyChange, MAX_LENGTH_MONEY_CHANGE))
				.append(NEW_LINE);

		builder.append(NEW_LINE);
		builder.append(
				alignCenter("*** TERIMA KASIH ***", MAX_LENGTH_PARAGRAPH))
				.append(NEW_LINE);

		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);

		return builder.toString();
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

	public String getUserInformation() {
		return userInformation;
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

	public void setUserInformation(String userInformation) {
		this.userInformation = userInformation;
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
