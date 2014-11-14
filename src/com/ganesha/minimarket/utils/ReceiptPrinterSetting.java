package com.ganesha.minimarket.utils;

import java.io.Serializable;

public class ReceiptPrinterSetting implements Serializable {

	private static final long serialVersionUID = 7873123226057123678L;

	private String port;
	private int[] openDrawerCharaters;
	private int[] autoCutCharacters;
	private char delimiter;

	public int[] getAutoCutCharacters() {
		return autoCutCharacters;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public int[] getOpenDrawerCharaters() {
		return openDrawerCharaters;
	}

	public String getPort() {
		return port;
	}

	public void setAutoCutCharacters(int[] autoCutCharacters) {
		this.autoCutCharacters = autoCutCharacters;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public void setOpenDrawerCharaters(int[] openDrawerCharaters) {
		this.openDrawerCharaters = openDrawerCharaters;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
