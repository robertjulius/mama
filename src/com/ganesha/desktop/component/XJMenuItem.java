package com.ganesha.desktop.component;

import java.awt.Window;

import javax.swing.JMenuItem;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.desktop.component.permissionutils.PermissionChecker;
import com.ganesha.desktop.component.permissionutils.PermissionControl;

public class XJMenuItem extends JMenuItem implements XComponentConstants,
		PermissionControl {
	private static final long serialVersionUID = 8731044804764016513L;

	private boolean permissionRequired = true;
	private String permissionCode;

	public XJMenuItem(String text, String permissionCode) {
		super(text);
		this.permissionCode = permissionCode;
		setEnabled(true);
	}

	@Override
	public String getPermissionCode() {
		return permissionCode;
	}

	@Override
	public boolean isPermissionRequired() {
		return permissionRequired;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (!enabled || !permissionRequired) {
			super.setEnabled(enabled);
			return;
		}
		try {
			boolean permitted = PermissionChecker.checkPermission(this);
			if (!permitted) {
				super.setEnabled(false);
			} else {
				super.setEnabled(enabled);
			}
		} catch (Exception ex) {
			ExceptionHandler.handleException((Window) getParent(), ex);
			return;
		}
	}

	@Override
	public void setPermissionRequired(boolean permissionRequired) {
		this.permissionRequired = permissionRequired;
	}
}
