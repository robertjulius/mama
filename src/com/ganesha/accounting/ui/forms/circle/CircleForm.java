package com.ganesha.accounting.ui.forms.circle;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.constants.Enums.CircleUnit;
import com.ganesha.accounting.facade.CircleFacade;
import com.ganesha.accounting.model.Circle;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.exception.ActionTypeNotSupported;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XEtchedBorder;
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

public class CircleForm extends XJDialog {

	private static final long serialVersionUID = 1401014426195840845L;

	private XJButton btnSimpan;
	private XJComboBox cmbUnits;
	private XJTextField txtName;
	private ActionType actionType;
	private JSeparator separator;
	private XJLabel lblDuration;
	private XJTextField txtDuration;
	private XJButton btnBatal;
	private XJButton btnHapus;
	private XJPanel pnlDisable;
	private XJCheckBox chkDisabled;

	private Integer circleId;

	public CircleForm(Window parent, ActionType actionType) {
		super(parent);
		this.actionType = actionType;
		setTitle("Form Pengaturan Siklus");
		setPermissionCode(PermissionConstants.STOCK_FORM);
		setCloseOnEsc(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				try {
					initForm();
				} catch (Exception ex) {
					ExceptionHandler.handleException(CircleForm.this, ex);
				}
			}
		});
		getContentPane().setLayout(
				new MigLayout("", "[400]", "[grow][grow][10][grow]"));

		XJPanel pnlInput = new XJPanel();
		pnlInput.setBorder(new XEtchedBorder());
		getContentPane().add(pnlInput, "cell 0 0,grow");
		pnlInput.setLayout(new MigLayout("", "[150][grow]", "[][][]"));

		XJLabel lblNama = new XJLabel();
		pnlInput.add(lblNama, "cell 0 0");
		lblNama.setText("Nama Siklus");

		txtName = new XJTextField();
		txtName.setUpperCaseOnFocusLost(true);
		pnlInput.add(txtName, "cell 1 0,growx");

		XJLabel lblSatuan = new XJLabel();
		pnlInput.add(lblSatuan, "cell 0 1");
		lblSatuan.setText("Satuan");

		cmbUnits = new XJComboBox(GeneralConstants.CMB_BOX_CIRCLE_UNITS);
		pnlInput.add(cmbUnits, "cell 1 1,growx");

		lblDuration = new XJLabel();
		pnlInput.add(lblDuration, "cell 0 2");
		lblDuration.setText("Durasi");

		txtDuration = new XJTextField();
		pnlInput.add(txtDuration, "cell 1 2,growx");

		pnlDisable = new XJPanel();
		getContentPane().add(pnlDisable, "cell 0 1,alignx right,growy");
		pnlDisable.setLayout(new MigLayout("", "[]", "[]"));

		chkDisabled = new XJCheckBox();
		chkDisabled.setFont(new Font("Tahoma", Font.BOLD, FONT_SIZE_SMALLEST));
		chkDisabled.setText("Siklus ini sudah tidak aktif lagi");
		pnlDisable.add(chkDisabled, "cell 0 0");

		separator = new JSeparator();
		getContentPane().add(separator, "cell 0 2,grow");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 3,grow");
		pnlButton.setLayout(new MigLayout("", "[][grow][][]", "[grow]"));

		btnSimpan = new XJButton();
		btnSimpan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(false);
				} catch (Exception ex) {
					ExceptionHandler.handleException(CircleForm.this, ex);
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

		btnHapus = new XJButton();
		btnHapus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					save(true);
				} catch (Exception ex) {
					ExceptionHandler.handleException(CircleForm.this, ex);
				}
			}
		});
		btnHapus.setText("<html><center>Hapus<br/>Siklus</center></html>");
		pnlButton.add(btnHapus, "cell 2 0");
		btnSimpan.setText("<html><center>Simpan<br/>[F12]</center></html>");
		pnlButton.add(btnSimpan, "cell 3 0");

		pack();
		setLocationRelativeTo(null);
	}

	public Integer getIdBarang() {
		return circleId;
	}

	public void setFormDetailValue(Circle circle) {
		circleId = circle.getId();

		txtName.setText(circle.getName());
		cmbUnits.setSelectedItem(circle.getUnit());
		txtDuration
				.setText(Formatter.formatNumberToString(circle.getDuration()));
		chkDisabled.setSelected(circle.getDisabled());

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

	private void initForm() throws ActionTypeNotSupported {
		if (actionType == ActionType.CREATE) {
			/*
			 * Do nothing
			 */
		} else if (actionType == ActionType.UPDATE) {
			cmbUnits.setEditable(false);
			txtDuration.setEditable(false);
		} else {
			throw new ActionTypeNotSupported(actionType);
		}
	}

	private void save(boolean deleted) throws ActionTypeNotSupported,
			UserException {
		validateForm();

		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			CircleFacade facade = CircleFacade.getInstance();

			String name = txtName.getText();
			CircleUnit unit = (CircleUnit) ((ComboBoxObject) cmbUnits
					.getSelectedItem()).getId();
			Integer duration = Formatter.formatStringToNumber(
					txtDuration.getText()).intValue();

			boolean disabled = chkDisabled.isSelected();

			Circle circle = null;
			if (actionType == ActionType.CREATE) {
				circle = facade.addNewCircle(name, unit, duration, disabled,
						deleted, session);
				circleId = circle.getId();
				dispose();
			} else if (actionType == ActionType.UPDATE) {
				circle = facade.updateExistingCircle(circleId, name, unit,
						duration, disabled, deleted, session);
				dispose();
			} else {
				throw new ActionTypeNotSupported(actionType);
			}

			ActivityLogFacade.doLog(getPermissionCode(), actionType,
					Main.getUserLogin(), circle, session);
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
			throw new UserException("Nama Siklus harus diisi");
		}
	}
}
