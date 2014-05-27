package com.treecore.crash;

import com.treecore.crash.data.CrashReportData;
import com.treecore.crash.exception.ReportSenderException;

public abstract interface TIReportSender {
	public abstract void send(CrashReportData paramCrashReportData)
			throws ReportSenderException;
}