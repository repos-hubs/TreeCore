package com.treecore.crash.data;

import com.treecore.crash.TCrash;

public class CrashReportFileNameParser {
	public boolean isSilent(String reportFileName) {
		return reportFileName.contains(TCrash.SILENT_SUFFIX);
	}

	public boolean isApproved(String reportFileName) {
		return (isSilent(reportFileName))
				|| (reportFileName.contains("-approved"));
	}
}