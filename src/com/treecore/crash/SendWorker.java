package com.treecore.crash;

import android.content.Context;
import com.treecore.crash.data.CrashReportData;
import com.treecore.crash.data.CrashReportFileNameParser;
import com.treecore.crash.data.CrashReportFinder;
import com.treecore.crash.data.TCrashReportPersister;
import com.treecore.crash.exception.ReportSenderException;
import com.treecore.utils.log.TLog;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

final class SendWorker extends Thread {
	private final boolean approvePendingReports;
	private final CrashReportFileNameParser fileNameParser = new CrashReportFileNameParser();

	public SendWorker(boolean approvePendingReports) {
		this.approvePendingReports = approvePendingReports;
	}

	public void run() {
		if (this.approvePendingReports) {
			approvePendingReports();
		}
		checkAndSendReports();
	}

	private void approvePendingReports() {
		TLog.d(TCrash.TAG, "Mark all pending reports as approved.");

		CrashReportFinder reportFinder = new CrashReportFinder();
		String[] reportFileNames = reportFinder.getCrashReportFiles();

		for (String reportFileName : reportFileNames)
			if (!this.fileNameParser.isApproved(reportFileName)) {
				File reportFile = new File(TCrash.getInstance().getFilePath(),
						reportFileName);

				String newName = reportFileName.replace(".stacktrace",
						"-approved.stacktrace");

				File newFile = new File(TCrash.getInstance().getFilePath(),
						newName);
				if (!reportFile.renameTo(newFile))
					TLog.e(TCrash.TAG, "Could not rename approved report from "
							+ reportFile + " to " + newFile);
			}
	}

	private void checkAndSendReports() {
		TLog.d(TCrash.TAG, "#checkAndSendReports - start");
		CrashReportFinder reportFinder = new CrashReportFinder();
		String[] reportFiles = reportFinder.getCrashReportFiles();
		Arrays.sort(reportFiles);

		int reportsSentCount = 0;

		for (String curFileName : reportFiles)
			if (reportsSentCount > 1) {
				deleteFile(curFileName);
			} else {
				TLog.i(TCrash.TAG, "Sending file " + curFileName);
				try {
					TCrashReportPersister persister = new TCrashReportPersister();
					CrashReportData previousCrashReport = persister
							.load(curFileName);
					sendCrashReport(previousCrashReport);
					deleteFile(curFileName);
				} catch (RuntimeException e) {
					TLog.e(TCrash.TAG, "Failed to send crash reports for "
							+ curFileName + e.getMessage());
					deleteFile(curFileName);
					break;
				} catch (IOException e) {
					TLog.e(TCrash.TAG, "Failed to load crash report for "
							+ curFileName + e.getMessage());
					deleteFile(curFileName);
					break;
				} catch (ReportSenderException e) {
					TLog.e(TCrash.TAG, "Failed to send crash report for "
							+ curFileName + e.getMessage());
					break;
				}

				reportsSentCount++;
			}
		TLog.d(TCrash.TAG, "#checkAndSendReports - finish");
	}

	private void sendCrashReport(CrashReportData errorContent)
			throws ReportSenderException {
		boolean sentAtLeastOnce = false;
		for (TIReportSender sender : TCrash.getInstance().getReportSenders())
			try {
				sender.send(errorContent);

				sentAtLeastOnce = true;
			} catch (ReportSenderException e) {
				if (!sentAtLeastOnce) {
					throw e;
				}
				TLog.w(TCrash.TAG,
						"ReportSender of class "
								+ sender.getClass().getName()
								+ " failed but other senders completed their task. ACRA will not send this report again.");
			}
	}

	private void deleteFile(String fileName) {
		boolean deleted = TCrash.getInstance().getContext()
				.deleteFile(fileName);
		if (!deleted)
			TLog.w(TCrash.TAG, "Could not delete error report : " + fileName);
	}
}