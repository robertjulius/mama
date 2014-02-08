package com.ganesha.minimarket.ui.forms.stock;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants.ActionType;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJRadioButton;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.StockFacade;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemStock;
import com.ganesha.minimarket.utils.BarcodeUtils;

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

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJRadioButton rdBarangAktif;
	private XJRadioButton rdBarangTidakAktif;
	private XJLabel lblBarcodef;
	private XJTextField txtBarcode;
	private XJButton btnPrintBarcode;
	{
		tableParameters.put(ColumnEnum.CODE, new XTableParameter(0, 25, false,
				"Kode", XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.NAME,
				new XTableParameter(1, 300, false, "Nama Barang",
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.STOCK, new XTableParameter(2, 25, false,
				"Stok", XTableConstants.CELL_RENDERER_RIGHT, Integer.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(3, 50, false,
				"Satuan", XTableConstants.CELL_RENDERER_CENTER, String.class));

		tableParameters.put(ColumnEnum.BUY_PRICE, new XTableParameter(4, 75,
				false, "Harga Beli", XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.HPP, new XTableParameter(5, 25, false,
				"HPP", XTableConstants.CELL_RENDERER_RIGHT, Double.class));

		tableParameters.put(ColumnEnum.SELL_PRICE, new XTableParameter(6, 75,
				false, "Harga Jual", XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));
	}

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
		XTableUtils.initTable(table, tableParameters);

		JPanel pnlFilter = new JPanel();
		getContentPane().add(pnlFilter, "cell 0 0,grow");
		pnlFilter.setLayout(new MigLayout("", "[][grow][]", "[][][][grow]"));

		XJLabel lblKode = new XJLabel();
		lblKode.setText("Kode Barang");
		pnlFilter.add(lblKode, "cell 0 0");

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
		lblNama.setText("Nama Barang");
		pnlFilter.add(lblNama, "cell 0 1");

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

		lblBarcodef = new XJLabel();
		lblBarcodef.setText("Barcode [F8]");
		pnlFilter.add(lblBarcodef, "cell 0 2,alignx trailing");

		txtBarcode = new XJTextField();
		pnlFilter.add(txtBarcode, "cell 1 2 2 1,growx");

		JPanel pnlRadioButton = new JPanel();
		pnlFilter.add(pnlRadioButton, "cell 1 3,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][]"));

		rdBarangAktif = new XJRadioButton();
		rdBarangAktif.setText("Barang Aktif");
		pnlRadioButton.add(rdBarangAktif, "cell 0 0");
		rdBarangAktif.setSelected(true);
		btnGroup.add(rdBarangAktif);

		rdBarangTidakAktif = new XJRadioButton();
		rdBarangTidakAktif.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadData();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		rdBarangTidakAktif.setText("Barang Tidak Aktif");
		pnlRadioButton.add(rdBarangTidakAktif, "cell 0 1");
		btnGroup.add(rdBarangTidakAktif);

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
		panel.setLayout(new MigLayout("", "[][][][]", "[]"));

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
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
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

		btnPrintBarcode = new XJButton();
		btnPrintBarcode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					printBarcode();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		btnPrintBarcode
				.setText("<html><center>Print Barcode<br/>[F6]</center><html>");
		panel.add(btnPrintBarcode, "cell 2 0");
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[Enter]</center></html>");
		panel.add(btnDetail, "cell 3 0");

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
		case KeyEvent.VK_F6:
			btnPrintBarcode.doClick();
			break;
		default:
			break;
		}
	}

	private void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String code = txtKode.getText();
			String barcode = txtBarcode.getText();
			String name = txtNama.getText();
			boolean disabled = rdBarangTidakAktif.isSelected();

			StockFacade facade = StockFacade.getInstance();
			List<ItemStock> itemStocks = facade.search(code, barcode, name,
					disabled, null, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(itemStocks.size());

			for (int i = 0; i < itemStocks.size(); ++i) {
				ItemStock itemStock = itemStocks.get(i);
				Item item = itemStock.getItem();

				tableModel.setValueAt(item.getCode(), i,
						tableParameters.get(ColumnEnum.CODE).getColumnIndex());

				tableModel.setValueAt(item.getName(), i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(Formatter.formatNumberToString(itemStock
						.getStock()), i, tableParameters.get(ColumnEnum.STOCK)
						.getColumnIndex());

				tableModel.setValueAt(itemStock.getUnit(), i, tableParameters
						.get(ColumnEnum.UNIT).getColumnIndex());
				tableModel
						.setValueAt(Formatter.formatNumberToString(itemStock
								.getBuyPrice()), i,
								tableParameters.get(ColumnEnum.BUY_PRICE)
										.getColumnIndex());

				tableModel.setValueAt(
						Formatter.formatNumberToString(itemStock.getHpp()), i,
						tableParameters.get(ColumnEnum.HPP).getColumnIndex());

				tableModel.setValueAt(Formatter.formatNumberToString(itemStock
						.getSellPrice()), i,
						tableParameters.get(ColumnEnum.SELL_PRICE)
								.getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	private void printBarcode() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			String code = (String) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.CODE).getColumnIndex());

			StockFacade facade = StockFacade.getInstance();
			ItemStock itemStock = facade.getDetail(code, session);
			Item item = itemStock.getItem();

			String barcode = item.getBarcode();
			if (barcode == null || barcode.trim().equals("")) {
				throw new UserException("Barcode untuk barang ["
						+ item.getCode() + "] " + item.getName()
						+ " belum diregistrasi");
			}

			File file = BarcodeUtils.generateBarcode(Long.parseLong(item
					.getBarcode()));
			BarcodeUtils.print(file);

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
			String code = (String) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.CODE).getColumnIndex());

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

	private enum ColumnEnum {
		CODE, NAME, STOCK, UNIT, BUY_PRICE, HPP, SELL_PRICE
	}
}