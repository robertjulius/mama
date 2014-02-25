package com.ganesha.minimarket.ui.forms.reports;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

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
import com.ganesha.minimarket.facade.StockOpnameFacade;
import com.ganesha.minimarket.model.StockOpnameDetail;
import com.ganesha.minimarket.model.StockOpnameHeader;
import com.ganesha.minimarket.utils.PermissionConstants;

public class StockOpnameReportListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTable table;
	private XJButton btnKeluar;
	private XJDateChooser dtChBegin;
	private XJDateChooser dtChEnd;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJButton btnPreview;
	{
		tableParameters.put(ColumnEnum.NUM, new XTableParameter(0, 10, false,
				"No", false, XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.DATE, new XTableParameter(1, 200, false,
				"Tanggal", false, XTableConstants.CELL_RENDERER_LEFT,
				Date.class));
	}

	public StockOpnameReportListDialog(Window parent) {
		super(parent);
		getContentPane().setLayout(
				new MigLayout("", "[300,grow]", "[][300,grow][]"));

		setTitle("Laporan Stock Opname");
		setPermissionCode(PermissionConstants.REPORT_STOCKOPNAME_LIST);

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				try {
					btnPreview.doClick();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							StockOpnameReportListDialog.this, ex);
				}
			}
		};
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,alignx left,growy");
		pnlFilter.setLayout(new MigLayout("", "[][][]", "[][][]"));

		XJLabel lblTanggal = new XJLabel();
		pnlFilter.add(lblTanggal, "cell 0 0 2 1");
		lblTanggal.setText("Tanggal Stock Opname");

		XJLabel lblDari = new XJLabel();
		pnlFilter.add(lblDari, "cell 0 1");
		lblDari.setText("Dari");

		dtChBegin = new XJDateChooser();
		pnlFilter.add(dtChBegin, "cell 1 1,growx");
		dtChBegin.setDate(CommonUtils.getCurrentDate());
		dtChBegin.getCalendarButton().setMnemonic('D');

		XJButton btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							StockOpnameReportListDialog.this, ex);
				}
			}
		});
		pnlFilter.add(btnRefresh, "cell 2 1 1 2,aligny center");
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");

		XJLabel lblSampai = new XJLabel();
		pnlFilter.add(lblSampai, "cell 0 2");
		lblSampai.setText("Sampai");

		dtChEnd = new XJDateChooser();
		pnlFilter.add(dtChEnd, "cell 1 2,growx");
		dtChEnd.setDate(CommonUtils.getCurrentDate());
		dtChEnd.getCalendarButton().setMnemonic('S');

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][]", "[]"));

		btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
		panel.add(btnKeluar, "cell 0 0");

		btnPreview = new XJButton();
		btnPreview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					preview();
				} catch (Exception ex) {
					ExceptionHandler.handleException(
							StockOpnameReportListDialog.this, ex);
				}
			}
		});
		btnPreview
				.setText("<html><center>Preview Laporan<br/>[F12]</center></html>");
		panel.add(btnPreview, "cell 1 0");

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void loadData() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			Date beginDate = CommonUtils.validateDateBegin(dtChBegin.getDate());
			Date endDate = CommonUtils.validateDateEnd(dtChEnd.getDate());

			List<StockOpnameHeader> searchResults = StockOpnameFacade
					.getInstance().search(beginDate, endDate, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(searchResults.size());

			for (int i = 0; i < searchResults.size(); ++i) {
				StockOpnameHeader searchResult = searchResults.get(i);

				tableModel.setValueAt(i + 1, i,
						tableParameters.get(ColumnEnum.NUM).getColumnIndex());

				tableModel.setValueAt(Formatter
						.formatTimestampToString(searchResult
								.getLastUpdatedTimestamp()), i, tableParameters
						.get(ColumnEnum.DATE).getColumnIndex());
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

	private void preview() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			Date lastUpdatedTimestamp = Formatter
					.formatStringToTimestamp((String) table.getModel()
							.getValueAt(
									selectedRow,
									tableParameters.get(ColumnEnum.DATE)
											.getColumnIndex()));

			Criteria criteria = session.createCriteria(StockOpnameDetail.class);
			criteria.createAlias("stockOpnameHeader", "stockOpnameHeader");

			criteria.add(Restrictions.eq(
					"stockOpnameHeader.lastUpdatedTimestamp",
					lastUpdatedTimestamp));

			@SuppressWarnings("unchecked")
			List<StockOpnameDetail> stockOpnameDetails = criteria.list();

			if (stockOpnameDetails != null & !stockOpnameDetails.isEmpty()) {

				Timestamp beginTimestamp = stockOpnameDetails.get(0)
						.getStockOpnameHeader().getPerformedBeginTimestamp();

				Timestamp endTimestamp = stockOpnameDetails.get(0)
						.getStockOpnameHeader().getPerformedEndTimestamp();

				StockOpnameFacade facade = StockOpnameFacade.getInstance();
				JasperPrint jasperPrint = facade.prepareJasper(
						stockOpnameDetails, beginTimestamp, endTimestamp);

				ReportViewerDialog.viewReport(this, "Laporan Stock Opname",
						new JRViewer(jasperPrint));
			}
		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		NUM, DATE
	}
}
