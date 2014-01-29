package com.ganesha.minimarket.ui.forms.systemsetting;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;

public class SystemSettingForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	public static final String SYSTEM_SETTING_PRINTER_RECEIPT = "system.setting.printer.receipt";

	private XJButton btnSimpan;
	private JPanel pnlKode;
	private XJButton btnBatal;
	private XJComboBox cmbReceiptPrinter;

	public SystemSettingForm(Window parent) {
		super(parent);
		setCloseOnEsc(false);
		setTitle("System Setting");
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		pnlKode = new JPanel();
		pnlKode.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlKode, "cell 0 0,grow");
		pnlKode.setLayout(new MigLayout("", "[][300]", "[]"));

		XJLabel lblPrinterStruck = new XJLabel();
		pnlKode.add(lblPrinterStruck, "cell 0 0,alignx trailing");
		lblPrinterStruck.setText("Receipt Printer");

		cmbReceiptPrinter = new XJComboBox(getReceiptPrinterComboBoxList());
		pnlKode.add(cmbReceiptPrinter, "cell 1 0,growx");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(SystemSettingForm.this, ex);
				}
			}
		});

		btnBatal = new XJButton();
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBatal.setMnemonic('Q');
		btnBatal.setText("<html><center>Batal<br/>[Alt+Q]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 1 0");

		try {
			initForm();
		} catch (AppException ex) {
			ExceptionHandler.handleException(this, ex);
		}

		pack();
		setLocationRelativeTo(null);
	}

	public void initForm() throws AppException {
		String selectedItem = (String) SystemSetting
				.get(SYSTEM_SETTING_PRINTER_RECEIPT);
		ComboBoxObject comboBoxObject = new ComboBoxObject(selectedItem,
				selectedItem);
		cmbReceiptPrinter.setSelectedItem(comboBoxObject);
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

	private void batal() {
		dispose();
	}

	private ComboBoxObject[] getReceiptPrinterComboBoxList() {
		PrintService[] printServices = PrinterJob.lookupPrintServices();
		ComboBoxObject[] comboBoxObjects = new ComboBoxObject[printServices.length + 1];
		comboBoxObjects[0] = new ComboBoxObject(null, null);
		for (int i = 0; i < printServices.length; ++i) {
			comboBoxObjects[i + 1] = new ComboBoxObject(
					printServices[i].getName(), printServices[i].getName());
		}
		return comboBoxObjects;
	}

	private void save() throws UserException, AppException {
		validateForm();
		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbReceiptPrinter
				.getSelectedItem();
		String printServiceName = (String) comboBoxObject.getId();
		SystemSetting.save(SYSTEM_SETTING_PRINTER_RECEIPT, printServiceName);
		dispose();
	}

	private void validateForm() throws UserException {
		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbReceiptPrinter
				.getSelectedItem();
		String printServiceName = (String) comboBoxObject.getId();
		if (printServiceName == null) {
			throw new UserException("Printer Receipt harus diisi");
		}
	}
}
