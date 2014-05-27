package com.treecore.exception;

public class TException extends Exception {
	private static final long serialVersionUID = 1L;

	public TException() {
	}

	public TException(String detailMessage) {
		super(detailMessage);
	}
}