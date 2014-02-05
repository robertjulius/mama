package com.ganesha.desktop.component;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public abstract class XJFrame extends JFrame implements XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	private KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager
			.getCurrentKeyboardFocusManager();
	private MyDispatcher dispatcher = new MyDispatcher();

	public XJFrame() {
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

	protected abstract void keyEventListener(int keyCode);

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_RELEASED) {
				int keyCode = e.getKeyCode();
				keyEventListener(keyCode);
			}
			return false;
		}
	}
}
