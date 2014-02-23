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
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.coreapps.constants.Enums.ActionType;
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
import com.ganesha.minimarket.facade.ItemFacade;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.utils.BarcodeUtils;
import com.ganesha.minimarket.utils.PermissionConstants;

public class StockListDialog extends XJTableDialog {
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
		tableParameters.put(ColumnEnum.CODE,
				new XTableParameter(0, 25, false, "Kode", false,
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.NAME, new XTableParameter(1, 300, false,
				"Nama Barang", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.STOCK, new XTableParameter(2, 25, false,
				"Stok", false, XTableConstants.CELL_RENDERER_RIGHT,
				Integer.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(3, 50, false,
				"Satuan", false, XTableConstants.CELL_RENDERER_CENTER,
				String.class));

		tableParameters.put(ColumnEnum.BUY_PRICE, new XTableParameter(4, 75,
				false, "Harga Beli", false,
				XTableConstants.CELL_RENDERER_RIGHT, Double.class));

		tableParameters.put(ColumnEnum.HPP,
				new XTableParameter(5, 25, false, "HPP", false,
						XTableConstants.CELL_RENDERER_RIGHT, Double.class));

		tableParameters.put(ColumnEnum.SELL_PRICE, new XTableParameter(6, 75,
				false, "Harga Jual", false,
				XTableConstants.CELL_RENDERER_RIGHT, Double.class));

		tableParameters.put(ColumnEnum.ID, new XTableParameter(7, 0, false,
				"ID", true, XTableConstants.CELL_RENDERER_LEFT, Integer.class));
	}

	public StockListDialog(Window parent) {
		super(parent);

		setTitle("Master Barang");
		setPermissionCode(PermissionConstants.STOCK_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[1000]", "[][300,grow][]"));

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
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockListDialog.this, ex);
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
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(txtNama, "cell 1 1 2 1,growx");
		txtNama.setColumns(10);

		lblBarcodef = new XJLabel();
		lblBarcodef.setText("Barcode [F8]");
		pnlFilter.add(lblBarcodef, "cell 0 2");

		txtBarcode = new XJTextField();
		txtBarcode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(txtBarcode, "cell 1 2 2 1,growx");

		XJPanel pnlRadioButton = new XJPanel();
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
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockListDialog.this, ex);
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
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockListDialog.this, ex);
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
				try {
					showDetail();
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockListDialog.this, ex);
				}
			}
		});

		btnPrintBarcode = new XJButton();
		btnPrintBarcode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					printBarcode();
				} catch (Exception ex) {
					ExceptionHandler.handleException(StockListDialog.this, ex);
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
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String code = txtKode.getText();
			String barcode = txtBarcode.getText();
			String name = txtNama.getText();
			boolean disabled = rdBarangTidakAktif.isSelected();

			ItemFacade facade = ItemFacade.getInstance();
			List<Item> items = facade.search(code, barcode, name, disabled,
					session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(items.size());

			for (int i = 0; i < items.size(); ++i) {
				Item item = items.get(i);

				tableModel.setValueAt(item.getCode(), i,
						tableParameters.get(ColumnEnum.CODE).getColumnIndex());

				tableModel.setValueAt(item.getName(), i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(Formatter.formatNumberToString(facade
						.calculateStock(item)), i,
						tableParameters.get(ColumnEnum.STOCK).getColumnIndex());

				tableModel.setValueAt(item.getUnit(), i,
						tableParameters.get(ColumnEnum.UNIT).getColumnIndex());
				tableModel.setValueAt(Formatter.formatNumberToString(facade
						.getLastBuyPrice(item)), i,
						tableParameters.get(ColumnEnum.BUY_PRICE)
								.getColumnIndex());

				tableModel.setValueAt(
						Formatter.formatNumberToString(item.getHpp()), i,
						tableParameters.get(ColumnEnum.HPP).getColumnIndex());

				tableModel.setValueAt(Formatter.formatNumberToString(item
						.getSellPrice()), i,
						tableParameters.get(ColumnEnum.SELL_PRICE)
								.getColumnIndex());

				tableModel.setValueAt(item.getId(), i,
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
			btnRegistrasi.doClick();
			break;
		case KeyEvent.VK_F6:
			btnPrintBarcode.doClick();
			break;
		case KeyEvent.VK_F8:
			txtBarcode.setText("");
			txtBarcode.requestFocus();
			break;
		default:
			break;
		}
	}

	private void printBarcode() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			int itemId = (int) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.ID).getColumnIndex());

			ItemFacade facade = ItemFacade.getInstance();
			Item item = facade.getDetail(itemId, session);

			String barcode = item.getBarcode();
			if (barcode == null || barcode.trim().equals("")) {
				throw new UserException("Barcode untuk barang ["
						+ item.getCode() + "] " + item.getName()
						+ " belum diregistrasi");
			}

			File file = BarcodeUtils.generatePdfFile(item.getBarcode());
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
			int itemId = (int) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.ID).getColumnIndex());

			ItemFacade facade = ItemFacade.getInstance();
			Item item = facade.getDetail(itemId, session);

			StockForm stockForm = new StockForm(this, ActionType.UPDATE);
			stockForm.setFormDetailValue(item);
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
		CODE, NAME, STOCK, UNIT, BUY_PRICE, HPP, SELL_PRICE, ID
	}
}
