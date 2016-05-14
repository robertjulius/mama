package com.ganesha.desktop.exeptions;

import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;

import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;

import net.miginfocom.swing.MigLayout;

public class UserExceptionHandler extends XJDialog {
	private static final long serialVersionUID = -3986040446960213196L;
	private XJLabel lblMessage;

	public static void handleException(Window parent, String message,
			String title) {
		UserExceptionHandler exceptionHandler = new UserExceptionHandler(parent);
		if (title == null) {
			title = "Peringatan";
		}
		exceptionHandler.setTitle(title);
		exceptionHandler.setMessage("<html><center>PROSES GAGAL<br/><br/>"
				+ message + "</center></html>");

		exceptionHandler.pack();
		exceptionHandler.setLocationRelativeTo(parent);
		exceptionHandler.setVisible(true);
	}

	public static void handleException(Window parent, Throwable ex, String title) {
		handleException(parent, ex.getMessage(), title);
	}

	private UserExceptionHandler(Window parent) {
		super(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setPermissionRequired(false);

		getContentPane().setLayout(
				new MigLayout("", "[500,grow]", "[200px,grow]"));

		XJPanel pnlMessage = new XJPanel();
		getContentPane()
				.add(pnlMessage, "cell 0 0,alignx center,aligny center");
		pnlMessage.setLayout(new MigLayout("", "[]", "[]"));

		lblMessage = new XJLabel();
		lblMessage.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblMessage.setText("User Message");
		pnlMessage.add(lblMessage, "cell 0 0");
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private void setMessage(String message) {
		lblMessage.setText(message);
	}
}
