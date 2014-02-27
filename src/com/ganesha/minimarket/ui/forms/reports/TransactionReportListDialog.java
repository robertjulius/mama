package com.ganesha.minimarket.ui.forms.reports;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.facade.ExpenseFacade;
import com.ganesha.accounting.facade.RevenueFacade;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.Formatter;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
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
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.constants.Enums.TransactionType;
import com.ganesha.minimarket.facade.PurchaseReportFacade;
import com.ganesha.minimarket.facade.PurchaseReturnReportFacade;
import com.ganesha.minimarket.facade.SaleReportFacade;
import com.ganesha.minimarket.facade.SaleReturnReportFacade;
import com.ganesha.minimarket.facade.TransactionReportFacade;
import com.ganesha.minimarket.utils.PermissionConstants;

public class TransactionReportListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtNoTransaksi;
	private XJTable table;
	private XJButton btnKeluar;
	private XJDateChooser dtChBegin;
	private XJDateChooser dtChEnd;

	private TransactionReportFacade facade;
	private Map<TransactionType, TransactionReportFacade> facades = new HashMap<>();
	{
		facades.put(TransactionType.PURCHASE,
				PurchaseReportFacade.getInstance());
		facades.put(TransactionType.PURCHASE_RETURN,
				PurchaseReturnReportFacade.getInstance());
		facades.put(TransactionType.SALES, SaleReportFacade.getInstance());
		facades.put(TransactionType.SALES_RETURN,
				SaleReturnReportFacade.getInstance());
		facades.put(TransactionType.EXPENSES, ExpenseFacade.getInstance());
		facades.put(TransactionType.REVENUES, RevenueFacade.getInstance());
	}

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJComboBox cmbJenisTransaksi;
	private XJButton btnPreview;
	{
		tableParameters.put(ColumnEnum.TRANSACTION_NUM, new XTableParameter(0,
				200, false, "No. Transaksi", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.DATE, new XTableParameter(1, 75, false,
				"Tanggal", false, XTableConstants.CELL_RENDERER_LEFT,
				Date.class));

		tableParameters.put(ColumnEnum.CLIENT_CODE, new XTableParameter(2, 100,
				false, "Kode Klien", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.CLIENT_NAME, new XTableParameter(3, 300,
				false, "Nama Klien", false, XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.AMOUNT, new XTableParameter(4, 50,
				false, "Total", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));
	}

	public TransactionReportListDialog(Window parent) {
		super(parent);
		setPermissionCode(PermissionConstants.REPORT_TRX_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				try {
					facade.showDetail(null);
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							TransactionReportListDialog.this, ex);
				}
			}
		};
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,alignx left,growy");
		pnlFilter.setLayout(new MigLayout("", "[grow][50][]", "[]"));

		XJPanel pnlKriteria = new XJPanel();
		pnlKriteria.setBorder(new XEtchedBorder());
		pnlFilter.add(pnlKriteria, "cell 0 0,grow");
		pnlKriteria.setLayout(new MigLayout("", "[][300,grow]", "[][][]"));

		XJLabel lblJenisTransaksi = new XJLabel();
		lblJenisTransaksi.setText("Jenis Transaksi");
		pnlKriteria.add(lblJenisTransaksi, "cell 0 0,alignx trailing");

		cmbJenisTransaksi = new XJComboBox(GeneralConstants.CMB_BOX_TRX_TYPES);
		cmbJenisTransaksi.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					onComboBoxSelected();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							TransactionReportListDialog.this, ex);
				}
			}
		});
		pnlKriteria.add(cmbJenisTransaksi, "cell 1 0,growx");

		XJLabel lblNoTransaksi = new XJLabel();
		pnlKriteria.add(lblNoTransaksi, "flowx,cell 0 1");
		lblNoTransaksi.setText("No Transaksi");

		txtNoTransaksi = new XJTextField();
		pnlKriteria.add(txtNoTransaksi, "cell 1 1,growx");
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
							TransactionReportListDialog.this, ex);
				}
			}
		});
		txtNoTransaksi.setColumns(10);

		XJPanel pnlRangeTanggal = new XJPanel();
		pnlKriteria.add(pnlRangeTanggal, "cell 0 2 2 1,growx");
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
							TransactionReportListDialog.this, ex);
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

		btnPreview = new XJButton();
		btnPreview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					preview();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							TransactionReportListDialog.this, ex);
				}
			}
		});
		btnPreview
				.setText("<html><center>Preview Laporan<br/>[F12]</center></html>");
		panel.add(btnPreview, "cell 1 0,growx");

		cmbJenisTransaksi.setSelectedIndex(2);

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void loadData() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			String transactionNumber = txtNoTransaksi.getText();
			Date beginDate = CommonUtils.validateDateBegin(dtChBegin.getDate());
			Date endDate = CommonUtils.validateDateEnd(dtChEnd.getDate());

			List<Map<String, Object>> searchResults = facade.searchTransaction(
					transactionNumber, beginDate, endDate, session);

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
						tableParameters.get(ColumnEnum.CLIENT_CODE)
								.getColumnIndex());

				tableModel.setValueAt(clientNameValue, i,
						tableParameters.get(ColumnEnum.CLIENT_NAME)
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
		case KeyEvent.VK_F12:
			btnPreview.doClick();
			break;
		default:
			break;
		}
	}

	private void onComboBoxSelected() throws AppException, UserException {

		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbJenisTransaksi
				.getSelectedItem();

		TransactionType transactionType = (TransactionType) comboBoxObject
				.getObject();

		setTitle("Laporan Transaksi " + comboBoxObject.getText());
		facade = facades.get(transactionType);

		loadDataInThread();
	}

	private void preview() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			String transactionNumber = txtNoTransaksi.getText();
			Date beginDate = CommonUtils.validateDateBegin(dtChBegin.getDate());
			Date endDate = CommonUtils.validateDateEnd(dtChEnd.getDate());
			facade.previewReport(this, transactionNumber, beginDate, endDate,
					session);
		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		TRANSACTION_NUM, DATE, CLIENT_CODE, CLIENT_NAME, AMOUNT
	}
}
