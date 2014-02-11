package com.ganesha.minimarket.ui.forms.stockopname;

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

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
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
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.Main;
import com.ganesha.minimarket.facade.StockFacade;
import com.ganesha.minimarket.facade.StockOpnameFacade;
import com.ganesha.minimarket.facade.StockOpnameFacade.StockQueueMethod;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemStock;
import com.ganesha.minimarket.model.StockOpnameDetail;
import com.ganesha.minimarket.ui.forms.stockopname.StockOpnameConfirmationDialog.ConfirmType;
import com.ganesha.minimarket.utils.PermissionConstants;

public class StockOpnameListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTable table;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJButton btnLanjut;
	private XJButton btnBatal;
	private XJPanel pnlInformation;
	private XJLabel lblPerformedBy;
	private XJTextField txtPerformedBy;
	private XJLabel lblPerformedDate;
	private XJTextField txtStartTimestamp;

	private Timestamp startTimestamp;
	private Timestamp stopTimestamp;

	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 10, false,
				"No", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.CODE,
				new XTableParameter(1, 50, false, "Kode", false,
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.NAME, new XTableParameter(2, 300, false,
				"Nama Barang", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(3, 50, false,
				"Satuan", false, XTableConstants.CELL_RENDERER_CENTER,
				String.class));

		tableParameters.put(ColumnEnum.QUANTITY_SISTEM, new XTableParameter(4,
				50, false, "Qty Sistem", false,
				XTableConstants.CELL_RENDERER_RIGHT, Integer.class));

		tableParameters.put(ColumnEnum.QUANTITY_MANUAL, new XTableParameter(5,
				50, true, "Qty Manual", false,
				XTableConstants.CELL_RENDERER_RIGHT, Integer.class));

		tableParameters.put(ColumnEnum.DEVIATION, new XTableParameter(6, 75,
				false, "Selisih", false, XTableConstants.CELL_RENDERER_RIGHT,
				Integer.class));
	}

	public StockOpnameListDialog(Window parent) {
		super(parent);

		setTitle("Stock Opname");
		setPermissionCode(PermissionConstants.STOCKOPNAME_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[1000,grow]", "[grow][300,grow][]"));
		setCloseOnEsc(false);

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);
		table.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("tableCellEditor")) {
					int row = table.getEditingRow();
					int column = table.getEditingColumn();
					if (column == tableParameters.get(
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

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel pnlButton = new XJPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[200][200]", "[]"));

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
		pnlButton.add(btnLanjut, "cell 1 0,growx");

		try {
			loadDataInThread();
		} catch (Exception ex) {
			ExceptionHandler.handleException(this, ex);
		}

		startTimestamp = CommonUtils.getCurrentTimestamp();

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			StockFacade facade = StockFacade.getInstance();
			List<ItemStock> itemStocks = facade.search(null, null, null, false,
					null, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(itemStocks.size());

			for (int i = 0; i < itemStocks.size(); ++i) {
				ItemStock itemStock = itemStocks.get(i);
				Item item = itemStock.getItem();

				tableModel.setValueAt(i + 1, i,
						tableParameters.get(ColumnEnum.NUM).getColumnIndex());

				tableModel.setValueAt(item.getCode(), i,
						tableParameters.get(ColumnEnum.CODE).getColumnIndex());

				tableModel.setValueAt(item.getName(), i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(itemStock.getUnit(), i, tableParameters
						.get(ColumnEnum.UNIT).getColumnIndex());

				tableModel.setValueAt(Formatter.formatNumberToString(itemStock
						.getStock()), i,
						tableParameters.get(ColumnEnum.QUANTITY_SISTEM)
								.getColumnIndex());

				tableModel.setValueAt(0, i,
						tableParameters.get(ColumnEnum.QUANTITY_MANUAL)
								.getColumnIndex());

				tableModel.setValueAt(0, i,
						tableParameters.get(ColumnEnum.DEVIATION)
								.getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnLanjut.doClick();
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

	private List<StockOpnameDetail> createStockOpnameList() throws AppException {
		List<StockOpnameDetail> stockOpnameDetails = new ArrayList<>();
		StockOpnameFacade facade = StockOpnameFacade.getInstance();
		{
			Session session = HibernateUtils.openSession();
			try {
				int rowCount = table.getRowCount();

				for (int i = 0; i < rowCount; ++i) {
					String itemCode = (String) table.getValueAt(i,
							tableParameters.get(ColumnEnum.CODE)
									.getColumnIndex());

					int quantitySistem = Formatter.formatStringToNumber(
							(String) table.getValueAt(
									i,
									tableParameters.get(
											ColumnEnum.QUANTITY_SISTEM)
											.getColumnIndex())).intValue();

					int quantityManual = Formatter.formatStringToNumber(
							table.getValueAt(
									i,
									tableParameters.get(
											ColumnEnum.QUANTITY_MANUAL)
											.getColumnIndex()).toString())
							.intValue();

					int overCount = quantityManual - quantitySistem;
					if (overCount < 0) {
						overCount = 0;
					}

					int lossCount = quantitySistem - quantityManual;
					if (lossCount < 0) {
						lossCount = 0;
					}

					stockOpnameDetails.add(facade.createStockOpnameEntity(
							itemCode, quantityManual, overCount, lossCount,
							StockQueueMethod.FIFO, session));
				}
			} finally {
				session.close();
			}

			return stockOpnameDetails;
		}
	}

	private void lanjut() throws AppException {

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
					facade.save(stockOpnames, startTimestamp, stopTimestamp,
							session);
				}
				session.getTransaction().commit();
				dispose();
			} catch (RuntimeException e) {
				session.getTransaction().rollback();
				throw e;
			} finally {
				session.close();
			}
		}
	}

	private void setTotalPerRow(int row) {
		if (row < 0) {
			return;
		}

		int quantitySistem = Formatter.formatStringToNumber(
				table.getValueAt(
						row,
						tableParameters.get(ColumnEnum.QUANTITY_SISTEM)
								.getColumnIndex()).toString()).intValue();
		table.setValueAt(Formatter.formatNumberToString(quantitySistem), row,
				tableParameters.get(ColumnEnum.QUANTITY_SISTEM)
						.getColumnIndex());

		int quantityManual = Formatter.formatStringToNumber(
				table.getValueAt(
						row,
						tableParameters.get(ColumnEnum.QUANTITY_MANUAL)
								.getColumnIndex()).toString()).intValue();
		table.setValueAt(Formatter.formatNumberToString(quantityManual), row,
				tableParameters.get(ColumnEnum.QUANTITY_MANUAL)
						.getColumnIndex());

		double deviation = quantityManual - quantitySistem;
		table.setValueAt(Formatter.formatNumberToString(deviation), row,
				tableParameters.get(ColumnEnum.DEVIATION).getColumnIndex());
	}

	private enum ColumnEnum {
		NUM, CODE, NAME, UNIT, QUANTITY_SISTEM, QUANTITY_MANUAL, DEVIATION
	}
}
