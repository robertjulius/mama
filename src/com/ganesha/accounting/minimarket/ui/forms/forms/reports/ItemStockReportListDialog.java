package com.ganesha.accounting.minimarket.ui.forms.forms.reports;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.formatter.Formatter;
import com.ganesha.accounting.minimarket.facade.ItemStockReportFacade;
import com.ganesha.accounting.minimarket.facade.StockFacade;
import com.ganesha.accounting.minimarket.model.Item;
import com.ganesha.accounting.minimarket.model.ItemStock;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;

public class ItemStockReportListDialog extends XJDialog {

	private static final long serialVersionUID = 1452286313727721700L;
	private static final ComboBoxObject[] CMB_ORDER_BY;
	static {
		CMB_ORDER_BY = new ComboBoxObject[3];
		CMB_ORDER_BY[0] = new ComboBoxObject("item.code", "Kode Barang");
		CMB_ORDER_BY[1] = new ComboBoxObject("item.name", "Nama Barang");
		CMB_ORDER_BY[2] = new ComboBoxObject("stock", "Jumlah Stok");
	}

	private XJTable table;

	private XJButton btnSelesai;
	private XJButton btnKeluar;
	private JPanel pnlInformation;
	private XJLabel lblStatusStokBarang;
	private XJLabel lblPerformedDate;
	private XJLabel lblTanggal;
	private XJLabel lblUrutBerdasarkan;
	private XJComboBox cmbOrderBy;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 5, false,
				"No", XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.CODE, new XTableParameter(1, 50, false,
				"Kode", XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.NAME,
				new XTableParameter(2, 200, false, "Nama Barang",
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(3, 20,
				false, "Qty", XTableConstants.CELL_RENDERER_RIGHT,
				Integer.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(4, 50, false,
				"Satuan", XTableConstants.CELL_RENDERER_CENTER, String.class));
	}

	public ItemStockReportListDialog(Window parent) {
		super(parent);

		setTitle("Laporan Stok Barang");
		getContentPane().setLayout(
				new MigLayout("", "[600,grow]", "[grow][300,grow][]"));

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);
		table.setAutoCreateRowSorter(true);

		pnlInformation = new JPanel();
		getContentPane().add(pnlInformation, "cell 0 0");
		pnlInformation.setLayout(new MigLayout("", "[grow][grow][grow]",
				"[][][]"));

		lblStatusStokBarang = new XJLabel();
		lblStatusStokBarang.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblStatusStokBarang.setText("STATUS STOK BARANG");
		pnlInformation.add(lblStatusStokBarang, "cell 0 0 3 1");

		lblPerformedDate = new XJLabel();
		lblPerformedDate.setText("Tanggal: ");
		pnlInformation.add(lblPerformedDate, "cell 0 1");

		lblTanggal = new XJLabel();
		lblTanggal.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTanggal.setText(Formatter.formatTimestampToString(CommonUtils
				.getCurrentDate()));
		pnlInformation.add(lblTanggal, "cell 1 1 2 1");

		lblUrutBerdasarkan = new XJLabel();
		lblUrutBerdasarkan.setText("Urut Berdasarkan: ");
		pnlInformation.add(lblUrutBerdasarkan, "cell 0 2 2 1");

		cmbOrderBy = new XJComboBox(CMB_ORDER_BY);
		cmbOrderBy.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadData();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		pnlInformation.add(cmbOrderBy, "cell 2 2,growx");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, "cell 0 2,alignx center,growy");
		pnlButton.setLayout(new MigLayout("", "[200][200]", "[]"));

		btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
		pnlButton.add(btnKeluar, "cell 0 0,growx");

		btnSelesai = new XJButton();
		btnSelesai.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					preview();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		btnSelesai
				.setText("<html><center>Preview Laporan<br/>[F12]</center></html>");
		pnlButton.add(btnSelesai, "cell 1 0,growx");

		try {
			loadData();
		} catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnSelesai.doClick();
			break;
		default:
			break;
		}
	}

	private void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String[] orderBy = { (String) ((ComboBoxObject) cmbOrderBy
					.getSelectedItem()).getId() };

			StockFacade facade = StockFacade.getInstance();
			List<ItemStock> itemStocks = facade.search(null, null, false,
					orderBy, session);

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
						tableParameters.get(ColumnEnum.QUANTITY)
								.getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	private void preview() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			ComboBoxObject comboBoxObject = (ComboBoxObject) cmbOrderBy
					.getSelectedItem();

			String orderById = (String) comboBoxObject.getId();
			String orderByText = comboBoxObject.getText();

			ItemStockReportFacade facade = ItemStockReportFacade.getInstance();
			facade.previewReport(this, null, null, orderById, orderByText,
					session);
		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		NUM, CODE, NAME, QUANTITY, UNIT
	}
}