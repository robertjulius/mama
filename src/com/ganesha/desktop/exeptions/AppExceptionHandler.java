package com.ganesha.desktop.exeptions;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.slf4j.LoggerFactory;

import com.ganesha.coreapps.constants.Loggers;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJPanel;

public class AppExceptionHandler extends XJDialog {

	private static final long serialVersionUID = -3986040446960213196L;
	private static final int MESSAGE_MAX_LENGTH = 150;

	private JButton btnOk;
	private JTextArea txtStackTrace;
	private JLabel lblMessage;

	public static void handleException(Window parent, Exception ex) {
		AppExceptionHandler exceptionHandler = new AppExceptionHandler(parent);
		exceptionHandler.setMessage(ex.getMessage());
		exceptionHandler.setStackTrace(ex);
		exceptionHandler.pack();
		exceptionHandler.setLocationRelativeTo(null);
		exceptionHandler.setVisible(true);
	}

	private AppExceptionHandler(Window parent) {
		super(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setPermissionRequired(false);
		setTitle("ERROR");
		getContentPane().setLayout(
				new MigLayout("", "[800,grow]", "[grow][400,grow][]"));

		XJPanel pnlMessage = new XJPanel();
		getContentPane().add(pnlMessage, "cell 0 0,grow");
		pnlMessage.setLayout(new MigLayout("", "[]", "[][]"));

		JLabel lblErrorMessage = new JLabel("Error Message:");
		lblErrorMessage.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlMessage.add(lblErrorMessage, "cell 0 0");

		lblMessage = new JLabel(
				"Message Message Message Message Message Message ");
		pnlMessage.add(lblMessage, "cell 0 1");

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 1,grow");

		txtStackTrace = new JTextArea();
		txtStackTrace.setEditable(false);
		txtStackTrace.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane.setViewportView(txtStackTrace);

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[100]", "[]"));

		btnOk = new JButton();
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
		if (message == null) {
			message = "<null>";
		} else if (message.length() > MESSAGE_MAX_LENGTH) {
			message = message.substring(0, MESSAGE_MAX_LENGTH) + "...";
		}
		lblMessage.setText(message);
	}

	private void setStackTrace(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);
		printWriter.flush();
		txtStackTrace.setText(stringWriter.toString());

		LoggerFactory.getLogger(Loggers.APPLICATION).error(
				throwable.getMessage(), throwable);
	}
}
