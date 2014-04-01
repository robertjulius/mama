package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJRadioButton;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.minimarket.utils.PermissionConstants;

public class ReceiptPrinterStatusForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;
	private XJPanel pnlStatus;
	private XJRadioButton rdbtnOn;
	private XJRadioButton rdbtnOff;
	private XJPanel panel;
	private XJButton btnBatal;
	private XJButton btnSimpan;
	/**
	 * @wbp.nonvisual location=108,71
	 */
	private final ButtonGroup buttonGroup = new ButtonGroup();

	public ReceiptPrinterStatusForm(Window parent) {
		super(parent);
		setTitle("Receipt Printer Status");
		setPermissionCode(PermissionConstants.SETTING_RECEIPTPRINTERSTATUS);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][]"));

		pnlStatus = new XJPanel();
		pnlStatus.setBorder(new XEtchedBorder());
		getContentPane().add(pnlStatus, "cell 0 0,grow");
		pnlStatus.setLayout(new MigLayout("", "[300]", "[][]"));

		rdbtnOn = new XJRadioButton();
		rdbtnOn.setMnemonic('N');
		rdbtnOn.setText("On");
		buttonGroup.add(rdbtnOn);
		pnlStatus.add(rdbtnOn, "cell 0 0");

		rdbtnOff = new XJRadioButton();
		rdbtnOff.setMnemonic('F');
		rdbtnOff.setText("Off");
		buttonGroup.add(rdbtnOff);
		pnlStatus.add(rdbtnOff, "cell 0 1");

		panel = new XJPanel();
		getContentPane().add(panel, "cell 0 1,grow");
		panel.setLayout(new MigLayout("", "[100][grow][100]", "[]"));

		btnBatal = new XJButton();
		btnBatal.setText("<html><center>Batal<br/>[Esc]</center></html>");
		panel.add(btnBatal, "cell 0 0,growx");

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ReceiptPrinterStatusForm.this, ex);
				}

			}
		});
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		panel.add(btnSimpan, "cell 2 0,growx");

		try {
			initForm();
		} catch (AppException ex) {
			ExceptionHandler.handleException(this, ex);
		}

		pack();
		setLocationRelativeTo(parent);
	}

	public void initForm() throws AppException {
		Boolean statusOn = (Boolean) SystemSetting
				.get(GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT_STATUS);
		if (statusOn == null) {
			statusOn = false;
		}

		if (statusOn) {
			rdbtnOn.setSelected(true);
			rdbtnOff.setSelected(false);
		} else {
			rdbtnOn.setSelected(false);
			rdbtnOff.setSelected(true);
		}
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnSimpan.doClick();
			break;
		default:
			break;
		}
	}

	private void save() throws AppException {
		if (rdbtnOn.isSelected()) {
			SystemSetting.save(
					GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT_STATUS,
					true);
		} else {
			SystemSetting.save(
					GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT_STATUS,
					false);
		}
		dispose();
	}
}
