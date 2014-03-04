package com.ganesha.minimarket.ui.forms.stockopname;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.coreapps.facade.ActivityLogFacade;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.ItemFacade;
import com.ganesha.minimarket.facade.StockOpnameFacade;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.StockOpnameDetail;
import com.ganesha.minimarket.model.StockOpnameHeader;
import com.ganesha.minimarket.ui.forms.stockopname.StockOpnameConfirmationDialog.ConfirmType;
import com.ganesha.minimarket.utils.PermissionConstants;

public class StockOpnameListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTable tblAvailableItem;
	private XJTable tblStockOpname;

	private final Map<ColumnEnum, XTableParameter> tableParametersForAvailableItem = new HashMap<>();
	private final Map<ColumnEnum, XTableParameter> tableParametersForStockOpname = new HashMap<>();
	private XJButton btnLanjut;
	private XJButton btnBatal;
	private XJPanel pnlInformation;
	private XJLabel lblPerformedBy;
	private XJTextField txtPerformedBy;
	private XJLabel lblPerformedDate;
	private XJTextField txtStartTimestamp;

	private Timestamp startTimestamp;
	private Timestamp stopTimestamp;
	private JScrollPane scrollAvailableItem;
	private XJPanel pnlBarcode;
	private XJTextField txtBarcode;
	private XJLabel lblBarcode;
	private XJButton btnTambah;
	private XJButton btnHapus;

	{
		tableParametersForAvailableItem.put(ColumnEnum.NUM,
				new XTableParameter(0, 10, false, "No", false,
						XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParametersForAvailableItem.put(ColumnEnum.CODE,
				new XTableParameter(1, 75, false, "Kode", false,
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParametersForAvailableItem.put(ColumnEnum.BARCODE,
				new XTableParameter(2, 100, false, "Barcode", false,
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParametersForAvailableItem.put(ColumnEnum.NAME,
				new XTableParameter(3, 400, false, "Nama Barang", false,
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParametersForAvailableItem.put(ColumnEnum.UNIT,
				new XTableParameter(4, 0, false, "Satuan", true,
						XTableConstants.CELL_RENDERER_CENTER, String.class));

		tableParametersForAvailableItem.put(ColumnEnum.QUANTITY_SISTEM,
				new XTableParameter(5, 0, false, "Qty Sistem", true,
						XTableConstants.CELL_RENDERER_RIGHT, Integer.class));

		tableParametersForAvailableItem.put(ColumnEnum.ID, new XTableParameter(
				6, 0, false, "ID", true, XTableConstants.CELL_RENDERER_LEFT,
				Integer.class));
	}

	{
		tableParametersForStockOpname.put(ColumnEnum.NUM, new XTableParameter(
				0, 10, false, "No", false,
				XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParametersForStockOpname.put(ColumnEnum.CODE, new XTableParameter(
				1, 75, false, "Kode", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParametersForStockOpname.put(ColumnEnum.NAME, new XTableParameter(
				2, 400, false, "Nama Barang", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParametersForStockOpname.put(ColumnEnum.UNIT, new XTableParameter(
				3, 50, false, "Satuan", false,
				XTableConstants.CELL_RENDERER_CENTER, String.class));

		tableParametersForStockOpname.put(ColumnEnum.QUANTITY_SISTEM,
				new XTableParameter(4, 50, false, "Qty Sistem", false,
						XTableConstants.CELL_RENDERER_RIGHT, Integer.class));

		tableParametersForStockOpname.put(ColumnEnum.QUANTITY_MANUAL,
				new XTableParameter(5, 50, true, "Qty Manual", false,
						XTableConstants.CELL_RENDERER_RIGHT, Integer.class));

		tableParametersForStockOpname.put(ColumnEnum.DEVIATION,
				new XTableParameter(6, 50, false, "Selisih", false,
						XTableConstants.CELL_RENDERER_RIGHT, Integer.class));

		tableParametersForStockOpname.put(ColumnEnum.ID, new XTableParameter(7,
				0, false, "ID", true, XTableConstants.CELL_RENDERER_LEFT,
				Integer.class));

		tableParametersForStockOpname.put(ColumnEnum.BARCODE,
				new XTableParameter(8, 0, false, "Barcode", true,
						XTableConstants.CELL_RENDERER_LEFT, String.class));
	}

	public StockOpnameListDialog(Window parent) {
		super(parent);

		setTitle("Stock Opname");
		setPermissionCode(PermissionConstants.STOCKOPNAME_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[800][300][grow][]", "[][150][][350][]"));
		setCloseOnEsc(false);

		tblStockOpname = new XJTable(false);
		XTableUtils.initTable(tblStockOpname, tableParametersForStockOpname);
		tblStockOpname.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("tableCellEditor")) {
					int row = tblStockOpname.getEditingRow();
					int column = tblStockOpname.getEditingColumn();
					if (column == tableParametersForStockOpname.get(
							ColumnEnum.QUANTITY_MANUAL).getColumnIndex()) {
						setTotalPerRow(row);
					}
				}
			}
		});

		pnlInformation = new XJPanel();
		getContentPane().add(pnlInformation, "cell 0 0");
		pnlInformation.setLayout(new MigLayout("", "[][300]", "[][]"));

		lblPerformedBy = new XJLabel();
		lblPerformedBy.setText("Dilakukan Oleh");
		pnlInformation.add(lblPerformedBy, "cell 0 0");

		txtPerformedBy = new XJTextField();
		txtPerformedBy.setText(Main.getUserLogin().getName());
		txtPerformedBy.setEditable(false);
		pnlInformation.add(txtPerformedBy, "cell 1 0,growx");

		lblPerformedDate = new XJLabel();
		lblPerformedDate.setText("Tanggal");
		pnlInformation.add(lblPerformedDate, "cell 0 1");

		txtStartTimestamp = new XJTextField();
		txtStartTimestamp.setText(Formatter.formatTimestampToString(CommonUtils
				.getCurrentDate()));
		txtStartTimestamp.setEditable(false);
		pnlInformation.add(txtStartTimestamp, "cell 1 1,growx");

		scrollAvailableItem = new JScrollPane((Component) null);
		getContentPane().add(scrollAvailableItem, "cell 0 1,grow");

		tblAvailableItem = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				boolean isFocus = tblAvailableItem.isFocusOwner();
				if (!isFocus) {
					return;
				}
				btnTambah.doClick();
			}
		};
		XTableUtils
				.initTable(tblAvailableItem, tableParametersForAvailableItem);
		scrollAvailableItem.setViewportView(tblAvailableItem);

		pnlBarcode = new XJPanel();
		getContentPane().add(pnlBarcode, "cell 1 1,grow");
		pnlBarcode.setLayout(new MigLayout("", "[grow]", "[][]"));

		lblBarcode = new XJLabel();
		lblBarcode.setText("Barcode [F8]");
		pnlBarcode.add(lblBarcode, "cell 0 0");

		txtBarcode = new XJTextField();
		txtBarcode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cariBarcode();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							StockOpnameListDialog.this, ex);
				}
			}
		});
		pnlBarcode.add(txtBarcode, "cell 0 1,growx");

		btnTambah = new XJButton();
		btnTambah.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tambah();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							StockOpnameListDialog.this, ex);
				}
			}
		});
		btnTambah
				.setText("<html><center>Tambah ke daftar Stock Opname<br/>[Enter]</center></html>");
		getContentPane().add(btnTambah, "cell 0 2,alignx right");

		JScrollPane scrollStockOpname = new JScrollPane(tblStockOpname);
		getContentPane().add(scrollStockOpname, "cell 0 3 3 1,grow");

		btnHapus = new XJButton();
		btnHapus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					hapus();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							StockOpnameListDialog.this, ex);
				}
			}
		});
		btnHapus.setText("<html><center>Hapus<br/>[Delete]</center></html>");
		getContentPane().add(btnHapus, "cell 3 3");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 4 3 1,grow");
		pnlButton.setLayout(new MigLayout("", "[200][grow][200]", "[]"));

		btnBatal = new XJButton();
		btnBatal.setMnemonic('Q');
		btnBatal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				batal();
			}
		});
		btnBatal.setText("<html><center>Batal<br/>[Alt+Q]</center></html>");
		pnlButton.add(btnBatal, "cell 0 0,growx");

		btnLanjut = new XJButton();
		btnLanjut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					lanjut();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							StockOpnameListDialog.this, ex);
				}
			}
		});
		btnLanjut.setText("<html><center>Lanjut<br/>[F12]</center></html>");
		pnlButton.add(btnLanjut, "cell 2 0,growx");

		try {
			loadDataInThread();
		} catch (Exception ex) {
			ExceptionHandler.handleException(this, ex);
		}

		startTimestamp = CommonUtils.getCurrentTimestamp();

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			ItemFacade facade = ItemFacade.getInstance();
			List<Item> items = facade.search(null, null, null, false, session);

			XTableModel tableModel = (XTableModel) tblAvailableItem.getModel();
			tableModel.setRowCount(items.size());

			for (int i = 0; i < items.size(); ++i) {
				Item item = items.get(i);

				tableModel.setValueAt(i + 1, i, tableParametersForAvailableItem
						.get(ColumnEnum.NUM).getColumnIndex());

				tableModel.setValueAt(item.getCode(), i,
						tableParametersForAvailableItem.get(ColumnEnum.CODE)
								.getColumnIndex());

				tableModel.setValueAt(item.getBarcode(), i,
						tableParametersForAvailableItem.get(ColumnEnum.BARCODE)
								.getColumnIndex());

				tableModel.setValueAt(item.getName(), i,
						tableParametersForAvailableItem.get(ColumnEnum.NAME)
								.getColumnIndex());

				tableModel.setValueAt(item.getUnit(), i,
						tableParametersForAvailableItem.get(ColumnEnum.UNIT)
								.getColumnIndex());

				tableModel.setValueAt(
						Formatter.formatNumberToString(facade
								.calculateStock(item)),
						i,
						tableParametersForAvailableItem.get(
								ColumnEnum.QUANTITY_SISTEM).getColumnIndex());

				tableModel.setValueAt(item.getId(), i,
						tableParametersForAvailableItem.get(ColumnEnum.ID)
								.getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F8:
			txtBarcode.setText("");
			txtBarcode.requestFocus();
			break;
		case KeyEvent.VK_F12:
			btnLanjut.doClick();
			break;
		case KeyEvent.VK_DELETE:
			boolean isFocus = tblStockOpname.isFocusOwner();
			if (!isFocus) {
				return;
			}
			btnHapus.doClick();
			break;
		default:
			break;
		}
	}

	private void batal() {
		String message = "Apakah Anda yakin ingin membatalkan proses stock opname ini?";
		int selectedOption = JOptionPane.showConfirmDialog(this, message,
				"Membatalkan Stock Opname", JOptionPane.YES_NO_OPTION);
		if (selectedOption == JOptionPane.YES_OPTION) {
			dispose();
		}
	}

	private void cariBarcode() throws UserException, AppException {
		String barcode = txtBarcode.getText();
		if (barcode == null || barcode.trim().equals("")) {
			return;
		}

		boolean found = false;
		String barcodeOnTable = null;
		XTableModel tableModel = (XTableModel) tblAvailableItem.getModel();
		for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); ++rowIndex) {
			barcodeOnTable = (String) tableModel.getValueAt(rowIndex,
					tableParametersForAvailableItem.get(ColumnEnum.BARCODE)
							.getColumnIndex());
			if (barcode.equals(barcodeOnTable)) {
				found = true;
				tblAvailableItem.changeSelection(rowIndex,
						tableParametersForAvailableItem.get(ColumnEnum.BARCODE)
								.getColumnIndex(), false, false);
				break;
			}
		}

		if (!found) {
			throw new UserException("Barang dengan barcode " + barcode
					+ " tidak ditemukan.");
		}

		tambah();
		txtBarcode.setText("");
	}

	private List<StockOpnameDetail> createStockOpnameList()
			throws AppException, UserException {
		List<StockOpnameDetail> stockOpnameDetails = new ArrayList<>();
		StockOpnameFacade facade = StockOpnameFacade.getInstance();
		{
			Session session = HibernateUtils.openSession();
			try {
				int rowCount = tblStockOpname.getRowCount();

				for (int i = 0; i < rowCount; ++i) {
					int itemId = (int) tblStockOpname.getModel().getValueAt(
							i,
							tableParametersForStockOpname.get(ColumnEnum.ID)
									.getColumnIndex());

					int quantitySistem = Formatter.formatStringToNumber(
							tblStockOpname
									.getModel()
									.getValueAt(
											i,
											tableParametersForStockOpname.get(
													ColumnEnum.QUANTITY_SISTEM)
													.getColumnIndex())
									.toString()).intValue();

					int quantityManual = Formatter.formatStringToNumber(
							tblStockOpname
									.getModel()
									.getValueAt(
											i,
											tableParametersForStockOpname.get(
													ColumnEnum.QUANTITY_MANUAL)
													.getColumnIndex())
									.toString()).intValue();

					int overCount = quantityManual - quantitySistem;
					if (overCount < 0) {
						overCount = 0;
					}

					int lossCount = quantitySistem - quantityManual;
					if (lossCount < 0) {
						lossCount = 0;
					}

					stockOpnameDetails.add(facade.createStockOpnameEntity(
							itemId, quantityManual, overCount, lossCount,
							session));
				}
			} finally {
				session.close();
			}

			return stockOpnameDetails;
		}
	}

	private void hapus() throws AppException {
		int selectedRow = tblStockOpname.getSelectedRow();
		int column = tblStockOpname.getSelectedColumn();
		if (selectedRow < 0) {
			return;
		}

		XTableModel tableModelStockOpname = (XTableModel) tblStockOpname
				.getModel();

		int itemId = (int) tableModelStockOpname.getValueAt(selectedRow,
				tableParametersForStockOpname.get(ColumnEnum.ID)
						.getColumnIndex());

		String code = (String) tableModelStockOpname.getValueAt(selectedRow,
				tableParametersForStockOpname.get(ColumnEnum.CODE)
						.getColumnIndex());

		String barcode = (String) tableModelStockOpname.getValueAt(selectedRow,
				tableParametersForStockOpname.get(ColumnEnum.BARCODE)
						.getColumnIndex());

		String name = (String) tableModelStockOpname.getValueAt(selectedRow,
				tableParametersForStockOpname.get(ColumnEnum.NAME)
						.getColumnIndex());

		String unit = (String) tableModelStockOpname.getValueAt(selectedRow,
				tableParametersForStockOpname.get(ColumnEnum.UNIT)
						.getColumnIndex());

		int quantitySystem = Formatter.formatStringToNumber(
				tableModelStockOpname.getValueAt(
						selectedRow,
						tableParametersForStockOpname.get(
								ColumnEnum.QUANTITY_SISTEM).getColumnIndex())
						.toString()).intValue();

		int i = 0;
		for (; i < tblAvailableItem.getRowCount(); ++i) {
			int itemIdAtAvailableItem = (int) tblAvailableItem.getModel()
					.getValueAt(
							i,
							tableParametersForAvailableItem.get(ColumnEnum.ID)
									.getColumnIndex());

			if (itemId > itemIdAtAvailableItem) {
				/*
				 * Do nothing. Lets assign i = i+1
				 */
			} else {
				break;
			}
		}

		tableModelStockOpname.removeRow(selectedRow);
		reorderRowNumber(tblStockOpname,
				tableParametersForStockOpname.get(ColumnEnum.NUM)
						.getColumnIndex());

		moveBack(i, itemId, code, barcode, name, unit, quantitySystem);
		reorderRowNumber(tblAvailableItem,
				tableParametersForAvailableItem.get(ColumnEnum.NUM)
						.getColumnIndex());

		int rowCount = tblStockOpname.getRowCount();
		if (rowCount <= selectedRow) {
			--selectedRow;
		}
		tblStockOpname.changeSelection(selectedRow, column, false, false);
	}

	private void lanjut() throws AppException, UserException {

		validateForm();

		stopTimestamp = CommonUtils.getCurrentTimestamp();

		StockOpnameFacade facade = StockOpnameFacade.getInstance();
		List<StockOpnameDetail> stockOpnames = createStockOpnameList();
		JasperPrint jasperPrint = facade.prepareJasper(stockOpnames,
				startTimestamp, stopTimestamp);

		if (jasperPrint != null) {
			Session session = HibernateUtils.openSession();
			try {
				JRViewer viewer = new JRViewer(jasperPrint);
				ConfirmType confirmType = StockOpnameConfirmationDialog
						.showConfirmation(this, viewer);

				session.beginTransaction();
				if (confirmType == ConfirmType.OK) {
					StockOpnameHeader stockOpnameHeader = facade.save(
							stockOpnames, startTimestamp, stopTimestamp,
							session);

					ActivityLogFacade.doLog(getPermissionCode(),
							ActionType.TRANSACTION, Main.getUserLogin(),
							stockOpnameHeader, session);
					session.getTransaction().commit();

					dispose();
				} else {
					session.getTransaction().rollback();
				}
			} catch (RuntimeException e) {
				session.getTransaction().rollback();
				throw e;
			} finally {
				session.close();
			}
		}
	}

	private void moveBack(int rowIndex, Integer itemId, String code,
			String barcode, String name, String unit, Integer quantitySystem)
			throws AppException {

		Object[] objects = new Object[9];
		objects[tableParametersForAvailableItem.get(ColumnEnum.CODE)
				.getColumnIndex()] = code;
		objects[tableParametersForAvailableItem.get(ColumnEnum.BARCODE)
				.getColumnIndex()] = barcode;
		objects[tableParametersForAvailableItem.get(ColumnEnum.NAME)
				.getColumnIndex()] = name;
		objects[tableParametersForAvailableItem.get(ColumnEnum.UNIT)
				.getColumnIndex()] = unit;
		objects[tableParametersForAvailableItem.get(ColumnEnum.QUANTITY_SISTEM)
				.getColumnIndex()] = quantitySystem;
		objects[tableParametersForAvailableItem.get(ColumnEnum.ID)
				.getColumnIndex()] = itemId;

		XTableModel tableModel = (XTableModel) tblAvailableItem.getModel();
		tableModel.insertRow(rowIndex, objects);
	}

	private void reorderRowNumber(XJTable table, int numColumnIndex) {
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			table.setValueAt(i + 1, i, numColumnIndex);
		}
	}

	private void setTotalPerRow(int row) {
		if (row < 0) {
			return;
		}

		int quantitySistem = Formatter.formatStringToNumber(
				tblStockOpname
						.getModel()
						.getValueAt(
								row,
								tableParametersForStockOpname.get(
										ColumnEnum.QUANTITY_SISTEM)
										.getColumnIndex()).toString())
				.intValue();
		tblStockOpname.setValueAt(Formatter
				.formatNumberToString(quantitySistem), row,
				tableParametersForStockOpname.get(ColumnEnum.QUANTITY_SISTEM)
						.getColumnIndex());

		int quantityManual = Formatter.formatStringToNumber(
				tblStockOpname
						.getModel()
						.getValueAt(
								row,
								tableParametersForStockOpname.get(
										ColumnEnum.QUANTITY_MANUAL)
										.getColumnIndex()).toString())
				.intValue();
		tblStockOpname.setValueAt(Formatter
				.formatNumberToString(quantityManual), row,
				tableParametersForStockOpname.get(ColumnEnum.QUANTITY_MANUAL)
						.getColumnIndex());

		double deviation = quantityManual - quantitySistem;
		tblStockOpname.setValueAt(Formatter.formatNumberToString(deviation),
				row, tableParametersForStockOpname.get(ColumnEnum.DEVIATION)
						.getColumnIndex());
	}

	private void tambah() throws AppException {
		int selectedRow = tblAvailableItem.getSelectedRow();
		if (selectedRow < 0) {
			return;
		}

		XTableModel tableModelAvailableItem = (XTableModel) tblAvailableItem
				.getModel();

		int itemId = (int) tableModelAvailableItem.getValueAt(selectedRow,
				tableParametersForAvailableItem.get(ColumnEnum.ID)
						.getColumnIndex());

		String code = (String) tableModelAvailableItem.getValueAt(selectedRow,
				tableParametersForAvailableItem.get(ColumnEnum.CODE)
						.getColumnIndex());

		String barcode = (String) tableModelAvailableItem.getValueAt(
				selectedRow,
				tableParametersForAvailableItem.get(ColumnEnum.BARCODE)
						.getColumnIndex());

		String name = (String) tableModelAvailableItem.getValueAt(selectedRow,
				tableParametersForAvailableItem.get(ColumnEnum.NAME)
						.getColumnIndex());

		String unit = (String) tableModelAvailableItem.getValueAt(selectedRow,
				tableParametersForAvailableItem.get(ColumnEnum.UNIT)
						.getColumnIndex());

		int quantitySystem = Formatter.formatStringToNumber(
				tableModelAvailableItem.getValueAt(
						selectedRow,
						tableParametersForAvailableItem.get(
								ColumnEnum.QUANTITY_SISTEM).getColumnIndex())
						.toString()).intValue();

		int i = 0;
		for (; i < tblStockOpname.getRowCount(); ++i) {
			int itemIdAtStockOpname = (int) tblStockOpname.getModel()
					.getValueAt(
							i,
							tableParametersForStockOpname.get(ColumnEnum.ID)
									.getColumnIndex());

			if (itemId > itemIdAtStockOpname) {
				/*
				 * Do nothing. Lets assign i = i+1
				 */
			} else {
				break;
			}
		}

		tableModelAvailableItem.removeRow(selectedRow);
		reorderRowNumber(tblAvailableItem,
				tableParametersForAvailableItem.get(ColumnEnum.NUM)
						.getColumnIndex());

		tambah(i, itemId, code, barcode, name, unit, quantitySystem);
		reorderRowNumber(tblStockOpname,
				tableParametersForStockOpname.get(ColumnEnum.NUM)
						.getColumnIndex());

		tblStockOpname.requestFocus();
		tblStockOpname.changeSelection(i,
				tableParametersForStockOpname.get(ColumnEnum.QUANTITY_MANUAL)
						.getColumnIndex(), false, false);
	}

	private void tambah(int rowIndex, Integer itemId, String code,
			String barcode, String name, String unit, Integer quantitySystem)
			throws AppException {

		Object[] objects = new Object[9];
		objects[tableParametersForStockOpname.get(ColumnEnum.CODE)
				.getColumnIndex()] = code;
		objects[tableParametersForStockOpname.get(ColumnEnum.BARCODE)
				.getColumnIndex()] = barcode;
		objects[tableParametersForStockOpname.get(ColumnEnum.NAME)
				.getColumnIndex()] = name;
		objects[tableParametersForStockOpname.get(ColumnEnum.UNIT)
				.getColumnIndex()] = unit;
		objects[tableParametersForStockOpname.get(ColumnEnum.QUANTITY_SISTEM)
				.getColumnIndex()] = quantitySystem;
		objects[tableParametersForStockOpname.get(ColumnEnum.QUANTITY_MANUAL)
				.getColumnIndex()] = null;
		objects[tableParametersForStockOpname.get(ColumnEnum.DEVIATION)
				.getColumnIndex()] = null;
		objects[tableParametersForStockOpname.get(ColumnEnum.ID)
				.getColumnIndex()] = itemId;

		XTableModel tableModel = (XTableModel) tblStockOpname.getModel();
		tableModel.insertRow(rowIndex, objects);
	}

	private void validateForm() throws UserException {
		XTableModel tableModel = (XTableModel) tblStockOpname.getModel();
		for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); ++rowIndex) {
			String code = (String) tableModel.getValueAt(rowIndex,
					tableParametersForStockOpname.get(ColumnEnum.CODE)
							.getColumnIndex());

			String name = (String) tableModel.getValueAt(rowIndex,
					tableParametersForStockOpname.get(ColumnEnum.NAME)
							.getColumnIndex());

			Object cellValue = tableModel.getValueAt(
					rowIndex,
					tableParametersForStockOpname.get(
							ColumnEnum.QUANTITY_MANUAL).getColumnIndex());

			if (cellValue == null) {
				throw new UserException("Quantity Manual untuk item " + code
						+ " " + name + " belum diset");
			}

			Integer quantityManual = Formatter.formatStringToNumber(
					cellValue.toString()).intValue();

			if (quantityManual == null) {
				throw new UserException("Quantity Manual untuk item " + code
						+ " " + name + " belum diset");
			}
		}
	}

	private enum ColumnEnum {
		NUM, CODE, NAME, UNIT, QUANTITY_SISTEM, QUANTITY_MANUAL, DEVIATION, ID, BARCODE
	}
}
