package com.ganesha.desktop.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.UIManager;

public class UIManagerColorKeys {
	public static void main(String[] args) throws Exception {
		List<String> colorKeys = new ArrayList<String>();
		Hashtable<Object, Object> uiDefaults = UIManager
				.getLookAndFeelDefaults();
		Set<Entry<Object, Object>> entries = uiDefaults.entrySet();
		for (Entry<Object, Object> entry : entries) {
			if (entry.getValue() instanceof Color) {
				colorKeys.add((String) entry.getKey());
			}
		}

		// sort the color keys
		Collections.sort(colorKeys);

		// print the color keys
		for (String colorKey : colorKeys) {
			System.out.println(colorKey + " : " + uiDefaults.get(colorKey));
		}
	}
}