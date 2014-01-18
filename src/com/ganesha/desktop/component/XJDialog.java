package com.ganesha.desktop.component;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;

public abstract class XJDialog extends JDialog implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	private KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager
			.getCurrentKeyboardFocusManager();
	private MyDispatcher dispatcher = new MyDispatcher();

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

	public void setCloseOnEsc(boolean closeOnEsc) {
		this.closeOnEsc = closeOnEsc;
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
