package com.ganesha.prepaid.ui.forms;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.exception.ActionTypeNotSupported;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJCheckBox;
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
import com.ganesha.prepaid.facade.MultiFacade;
import com.ganesha.prepaid.model.MultiMap;

public class MultiMapForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private ActionType actionType;
	private XJPanel pnlKiri;
	private XJLabel lblItem;
	private XJTextField txtItemId;
	private XJButton btnBatal;
	private XJTextField txtItemName;
	private XJButton btnCariBarang;

	private XJLabel lblMultiName;
	private XJTextField txtName;
	private Integer multiMapId;
	private XJPanel panel;
	private XJCheckBox chkDisabled;
	private XJButton btnHapusMultiMap;

	public MultiMapForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Mapping Voucher Multi");
		setPermissionCode(PermissionConstants.VOUCHER_TYPE_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[600,grow]", "[grow][grow][grow]"));

		pnlKiri = new XJPanel();
		getContentPane().add(pnlKiri, "cell 0 0,grow");
		pnlKiri.setLayout(new MigLayout("", "[][100px][grow][]", "[][]"));

		lblMultiName = new XJLabel();
		lblMultiName.setText("Nama Multi");
		pnlKiri.add(lblMultiName, "cell 0 0,alignx trailing");

		txtName = new XJTextField();
		pnlKiri.add(txtName, "cell 1 0 2 1,growx");

		lblItem = new XJLabel();
		pnlKiri.add(lblItem, "cell 0 1");
		lblItem.setText("Barang");

		txtItemId = new XJTextField();
		txtItemId.setEditable(false);
		pnlKiri.add(txtItemId, "cell 1 1,growx");

		txtItemName = new XJTextField();
		txtItemName.setEditable(false);
		pnlKiri.add(txtItemName, "cell 2 1,growx");

		btnCariBarang = new XJButton("Cari Barang [F5]");
		btnCariBarang.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cariBarang();
				} catch (Exception ex) {
					ExceptionHandler.handleException(MultiMapForm.this, ex);
				}
			}
		});
		pnlKiri.add(btnCariBarang, "cell 3 1");

		panel = new XJPanel();
		getContentPane().add(panel, "cell 0 1,alignx right,growy");
		panel.setLayout(new MigLayout("", "[]", "[]"));

		chkDisabled = new XJCheckBox();
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, 12));
		chkDisabled.setText("Tipe Multi ini sudah tidak aktif lagi");
		panel.add(chkDisabled, "cell 0 0");

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
					ExceptionHandler.handleException(MultiMapForm.this, ex);
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

		btnHapusMultiMap = new XJButton();
		btnHapusMultiMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(MultiMapForm.this, ex);
				}
			}
		});
		btnHapusMultiMap
				.setText("<html><center>Hapus<br/>Multi Map</center></html>");
		pnlButton.add(btnHapusMultiMap, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		pack();
		setLocationRelativeTo(parent);
	}

	public void setFormDetailValue(MultiMap multiMap) {
		multiMapId = multiMap.getId();

		txtName.setText(multiMap.getName());
		txtItemId.setText(multiMap.getItem().getId().toString());
		txtItemName.setText(multiMap.getItem().getName());
		chkDisabled.setSelected(multiMap.getDisabled());

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

	private void save(boolean deleted) throws ActionTypeNotSupported,
			UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			String name = txtName.getText();
			Integer itemId = Formatter
					.formatStringToNumber(txtItemId.getText()).intValue();
			boolean disabled = chkDisabled.isSelected();

			MultiFacade facade = MultiFacade.getInstance();
			MultiMap multiMap = null;
			if (actionType == ActionType.CREATE) {
				multiMap = facade.addNewMultiMap(name, itemId, disabled,
						deleted, session);
				multiMapId = multiMap.getId();
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				multiMap = facade.updateExistingMultiMap(multiMapId, name,
						itemId, disabled, deleted, session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}

			ActivityLogFacade.doLog(getPermissionCode(), actionType,
					Main.getUserLogin(), multiMap, session);
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
			throw new UserException("Nama Multi harus diisi");
		}
		if (txtItemId.getText().trim().equals("")) {
			throw new UserException("Item harus diisi");
		}
	}
}
