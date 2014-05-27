package com.treecore.crash.data;

import java.util.EnumMap;

public final class CrashReportData extends EnumMap<ReportField, String> {
	private static final long serialVersionUID = 4112578634029874840L;

	public CrashReportData() {
		super(ReportField.class);
	}

	public String getProperty(ReportField key) {
		return (String) super.get(key);
	}
}