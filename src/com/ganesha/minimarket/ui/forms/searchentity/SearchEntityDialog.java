package com.ganesha.minimarket.ui.forms.searchentity;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ganesha.core.desktop.ExceptionHandler;
import com.ganesha.core.exception.AppException;
import com.ganesha.desktop.component.XJButton;
import com.ganesha.desktop.component.XJLabel;
import com.ganesha.desktop.component.XJPanel;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.XJTableDialog;
import com.ganesha.desktop.component.XJTextField;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.hibernate.HibernateUtils;
import com.ganesha.model.TableEntity;

public class SearchEntityDialog extends XJTableDialog {
	private static final long serialVersionUID = 1452286313727721700L;
	private XJTextField txtKode;
	private XJTextField txtNama;
	private XJTable table;

	private Class<?> entityClass;
	private Integer selectedId;
	private String selectedCode;
	private String selectedName;

	private XJButton btnPilih;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	{
		tableParameters.put(ColumnEnum.ID,
				new XTableParameter(0, 0, false, "ID", true,
						XTableConstants.CELL_RENDERER_CENTER, Integer.class));

		tableParameters.put(ColumnEnum.CODE, new XTableParameter(1, 50, false,
				"Kode", false, XTableConstants.CELL_RENDERER_CENTER,
				String.class));

		tableParameters.put(ColumnEnum.NAME,
				new XTableParameter(2, 300, false, "Nama", false,
						XTableConstants.CELL_RENDERER_LEFT, String.class));
	}

	public SearchEntityDialog(String title, Window parent, Class<?> entityClass) {
		super(parent);
		this.entityClass = entityClass;

		setPermissionRequired(false);
		setTitle(title);
		getContentPane().setLayout(
				new MigLayout("", "[500,grow]", "[][300,grow][]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				btnPilih.doClick();
			}
		};
		XTableUtils.initTable(table, tableParameters);

		XJPanel pnlFilter = new XJPanel();
		getContentPane().add(pnlFilter, "cell 0 0,grow");
		pnlFilter.setLayout(new MigLayout("", "[100][grow]", "[][]"));

		XJLabel lblKode = new XJLabel();
		lblKode.setText("Kode");
		pnlFilter.add(lblKode, "cell 0 0");

		txtKode = new XJTextField();
		txtKode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					/*
					 * TODO Perbaiki supaya kalo pas key = alt+tab, ga usah load
					 * data
					 */
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(SearchEntityDialog.this,
							ex);
				}
			}
		});
		pnlFilter.add(txtKode, "cell 1 0,growx");
		txtKode.setColumns(10);

		XJLabel lblName = new XJLabel();
		lblName.setText("Name");
		pnlFilter.add(lblName, "cell 0 1");

		txtNama = new XJTextField();
		txtNama.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					loadDataInThread();
				} catch (Exception ex) {
					ExceptionHandler.handleException(SearchEntityDialog.this,
							ex);
				}
			}
		});
		pnlFilter.add(txtNama, "cell 1 1,growx");
		txtNama.setColumns(10);

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 1,grow");

		XJPanel panel = new XJPanel();
		getContentPane().add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[]", "[]"));

		btnPilih = new XJButton();
		btnPilih.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					pilih();
				} catch (Exception ex) {
					ExceptionHandler.handleException(SearchEntityDialog.this,
							ex);
				}
			}
		});
		panel.add(btnPilih, "cell 0 0");
		btnPilih.setText("<html><center>Pilih<br/>[Enter]</center><html>");

		pack();
		setLocationRelativeTo(null);

		loadDataInThread();
	}

	public String getSelectedCode() {
		return selectedCode;
	}

	public Integer getSelectedId() {
		return selectedId;
	}

	public String getSelectedName() {
		return selectedName;
	}

	@Override
	public void loadData() throws AppException {
		Session session = HibernateUtils.openSession();
		try {
			String code = txtKode.getText();
			String name = txtNama.getText();

			Criteria criteria = session.createCriteria(entityClass);
			criteria.add(Restrictions.like("code", "%" + code + "%")
					.ignoreCase());
			criteria.add(Restrictions.like("name", "%" + name + "%")
					.ignoreCase());
			criteria.add(Restrictions.eq("disabled", false));
			criteria.add(Restrictions.eq("deleted", false));

			List<?> searchResults = criteria.list();

			XTableModel tableModel = (XTableModel) table.getModel();
			tableModel.setRowCount(searchResults.size());

			for (int i = 0; i < searchResults.size(); ++i) {
				TableEntity searchResult = (TableEntity) searchResults.get(i);

				Class<?> clazz = searchResult.getClass();
				Method getIdMethod = clazz.getDeclaredMethod("getId");
				Method getCodeMethod = clazz.getDeclaredMethod("getCode");
				Method getNameMethod = clazz.getDeclaredMethod("getName");

				int idValue = (int) getIdMethod.invoke(searchResult);
				String codeValue = (String) getCodeMethod.invoke(searchResult);
				String nameValue = (String) getNameMethod.invoke(searchResult);

				tableModel.setValueAt(idValue, i,
						tableParameters.get(ColumnEnum.ID).getColumnIndex());

				tableModel.setValueAt(codeValue, i,
						tableParameters.get(ColumnEnum.CODE).getColumnIndex());

				tableModel.setValueAt(nameValue, i,
						tableParameters.get(ColumnEnum.NAME).getColumnIndex());
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new AppException(e);
		} catch (IllegalAccessException e) {
			throw new AppException(e);
		} catch (IllegalArgumentException e) {
			throw new AppException(e);
		} catch (InvocationTargetException e) {
			throw new AppException(e);
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

	private void pilih() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			return;
		}
		selectedId = (Integer) table.getModel().getValueAt(selectedRow,
				tableParameters.get(ColumnEnum.ID).getColumnIndex());
		selectedCode = (String) table.getModel().getValueAt(selectedRow,
				tableParameters.get(ColumnEnum.CODE).getColumnIndex());
		selectedName = (String) table.getModel().getValueAt(selectedRow,
				tableParameters.get(ColumnEnum.NAME).getColumnIndex());
		dispose();
	}

	private enum ColumnEnum {
		ID, CODE, NAME
	}
}
