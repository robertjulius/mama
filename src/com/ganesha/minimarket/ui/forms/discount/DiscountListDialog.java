package com.ganesha.minimarket.ui.forms.discount;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.core.exception.UserException;
import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.XEtchedBorder;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJRadioButton;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.facade.DiscountFacade;
import com.ganesha.minimarket.model.Discount;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.utils.PermissionConstants;

public class DiscountListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtKodeBarang;
	private XJTable table;
	private XJButton btnKeluar;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private XJTextField txtNamaBarang;
	private XJRadioButton rdPromoTidakAktif;
	private XJRadioButton rdPromoAktif;
	
	private final ButtonGroup btnGroup = new ButtonGroup();
	private XJButton btnDetail;
	private XJButton btnTambah;
	{
		tableParameters.put(ColumnEnum.ITEM_ID, new XTableParameter(0, 0,
				false, "ID", true, XTableConstants.CELL_RENDERER_LEFT,
				Integer.class));

		tableParameters.put(ColumnEnum.ITEM_CODE, new XTableParameter(1, 100,
				false, "Kode Barang", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.ITEM_NAME, new XTableParameter(2, 300,
				false, "Nama Barang", false,
				XTableConstants.CELL_RENDERER_LEFT, String.class));

		tableParameters.put(ColumnEnum.QUANTITY, new XTableParameter(3, 50,
				false, "Qty", false, XTableConstants.CELL_RENDERER_CENTER,
				Integer.class));

		tableParameters.put(ColumnEnum.DISCOUNT, new XTableParameter(4, 50,
				false, "%", false, XTableConstants.CELL_RENDERER_CENTER,
				Double.class));
	}

	public DiscountListDialog(Window parent) {
		super(parent);
		setTitle("Master Diskon");
		setPermissionCode(PermissionConstants.DISCOUNT_LIST);
		getContentPane().setLayout(
				new MigLayout("", "[600,grow]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnDetail.doClick();
			}
		};
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,alignx left,growy");
		pnlFilter.setLayout(new MigLayout("", "[]", "[]"));

		XJPanel pnlKriteria = new XJPanel();
		pnlKriteria.setBorder(new XEtchedBorder());
		pnlFilter.add(pnlKriteria, "cell 0 0,grow");
		pnlKriteria.setLayout(new MigLayout("", "[][300,grow]", "[][][grow]"));

		XJLabel lblKode = new XJLabel();
		pnlKriteria.add(lblKode, "flowx,cell 0 0");
		lblKode.setText("Kode Barang");

		txtKodeBarang = new XJTextField();
		pnlKriteria.add(txtKodeBarang, "cell 1 0,growx");
		txtKodeBarang.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					/*
					 * TODO Perbaiki supaya kalo pas key = alt+tab, ga usah load
					 * data
					 */
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(DiscountListDialog.this,
							ex);
				}
			}
		});
		txtKodeBarang.setColumns(10);

		XJLabel lblNamaBarang = new XJLabel();
		lblNamaBarang.setText("Nama Barang");
		pnlKriteria.add(lblNamaBarang, "cell 0 1");

		txtNamaBarang = new XJTextField();
		pnlKriteria.add(txtNamaBarang, "cell 1 1,growx");

		XJPanel pnlRadioButton = new XJPanel();
		pnlKriteria.add(pnlRadioButton, "cell 1 2,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][][]"));

		rdPromoAktif = new XJRadioButton();
		rdPromoAktif.setSelected(true);
		rdPromoAktif.setText("Promo Aktif");
		pnlRadioButton.add(rdPromoAktif, "cell 0 0");
		btnGroup.add(rdPromoAktif);

		rdPromoTidakAktif = new XJRadioButton();
		rdPromoTidakAktif.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(DiscountListDialog.this,
							ex);
				}
			}
		});
		rdPromoTidakAktif.setText("Promo Tidak Aktif");
		pnlRadioButton.add(rdPromoTidakAktif, "cell 0 1");
		btnGroup.add(rdPromoTidakAktif);

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));

		btnKeluar = new XJButton();
		btnKeluar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnKeluar.setText("<html><center>Keluar<br/>[Esc]</center></html>");
		panel.add(btnKeluar, "cell 0 0");

		btnTambah = new XJButton();
		btnTambah.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					tambah();
				} catch (Exception ex) {
					ExceptionHandler.handleException(DiscountListDialog.this,
							ex);
				}
			}
		});
		btnTambah
				.setText("<html><center>Tambah Diskon Baru<br/>[F5]</center><html>");
		panel.add(btnTambah, "cell 1 0");

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showDetail();
				} catch (Exception ex) {
					ExceptionHandler.handleException(DiscountListDialog.this,
							ex);
				}
			}
		});
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[Enter]</center></html>");
		panel.add(btnDetail, "cell 2 0");

		try {
			loadDataInThread();
		} catch (Exception ex) {
			ExceptionHandler.handleException(this, ex);
		}

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	public void loadData() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			String itemCode = txtKodeBarang.getText();
			String itemName = txtNamaBarang.getText();
			boolean disabled = rdPromoTidakAktif.isSelected();

			List<Discount> discounts = DiscountFacade.getInstance().search(
					itemCode, itemName, disabled, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(discounts.size());

			for (int i = 0; i < discounts.size(); ++i) {
				Discount discount = discounts.get(i);
				Item item = discount.getItem();

				tableModel.setValueAt(item.getId(), i,
						tableParameters.get(ColumnEnum.ITEM_ID)
								.getColumnIndex());

				tableModel.setValueAt(item.getCode(), i,
						tableParameters.get(ColumnEnum.ITEM_CODE)
								.getColumnIndex());

				tableModel.setValueAt(item.getName(), i,
						tableParameters.get(ColumnEnum.ITEM_NAME)
								.getColumnIndex());

				tableModel.setValueAt(discount.getQuantity(), i,
						tableParameters.get(ColumnEnum.QUANTITY)
								.getColumnIndex());

				tableModel.setValueAt(Formatter.formatNumberToString(discount
						.getDiscountPercent()), i,
						tableParameters.get(ColumnEnum.DISCOUNT)
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

	private void showDetail() throws AppException, UserException {
		Session session = HibernateUtils.openSession();
		try {
			int selectedRow = table.getSelectedRow();
			if (selectedRow < 0) {
				return;
			}
			int itemId = (int) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.ITEM_ID).getColumnIndex());
			int quantity = Formatter.formatStringToNumber(
					table.getModel()
							.getValueAt(
									selectedRow,
									tableParameters.get(ColumnEnum.QUANTITY)
											.getColumnIndex()).toString())
					.intValue();

			DiscountFacade facade = DiscountFacade.getInstance();
			Discount discount = facade.getDetail(itemId, quantity, session);

			DiscountForm stockForm = new DiscountForm(this);
			stockForm.setFormDetailValue(discount);
			stockForm.setVisible(true);

			loadDataInThread();
		} finally {
			session.close();
		}
	}

	private void tambah() throws AppException, UserException {
		new DiscountForm(DiscountListDialog.this).setVisible(true);
		loadDataInThread();

		int row = table.getRowCount() - 1;
		int column = table.getSelectedColumn();
		table.requestFocus();
		table.changeSelection(row, column, false, false);
	}

	private enum ColumnEnum {
		ITEM_ID, ITEM_CODE, ITEM_NAME, QUANTITY, DISCOUNT
	}
}
