package com.ganesha.desktop.component;

public class ComboBoxObject {

	private Object id;
	private String text;

	public ComboBoxObject(Object id, String text) {
		this.id = id;
		this.text = text;
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
