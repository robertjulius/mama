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

import net.miginfocom.swing.MigLayout;

import com.ganesha.accounting.formatter.Formatter;
import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Good;
import com.ganesha.accounting.minimarket.model.GoodStock;
import com.ganesha.accounting.minimarket.ui.commons.MyTableModel;
import com.ganesha.core.exception.AppException;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJRadioButton;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;

public class StockListDialog extends XJDialog {
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

	public StockListDialog(Window parent) {
		super(parent);

		setTitle("Master Barang");
		getContentPane().setLayout(
				new MigLayout("", "[1000,grow]", "[][300,grow][]"));

		table = new XJTable();
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

		XJLabel lblName = new XJLabel();
		lblName.setText("Name");
		pnlFilter.add(lblName, "cell 0 1,alignx trailing");

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

		XJButton btnRefresh = new XJButton();
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
		panel.setLayout(new MigLayout("", "[][]", "[]"));

		btnTambah = new XJButton();
		btnTambah.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new StockForm(StockListDialog.this).setVisible(true);
			}
		});
		panel.add(btnTambah, "cell 0 0");
		btnTambah
				.setText("<html><center>Tambah Barang Baru<br/>[F1]</center><html>");

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDetail();
			}
		});
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[F2]</center></html>");
		panel.add(btnDetail, "cell 1 0");

		btnRefresh.doClick();

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F1:
			btnTambah.doClick();
			break;
		case KeyEvent.VK_F2:
			btnDetail.doClick();
			break;
		default:
			break;
		}
	}

	private void initTable() {
		MyTableModel tableModel = new MyTableModel();
		tableModel.setColumnIdentifiers(new String[] { "Kode", "Name", "Stock",
				"Satuan", "Harga Beli", "Harga Jual" });
		tableModel.setColumnEditable(new boolean[] { false, false, false,
				false, false, false });
		table.setModel(tableModel);
	}

	private void loadData() throws AppException {
		String code = txtKode.getText();
		String name = txtNama.getText();
		boolean disabled = false;

		StockFacade facade = StockFacade.getInstance();
		List<GoodStock> goodStocks = facade.search(code, name, disabled);

		MyTableModel tableModel = (MyTableModel) table.getModel();
		tableModel.setRowCount(goodStocks.size());

		for (int i = 0; i < goodStocks.size(); ++i) {
			GoodStock goodStock = goodStocks.get(i);
			Good good = goodStock.getGood();
			tableModel.setValueAt(good.getCode(), i, 0);
			tableModel.setValueAt(good.getName(), i, 1);
			tableModel.setValueAt(goodStock.getStock(), i, 2);
			tableModel.setValueAt(goodStock.getUnit(), i, 3);
			tableModel.setValueAt(Formatter
					.formatPriceBigDecimalToString(goodStock.getSellPrice()),
					i, 4);
			tableModel.setValueAt(Formatter
					.formatPriceBigDecimalToString(goodStock.getSellPrice()),
					i, 5);
		}
	}

	private void showDetail() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			return;
		}
		String code = (String) table.getModel().getValueAt(selectedRow, 0);

		StockFacade facade = StockFacade.getInstance();
		GoodStock goodStock = facade.getDetail(code);

		StockForm stockForm = new StockForm(this);
		stockForm.setFormDetailValue(goodStock);
		stockForm.setVisible(true);
	}
}
