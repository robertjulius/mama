package com.ganesha.desktop.component;

public class ComboBoxObject {

	private Object id;
	private String text;

	public ComboBoxObject(Object id, String text) {
		this.id = id;
		this.text = text;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ComboBoxObject)) {
			return false;
		}

		ComboBoxObject object = (ComboBoxObject) obj;

		boolean equalId = false;
		if (id == null) {
			equalId = object.getId() == null;
		} else {
			equalId = id.equals(object.getId());
		}

		boolean equalText = false;
		if (id == null) {
			equalText = object.getText() == null;
		} else {
			equalText = id.equals(object.getText());
		}

		return equalId && equalText;
	}

	public Object getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
