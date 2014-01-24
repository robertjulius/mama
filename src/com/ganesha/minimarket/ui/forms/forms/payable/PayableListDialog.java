package com.ganesha.minimarket.ui.forms.forms.payable;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.PayableFacade;
import com.ganesha.minimarket.facade.SupplierFacade;
import com.ganesha.minimarket.model.PayableSummary;
import com.ganesha.minimarket.model.Supplier;

public class PayableListDialog extends XJDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJTable table;

	private XJButton btnDetail;
	private XJButton btnRefresh;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.CODE, new XTableParameter(0, 30, false,
				"Kode Supplier", XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.NAME, new XTableParameter(1, 100, false,
				"Nama Supplier", XTableConstants.CELL_RENDERER_LEFT,
				String.class));

		tableParameters.put(ColumnEnum.REMAINING_AMOUNT, new XTableParameter(2,
				30, false, "Sisa Pembayaran",
				XTableConstants.CELL_RENDERER_RIGHT, Double.class));
	}

	public PayableListDialog(Window parent) {
		super(parent);

		setTitle("Master Hutang");
		getContentPane().setLayout(
				new MigLayout("", "[500,grow]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnDetail.doClick();
			}
		};
		XTableUtils.initTable(table, tableParameters);
		
		JPanel pnlFilter = new JPanel();
		getContentPane().add(pnlFilter, "cell 0 0,grow");
		pnlFilter.setLayout(new MigLayout("", "[100][grow][]", "[][][grow]"));

		XJLabel lblKode = new XJLabel();
		lblKode.setText("Kode");
		pnlFilter.add(lblKode, "cell 0 0,alignx trailing");

		txtKode = new XJTextField();
		txtKode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					/*
					 * TODO Perbaiki supaya kalo pas key = alt+tab, ga usah load
					 * data
					 */
					loadData();
				} catch (AppException ex) {
					ex.printStackTrace();
				}
			}
		});
		pnlFilter.add(txtKode, "cell 1 0 2 1,growx");
		txtKode.setColumns(10);

		XJLabel lblNama = new XJLabel();
		lblNama.setText("Nama");
		pnlFilter.add(lblNama, "cell 0 1,alignx trailing");

		txtNama = new XJTextField();
		txtNama.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadData();
				} catch (AppException ex) {
					ex.printStackTrace();
				}
			}
		});
		pnlFilter.add(txtNama, "cell 1 1 2 1,growx");
		txtNama.setColumns(10);

		btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadData();
				} catch (AppException ex) {
					ex.printStackTrace();
				}
			}
		});
		btnRefresh.setMnemonic('R');
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");
		pnlFilter.add(btnRefresh, "cell 2 2,grow");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][]", "[]"));

		XJButton btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
		panel.add(btnKeluar, "cell 0 0");

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDetail();
			}
		});
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[Enter]</center></html>");
		panel.add(btnDetail, "cell 1 0");

		btnRefresh.doClick();

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

	private void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String code = txtKode.getText();
			String name = txtNama.getText();

			PayableFacade facade = PayableFacade.getInstance();

			List<Map<String, Object>> payableSummaries = facade.search(code,
					name, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(payableSummaries.size());

			for (int i = 0; i < payableSummaries.size(); ++i) {

				Map<String, Object> payableSummary = payableSummaries.get(i);

				tableModel.setValueAt(payableSummary.get("code"), i,
						tableParameters.get(ColumnEnum.CODE).getColumnIndex());

				tableModel.setValueAt(payableSummary.get("name"), i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(payableSummary.get("remainingAmount"), i,
						tableParameters.get(ColumnEnum.REMAINING_AMOUNT)
								.getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	private void showDetail() {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			String code = (String) table.getModel().getValueAt(selectedRow, 0);
			Supplier supplier = SupplierFacade.getInstance().getDetail(code,
					session);

			PayableFacade facade = PayableFacade.getInstance();
			PayableSummary payableSummary = facade.getSummary(supplier.getId(),
					session);

			PayableForm stockForm = new PayableForm(this);
			stockForm.setFormDetailValue(payableSummary, supplier);
			stockForm.setVisible(true);

			btnRefresh.doClick();
		} finally {
			session.close();
		}
	}

	private enum ColumnEnum {
		CODE, NAME, REMAINING_AMOUNT
	}
}
