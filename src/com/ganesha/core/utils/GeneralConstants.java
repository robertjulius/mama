package com.ganesha.core.utils;

public class GeneralConstants {

	public static final String USER_SESSION = "userSession";

	public static final String MODULE_SESSION = "moduleSession";
	public static final String REC_STATUS_ACTIVE = "A";

	public static final String REC_STATUS_NONACTIVE = "N";
	public static final String EMPTY_STRING = "";

	public static String PREFIX_TRX_NUMBER_PURCHASE = "TRXPUR";
	public static String PREFIX_TRX_NUMBER_PURCHASE_RETURN = "TRXPURRTN";
	public static String PREFIX_TRX_NUMBER_SALES = "TRXSAL";
	public static String PREFIX_TRX_NUMBER_SALES_RETURN = "TRXSALRTN";

	public static enum ActionType {
		CREATE, READ, UPDATE, DELETE, OTHER
	}

}
