package com.ganesha.desktop.component;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.slf4j.LoggerFactory;

import com.ganesha.core.exception.UserException;
import com.ganesha.coreapps.constants.Loggers;
import com.ganesha.desktop.component.permissionutils.PermissionChecker;
import com.ganesha.desktop.component.permissionutils.PermissionControl;
import com.ganesha.desktop.exeptions.ExceptionHandler;

public abstract class XJDialog extends JDialog implements XComponentConstants,
		PermissionControl {
	private static final long serialVersionUID = 8731044804764016513L;

	private KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager
			.getCurrentKeyboardFocusManager();
	private MyDispatcher dispatcher = new MyDispatcher();

	private String permissionCode;
	private boolean permissionRequired = true;
	private boolean closeOnEsc = true;
	private boolean showConfirmationOnClose = false;
	private String confirmationSentenceOnClose = "Exiting this window will discards all changes you made. Are you sure to exit this window?";

	public XJDialog(Window parent) {
		super(parent, ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		getContentPane().setBackground(PNL_BG);

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
		return permissionCode;
	}

	@Override
	public boolean isPermissionRequired() {
		return permissionRequired;
	}

	public void setCloseOnEsc(boolean closeOnEsc) {
		this.closeOnEsc = closeOnEsc;
	}
	
	public void showConfirmationOnClose(boolean b) {
		this.showConfirmationOnClose = b;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	@Override
	public void setPermissionRequired(boolean permissionRequired) {
		this.permissionRequired = permissionRequired;
	}
	
	public void setConfirmationSentenceOnClose(String confirmationSentenceOnClose) {
		this.confirmationSentenceOnClose = confirmationSentenceOnClose;
	}

	@Override
	public void setVisible(boolean visible) {
		if (!visible || !permissionRequired) {
			super.setVisible(visible);
			return;
		}

		String windowName = this.getClass().getName();
		try {
			boolean permitted = PermissionChecker.checkPermission(this);
			if (!permitted) {
				LoggerFactory.getLogger(Loggers.WINDOW).trace(
						"Access to open " + windowName + " is not permitted");
				super.setVisible(false);
				throw new UserException(
						"Anda tidak mempunyai ijin untuk mengakses form ini");
			} else {
				LoggerFactory.getLogger(Loggers.WINDOW).trace(
						"Access to open " + windowName
								+ " is permitted, the window will open");
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
						if (showConfirmationOnClose) {
							int selectedOption = JOptionPane.showConfirmDialog(getParent(), confirmationSentenceOnClose, "Confirmation", JOptionPane.YES_NO_OPTION);
							if (selectedOption == JOptionPane.YES_OPTION) {
								dispose();
							}
						} else {
							dispose();
						}
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
