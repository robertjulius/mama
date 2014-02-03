package com.ganesha.desktop.component.permissionutils;

import java.awt.Component;
import java.util.HashMap;

public class PermissionMap extends HashMap<String, Component> {

	private static final long serialVersionUID = 7888344010737076554L;
	private static final PermissionMap instance = new PermissionMap();

	public static PermissionMap getInstance() {
		return instance;
	}

	@Override
	public Component put(String key, Component value) {
		if (containsKey(key)) {
			throw new RuntimeException("Permission Code " + key
					+ " alredy registered to " + value.getName());
		}
		return super.put(key, value);
	}
}
