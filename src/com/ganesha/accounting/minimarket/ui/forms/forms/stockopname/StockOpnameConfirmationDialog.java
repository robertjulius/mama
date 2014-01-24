package com.ganesha.accounting.minimarket.ui.forms.forms.stockopname;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.swing.JRViewer;

import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;

public class StockOpnameConfirmationDialog extends XJDialog {
	private static final long serialVersionUID = 1452286313727721700L;

	private JPanel pnlJasper;
	private XJButton btnSelesai;
	private ConfirmType confirmType;

	public static ConfirmType showConfirmation(Window parent, JRViewer viewer) {
		StockOpnameConfirmationDialog dialog = new StockOpnameConfirmationDialog(
				parent, viewer);
		dialog.setVisible(true);
		return dialog.confirmType;
	}

	private StockOpnameConfirmationDialog(Window parent, JRViewer viewer) {
		super(parent);
		setPreferredSize(new Dimension(1000, 700));

		pnlJasper = new JPanel();
		getContentPane().add(pnlJasper, BorderLayout.CENTER);
		pnlJasper.setLayout(new BorderLayout(0, 0));
		pnlJasper.add(viewer, BorderLayout.CENTER);

		JPanel pnlButton = new JPanel();
		pnlJasper.add(pnlButton, BorderLayout.SOUTH);
		pnlButton.setLayout(new MigLayout("", "[][grow][]", "[]"));

		XJButton btnKoreksi = new XJButton();
		btnKoreksi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmType = ConfirmType.CANCEL;
			}
		});
		btnKoreksi.setText("<html><center>Koreksi<br/>[Esc]</center></html>");
		pnlButton.add(btnKoreksi, "cell 0 0");

		btnSelesai = new XJButton();
		btnSelesai.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				simpan();
			}
		});
		btnSelesai
				.setText("<html><center>Selesai & Simpan Data<br/>[F12]</center></html>");
		pnlButton.add(btnSelesai, "cell 2 0");

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnSelesai.doClick();
			break;
		default:
			break;
		}
	}

	private void simpan() {
		confirmType = ConfirmType.OK;
		dispose();
	}

	public static enum ConfirmType {
		OK, CANCEL
	}
}