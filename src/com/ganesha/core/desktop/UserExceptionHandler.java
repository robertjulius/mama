package com.ganesha.core.desktop;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;

public class UserExceptionHandler extends XJDialog {
	private static final long serialVersionUID = -3986040446960213196L;
	private XJButton btnOk;
	private XJLabel lblMessage;

	public static void handleException(Window parent, Exception ex) {
		UserExceptionHandler exceptionHandler = new UserExceptionHandler(parent);
		exceptionHandler.setMessage("<html><center>PROSES GAGAL<br/><br/>"
				+ ex.getMessage() + "</center></html>");
		exceptionHandler.pack();
		exceptionHandler.setLocationRelativeTo(null);
		exceptionHandler.setVisible(true);
	}

	private UserExceptionHandler(Window parent) {
		super(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setPermissionRequired(false);

		setTitle("Peringatan");
		getContentPane().setLayout(
				new MigLayout("", "[500,grow]", "[200px,grow][]"));

		JPanel pnlMessage = new JPanel();
		getContentPane()
				.add(pnlMessage, "cell 0 0,alignx center,aligny center");
		pnlMessage.setLayout(new MigLayout("", "[]", "[]"));

		lblMessage = new XJLabel();
		lblMessage.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblMessage.setText("User Message");
		pnlMessage.add(lblMessage, "cell 0 0");

		JPanel pnlButton = new JPanel();
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
