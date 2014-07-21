package com.ganesha.prepaid.ui.forms;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;

import com.ganesha.core.exception.AppException;
import com.ganesha.coreapps.constants.Enums.ActionType;
import com.ganesha.desktop.component.ComboBoxObject;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJComboBox;
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
import com.ganesha.desktop.exeptions.ExceptionHandler;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.minimarket.utils.PermissionConstants;
import com.ganesha.prepaid.facade.MultiFacade;
import com.ganesha.prepaid.facade.ProviderFacade;
import com.ganesha.prepaid.model.MultiMap;
import com.ganesha.prepaid.model.Provider;

public class MultiMapListDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJComboBox cmbProvider;
	private XJTextField txtName;
	private XJTable table;

	/**
	 * @wbp.nonvisual location=238,101
	 */
	private final ButtonGroup btnGroup = new ButtonGroup();
	private XJButton btnTambah;
	private XJButton btnDetail;
	private XJButton btnRefresh;
	private XJRadioButton rdMultiMapAktif;
	private XJRadioButton rdMultiMapTidakAktif;
	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.NAME, new XTableParameter(0, 150, false,
				"Nama", false, XTableConstants.CELL_RENDERER_CENTER,
				String.class));

		tableParameters.put(ColumnEnum.ITEM, new XTableParameter(1, 200, false,
				"Barang", false, XTableConstants.CELL_RENDERER_CENTER,
				String.class));

		tableParameters.put(ColumnEnum.ID, new XTableParameter(2, 0, false,
				"ID", true, XTableConstants.CELL_RENDERER_LEFT, Integer.class));
	}

	public MultiMapListDialog(Window parent) {
		super(parent);

		setTitle("Master Tipe Voucher");
		setPermissionCode(PermissionConstants.VOUCHER_TYPE_LIST);
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

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,grow");
		pnlFilter.setLayout(new MigLayout("", "[100][grow][]", "[][][grow]"));

		XJLabel lblName = new XJLabel();
		lblName.setText("Nama Voucher");
		pnlFilter.add(lblName, "cell 0 0");

		txtName = new XJTextField();
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(MultiMapListDialog.this,
							ex);
				}
			}
		});
		pnlFilter.add(txtName, "cell 1 0 2 1,growx");
		txtName.setColumns(10);

		XJLabel lblProvider = new XJLabel();
		lblProvider.setText("Provider");
		pnlFilter.add(lblProvider, "cell 0 1");

		cmbProvider = new XJComboBox();
		cmbProvider.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(MultiMapListDialog.this,
							ex);
				}
			}
		});
		pnlFilter.add(cmbProvider, "cell 1 1 2 1,growx");

		XJPanel pnlRadioButton = new XJPanel();
		pnlFilter.add(pnlRadioButton, "cell 1 2,grow");
		pnlRadioButton.setLayout(new MigLayout("", "[]", "[][]"));

		rdMultiMapAktif = new XJRadioButton();
		rdMultiMapAktif.setText("Tipe Voucher Aktif");
		pnlRadioButton.add(rdMultiMapAktif, "cell 0 0");
		rdMultiMapAktif.setSelected(true);
		btnGroup.add(rdMultiMapAktif);

		rdMultiMapTidakAktif = new XJRadioButton();
		rdMultiMapTidakAktif.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(MultiMapListDialog.this,
							ex);
				}
			}
		});
		rdMultiMapTidakAktif.setText("Tipe Voucher Tidak Aktif");
		pnlRadioButton.add(rdMultiMapTidakAktif, "cell 0 1");
		btnGroup.add(rdMultiMapTidakAktif);

		btnRefresh = new XJButton();
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(MultiMapListDialog.this,
							ex);
				}
			}
		});
		btnRefresh.setMnemonic('R');
		btnRefresh.setText("<html><center>Refresh<br/>[Alt+R]</center></html>");
		pnlFilter.add(btnRefresh, "cell 2 2,grow");

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));

		btnTambah = new XJButton();
		btnTambah.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tambah();
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
		panel.add(btnTambah, "cell 1 0");
		btnTambah
				.setText("<html><center>Tambah Tipe Voucher Baru<br/>[F5]</center><html>");

		btnDetail = new XJButton();
		btnDetail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDetail();
			}
		});
		btnDetail
				.setText("<html><center>Lihat Detail<br/>[Enter]</center></html>");
		panel.add(btnDetail, "cell 2 0");

		loadComboProvider();
		btnRefresh.doClick();

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String name = txtName.getText();

			boolean disabled = rdMultiMapTidakAktif.isSelected();

			MultiFacade facade = MultiFacade.getInstance();

			List<MultiMap> multiMaps = facade.search(name, disabled, session);

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(multiMaps.size());

			for (int i = 0; i < multiMaps.size(); ++i) {
				MultiMap multiMap = multiMaps.get(i);

				tableModel.setValueAt(multiMap.getName(), i, tableParameters
						.get(ColumnEnum.NAME).getColumnIndex());

				tableModel.setValueAt(multiMap.getItem().getName(), i,
						tableParameters.get(ColumnEnum.ITEM).getColumnIndex());

				tableModel.setValueAt(multiMap.getId(), i,
						tableParameters.get(ColumnEnum.ID).getColumnIndex());
			}
		} finally {
			session.close();
		}
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_F5:
			btnTambah.doClick();
			break;
		default:
			break;
		}
	}

	private void loadComboProvider() {
		Session session = HibernateUtils.openSession();
		try {
			List<ComboBoxObject> comboBoxObjects = new ArrayList<ComboBoxObject>();
			List<Provider> providers = ProviderFacade.getInstance().getAll(
					session);
			for (Provider provider : providers) {
				ComboBoxObject comboBoxObject = new ComboBoxObject(provider,
						provider.getName());
				comboBoxObjects.add(comboBoxObject);
			}
			comboBoxObjects.add(0, new ComboBoxObject(new Provider(), null));
			cmbProvider.setModel(new DefaultComboBoxModel<ComboBoxObject>(
					comboBoxObjects.toArray(new ComboBoxObject[] {})));
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
			int id = (int) table.getModel().getValueAt(selectedRow,
					tableParameters.get(ColumnEnum.ID).getColumnIndex());

			MultiFacade facade = MultiFacade.getInstance();
			MultiMap multiMap = facade.getDetail(id, session);

			MultiMapForm multiMapForm = new MultiMapForm(this,
					ActionType.UPDATE);
			multiMapForm.setFormDetailValue(multiMap);
			multiMapForm.setVisible(true);

			btnRefresh.doClick();
		} finally {
			session.close();
		}
	}

	private void tambah() {
		new MultiMapForm(MultiMapListDialog.this, ActionType.CREATE)
				.setVisible(true);
		btnRefresh.doClick();

		int row = table.getRowCount() - 1;
		int column = table.getSelectedColumn();
		table.requestFocus();
		table.changeSelection(row, column, false, false);
	}

	private enum ColumnEnum {
		NAME, ITEM, ID
	}
}
