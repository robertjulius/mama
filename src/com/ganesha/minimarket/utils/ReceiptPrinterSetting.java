package com.ganesha.minimarket.utils;

import java.io.Serializable;

public class ReceiptPrinterSetting implements Serializable {

	private static final long serialVersionUID = 7873123226057123678L;

	private int paperWidth;
	private int quantityLength;
	private int priceLength;
	private int discountLength;
	private int amountLength;
	private int summaryLeftLength;
	private int summaryRightLength;

	private int rowCountBeforeHeader;
	private int rowCountAfterFooter;

	private String printerName;
	private boolean controlCodeEnabled;
	private char delimiter;
	private boolean openDrawerEnabled;
	private byte[] openDrawerCommand;
	private boolean cutEnabled;
	private byte[] cutCommand;

	public int getQuantityLength() {
		return quantityLength;
	}

	public void setQuantityLength(int quantityLength) {
		this.quantityLength = quantityLength;
	}

	public int getPriceLength() {
		return priceLength;
	}

	public void setPriceLength(int priceLength) {
		this.priceLength = priceLength;
	}

	public int getDiscountLength() {
		return discountLength;
	}

	public void setDiscountLength(int discountLength) {
		this.discountLength = discountLength;
	}

	public int getAmountLength() {
		return amountLength;
	}

	public void setAmountLength(int amountLength) {
		this.amountLength = amountLength;
	}

	public int getPaperWidth() {
		return paperWidth;
	}

	public void setPaperWidth(int paperWidth) {
		this.paperWidth = paperWidth;
	}

	public int getRowCountBeforeHeader() {
		return rowCountBeforeHeader;
	}

	public int getSummaryLeftLength() {
		return summaryLeftLength;
	}

	public void setSummaryLeftLength(int summaryLeftLength) {
		this.summaryLeftLength = summaryLeftLength;
	}

	public int getSummaryRightLength() {
		return summaryRightLength;
	}

	public void setSummaryRightLength(int summaryRightLength) {
		this.summaryRightLength = summaryRightLength;
	}

	public void setRowCountBeforeHeader(int rowCountBeforeHeader) {
		this.rowCountBeforeHeader = rowCountBeforeHeader;
	}

	public int getRowCountAfterFooter() {
		return rowCountAfterFooter;
	}

	public void setRowCountAfterFooter(int rowCountAfterFooter) {
		this.rowCountAfterFooter = rowCountAfterFooter;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	public boolean isControlCodeEnabled() {
		return controlCodeEnabled;
	}

	public void setControlCodeEnabled(boolean controlCodeEnabled) {
		this.controlCodeEnabled = controlCodeEnabled;
	}

	public boolean isOpenDrawerEnabled() {
		return openDrawerEnabled;
	}

	public void setOpenDrawerEnabled(boolean openDrawerEnabled) {
		this.openDrawerEnabled = openDrawerEnabled;
	}

	public byte[] getOpenDrawerCommand() {
		return openDrawerCommand;
	}

	public void setOpenDrawerCommand(byte[] openDrawerCommand) {
		this.openDrawerCommand = openDrawerCommand;
	}

	public boolean isCutEnabled() {
		return cutEnabled;
	}

	public void setCutEnabled(boolean cutEnabled) {
		this.cutEnabled = cutEnabled;
	}

	public byte[] getCutCommand() {
		return cutCommand;
	}

	public void setCutCommand(byte[] cutCommand) {
		this.cutCommand = cutCommand;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}
}