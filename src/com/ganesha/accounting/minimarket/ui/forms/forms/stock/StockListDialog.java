package com.ganesha.accounting.minimarket.ui.forms.forms.stock;

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

import com.ganesha.accounting.formatter.Formatter;
import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Item;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJRadioButton;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.hibernate.HibernateUtils;

public class StockListDialog extends XJDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJTable table;

	/**
	 * @wbp.nonvisual location=238,101
	 */
	private final ButtonGroup btnGroup = new ButtonGroup();
	private XJButton btnRegistrasi;
	private XJButton btnDetail;
	private XJButton btnRefresh;

	public StockListDialog(Window parent) {
		super(parent);

		setTitle("Master Barang");
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
		pnlFilter.setLayout(new MigLayout("", "[100][grow][]", "[][][grow]"));

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
		lblNama.setText("Name");
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

		JPanel pnlRadioButton = new JPanel();
		pnlFilter.add(pnlRadioButton, "cell 1 2,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][]"));

		XJRadioButton rdbtnBarangAktif = new XJRadioButton();
		rdbtnBarangAktif.setText("Barang Aktif");
		pnlRadioButton.add(rdbtnBarangAktif, "cell 0 0");
		rdbtnBarangAktif.setSelected(true);
		btnGroup.add(rdbtnBarangAktif);

		XJRadioButton rdbtnBarangTidakAktif = new XJRadioButton();
		rdbtnBarangTidakAktif.setText("Barang Tidak Aktif");
		pnlRadioButton.add(rdbtnBarangTidakAktif, "cell 0 1");
		btnGroup.add(rdbtnBarangTidakAktif);

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
		pnlFilter.add(btnRefresh, "cell 2 2,grow");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));

		btnRegistrasi = new XJButton();
		btnRegistrasi.addActionListener(new ActionListener() {
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
		btnKeluar.setText("<html></center>Keluar<br/>[ESC]</center></html>");
		panel.add(btnKeluar, "cell 0 0");
		panel.add(btnRegistrasi, "cell 1 0");
		btnRegistrasi
				.setText("<html><center>Registrasi Barang Baru<br/>[F5]</center><html>");

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
			btnRegistrasi.doClick();
			break;
		default:
			break;
		}
	}

	private void initTable() {
		XTableModel tableModel = new XTableModel();
		tableModel.setColumnIdentifiers(new String[] { "Kode", "Nama", "Stock",
				"Satuan", "Harga Beli", "HPP", "Harga Jual" });
		tableModel.setColumnEditable(new boolean[] { false, false, false,
				false, false, false, false });
		table.setModel(tableModel);

		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(25);
		columnModel.getColumn(1).setPreferredWidth(300);
		columnModel.getColumn(2).setPreferredWidth(25);
		columnModel.getColumn(3).setPreferredWidth(50);
		columnModel.getColumn(4).setPreferredWidth(75);
		columnModel.getColumn(5).setPreferredWidth(25);
		columnModel.getColumn(6).setPreferredWidth(75);

		columnModel.getColumn(2).setCellRenderer(
				XTableConstants.CELL_RENDERER_RIGHT);
		columnModel.getColumn(4).setCellRenderer(
				XTableConstants.CELL_RENDERER_RIGHT);
		columnModel.getColumn(5).setCellRenderer(
				XTableConstants.CELL_RENDERER_RIGHT);
		columnModel.getColumn(6).setCellRenderer(
				XTableConstants.CELL_RENDERER_RIGHT);
	}

	private void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String code = txtKode.getText();
			String name = txtNama.getText();
			boolean disabled = false;

			StockFacade facade = StockFacade.getInstance();
			List<ItemStock> itemStocks = facade.search(code, name, disabled,
					session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(itemStocks.size());

			for (int i = 0; i < itemStocks.size(); ++i) {
				ItemStock itemStock = itemStocks.get(i);
				Item item = itemStock.getItem();
				tableModel.setValueAt(item.getCode(), i, 0);
				tableModel.setValueAt(item.getName(), i, 1);
				tableModel.setValueAt(itemStock.getStock(), i, 2);
				tableModel.setValueAt(itemStock.getUnit(), i, 3);
				tableModel
						.setValueAt(Formatter.formatNumberToString(itemStock
								.getBuyPrice()), i, 4);
				tableModel.setValueAt(
						Formatter.formatNumberToString(itemStock.getHpp()), i,
						5);
				tableModel.setValueAt(Formatter.formatNumberToString(itemStock
						.getSellPrice()), i, 6);
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

			StockFacade facade = StockFacade.getInstance();
			ItemStock itemStock = facade.getDetail(code, session);

			StockForm stockForm = new StockForm(this, ActionType.UPDATE);
			stockForm.setFormDetailValue(itemStock);
			stockForm.setVisible(true);

			btnRefresh.doClick();
		} finally {
			session.close();
		}
	}

	private void tambah() {
		new StockForm(StockListDialog.this, ActionType.CREATE).setVisible(true);
		btnRefresh.doClick();

		int row = table.getRowCount() - 1;
		int column = table.getSelectedColumn();
		table.requestFocus();
		table.changeSelection(row, column, false, false);
	}
}
