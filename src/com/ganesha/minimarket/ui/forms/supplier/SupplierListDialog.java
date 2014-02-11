package com.ganesha.minimarket.ui.forms.supplier;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJRadioButton;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.SupplierFacade;
import com.ganesha.minimarket.model.Supplier;
import com.ganesha.minimarket.utils.PermissionConstants;

public class SupplierListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJTable table;

	/**
	 * @wbp.nonvisual location=238,101
	 */
	private final ButtonGroup btnGroup = new ButtonGroup();
	private XJButton btnTambah;
	private XJButton btnDetail;
	private XJTextField txtKontakPerson;
	private XJButton btnRefresh;
	private XJRadioButton rdSupplierAktif;
	private XJRadioButton rdSupplierTidakAktif;
	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.CODE, new XTableParameter(0, 25, false,
				"Kode", false, XTableConstants.CELL_RENDERER_CENTER,
				String.class));

		tableParameters.put(ColumnEnum.NAME, new XTableParameter(1, 300, false,
				"Nama Supplier", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.CONTACT_PERSON1, new XTableParameter(2,
				75, false, "Kontak Person 1", false,
				XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.CONTACT_PERSON2, new XTableParameter(3,
				75, false, "Kontak Person 2", false,
				XTableConstants.CELL_RENDERER_CENTER, String.class));

		tableParameters.put(ColumnEnum.PHONE, new XTableParameter(4, 25, false,
				"Telepon", false, XTableConstants.CELL_RENDERER_CENTER,
				Double.class));
	}

	public SupplierListDialog(Window parent) {
		super(parent);

		setTitle("Master Supplier");
		setPermissionCode(PermissionConstants.SUPPLIER_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[1000,grow]", "[][300,grow][]"));

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
		pnlFilter.setLayout(new MigLayout("", "[100][grow][]", "[][][][grow]"));

		XJLabel lblKode = new XJLabel();
		lblKode.setText("Kode");
		pnlFilter.add(lblKode, "cell 0 0,alignx trailing");

		txtKode = new XJTextField();
		txtKode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					/*
					 * TODO Perbaiki supaya kalo pas key = alt+tab, ga usah load
					 * data
					 */
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(SupplierListDialog.this,
							ex);
				}
			}
		});
		pnlFilter.add(txtKode, "cell 1 0 2 1,growx");
		txtKode.setColumns(10);

		XJLabel lblNama = new XJLabel();
		lblNama.setText("Nama");
		pnlFilter.add(lblNama, "cell 0 1,alignx trailing");

		txtNama = new XJTextField();
		txtNama.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(SupplierListDialog.this,
							ex);
				}
			}
		});
		pnlFilter.add(txtNama, "cell 1 1 2 1,growx");
		txtNama.setColumns(10);

		XJLabel lblKontakPerson = new XJLabel();
		lblKontakPerson.setText("Kontak Person");
		pnlFilter.add(lblKontakPerson, "cell 0 2,alignx trailing");

		txtKontakPerson = new XJTextField();
		txtKontakPerson.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(SupplierListDialog.this,
							ex);
				}
			}
		});
		pnlFilter.add(txtKontakPerson, "cell 1 2 2 1,growx");

		XJPanel pnlRadioButton = new XJPanel();
		pnlFilter.add(pnlRadioButton, "cell 1 3,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][]"));

		rdSupplierAktif = new XJRadioButton();
		rdSupplierAktif.setText("Supplier Aktif");
		pnlRadioButton.add(rdSupplierAktif, "cell 0 0");
		rdSupplierAktif.setSelected(true);
		btnGroup.add(rdSupplierAktif);

		rdSupplierTidakAktif = new XJRadioButton();
		rdSupplierTidakAktif.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(SupplierListDialog.this,
							ex);
				}
			}
		});
		rdSupplierTidakAktif.setText("Supplier Tidak Aktif");
		pnlRadioButton.add(rdSupplierTidakAktif, "cell 0 1");
		btnGroup.add(rdSupplierTidakAktif);

		btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(SupplierListDialog.this,
							ex);
				}
			}
		});
		btnRefresh.setMnemonic('R');
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");
		pnlFilter.add(btnRefresh, "cell 2 3,grow");

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
				.setText("<html><center>Tambah Supplier Baru<br/>[F5]</center><html>");

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

		btnRefresh.doClick();

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String code = txtKode.getText();
			String name = txtNama.getText();
			String kontakPerson = txtKontakPerson.getText();
			boolean disabled = rdSupplierTidakAktif.isSelected();

			SupplierFacade facade = SupplierFacade.getInstance();

			List<Supplier> suppliers = facade.search(code, name, kontakPerson,
					disabled, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(suppliers.size());

			for (int i = 0; i < suppliers.size(); ++i) {
				Supplier supplier = suppliers.get(i);

				tableModel.setValueAt(supplier.getCode(), i, tableParameters
						.get(ColumnEnum.CODE).getColumnIndex());

				tableModel.setValueAt(supplier.getName(), i, tableParameters
						.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(supplier.getContactPerson1(), i,
						tableParameters.get(ColumnEnum.CONTACT_PERSON1)
								.getColumnIndex());

				tableModel.setValueAt(supplier.getContactPerson2(), i,
						tableParameters.get(ColumnEnum.CONTACT_PERSON2)
								.getColumnIndex());

				tableModel.setValueAt(supplier.getPhone1(), i, tableParameters
						.get(ColumnEnum.PHONE).getColumnIndex());
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

	private void showDetail() {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			String code = (String) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.CODE).getColumnIndex());

			SupplierFacade facade = SupplierFacade.getInstance();
			Supplier supplier = facade.getDetail(code, session);

			SupplierForm supplierForm = new SupplierForm(this,
					ActionType.UPDATE);
			supplierForm.setFormDetailValue(supplier);
			supplierForm.setVisible(true);

			btnRefresh.doClick();
		} finally {
			session.close();
		}
	}

	private void tambah() {
		new SupplierForm(SupplierListDialog.this, ActionType.CREATE)
				.setVisible(true);
		btnRefresh.doClick();

		int row = table.getRowCount() - 1;
		int column = table.getSelectedColumn();
		table.requestFocus();
		table.changeSelection(row, column, false, false);
	}

	private enum ColumnEnum {
		CODE, NAME, CONTACT_PERSON1, CONTACT_PERSON2, PHONE
	}
}
