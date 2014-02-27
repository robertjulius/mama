package com.ganesha.minimarket.ui.forms.reports;

import java.awt.Font;
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

import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.ItemStockReportFacade;
import com.ganesha.minimarket.utils.PermissionConstants;

public class ItemStockReportListDialog extends XJTableDialog {

	private static final long serialVersionUID = 1452286313727721700L;
	private static final ComboBoxObject[] CMB_ORDER_BY;
	static {
		List<ComboBoxObject> comboBoxObjects = new ArrayList<>();
		comboBoxObjects.add(new ComboBoxObject("code", "Kode Barang"));
		comboBoxObjects.add(new ComboBoxObject("name", "Nama Barang"));
		comboBoxObjects.add(new ComboBoxObject("quantity", "Jumlah Stok"));

		CMB_ORDER_BY = new ComboBoxObject[comboBoxObjects.size()];
		comboBoxObjects.toArray(CMB_ORDER_BY);
	}

	private XJTable table;

	private XJButton btnSelesai;
	private XJButton btnKeluar;
	private XJPanel pnlInformation;
	private XJLabel lblStatusStokBarang;
	private XJLabel lblPerformedDate;
	private XJLabel lblTanggal;
	private XJLabel lblUrutBerdasarkan;
	private XJComboBox cmbOrderBy;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 5, false,
				"No", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.CODE,
				new XTableParameter(1, 50, false, "Kode", false,
						XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.NAME, new XTableParameter(2, 200, false,
				"Nama Barang", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(3, 20,
				false, "Qty", false, XTableConstants.CELL_RENDERER_RIGHT,
				Integer.class));

		tableParameters.put(ColumnEnum.UNIT, new XTableParameter(4, 50, false,
				"Satuan", false, XTableConstants.CELL_RENDERER_CENTER,
				String.class));
	}

	public ItemStockReportListDialog(Window parent) {
		super(parent);

		setTitle("Laporan Stok Barang");
		setPermissionCode(PermissionConstants.REPORT_STOCK_LIST);

		getContentPane().setLayout(
				new MigLayout("", "[600,grow]", "[grow][300,grow][]"));

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);

		pnlInformation = new XJPanel();
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
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ItemStockReportListDialog.this, ex);
				}
			}
		});
		pnlInformation.add(cmbOrderBy, "cell 2 2,growx");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel pnlButton = new XJPanel();
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
					ExceptionHandler.handleException(
							ItemStockReportListDialog.this, ex);
				}
			}
		});
		btnSelesai
				.setText("<html><center>Preview Laporan<br/>[F12]</center></html>");
		pnlButton.add(btnSelesai, "cell 1 0,growx");

		try {
			loadDataInThread();
		} catch (Exception ex) {
			ExceptionHandler.handleException(this, ex);
		}

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String orderBy = (String) ((ComboBoxObject) cmbOrderBy
					.getSelectedItem()).getObject();

			ItemStockReportFacade facade = ItemStockReportFacade.getInstance();
			List<Map<String, Object>> itemMaps = facade
					.search(orderBy, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(itemMaps.size());

			for (int i = 0; i < itemMaps.size(); ++i) {
				Map<String, Object> itemMap = itemMaps.get(i);

				String code = (String) itemMap.get("code");
				String name = (String) itemMap.get("name");
				String unit = (String) itemMap.get("unit");
				Integer quantity = Formatter.formatStringToNumber(
						itemMap.get("quantity").toString()).intValue();

				tableModel.setValueAt(i + 1, i,
						tableParameters.get(ColumnEnum.NUM).getColumnIndex());

				tableModel.setValueAt(code, i,
						tableParameters.get(ColumnEnum.CODE).getColumnIndex());

				tableModel.setValueAt(name, i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(unit, i,
						tableParameters.get(ColumnEnum.UNIT).getColumnIndex());

				tableModel.setValueAt(Formatter.formatNumberToString(quantity),
						i, tableParameters.get(ColumnEnum.QUANTITY)
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
			btnSelesai.doClick();
			break;
		default:
			break;
		}
	}

	private void preview() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			ComboBoxObject comboBoxObject = (ComboBoxObject) cmbOrderBy
					.getSelectedItem();

			String orderById = (String) comboBoxObject.getObject();
			String orderByText = comboBoxObject.getText();

			ItemStockReportFacade facade = ItemStockReportFacade.getInstance();
			facade.previewReport(this, orderById, orderByText, session);
		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		NUM, CODE, NAME, QUANTITY, UNIT
	}
}
