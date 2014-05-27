package com.treecore.db.exception;

public class TDBNotOpenException extends Exception {
	private static final long serialVersionUID = 1L;

	public TDBNotOpenException() {
	}

	public TDBNotOpenException(String detailMessage) {
		super(detailMessage);
	}
}