package com.ganesha.accounting.minimarket.ui.forms.forms.inquiry;

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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.formatter.Formatter;
import com.ganesha.accounting.minimarket.facade.PurchaseInquiryFacade;
import com.ganesha.accounting.minimarket.facade.PurchaseReturnInquiryFacade;
import com.ganesha.accounting.minimarket.facade.SaleInquiryFacade;
import com.ganesha.accounting.minimarket.facade.SaleReturnInquiryFacade;
import com.ganesha.accounting.minimarket.facade.TransactionInquiryFacade;
import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.CommonUtils;
import com.ganesha.core.utils.GeneralConstants;
import com.ganesha.core.utils.GeneralConstants.TransactionType;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
import com.ganesha.desktop.component.XJDateChooser;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;

public class TransactionInquiryListDialog extends XJDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtNoTransaksi;
	private XJTable table;
	private XJButton btnKeluar;
	private XJDateChooser dtChBegin;
	private XJDateChooser dtChEnd;

	private TransactionInquiryFacade facade;
	private Map<TransactionType, TransactionInquiryFacade> facades = new HashMap<>();
	{
		facades.put(TransactionType.PURCHASE,
				PurchaseInquiryFacade.getInstance());
		facades.put(TransactionType.PURCHASE_RETURN,
				PurchaseReturnInquiryFacade.getInstance());
		facades.put(TransactionType.SALES, SaleInquiryFacade.getInstance());
		facades.put(TransactionType.SALES_RETURN,
				SaleReturnInquiryFacade.getInstance());
	}

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJComboBox cmbJenisTransaksi;
	{
		tableParameters.put(ColumnEnum.TRANSACTION_NUM,
				new XTableParameter(0, 200, false, "No. Transaksi",
						XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.DATE, new XTableParameter(1, 75, false,
				"Tanggal", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.CLIENT_CODE, new XTableParameter(2, 100,
				false, "Kode Klien", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.CLIENT_NAME, new XTableParameter(3, 300,
				false, "Nama Klien", XTableConstants.CELL_RENDERER_LEFT));

		tableParameters.put(ColumnEnum.AMOUNT, new XTableParameter(4, 50,
				false, "Total", XTableConstants.CELL_RENDERER_RIGHT));
	}

	public TransactionInquiryListDialog(Window parent) {
		super(parent);
		getContentPane().setLayout(
				new MigLayout("", "[1200,grow]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnKeluar.doClick();
			}
		};
		XTableUtils.initTable(table, tableParameters);

		JPanel pnlFilter = new JPanel();
		getContentPane().add(pnlFilter, "cell 0 0,alignx left,growy");
		pnlFilter.setLayout(new MigLayout("", "[grow][50][]", "[]"));

		JPanel pnlKriteria = new JPanel();
		pnlKriteria
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
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
					ExceptionHandler.handleException(ex);
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
					loadData();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		txtNoTransaksi.setColumns(10);

		JPanel pnlRangeTanggal = new JPanel();
		pnlKriteria.add(pnlRangeTanggal, "cell 0 2 2 1,growx");
		pnlRangeTanggal.setLayout(new MigLayout("", "[][grow][][grow]",
				"[][grow]"));

		XJLabel lblNama = new XJLabel();
		pnlRangeTanggal.add(lblNama, "cell 0 0 4 1");
		lblNama.setText("Tanggal Transaksi");

		XJLabel lblDari = new XJLabel();
		lblDari.setText("Dari");
		pnlRangeTanggal.add(lblDari, "cell 0 1");

		dtChBegin = new XJDateChooser();
		dtChBegin.getCalendarButton().setMnemonic('D');
		pnlRangeTanggal.add(dtChBegin, "cell 1 1,grow");

		XJLabel lblSampai = new XJLabel();
		lblSampai.setText("Sampai");
		pnlRangeTanggal.add(lblSampai, "cell 2 1");

		dtChEnd = new XJDateChooser();
		pnlRangeTanggal.add(dtChEnd, "cell 3 1,grow");

		XJButton btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadData();
				} catch (Exception ex) {
					ExceptionHandler.handleException(ex);
				}
			}
		});
		pnlFilter.add(btnRefresh, "cell 2 0,aligny bottom");
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		JPanel panel = new JPanel();
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

		cmbJenisTransaksi.setSelectedIndex(2);

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private void loadData() throws AppException, UserException {
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

	private void onComboBoxSelected() throws AppException, UserException {

		ComboBoxObject comboBoxObject = (ComboBoxObject) cmbJenisTransaksi
				.getSelectedItem();

		TransactionType transactionType = (TransactionType) comboBoxObject
				.getId();

		setTitle("Inquiry Transaksi " + comboBoxObject.getText());
		facade = facades.get(transactionType);

		loadData();
	}

	private enum ColumnEnum {
		TRANSACTION_NUM, DATE, CLIENT_CODE, CLIENT_NAME, AMOUNT
	}
}
