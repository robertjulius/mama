package com.ganesha.desktop.component;

import java.awt.Font;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class XJComboBox extends JComboBox<ComboBoxObject> implements
		XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;

	public XJComboBox() {
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));
	}

	public XJComboBox(ComboBoxObject[] comboBoxObjects) {
		this();
		setModel(new DefaultComboBoxModel<ComboBoxObject>(comboBoxObjects));
	}

	@Override
	public void setSelectedItem(Object object) {
		ComboBoxModel<ComboBoxObject> model = getModel();
		int size = model.getSize();
		for (int i = 0; i < size; ++i) {
			ComboBoxObject comboBoxObject = model.getElementAt(i);
			if (object.equals(comboBoxObject.getId())) {
				super.setSelectedItem(comboBoxObject);
				return;
			}
		}
		super.setSelectedItem(object);
	}
}
