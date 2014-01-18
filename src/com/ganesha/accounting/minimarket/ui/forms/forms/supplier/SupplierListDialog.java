package com.ganesha.accounting.minimarket.ui.forms.forms.supplier;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.minimarket.facade.SupplierFacade;
import com.ganesha.accounting.minimarket.model.Supplier;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJRadioButton;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.hibernate.HibernateUtils;

public class SupplierListDialog extends XJDialog {
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

	public SupplierListDialog(Window parent) {
		super(parent);

		setTitle("Master Supplier");
		getContentPane().setLayout(
				new MigLayout("", "[1000,grow]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnDetail.doClick();
			}
		};
		initTable();

		JPanel pnlFilter = new JPanel();
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
					loadData();
				} catch (AppException ex) {
					ex.printStackTrace();
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
					loadData();
				} catch (AppException ex) {
					ex.printStackTrace();
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
					loadData();
				} catch (AppException ex) {
					ex.printStackTrace();
				}
			}
		});
		pnlFilter.add(txtKontakPerson, "cell 1 2 2 1,growx");

		JPanel pnlRadioButton = new JPanel();
		pnlFilter.add(pnlRadioButton, "cell 1 3,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][]"));

		XJRadioButton rdbtnSupplierAktif = new XJRadioButton();
		rdbtnSupplierAktif.setText("Supplier Aktif");
		pnlRadioButton.add(rdbtnSupplierAktif, "cell 0 0");
		rdbtnSupplierAktif.setSelected(true);
		btnGroup.add(rdbtnSupplierAktif);

		XJRadioButton rdbtnSupplierTidakAktif = new XJRadioButton();
		rdbtnSupplierTidakAktif.setText("Supplier Tidak Aktif");
		pnlRadioButton.add(rdbtnSupplierTidakAktif, "cell 0 1");
		btnGroup.add(rdbtnSupplierTidakAktif);

		btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadData();
				} catch (AppException ex) {
					ex.printStackTrace();
				}
			}
		});
		btnRefresh.setMnemonic('R');
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");
		pnlFilter.add(btnRefresh, "cell 2 3,grow");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		JPanel panel = new JPanel();
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
		btnKeluar.setText("<html><center>Keluar<br/>[ESC]</center></html>");
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
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnTambah.doClick();
			break;
		default:
			break;
		}
	}

	private void initTable() {
		XTableModel tableModel = new XTableModel();
		tableModel.setColumnIdentifiers(new String[] { "Kode", "Name",
				"Kontak Person 1", "Kontak Person 2", "Telepon" });
		tableModel.setColumnEditable(new boolean[] { false, false, false,
				false, false });
		table.setModel(tableModel);

		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(25);
		columnModel.getColumn(1).setPreferredWidth(300);
		columnModel.getColumn(2).setPreferredWidth(75);
		columnModel.getColumn(3).setPreferredWidth(75);
		columnModel.getColumn(4).setPreferredWidth(25);
	}

	private void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String code = txtKode.getText();
			String name = txtNama.getText();
			String kontakPerson = txtKontakPerson.getText();
			boolean disabled = false;

			SupplierFacade facade = SupplierFacade.getInstance();

			List<Supplier> suppliers = facade.search(code, name, kontakPerson,
					disabled, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(suppliers.size());

			for (int i = 0; i < suppliers.size(); ++i) {
				Supplier supplier = suppliers.get(i);
				tableModel.setValueAt(supplier.getCode(), i, 0);
				tableModel.setValueAt(supplier.getName(), i, 1);
				tableModel.setValueAt(supplier.getContactPerson1(), i, 2);
				tableModel.setValueAt(supplier.getContactPerson2(), i, 3);
				tableModel.setValueAt(supplier.getPhone1(), i, 4);
			}
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
			String code = (String) table.getModel().getValueAt(selectedRow, 0);

			SupplierFacade facade = SupplierFacade.getInstance();
			Supplier supplier = facade.getDetail(code, session);

			SupplierForm stockForm = new SupplierForm(this, ActionType.UPDATE);
			stockForm.setFormDetailValue(supplier);
			stockForm.setVisible(true);

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
}
