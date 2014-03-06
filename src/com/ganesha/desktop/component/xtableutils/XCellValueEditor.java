package com.ganesha.desktop.component.xtableutils;

import java.awt.Window;
import java.awt.event.KeyEvent;

import net.miginfocom.swing.MigLayout;

import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTextField;

public class XCellValueEditor extends XJDialog {

	private static final long serialVersionUID = -3607782552541121877L;

	public static final int OK = 1;
	private int returnValue;
	private String editorValue;
	private XJLabel lblCellName;
	private XJTextField txtCellValue;

	public XCellValueEditor(Window parent, String title, String initialValue) {
		super(parent);
		setTitle(title);
		setPermissionRequired(false);
		getContentPane().setLayout(new MigLayout("", "[200]", "[][]"));

		lblCellName = new XJLabel();
		getContentPane().add(lblCellName, "cell 0 0");
		lblCellName.setText(title);

		txtCellValue = new XJTextField();
		getContentPane().add(txtCellValue, "cell 0 1,growx");
		txtCellValue.setText(initialValue);

		pack();
		setLocationRelativeTo(parent);
	}

	public int getReturnValue() {
		return returnValue;
	}

	public String getValue() {
		return editorValue;
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			done();
			break;
		case KeyEvent.VK_ESCAPE:
			cancel();
			break;
		default:
			break;
		}
	}

	private void cancel() {
		returnValue = 0;
		dispose();
	}

	private void done() {
		editorValue = txtCellValue.getText();
		returnValue = OK;
		dispose();
	}
}
