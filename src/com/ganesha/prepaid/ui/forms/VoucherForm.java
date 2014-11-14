package com.ganesha.prepaid.ui.forms;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.exception.ActionTypeNotSupported;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.prepaid.facade.VoucherFacade;
import com.ganesha.prepaid.facade.VoucherTypeFacade;
import com.ganesha.prepaid.model.Voucher;
import com.ganesha.prepaid.model.VoucherType;

public class VoucherForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtQuantity;
	private ActionType actionType;
	private XJLabel lblVoucherType;
	private XJComboBox cmbVoucherType;
	private XJPanel pnlKiri;
	private XJLabel lblPrice;
	private XJTextField txtPrice;
	private XJButton btnBatal;
	private XJPanel pnlDisable;
	private XJCheckBox chkDisabled;
	private XJButton btnhapusvoucherType;

	private Integer voucherId;
	private XJLabel lblNamaPaket;
	private XJTextField txtPackageName;

	public VoucherForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Form Voucher");
		setPermissionCode(PermissionConstants.VOUCHER_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[500]", "[grow][grow][grow]"));

		pnlKiri = new XJPanel();
		getContentPane().add(pnlKiri, "cell 0 0,grow");
		pnlKiri.setLayout(new MigLayout("", "[][grow]", "[][][][]"));

		lblVoucherType = new XJLabel();
		pnlKiri.add(lblVoucherType, "cell 0 0");
		lblVoucherType.setText("Tipe Voucher");

		cmbVoucherType = new XJComboBox();
		pnlKiri.add(cmbVoucherType, "cell 1 0,growx");

		lblNamaPaket = new XJLabel();
		lblNamaPaket.setText("Nama Paket");
		pnlKiri.add(lblNamaPaket, "cell 0 1");

		txtPackageName = new XJTextField();
		pnlKiri.add(txtPackageName, "cell 1 1,growx");

		XJLabel lblQuantity = new XJLabel();
		pnlKiri.add(lblQuantity, "cell 0 2");
		lblQuantity.setText("Quantity (Modal)");

		txtQuantity = new XJTextField();
		txtQuantity
				.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		pnlKiri.add(txtQuantity, "cell 1 2,growx");
		txtQuantity.setUpperCaseOnFocusLost(true);

		lblPrice = new XJLabel();
		pnlKiri.add(lblPrice, "cell 0 3");
		lblPrice.setText("Harga Jual");

		txtPrice = new XJTextField();
		txtPrice.setFormatterFactory(GeneralConstants.FORMATTER_FACTORY_NUMBER);
		pnlKiri.add(txtPrice, "cell 1 3,growx");

		pnlDisable = new XJPanel();
		getContentPane().add(pnlDisable, "cell 0 1,alignx right,growy");
		pnlDisable.setLayout(new MigLayout("", "[]", "[]"));

		chkDisabled = new XJCheckBox();
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_SMALLEST));
		chkDisabled.setText("Tipe Voucher ini sudah tidak aktif lagi");
		pnlDisable.add(chkDisabled, "cell 0 0");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 2,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(false);
				} catch (Exception ex) {
					ExceptionHandler.handleException(VoucherForm.this, ex);
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

		btnhapusvoucherType = new XJButton();
		btnhapusvoucherType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(VoucherForm.this, ex);
				}
			}
		});
		btnhapusvoucherType
				.setText("<html><center>Hapus<br/>Voucher</center></html>");
		pnlButton.add(btnhapusvoucherType, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		loadComboVoucherType();

		pack();
		setLocationRelativeTo(parent);
	}

	public void setFormDetailValue(Voucher voucher) {
		voucherId = voucher.getId();

		txtPackageName.setText(voucher.getPackageName());
		txtQuantity.setText(Formatter.formatNumberToString(voucher
				.getQuantity()));
		cmbVoucherType.setSelectedItem(voucher.getVoucherType());
		txtPrice.setText(Formatter.formatNumberToString(voucher.getPrice()));
		chkDisabled.setSelected(voucher.getDisabled());

		btnSimpan
				.setText("<html><center>Simpan Perubahan<br/>[F12]</center></html>");
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

	private void loadComboVoucherType() {
		Session session = HibernateUtils.openSession();
		try {
			List<ComboBoxObject> comboBoxObjects = new ArrayList<ComboBoxObject>();
			List<VoucherType> voucherTypes = VoucherTypeFacade.getInstance()
					.getAll(session);
			for (VoucherType voucherType : voucherTypes) {
				ComboBoxObject comboBoxObject = new ComboBoxObject(voucherType,
						voucherType.getName());
				comboBoxObjects.add(comboBoxObject);
			}
			comboBoxObjects.add(0, new ComboBoxObject(new VoucherType(), null));
			cmbVoucherType.setModel(new DefaultComboBoxModel<ComboBoxObject>(
					comboBoxObjects.toArray(new ComboBoxObject[] {})));
		} finally {
			session.close();
		}
	}

	private void save(boolean deleted) throws ActionTypeNotSupported,
			UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			VoucherType voucherType = (VoucherType) ((ComboBoxObject) cmbVoucherType
					.getSelectedItem()).getObject();
			String packageName = txtPackageName.getText();
			Integer quantity = Formatter.formatStringToNumber(
					txtQuantity.getText()).intValue();
			BigDecimal price = BigDecimal.valueOf(Formatter
					.formatStringToNumber(txtPrice.getText()).doubleValue());
			boolean disabled = chkDisabled.isSelected();

			VoucherFacade facade = VoucherFacade.getInstance();
			Voucher voucher = null;
			if (actionType == ActionType.CREATE) {
				voucher = facade.addNewVoucher(voucherType, packageName,
						quantity, price, disabled, deleted, session);
				voucherId = voucher.getId();
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				voucher = facade.updateExistingVoucher(voucherId, voucherType,
						packageName, quantity, price, disabled, deleted,
						session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}

			ActivityLogFacade.doLog(getPermissionCode(), actionType,
					Main.getUserLogin(), voucher, session);
			session.getTransaction().commit();

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void validateForm() throws UserException {
		if (txtQuantity.getText().trim().equals("")) {
			throw new UserException("Nama harus diisi");
		}

		ComboBoxObject comboBoxObjectVoucherType = (ComboBoxObject) cmbVoucherType
				.getSelectedItem();
		if (comboBoxObjectVoucherType == null) {
			throw new UserException("VoucherType harus diisi");
		}

		VoucherType voucherType = (VoucherType) comboBoxObjectVoucherType
				.getObject();
		if (voucherType == null) {
			throw new UserException("VoucherType harus diisi");
		}

		Integer voucherTypeId = voucherType.getId();
		if (voucherTypeId == null) {
			throw new UserException("Tipe Voucher harus diisi");
		}

		if (txtPrice.getText().trim().equals("")) {
			throw new UserException("Item harus diisi");
		}
	}
}
