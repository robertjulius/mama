package com.ganesha.desktop.component.xtableutils;

import javax.swing.DefaultCellEditor;
import javax.swing.event.CellEditorListener;

import com.ganesha.desktop.component.XJTextField;

public class XCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 2241213811524087261L;

	public XCellEditor() {
		super(new XJTextField());
	}

	@Override
	public void addCellEditorListener(CellEditorListener listener) {
		super.addCellEditorListener(listener);
	}
}