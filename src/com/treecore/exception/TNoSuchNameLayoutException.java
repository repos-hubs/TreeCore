package com.treecore.exception;

public class TNoSuchNameLayoutException extends TException {
	private static final long serialVersionUID = 2780151262388197741L;

	public TNoSuchNameLayoutException() {
	}

	public TNoSuchNameLayoutException(String detailMessage) {
		super(detailMessage);
	}
}