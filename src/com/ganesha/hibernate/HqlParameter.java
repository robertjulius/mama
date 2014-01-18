package com.ganesha.hibernate;

import java.util.HashMap;

import org.hibernate.Query;

public class HqlParameter extends HashMap<String, Object> {
	private static final long serialVersionUID = -8586038737007986168L;

	private Query query;

	public HqlParameter(Query query) {
		this.query = query;
	}

	public void validate() {
		for (String key : query.getNamedParameters()) {
			query.setParameter(key, get(key));
		}
	}
}
