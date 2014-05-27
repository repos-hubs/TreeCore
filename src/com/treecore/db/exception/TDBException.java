package com.treecore.db.exception;

public class TDBException extends Exception {
	private static final long serialVersionUID = 1L;

	public TDBException() {
	}

	public TDBException(String detailMessage) {
		super(detailMessage);
	}
}