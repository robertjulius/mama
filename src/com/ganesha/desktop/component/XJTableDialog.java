package com.ganesha.desktop.component;

import java.awt.Window;

import com.ganesha.desktop.exeptions.ExceptionHandler;
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
					/*
					 * This thread is not safe because the same table model is
					 * shared between thread. For now the only known bugs is
					 * java.lang.ArrayIndexOutOfBoundsException will raised.
					 */
					if (ex instanceof ArrayIndexOutOfBoundsException
							&& ex.getStackTrace()[0].getClassName().equals("java.util.Vector")
							&& ex.getStackTrace()[0].getMethodName().equals("elementAt")) {
						/*
						 * do nothing for this exception
						 */
					} else {
						ExceptionHandler.handleException(XJTableDialog.this, ex);
					}
				}
			}
		};
		thread.start();
	}
}
