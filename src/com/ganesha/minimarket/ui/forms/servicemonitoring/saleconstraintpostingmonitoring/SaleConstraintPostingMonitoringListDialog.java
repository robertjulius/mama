package com.ganesha.minimarket.ui.forms.servicemonitoring.saleconstraintpostingmonitoring;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.constants.Enums.SaleConstraintPostingStatus;
import com.ganesha.minimarket.facade.SaleConstraintFacade;
import com.ganesha.minimarket.model.SaleConstraintHeader;
import com.ganesha.minimarket.utils.PermissionConstants;

public class SaleConstraintPostingMonitoringListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTable table;
	private XJButton btnKeluar;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJButton btnDetail;
	private XJButton btnRefresh;
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 50, false,
				"No", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.TRANSACTION_NUM, new XTableParameter(1,
				200, false, "No Transaksi", false,
				XTableConstants.CELL_RENDERER_CENTER, String.class));

		tableParameters.put(ColumnEnum.TRANSACTION_TIMESTAMP,
				new XTableParameter(2, 150, false, "Tanggal", false,
						XTableConstants.CELL_RENDERER_CENTER, Date.class));

		tableParameters.put(ColumnEnum.TOTAL, new XTableParameter(3, 100,
				false, "Total", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.POSTING_STATUS, new XTableParameter(4,
				200, false, "Status Posting", false,
				XTableConstants.CELL_RENDERER_CENTER, String.class));

		tableParameters.put(ColumnEnum.POSTING_TRIED_COUNT,
				new XTableParameter(5, 100, false, "Count", false,
						XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.ID, new XTableParameter(6, 0, false,
				"ID", true, XTableConstants.CELL_RENDERER_LEFT, Integer.class));

	}

	public SaleConstraintPostingMonitoringListDialog(Window parent) {
		super(parent);
		setTitle("Sale Constraint Posting Monitoring List");
		setPermissionCode(PermissionConstants.SALECONSTRAINT_POSTINGMONITORING_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[800,grow]", "[][300,grow][]"));

		table = new XJTable();
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,alignx center,growy");
		pnlFilter.setLayout(new MigLayout("", "[200]", "[]"));

		btnRefresh = new XJButton();
		btnRefresh.setMnemonic('R');
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							SaleConstraintPostingMonitoringListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(btnRefresh, "cell 0 0,growx");
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

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showDetail();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							SaleConstraintPostingMonitoringListDialog.this, ex);
				}
			}
		});
		btnDetail.setText("<html><center>Detail<br/>[F12]</center></html>");
		panel.add(btnDetail, "cell 1 0,growx");

		btnRefresh.doClick();

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	public void loadData() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			SaleConstraintFacade facade = SaleConstraintFacade.getInstance();
			List<SaleConstraintHeader> saleConstraintHeaders = facade
					.getAllHeaders(session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(saleConstraintHeaders.size());

			for (int i = 0; i < saleConstraintHeaders.size(); ++i) {
				SaleConstraintHeader saleConstraintHeader = saleConstraintHeaders
						.get(i);

				int numValue = i + 1;

				String transactionNumberValue = saleConstraintHeader
						.getTransactionNumber();

				Timestamp transactionTimestampValue = saleConstraintHeader
						.getTransactionTimestamp();

				BigDecimal totalAmountValue = saleConstraintHeader
						.getTotalAmount();

				SaleConstraintPostingStatus postingStatusValue = saleConstraintHeader
						.getPostingStatus();

				int postingTriedCountValue = saleConstraintHeader
						.getPostingTriedCount();

				int saleConstraintHeaderIdValue = saleConstraintHeader.getId();

				tableModel.setValueAt(numValue, i,
						tableParameters.get(ColumnEnum.NUM).getColumnIndex());

				tableModel.setValueAt(transactionNumberValue, i,
						tableParameters.get(ColumnEnum.TRANSACTION_NUM)
								.getColumnIndex());

				tableModel
						.setValueAt(
								Formatter
										.formatDateToString(transactionTimestampValue),
								i,
								tableParameters.get(
										ColumnEnum.TRANSACTION_TIMESTAMP)
										.getColumnIndex());

				tableModel.setValueAt(
						Formatter.formatNumberToString(totalAmountValue), i,
						tableParameters.get(ColumnEnum.TOTAL).getColumnIndex());

				tableModel.setValueAt(postingStatusValue, i, tableParameters
						.get(ColumnEnum.POSTING_STATUS).getColumnIndex());

				tableModel.setValueAt(postingTriedCountValue, i,
						tableParameters.get(ColumnEnum.POSTING_TRIED_COUNT)
								.getColumnIndex());

				tableModel.setValueAt(saleConstraintHeaderIdValue, i,
						tableParameters.get(ColumnEnum.ID).getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F12:
			btnDetail.doClick();
			break;
		default:
			break;
		}
	}

	private void showDetail() {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			int saleConstraintHeaderId = (int) table.getModel().getValueAt(
					selectedRow,
					tableParameters.get(ColumnEnum.ID).getColumnIndex());

			SaleConstraintFacade facade = SaleConstraintFacade.getInstance();
			SaleConstraintHeader saleConstraintHeader = facade.getHeader(
					saleConstraintHeaderId, session);

			SaleConstraintPostingMonitoringForm form = new SaleConstraintPostingMonitoringForm(
					this);
			form.setFormDetailValue(saleConstraintHeader);
			form.setVisible(true);
		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		NUM, TRANSACTION_NUM, TRANSACTION_TIMESTAMP, TOTAL, POSTING_STATUS, POSTING_TRIED_COUNT, ID
	}
}
