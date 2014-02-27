package com.ganesha.desktop.component;


public class ComboBoxObject {

	private Object object;
	private String text;

	public ComboBoxObject(Object object, String text) {
		this.object = object;
		this.text = text;
	}

	public Object getObject() {
		return object;
	}

	public String getText() {
		return text;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
