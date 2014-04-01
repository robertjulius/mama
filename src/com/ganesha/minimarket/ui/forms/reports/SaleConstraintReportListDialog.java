package com.ganesha.minimarket.ui.forms.reports;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
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
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDateChooser;
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
import com.ganesha.minimarket.facade.SaleConstraintFacade;
import com.ganesha.minimarket.model.SaleConstraintLog;
import com.ganesha.minimarket.utils.PermissionConstants;

public class SaleConstraintReportListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTable table;
	private XJButton btnKeluar;
	private XJDateChooser dtChBegin;
	private XJDateChooser dtChEnd;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 40, false,
				"No", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.DATE, new XTableParameter(1, 120, false,
				"Tanggal", false, XTableConstants.CELL_RENDERER_CENTER,
				Date.class));

		tableParameters.put(ColumnEnum.ITEM_CODE, new XTableParameter(2, 150,
				false, "Kode Barang", false,
				XTableConstants.CELL_RENDERER_CENTER, String.class));

		tableParameters.put(ColumnEnum.ITEM_NAME, new XTableParameter(3, 400,
				false, "Nama Barang", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(4, 40,
				false, "Qty", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.PRICE_PER_UNIT, new XTableParameter(5,
				125, false, "Harga Satuan", false,
				XTableConstants.CELL_RENDERER_RIGHT, Double.class));

		tableParameters.put(ColumnEnum.TOTAL, new XTableParameter(6, 125,
				false, "Total", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));
	}

	public SaleConstraintReportListDialog(Window parent) {
		super(parent);
		setTitle("Sale Constraint Report List");
		setPermissionCode(PermissionConstants.REPORT_CONSTRAINTSALE_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[1000,grow]", "[][300,grow][]"));

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,alignx left,growy");
		pnlFilter.setLayout(new MigLayout("", "[400][50][]", "[]"));

		XJPanel pnlRangeTanggal = new XJPanel();
		pnlFilter.add(pnlRangeTanggal, "cell 0 0,growx");
		pnlRangeTanggal.setLayout(new MigLayout("", "[][grow][][grow]",
				"[][grow]"));

		XJLabel lblTanggalTransaksi = new XJLabel();
		pnlRangeTanggal.add(lblTanggalTransaksi, "cell 0 0 4 1");
		lblTanggalTransaksi.setText("Tanggal Transaksi");

		XJLabel lblDari = new XJLabel();
		lblDari.setText("Dari");
		pnlRangeTanggal.add(lblDari, "cell 0 1");

		dtChBegin = new XJDateChooser();
		dtChBegin.setDate(CommonUtils.getCurrentDate());
		dtChBegin.getCalendarButton().setMnemonic('D');
		pnlRangeTanggal.add(dtChBegin, "cell 1 1,grow");

		XJLabel lblSampai = new XJLabel();
		lblSampai.setText("Sampai");
		pnlRangeTanggal.add(lblSampai, "cell 2 1");

		dtChEnd = new XJDateChooser();
		dtChEnd.setDate(CommonUtils.getCurrentDate());
		dtChEnd.getCalendarButton().setMnemonic('S');
		pnlRangeTanggal.add(dtChEnd, "cell 3 1,grow");

		XJButton btnRefresh = new XJButton();
		btnRefresh.setMnemonic('R');
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							SaleConstraintReportListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(btnRefresh, "cell 2 0,aligny bottom");
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[200]", "[]"));

		btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
		panel.add(btnKeluar, "cell 0 0,growx");

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	public void loadData() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			Date beginDate = CommonUtils.validateDateBegin(dtChBegin.getDate());
			Date endDate = CommonUtils.validateDateEnd(dtChEnd.getDate());

			SaleConstraintFacade facade = SaleConstraintFacade.getInstance();
			List<SaleConstraintLog> saleConstraintLogs = facade
					.searchSaleConstraintLog(beginDate, endDate, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(saleConstraintLogs.size());

			for (int i = 0; i < saleConstraintLogs.size(); ++i) {
				SaleConstraintLog saleConstraintLog = saleConstraintLogs.get(i);

				int numValue = i + 1;

				Timestamp transactionTimestampValue = saleConstraintLog
						.getTransactionTimestamp();

				String itemCodeValue = saleConstraintLog.getItemCode();

				String itemNameValue = saleConstraintLog.getItemName();

				int quantityValue = saleConstraintLog.getQuantity();

				BigDecimal pricePerUnitValue = saleConstraintLog
						.getPricePerUnit();

				BigDecimal totalAmountValue = saleConstraintLog
						.getTotalAmount();

				tableModel.setValueAt(numValue, i,
						tableParameters.get(ColumnEnum.NUM).getColumnIndex());

				tableModel
						.setValueAt(Formatter
								.formatDateToString(transactionTimestampValue),
								i, tableParameters.get(ColumnEnum.DATE)
										.getColumnIndex());

				tableModel.setValueAt(itemCodeValue, i,
						tableParameters.get(ColumnEnum.ITEM_CODE)
								.getColumnIndex());

				tableModel.setValueAt(itemNameValue, i,
						tableParameters.get(ColumnEnum.ITEM_NAME)
								.getColumnIndex());

				tableModel.setValueAt(Formatter
						.formatNumberToString(quantityValue), i,
						tableParameters.get(ColumnEnum.QUANTITY)
								.getColumnIndex());

				tableModel.setValueAt(Formatter
						.formatNumberToString(pricePerUnitValue), i,
						tableParameters.get(ColumnEnum.PRICE_PER_UNIT)
								.getColumnIndex());

				tableModel.setValueAt(
						Formatter.formatNumberToString(totalAmountValue), i,
						tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private enum ColumnEnum {
		NUM, DATE, ITEM_CODE, ITEM_NAME, QUANTITY, PRICE_PER_UNIT, TOTAL;
	}
}
