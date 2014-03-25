package com.ganesha.desktop.component.xcomponentutils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

public class XComponentUtils {

	public static Window getParentWindowOf(Component component) {
		Container parent = component.getParent();
		if (parent instanceof Window) {
			return (Window) parent;
		} else {
			return getParentWindowOf(((Component) parent).getParent());
		}
	}
}
