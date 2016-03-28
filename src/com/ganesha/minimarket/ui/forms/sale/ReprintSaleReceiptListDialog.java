package com.ganesha.minimarket.ui.forms.sale;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;

import com.ganesha.core.SystemSetting;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.DateUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.coreapps.constants.Loggers;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDateChooser;
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
import com.ganesha.desktop.exeptions.UserExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.GlobalFacade;
import com.ganesha.minimarket.facade.SaleFacade;
import com.ganesha.minimarket.facade.SaleReportFacade;
import com.ganesha.minimarket.model.SaleDetail;
import com.ganesha.minimarket.model.SaleHeader;
import com.ganesha.minimarket.utils.PermissionConstants;

import net.miginfocom.swing.MigLayout;

public class ReprintSaleReceiptListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtNoTransaksi;
	private XJTable table;
	private XJButton btnKeluar;
	private XJDateChooser dtChBegin;
	private XJDateChooser dtChEnd;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJButton btnReprint;
	{
		tableParameters.put(ColumnEnum.TRANSACTION_NUM, new XTableParameter(0,
				200, false, "No. Transaksi", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.DATE, new XTableParameter(1, 75, false,
				"Tanggal", false, XTableConstants.CELL_RENDERER_LEFT,
				Date.class));

		tableParameters.put(ColumnEnum.CUSTOMER_CODE, new XTableParameter(2, 100,
				false, "Kode Customer", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.CUSTOMER_NAME, new XTableParameter(3, 300,
				false, "Nama Customer", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.AMOUNT, new XTableParameter(4, 50,
				false, "Total", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));
	}

	public ReprintSaleReceiptListDialog(Window parent) {
		super(parent);
		setTitle("Cetak Ulang Struk Penjualan");
		setPermissionCode(PermissionConstants.REPORT_TRX_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnReprint.doClick();
			}
		};
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,alignx left,growy");
		pnlFilter.setLayout(new MigLayout("", "[grow][50][]", "[]"));

		XJPanel pnlKriteria = new XJPanel();
		pnlKriteria.setBorder(new XEtchedBorder());
		pnlFilter.add(pnlKriteria, "cell 0 0,grow");
		pnlKriteria.setLayout(new MigLayout("", "[][300,grow]", "[][]"));

		XJLabel lblNoTransaksi = new XJLabel();
		pnlKriteria.add(lblNoTransaksi, "flowx,cell 0 0");
		lblNoTransaksi.setText("No Transaksi");

		txtNoTransaksi = new XJTextField();
		pnlKriteria.add(txtNoTransaksi, "cell 1 0,growx");
		txtNoTransaksi.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					/*
					 * TODO Perbaiki supaya kalo pas key = alt+tab, ga usah load
					 * data
					 */
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ReprintSaleReceiptListDialog.this, ex);
				}
			}
		});
		txtNoTransaksi.setColumns(10);

		XJPanel pnlRangeTanggal = new XJPanel();
		pnlKriteria.add(pnlRangeTanggal, "cell 0 1 2 1,growx");
		pnlRangeTanggal.setLayout(new MigLayout("", "[][grow][][grow]",
				"[][grow]"));

		XJLabel lblTanggalTransaksi = new XJLabel();
		pnlRangeTanggal.add(lblTanggalTransaksi, "cell 0 0 4 1");
		lblTanggalTransaksi.setText("Tanggal Transaksi");

		XJLabel lblDari = new XJLabel();
		lblDari.setText("Dari");
		pnlRangeTanggal.add(lblDari, "cell 0 1");

		dtChBegin = new XJDateChooser();
		dtChBegin.setDate(DateUtils.getCurrent(Date.class));
		dtChBegin.getCalendarButton().setMnemonic('D');
		pnlRangeTanggal.add(dtChBegin, "cell 1 1,grow");

		XJLabel lblSampai = new XJLabel();
		lblSampai.setText("Sampai");
		pnlRangeTanggal.add(lblSampai, "cell 2 1");

		dtChEnd = new XJDateChooser();
		dtChEnd.setDate(DateUtils.getCurrent(Date.class));
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
							ReprintSaleReceiptListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(btnRefresh, "cell 2 0,aligny bottom");
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[200][200]", "[]"));

		btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
		panel.add(btnKeluar, "cell 0 0,growx");

		btnReprint = new XJButton();
		btnReprint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					reprint();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ReprintSaleReceiptListDialog.this, ex);
				}
			}
		});
		btnReprint
				.setText("<html><center>Cetak Ulang Struk<br/>[Enter]</center></html>");
		panel.add(btnReprint, "cell 1 0,growx");

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	public void loadData() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			String transactionNumber = txtNoTransaksi.getText();
			Date beginDate = DateUtils.validateDateBegin(dtChBegin.getDate());
			Date endDate = DateUtils.validateDateEnd(dtChEnd.getDate());

			List<Map<String, Object>> searchResults = SaleReportFacade.getInstance()
					.searchTransaction(transactionNumber, beginDate, endDate, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(searchResults.size());

			for (int i = 0; i < searchResults.size(); ++i) {
				Map<String, Object> searchResult = searchResults.get(i);

				String transactionNumberValue = (String) searchResult
						.get("transactionNumber");
				Timestamp transactionTimestampValue = (Timestamp) searchResult
						.get("transactionTimestamp");
				String clientCodeValue = (String) searchResult
						.get("clientCode");
				String clientNameValue = (String) searchResult
						.get("clientName");
				BigDecimal totalAmountValue = (BigDecimal) searchResult
						.get("totalAmount");

				tableModel.setValueAt(transactionNumberValue, i,
						tableParameters.get(ColumnEnum.TRANSACTION_NUM)
								.getColumnIndex());

				tableModel
						.setValueAt(Formatter
								.formatDateToString(transactionTimestampValue),
								i, tableParameters.get(ColumnEnum.DATE)
										.getColumnIndex());

				tableModel.setValueAt(clientCodeValue, i,
						tableParameters.get(ColumnEnum.CUSTOMER_CODE)
								.getColumnIndex());

				tableModel.setValueAt(clientNameValue, i,
						tableParameters.get(ColumnEnum.CUSTOMER_NAME)
								.getColumnIndex());

				tableModel
						.setValueAt(Formatter
								.formatNumberToString(totalAmountValue), i,
								tableParameters.get(ColumnEnum.AMOUNT)
										.getColumnIndex());
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

	private void reprint() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			String transactionNumber = (String) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.TRANSACTION_NUM).getColumnIndex());

			int transactionId = GlobalFacade.getInstance().getIdByCode("transaction_number", transactionNumber, "id",
					"sale_headers", session);
			SaleFacade saleFacade = SaleFacade.getInstance();
			SaleHeader saleHeader = saleFacade.getHeader(transactionId, session);
			List<SaleDetail> saleDetails = saleFacade.getDetails(saleHeader.getId(), session);

			printReceipt(saleFacade, saleHeader, saleDetails);

		} finally {
			session.close();
		}
	}
	
	private void printReceipt(SaleFacade facade, SaleHeader saleHeader, List<SaleDetail> saleDetails)
			throws UserException, AppException {

		Boolean statusOn = (Boolean) SystemSetting.get(GeneralConstants.SYSTEM_SETTING_PRINTER_RECEIPT_STATUS);
		if (statusOn == null) {
			String message = "Status Receipt Printer belum disetting. Lakukang setting terlebih dahulu melalui menu Setting > Receipt Printer Status";
			UserExceptionHandler.handleException(this, message, null);
			return;
		}
		if (!statusOn) {
			String message = "Printer stuck tidak dilakukan karena settingnya diatur menjadi OFF";
			UserExceptionHandler.handleException(this, message, null);
			return;
		}

		try {
			facade.cetakReceipt(saleHeader, saleDetails);
			LoggerFactory.getLogger(Loggers.SALE).debug("Receipt printer is worked fine.");
		} catch (Exception e) {
			LoggerFactory.getLogger(Loggers.APPLICATION).error(e.getMessage(), e);
			String message = "Transaksi ini sudah selesai, tapi struk tidak dapat dicetak karena ada masalah dengan printer.<br/><br/>"
					+ "Ucapkan mohon maaf atas tidak adanya struk ini kepada Customer.<br/><br/>"
					+ "Selanjutnya coba periksa semua koneksi ke printer dan pastikan printer dalam keadaan menyala.<br/>"
					+ "Bila masalah ini masih terulang lagi, laporkan ke Robert.";
			UserExceptionHandler.handleException(this, message, null);
		}
	}

	private enum ColumnEnum {
		TRANSACTION_NUM, DATE, CUSTOMER_CODE, CUSTOMER_NAME, AMOUNT
	}
}
