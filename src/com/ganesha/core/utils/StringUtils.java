package com.ganesha.core.utils;

public class StringUtils {

	public static String properCase(String phrase) {
		StringBuilder stringBuilder = new StringBuilder();
		String[] strings = phrase.split(" ");
		for (String string : strings) {
			stringBuilder.append(" ").append(
					string.substring(0, 1).toUpperCase()
							+ string.substring(1).toLowerCase());
		}
		return stringBuilder.toString().replaceFirst(" ", "");
	}
}
