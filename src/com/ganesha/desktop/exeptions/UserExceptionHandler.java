package com.ganesha.desktop.exeptions;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import net.miginfocom.swing.MigLayout;

import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;

public class UserExceptionHandler extends XJDialog {
	private static final long serialVersionUID = -3986040446960213196L;
	private XJButton btnOk;
	private XJLabel lblMessage;

	public static void handleException(Window parent, Throwable ex, String title) {
		UserExceptionHandler exceptionHandler = new UserExceptionHandler(parent);
		if (title == null) {
			title = "Peringatan";
		}
		exceptionHandler.setTitle(title);
		exceptionHandler.setMessage("<html><center>PROSES GAGAL<br/><br/>"
				+ ex.getMessage() + "</center></html>");
		exceptionHandler.pack();
		exceptionHandler.setLocationRelativeTo(parent);
		exceptionHandler.setVisible(true);
	}

	private UserExceptionHandler(Window parent) {
		super(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setPermissionRequired(false);

		getContentPane().setLayout(
				new MigLayout("", "[500,grow]", "[200px,grow][]"));

		XJPanel pnlMessage = new XJPanel();
		getContentPane()
				.add(pnlMessage, "cell 0 0,alignx center,aligny center");
		pnlMessage.setLayout(new MigLayout("", "[]", "[]"));

		lblMessage = new XJLabel();
		lblMessage.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblMessage.setText("User Message");
		pnlMessage.add(lblMessage, "cell 0 0");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[100]", "[]"));

		btnOk = new XJButton();
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnOk.setText("OK");
		pnlButton.add(btnOk, "cell 0 0,growx");
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
