package com.ganesha.desktop.component;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.permissionutils.PermissionChecker;
import com.ganesha.desktop.component.permissionutils.PermissionControl;

public abstract class XJDialog extends JDialog implements XComponentConstants,
		PermissionControl {
	private static final long serialVersionUID = 8731044804764016513L;

	private KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager
			.getCurrentKeyboardFocusManager();
	private MyDispatcher dispatcher = new MyDispatcher();

	private boolean permissionRequired = true;
	private boolean closeOnEsc = true;

	public XJDialog(Window parent) {
		super(parent, ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				keyboardFocusManager.addKeyEventDispatcher(dispatcher);
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				keyboardFocusManager.removeKeyEventDispatcher(dispatcher);
			}
		});
	}

	@Override
	public String getPermissionCode() {
		return getClass().getName();
	}

	@Override
	public boolean isPermissionRequired() {
		return permissionRequired;
	}

	public void setCloseOnEsc(boolean closeOnEsc) {
		this.closeOnEsc = closeOnEsc;
	}

	@Override
	public void setPermissionRequired(boolean permissionRequired) {
		this.permissionRequired = permissionRequired;
	}

	@Override
	public void setVisible(boolean visible) {
		if (!visible || !permissionRequired) {
			super.setVisible(visible);
			return;
		}
		try {
			boolean permitted = PermissionChecker.checkPermission(this);
			if (!permitted) {
				super.setVisible(false);
				throw new UserException(
						"Anda tidak mempunyai ijin untuk mengakses form ini");
			} else {
				super.setVisible(visible);
			}
		} catch (Exception ex) {
			ExceptionHandler.handleException((Window) getParent(), ex);
			return;
		}
	}

	protected abstract void keyEventListener(int keyCode);

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_RELEASED) {
				int keyCode = e.getKeyCode();
				switch (keyCode) {
				case KeyEvent.VK_ESCAPE:
					if (closeOnEsc) {
						dispose();
					} else {
						keyEventListener(keyCode);
					}
					break;
				default:
					keyEventListener(keyCode);
					break;
				}
			}
			return false;
		}
	}
}
