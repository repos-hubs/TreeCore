package com.treecore.exception;

public class TNoSuchCommandException extends TException {
	private static final long serialVersionUID = 1L;

	public TNoSuchCommandException() {
	}

	public TNoSuchCommandException(String detailMessage) {
		super(detailMessage);
	}
}