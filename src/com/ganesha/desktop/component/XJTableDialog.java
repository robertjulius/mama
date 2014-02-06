package com.ganesha.desktop.component;

import java.awt.Window;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;

public abstract class XJTableDialog extends XJDialog {

	private static final long serialVersionUID = -1693188598485815267L;

	public XJTableDialog(Window parent) {
		super(parent);
	}

	public abstract void loadData() throws AppException, UserException;

	public final void loadDataInThread() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					loadData();
				} catch (Exception ex) {
					ExceptionHandler.handleException(XJTableDialog.this, ex);
				}
			}
		};
		thread.start();
	}
}
