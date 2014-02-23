package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import net.miginfocom.swing.MigLayout;

import com.ganesha.accounting.utils.CoaConsistencyChecker;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.minimarket.utils.PermissionConsistencyChecker;
import com.ganesha.minimarket.utils.PermissionConstants;

public class DbConsistencyChecker extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;
	private XJPanel pnlButton;
	private XJButton btnCheckPermissionCode;
	private XJButton btnCheckCoaCode;
	private XJButton btnDone;

	public DbConsistencyChecker(Window parent) {
		super(parent);
		setTitle("Database Consistency Checker");
		setPermissionCode(PermissionConstants.MN_SETTING_DBCONSISTENCY);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][]"));

		pnlButton = new XJPanel();
		pnlButton.setBorder(new XEtchedBorder());
		getContentPane().add(pnlButton, "cell 0 0,grow");
		pnlButton.setLayout(new MigLayout("", "[300]", "[][][][]"));

		btnCheckCoaCode = new XJButton("Check Coa Code");
		btnCheckCoaCode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					checkCoa();
				} catch (Exception ex) {
					ExceptionHandler.handleException(DbConsistencyChecker.this,
							ex);
				}
			}
		});
		pnlButton.add(btnCheckCoaCode, "cell 0 1,growx");

		btnCheckPermissionCode = new XJButton("Check Permission Code");
		btnCheckPermissionCode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					checkPermission();
				} catch (Exception ex) {
					ExceptionHandler.handleException(DbConsistencyChecker.this,
							ex);
				}
			}
		});
		pnlButton.add(btnCheckPermissionCode, "cell 0 2,growx");

		btnDone = new XJButton();
		btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnDone.setMnemonic('D');
		btnDone.setText("Done");
		getContentPane().add(btnDone, "cell 0 1,alignx right");

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnDone.doClick();
			break;
		default:
			break;
		}
	}

	private void checkCoa() throws AppException {
		CoaConsistencyChecker coaChecker = new CoaConsistencyChecker();
		coaChecker.check();
	}

	private void checkPermission() throws AppException {
		PermissionConsistencyChecker permissionChecker = new PermissionConsistencyChecker();
		permissionChecker.initDB();
	}
}
