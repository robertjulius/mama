package com.ganesha.coreapps.exception;

import com.ganesha.core.exception.GaneshaException;
import com.ganesha.coreapps.constants.Enums.ActionType;

public class ActionTypeNotSupported extends GaneshaException {

	private static final long serialVersionUID = -9098238364164875570L;

	public ActionTypeNotSupported(ActionType actionType) {
		super("Action type " + actionType + " is not supported for this method");
	}
}
