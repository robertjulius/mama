package com.ganesha.minimarket.ui.forms.sale;

import java.awt.Window;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.table.TableModel;

import net.miginfocom.swing.MigLayout;

import com.ganesha.core.utils.Formatter;
import com.ganesha.desktop.component.XJDialog;
import com.ganesha.desktop.component.XJTable;
import com.ganesha.desktop.component.xtableutils.XTableConstants;
import com.ganesha.desktop.component.xtableutils.XTableModel;
import com.ganesha.desktop.component.xtableutils.XTableParameter;
import com.ganesha.desktop.component.xtableutils.XTableUtils;
import com.ganesha.minimarket.constants.Words;
import com.ganesha.minimarket.model.Item;
import com.ganesha.minimarket.model.ItemSellPrice;

public class SelectSellPriceForm extends XJDialog {

	private XJTable table;

	private static final long serialVersionUID = 1401014426195840845L;

	private final Map<ColumnEnum, XTableParameter> tableParameters = new HashMap<>();
	private BigDecimal selectedSellPrice;

	public static BigDecimal showDialog(Window parent, Item item) {
		SelectSellPriceForm form = new SelectSellPriceForm(parent);
		form.loadSellPrices(item);
		form.setVisible(true);
		return form.selectedSellPrice;
	}

	{
		tableParameters
				.put(ColumnEnum.NUM, new XTableParameter(0, 150, false, "",
						false, XTableConstants.CELL_RENDERER_CENTER,
						Integer.class));

		tableParameters.put(ColumnEnum.PRICE, new XTableParameter(1, 150,
				false, "Harga Jual (Rp)", false,
				XTableConstants.CELL_RENDERER_RIGHT, Double.class));
	}

	private SelectSellPriceForm(Window parent) {
		super(parent);
		setTitle("Pilih Harga Jual");
		setPermissionRequired(false);

		getContentPane().setLayout(new MigLayout("", "[300]", "[200]"));

		table = new XJTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected() {
				pilih();
			}
		};
		XTableUtils.initTable(table, tableParameters);

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, "cell 0 0,growx");

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	protected void keyEventListener(int keyCode) {
		switch (keyCode) {
		default:
			break;
		}
	}

	private void loadSellPrices(Item item) {
		List<ItemSellPrice> sellPrices = item.getSellPrices();
		XTableModel tableModel = (XTableModel) table.getModel();
		tableModel.setRowCount(sellPrices.size());

		for (int i = 0; i < sellPrices.size(); ++i) {
			ItemSellPrice sellPrice = sellPrices.get(i);
			tableModel.setValueAt(
					"Harga " + Words.getSequenceWord(sellPrice.getSequence()),
					i, tableParameters.get(ColumnEnum.NUM).getColumnIndex());
			tableModel.setValueAt(Formatter.formatNumberToString(sellPrice
					.getPrimaryKey().getSellPrice()), i,
					tableParameters.get(ColumnEnum.PRICE).getColumnIndex());
		}
	}

	private void pilih() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			return;
		}

		TableModel tableModel = table.getModel();
		selectedSellPrice = BigDecimal.valueOf(Formatter.formatStringToNumber(
				tableModel.getValueAt(selectedRow,
						tableParameters.get(ColumnEnum.PRICE).getColumnIndex())
						.toString()).doubleValue());
		dispose();
	}

	private enum ColumnEnum {
		NUM, PRICE
	}
}
