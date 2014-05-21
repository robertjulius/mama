package com.ganesha.prepaid.ui.forms;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJRadioButton;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.prepaid.facade.VoucherFacade;
import com.ganesha.prepaid.facade.VoucherTypeFacade;
import com.ganesha.prepaid.model.Voucher;
import com.ganesha.prepaid.model.VoucherType;

public class VoucherListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJComboBox cmbVoucherType;
	private XJTable table;

	/**
	 * @wbp.nonvisual location=238,101
	 */
	private final ButtonGroup btnGroup = new ButtonGroup();
	private XJButton btnTambah;
	private XJButton btnDetail;
	private XJButton btnRefresh;
	private XJRadioButton rdVoucherTypeAktif;
	private XJRadioButton rdVoucherTypeTidakAktif;
	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.VOUCHER_TYPE, new XTableParameter(0,
				150, false, "Tipe Voucher", false,
				XTableConstants.CELL_RENDERER_CENTER, String.class));

		tableParameters.put(ColumnEnum.PACKAGE_NAME, new XTableParameter(1,
				350, false, "Nama Paket", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(2, 150,
				false, "Harga", false, XTableConstants.CELL_RENDERER_RIGHT,
				String.class));

		tableParameters.put(ColumnEnum.ID, new XTableParameter(3, 0, false,
				"ID", true, XTableConstants.CELL_RENDERER_LEFT, Integer.class));
	}

	public VoucherListDialog(Window parent) {
		super(parent);

		setTitle("Master Voucher");
		setPermissionCode(PermissionConstants.VOUCHER_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[650,grow]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnDetail.doClick();
			}
		};
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,grow");
		pnlFilter.setLayout(new MigLayout("", "[100][grow][]", "[][grow]"));

		XJLabel lblVoucherType = new XJLabel();
		lblVoucherType.setText("Tipe Voucher");
		pnlFilter.add(lblVoucherType, "cell 0 0");

		cmbVoucherType = new XJComboBox();
		cmbVoucherType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(VoucherListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(cmbVoucherType, "cell 1 0 2 1,growx");

		XJPanel pnlRadioButton = new XJPanel();
		pnlFilter.add(pnlRadioButton, "cell 1 1,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][]"));

		rdVoucherTypeAktif = new XJRadioButton();
		rdVoucherTypeAktif.setText("Voucher Aktif");
		pnlRadioButton.add(rdVoucherTypeAktif, "cell 0 0");
		rdVoucherTypeAktif.setSelected(true);
		btnGroup.add(rdVoucherTypeAktif);

		rdVoucherTypeTidakAktif = new XJRadioButton();
		rdVoucherTypeTidakAktif.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(VoucherListDialog.this, ex);
				}
			}
		});
		rdVoucherTypeTidakAktif.setText("Voucher Tidak Aktif");
		pnlRadioButton.add(rdVoucherTypeTidakAktif, "cell 0 1");
		btnGroup.add(rdVoucherTypeTidakAktif);

		btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler
							.handleException(VoucherListDialog.this, ex);
				}
			}
		});
		btnRefresh.setMnemonic('R');
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");
		pnlFilter.add(btnRefresh, "cell 2 1,grow");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));

		btnTambah = new XJButton();
		btnTambah.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tambah();
			}
		});

		XJButton btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
		panel.add(btnKeluar, "cell 0 0");
		panel.add(btnTambah, "cell 1 0");
		btnTambah
				.setText("<html><center>Tambah Voucher Baru<br/>[F5]</center><html>");

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDetail();
			}
		});
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[Enter]</center></html>");
		panel.add(btnDetail, "cell 2 0");

		loadComboVoucherType();
		btnRefresh.doClick();

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			VoucherType voucherType = (VoucherType) ((ComboBoxObject) cmbVoucherType
					.getSelectedItem()).getObject();

			boolean disabled = rdVoucherTypeTidakAktif.isSelected();

			VoucherFacade facade = VoucherFacade.getInstance();

			List<Voucher> vouchers = facade.search(voucherType.getId(),
					disabled, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(vouchers.size());

			for (int i = 0; i < vouchers.size(); ++i) {
				Voucher voucher = vouchers.get(i);

				tableModel.setValueAt(voucher.getVoucherType().getName(), i,
						tableParameters.get(ColumnEnum.VOUCHER_TYPE)
								.getColumnIndex());

				tableModel.setValueAt(voucher.getPackageName(), i,
						tableParameters.get(ColumnEnum.PACKAGE_NAME)
								.getColumnIndex());

				tableModel.setValueAt(
						Formatter.formatNumberToString(voucher.getPrice()), i,
						tableParameters.get(ColumnEnum.PRICE).getColumnIndex());

				tableModel.setValueAt(voucher.getId(), i,
						tableParameters.get(ColumnEnum.ID).getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnTambah.doClick();
			break;
		default:
			break;
		}
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

	private void showDetail() {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			int id = (int) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.ID).getColumnIndex());

			VoucherFacade facade = VoucherFacade.getInstance();
			Voucher voucher = facade.getDetail(id, session);

			VoucherForm voucherForm = new VoucherForm(this, ActionType.UPDATE);
			voucherForm.setFormDetailValue(voucher);
			voucherForm.setVisible(true);

			btnRefresh.doClick();
		} finally {
			session.close();
		}
	}

	private void tambah() {
		new VoucherForm(VoucherListDialog.this, ActionType.CREATE)
				.setVisible(true);
		btnRefresh.doClick();

		int row = table.getRowCount() - 1;
		int column = table.getSelectedColumn();
		table.requestFocus();
		table.changeSelection(row, column, false, false);
	}

	private enum ColumnEnum {
		VOUCHER_TYPE, PACKAGE_NAME, PRICE, ID
	}
}
