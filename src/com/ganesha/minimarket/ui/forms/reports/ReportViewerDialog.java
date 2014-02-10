package com.ganesha.minimarket.ui.forms.reports;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import net.sf.jasperreports.swing.JRViewer;

import com.ganesha.desktop.component.XJDialog;
import com.ganesha.minimarket.utils.PermissionConstants;

public class ReportViewerDialog extends XJDialog {
	private static final long serialVersionUID = 5275444252124892379L;

	public static void viewReport(Window parent, String title, JRViewer viewer) {
		ReportViewerDialog dialog = new ReportViewerDialog(parent, title,
				viewer);
		dialog.setVisible(true);
	}

	public ReportViewerDialog(Window parent, String title, JRViewer viewer) {
		super(parent);
		setTitle(title);
		setPermissionCode(PermissionConstants.REPORT_VIEWER);
		setPreferredSize(new Dimension(1000, 700));
		getContentPane().add(viewer, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		// TODO Auto-generated method stub
	}
}
