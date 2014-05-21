package com.ganesha.prepaid.ui.forms;

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

	private MultiMap multiMap;

	public MultiMapForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Mapping Voucher Multi");
		setPermissionCode(PermissionConstants.VOUCHER_TYPE_FORM);
		setCloseOnEsc(false);

		getContentPane().setLayout(
				new MigLayout("", "[600,grow]", "[grow][grow]"));

		pnlKiri = new XJPanel();
		getContentPane().add(pnlKiri, "cell 0 0,grow");
		pnlKiri.setLayout(new MigLayout("", "[][100px][grow][]", "[]"));

		lblItem = new XJLabel();
		pnlKiri.add(lblItem, "cell 0 0");
		lblItem.setText("Barang");

		txtItemId = new XJTextField();
		txtItemId.setEditable(false);
		pnlKiri.add(txtItemId, "cell 1 0,growx");

		txtItemName = new XJTextField();
		txtItemName.setEditable(false);
		pnlKiri.add(txtItemName, "cell 2 0,growx");

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
		pnlKiri.add(btnCariBarang, "cell 3 0");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 1,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[][]", "[]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save();
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
		btnSimpan
				.setText("<html><center>Simpan Perubahan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 1 0");

		initForm();

		pack();
		setLocationRelativeTo(parent);
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

	private void initForm() {
		Session session = HibernateUtils.openSession();
		try {
			multiMap = MultiFacade.getInstance().getTheOnlyOne(session);
			Item item = multiMap.getItem();
			if (item != null) {
				txtItemId.setText(item.getId().toString());
				txtItemName.setText(item.getName());
			}
		} finally {
			session.close();
		}
	}

	private void save() throws ActionTypeNotSupported, UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();

			Integer itemId = Formatter
					.formatStringToNumber(txtItemId.getText()).intValue();

			MultiFacade facade = MultiFacade.getInstance();
			multiMap = facade.updateMultiMap(multiMap.getId(), itemId, session);
			dispose();

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
		if (txtItemId.getText().trim().equals("")) {
			throw new UserException("Item harus diisi");
		}
	}
}
