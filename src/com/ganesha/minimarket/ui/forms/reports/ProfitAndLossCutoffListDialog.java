package com.ganesha.minimarket.ui.forms.reports;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.accounting.facade.ProfitAndLossCutoffFacade;
import com.ganesha.accounting.model.ProfitAndLossCutoff;
import com.ganesha.core.exception.AppException;
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
import com.ganesha.minimarket.utils.PermissionConstants;

public class ProfitAndLossCutoffListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJDateChooser dtChEndDate;
	private XJDateChooser dtChBeginDate;
	private XJTable table;

	/**
	 * @wbp.nonvisual location=238,101
	 */
	private XJButton btnCutoff;
	private XJButton btnDetail;
	private XJButton btnRefresh;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJLabel lblDateEnd;
	{
		tableParameters.put(ColumnEnum.PERIOD_BEGIN, new XTableParameter(0,
				200, false, "Periode Dari", false,
				XTableConstants.CELL_RENDERER_CENTER, Date.class));

		tableParameters.put(ColumnEnum.PERIOD_END, new XTableParameter(1, 200,
				false, "Periode Hingga", false,
				XTableConstants.CELL_RENDERER_CENTER, Date.class));

		tableParameters.put(ColumnEnum.AMOUNT, new XTableParameter(2, 100,
				false, "Laba/Rugi", false, XTableConstants.CELL_RENDERER_RIGHT,
				Double.class));

		tableParameters.put(ColumnEnum.ID,
				new XTableParameter(3, 0, false, "ID", true,
						XTableConstants.CELL_RENDERER_CENTER, Integer.class));
	}

	public ProfitAndLossCutoffListDialog(Window parent) {
		super(parent);

		setTitle("Profit & Loss Cut Off");
		setPermissionCode(PermissionConstants.CUST_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[700,grow]", "[][300,grow][]"));

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
		pnlFilter.setLayout(new MigLayout("", "[200px][][200px]",
				"[][][][grow]"));

		XJLabel lblTanggalCutoff = new XJLabel();
		lblTanggalCutoff.setText("Tanggal Cut Off");
		pnlFilter.add(lblTanggalCutoff, "cell 0 0 3 1");

		dtChBeginDate = new XJDateChooser();
		dtChBeginDate.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ProfitAndLossCutoffListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(dtChBeginDate, "cell 0 1,growx");

		dtChEndDate = new XJDateChooser();
		dtChEndDate.addKeyListener(new KeyAdapter() {
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
							ProfitAndLossCutoffListDialog.this, ex);
				}
			}
		});

		lblDateEnd = new XJLabel("s/d");
		lblDateEnd.setText("-");
		pnlFilter.add(lblDateEnd, "cell 1 1,alignx trailing");
		pnlFilter.add(dtChEndDate, "cell 2 1,growx");

		btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ProfitAndLossCutoffListDialog.this, ex);
				}
			}
		});
		btnRefresh.setMnemonic('R');
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");
		pnlFilter.add(btnRefresh, "cell 0 2 3 1,alignx trailing");

		btnRefresh.doClick();

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));

		btnCutoff = new XJButton();
		btnCutoff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					performCutoff();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ProfitAndLossCutoffListDialog.this, ex);
				}
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
		panel.add(btnCutoff, "cell 1 0");
		btnCutoff
				.setText("<html><center>New Profit & Loss Cut Off<br/>[F5]</center><html>");

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showReport();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							ProfitAndLossCutoffListDialog.this, ex);
				}
			}
		});
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[Enter]</center></html>");
		panel.add(btnDetail, "cell 2 0");

		pack();
		setLocationRelativeTo(parent);
	}

	private void performCutoff() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			session.beginTransaction();
			ProfitAndLossCutoffFacade.getInstance().performProfitAndLossCutoff(
					session);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
		btnRefresh.doClick();
		table.requestFocus();
		XTableUtils.selectLastRow(table);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			Date beginDate = dtChBeginDate.getDate();
			Date endDate = dtChEndDate.getDate();

			ProfitAndLossCutoffFacade facade = ProfitAndLossCutoffFacade
					.getInstance();

			List<ProfitAndLossCutoff> profitAndLossCutoffs = facade.search(
					beginDate, endDate, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(profitAndLossCutoffs.size());

			for (int i = 0; i < profitAndLossCutoffs.size(); ++i) {
				ProfitAndLossCutoff profitAndLossCutoff = profitAndLossCutoffs
						.get(i);

				tableModel.setValueAt(profitAndLossCutoff.getId(), i,
						tableParameters.get(ColumnEnum.ID).getColumnIndex());

				ProfitAndLossCutoff previousProfitAndLossCutoff = profitAndLossCutoff
						.getPreviousProfitAndLossCutoff();
				Timestamp previousCutoffTimestamp = null;
				if (previousProfitAndLossCutoff == null) {
					previousCutoffTimestamp = null;
				} else {
					previousCutoffTimestamp = previousProfitAndLossCutoff
							.getCutoffTimestamp();
				}
				tableModel.setValueAt(Formatter
						.formatTimestampToString(previousCutoffTimestamp), i,
						tableParameters.get(ColumnEnum.PERIOD_BEGIN)
								.getColumnIndex());

				tableModel.setValueAt(Formatter
						.formatTimestampToString(profitAndLossCutoff
								.getCutoffTimestamp()), i,
						tableParameters.get(ColumnEnum.PERIOD_END)
								.getColumnIndex());

				tableModel
						.setValueAt(Formatter
								.formatNumberToString(profitAndLossCutoff
										.getLabaBersih()), i, tableParameters
								.get(ColumnEnum.AMOUNT).getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnCutoff.doClick();
			break;
		default:
			break;
		}
	}

	private void showReport() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			int profitAndLossCutoffId = (int) table.getModel().getValueAt(
					selectedRow,
					tableParameters.get(ColumnEnum.ID).getColumnIndex());

			ProfitAndLossCutoffFacade facade = ProfitAndLossCutoffFacade
					.getInstance();
			ProfitAndLossCutoff profitAndLossCutoff = facade.getDetail(
					profitAndLossCutoffId, session);

			ProfitAndLossCutoffFacade.getInstance().previewReport(
					ProfitAndLossCutoffListDialog.this, profitAndLossCutoff,
					session);

			btnRefresh.doClick();
		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		PERIOD_BEGIN, PERIOD_END, AMOUNT, ID
	}
}
