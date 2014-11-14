package com.ganesha.prepaid.ui.forms;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
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
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.ui.forms.searchentity.SearchEntityDialog;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.prepaid.facade.ProviderFacade;
import com.ganesha.prepaid.facade.VoucherTypeFacade;
import com.ganesha.prepaid.model.Provider;
import com.ganesha.prepaid.model.VoucherType;

public class VoucherTypeForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJTextField txtName;
	private ActionType actionType;
	private XJLabel lblProvider;
	private XJComboBox cmbProvider;
	private XJPanel pnlKiri;
	private XJLabel lblItem;
	private XJTextField txtItemId;
	private XJButton btnBatal;
	private XJPanel pnlDisable;
	private XJCheckBox chkDisabled;
	private XJButton btnHapusVoucherType;
	private XJTextField txtItemName;
	private XJButton btnCariBarang;

	private Integer voucherTypeId;

	public VoucherTypeForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Form Tipe Voucher");
		setPermissionCode(PermissionConstants.VOUCHER_TYPE_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[600,grow]", "[grow][grow][grow]"));

		pnlKiri = new XJPanel();
		getContentPane().add(pnlKiri, "cell 0 0,grow");
		pnlKiri.setLayout(new MigLayout("", "[][100px][grow][]", "[][][]"));

		XJLabel lblNama = new XJLabel();
		pnlKiri.add(lblNama, "cell 0 0");
		lblNama.setText("Nama");

		txtName = new XJTextField();
		pnlKiri.add(txtName, "cell 1 0 2 1,growx");
		txtName.setUpperCaseOnFocusLost(true);

		lblProvider = new XJLabel();
		pnlKiri.add(lblProvider, "cell 0 1");
		lblProvider.setText("Provider");

		cmbProvider = new XJComboBox();
		pnlKiri.add(cmbProvider, "cell 1 1 2 1,growx");

		lblItem = new XJLabel();
		pnlKiri.add(lblItem, "cell 0 2");
		lblItem.setText("Barang");

		txtItemId = new XJTextField();
		txtItemId.setEditable(false);
		pnlKiri.add(txtItemId, "cell 1 2,growx");

		txtItemName = new XJTextField();
		txtItemName.setEditable(false);
		pnlKiri.add(txtItemName, "cell 2 2,growx");

		btnCariBarang = new XJButton("Cari Barang [F5]");
		btnCariBarang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cariBarang();
				} catch (Exception ex) {
					ExceptionHandler.handleException(VoucherTypeForm.this, ex);
				}
			}
		});
		pnlKiri.add(btnCariBarang, "cell 3 2");

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
					ExceptionHandler.handleException(VoucherTypeForm.this, ex);
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

		btnHapusVoucherType = new XJButton();
		btnHapusVoucherType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(VoucherTypeForm.this, ex);
				}
			}
		});
		btnHapusVoucherType
				.setText("<html><center>Hapus<br/>Tipe Voucher</center></html>");
		pnlButton.add(btnHapusVoucherType, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		loadComboProvider();

		pack();
		setLocationRelativeTo(parent);
	}

	public void setFormDetailValue(VoucherType voucherType) {
		voucherTypeId = voucherType.getId();

		txtName.setText(voucherType.getName());
		cmbProvider.setSelectedItem(voucherType.getProvider());
		txtItemId.setText(voucherType.getItem().getId().toString());
		txtItemName.setText(voucherType.getItem().getName());
		chkDisabled.setSelected(voucherType.getDisabled());

		btnSimpan
				.setText("<html><center>Simpan Perubahan<br/>[F12]</center></html>");
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnCariBarang.doClick();
			break;
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

	private void cariBarang() {
		SearchEntityDialog searchEntityDialog = new SearchEntityDialog(
				"Cari Barang", this, Item.class);
		searchEntityDialog.setVisible(true);

		Integer itemId = searchEntityDialog.getSelectedId();
		if (itemId != null) {
			txtItemId.setText(searchEntityDialog.getSelectedId().toString());
			txtItemName.setText(searchEntityDialog.getSelectedName());
		}
	}

	private void loadComboProvider() {
		Session session = HibernateUtils.openSession();
		try {
			List<ComboBoxObject> comboBoxObjects = new ArrayList<ComboBoxObject>();
			List<Provider> providers = ProviderFacade.getInstance().getAll(
					session);
			for (Provider provider : providers) {
				ComboBoxObject comboBoxObject = new ComboBoxObject(provider,
						provider.getName());
				comboBoxObjects.add(comboBoxObject);
			}
			comboBoxObjects.add(0, new ComboBoxObject(new Provider(), null));
			cmbProvider.setModel(new DefaultComboBoxModel<ComboBoxObject>(
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

			String name = txtName.getText();
			Provider provider = (Provider) ((ComboBoxObject) cmbProvider
					.getSelectedItem()).getObject();
			Integer itemId = Formatter
					.formatStringToNumber(txtItemId.getText()).intValue();
			boolean disabled = chkDisabled.isSelected();

			VoucherTypeFacade facade = VoucherTypeFacade.getInstance();
			VoucherType voucherType = null;
			if (actionType == ActionType.CREATE) {
				voucherType = facade.addNewVoucherType(name, provider, itemId,
						disabled, deleted, session);
				voucherTypeId = voucherType.getId();
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				voucherType = facade.updateExistingVoucherType(voucherTypeId,
						name, provider, itemId, disabled, deleted, session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}

			ActivityLogFacade.doLog(getPermissionCode(), actionType,
					Main.getUserLogin(), voucherType, session);
			session.getTransaction().commit();

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	private void validateForm() throws UserException {
		if (txtName.getText().trim().equals("")) {
			throw new UserException("Nama harus diisi");
		}

		ComboBoxObject comboBoxObjectProvider = (ComboBoxObject) cmbProvider
				.getSelectedItem();
		if (comboBoxObjectProvider == null) {
			throw new UserException("Provider harus diisi");
		}

		Provider provider = (Provider) comboBoxObjectProvider.getObject();
		if (provider == null) {
			throw new UserException("Provider harus diisi");
		}

		Integer providerId = provider.getId();
		if (providerId == null) {
			throw new UserException("Provider harus diisi");
		}

		if (txtItemId.getText().trim().equals("")) {
			throw new UserException("Item harus diisi");
		}
	}
}
