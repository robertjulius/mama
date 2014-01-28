package com.ganesha.minimarket.utils;

import java.util.List;

public class ReceiptPrinter {

	private static final String NEW_LINE = "\r\n";

	private static final int MAX_LENGTH_COMPANY_NAME = 30;
	private static final int MAX_LENGTH_COMPANY_ADDRESS = 30;
	private static final int MAX_LENGTH_TRANSACTION_TIMESTAMP = 30;
	private static final int MAX_LENGTH_USER_INFORMATION = 30;
	private static final int MAX_LENGTH_ITEM_NAME = 20;
	private static final int MAX_LENGTH_QUANTITY = 5;
	private static final int MAX_LENGTH_PRICE_PER_UNIT = 10;
	private static final int MAX_LENGTH_DISCOUNT_PERCENT = 5;
	private static final int MAX_LENGTH_TOTAL_AMOUNT = 10;
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
					itemBelanja.getItemName()
							.substring(0, MAX_LENGTH_ITEM_NAME)).append(
					NEW_LINE);

			builder.append(alignRight(
					itemBelanja.getItemName().substring(0, MAX_LENGTH_QUANTITY),
					MAX_LENGTH_QUANTITY));

			builder.append(alignRight(
					itemBelanja.getPricePerUnit().substring(0,
							MAX_LENGTH_PRICE_PER_UNIT),
					MAX_LENGTH_PRICE_PER_UNIT));

			builder.append(alignRight(itemBelanja.getDiscountPercent()
					.substring(0, MAX_LENGTH_DISCOUNT_PERCENT),
					MAX_LENGTH_DISCOUNT_PERCENT));

			builder.append(alignRight(
					itemBelanja.getTotalAmount().substring(0,
							MAX_LENGTH_TOTAL_AMOUNT), MAX_LENGTH_TOTAL_AMOUNT));
		}
		return builder.toString();
	}

	public String buildStruct() {
		StringBuilder builder = new StringBuilder();

		builder.append(companyName.substring(0, MAX_LENGTH_COMPANY_NAME));
		builder.append(companyAddress.substring(0, MAX_LENGTH_COMPANY_ADDRESS));
		builder.append(transactionTimestamp.substring(0,
				MAX_LENGTH_TRANSACTION_TIMESTAMP));
		builder.append(userInformation
				.substring(0, MAX_LENGTH_USER_INFORMATION));

		builder.append(NEW_LINE);
		builder.append(buildItemList());
		builder.append(NEW_LINE);

		builder.append(
				alignRight(totalBelanja.substring(0, MAX_LENGTH_TOTAL_BELANJA),
						MAX_LENGTH_TOTAL_BELANJA)).append(NEW_LINE);

		builder.append(
				alignRight(pay.substring(0, MAX_LENGTH_PAY), MAX_LENGTH_PAY))
				.append(NEW_LINE);

		builder.append(
				alignRight(moneyChange.substring(0, MAX_LENGTH_MONEY_CHANGE),
						MAX_LENGTH_MONEY_CHANGE)).append(NEW_LINE);

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

	private String alignRight(String string, int width) {
		int length = string.length();
		int emtyCount = width - length;
		String emtyString = "";
		for (int i = 0; i < emtyCount; ++i) {
			emtyString += " ";
		}
		return emtyString + string;
	}

	public class ItemBelanja {
		private String itemName;
		private String quantiy;
		private String pricePerUnit;
		private String discountPercent;
		private String totalAmount;

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
