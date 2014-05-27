package com.treecore.crash;

import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.text.format.Time;
import com.treecore.crash.collector.CrashReportDataFactory;
import com.treecore.crash.data.CrashReportData;
import com.treecore.crash.data.CrashReportFileNameParser;
import com.treecore.crash.data.CrashReportFinder;
import com.treecore.crash.data.ReportField;
import com.treecore.crash.data.TCrashReportPersister;
import com.treecore.utils.TToastUtils;
import com.treecore.utils.log.TLog;
import java.io.File;
import java.util.Arrays;

public class TCrashErrorReporter implements Thread.UncaughtExceptionHandler {
	private final CrashReportDataFactory mCrashReportDataFactory;
	private final CrashReportFileNameParser mFileNameParser = new CrashReportFileNameParser();
	private final Thread.UncaughtExceptionHandler mDfltExceptionHandler;
	private Thread mBrokenThread;
	private Throwable mUnhandledThrowable;
	private static boolean mToastWaitEnded = true;

	public TCrashErrorReporter() {
		this.mCrashReportDataFactory = new CrashReportDataFactory(TCrash
				.getInstance().getContext());

		this.mDfltExceptionHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		checkReportsOnApplicationStart();
	}

	public void uncaughtException(Thread t, Throwable e) {
		try {
			if (!TCrash.getInstance().isEnable()) {
				if (this.mDfltExceptionHandler != null) {
					TLog.e(TCrash.TAG,
							"ACRA is disabled for "
									+ TCrash.getInstance().getContext()
											.getPackageName()
									+ " - forwarding uncaught Exception on to default ExceptionHandler");
					this.mDfltExceptionHandler.uncaughtException(t, e);
				} else {
					TLog.e(TCrash.TAG, "ACRA is disabled for "
							+ TCrash.getInstance().getContext()
									.getPackageName()
							+ " - no default ExceptionHandler");
				}
				return;
			}

			this.mBrokenThread = t;
			this.mUnhandledThrowable = e;

			TLog.e(TCrash.TAG, "ACRA caught a " + e.getClass().getSimpleName()
					+ " exception for "
					+ TCrash.getInstance().getContext().getPackageName()
					+ ". Building report.");

			handleException(e, true);
		} catch (Throwable fatality) {
			if (this.mDfltExceptionHandler != null)
				this.mDfltExceptionHandler.uncaughtException(t, e);
		}
	}

	private void endApplication() {
		Process.killProcess(Process.myPid());
		System.exit(10);
	}

	public SendWorker startSendingReports(boolean approveReportsFirst) {
		SendWorker worker = new SendWorker(approveReportsFirst);
		worker.start();
		return worker;
	}

	public void deletePendingReports() {
		deletePendingReports(true, true, 0);
	}

	private void checkReportsOnApplicationStart() {
		deletePendingNonApprovedReports(true);

		CrashReportFinder reportFinder = new CrashReportFinder();
		String[] filesList = reportFinder.getCrashReportFiles();

		if ((filesList != null) && (filesList.length > 0)) {
			filesList = reportFinder.getCrashReportFiles();

			if (TCrash.getInstance().getICarshListener() != null)
				TCrash.getInstance().getICarshListener()
						.onAppCrash(getLatestNonSilentReport(filesList));
		}
	}

	public void deletePendingNonApprovedReports(boolean keepOne) {
		int nbReportsToKeep = keepOne ? 1 : 0;
		deletePendingReports(false, true, nbReportsToKeep);
	}

	private void handleException(Throwable e, final boolean endApplication) {
		if (!TCrash.getInstance().isEnable()) {
			return;
		}

		if (e == null) {
			e = new Exception("Report requested by developer");
		}

		CrashReportData crashReportData = this.mCrashReportDataFactory
				.createCrashData(e, this.mBrokenThread);

		if (!TextUtils.isEmpty(TCrash.getInstance().getCrashContentShow())) {
			new Thread() {
				public void run() {
					Looper.prepare();
					TToastUtils.makeText(TCrash.getInstance()
							.getCrashContentShow(), 1);
					Looper.loop();
				}
			}.start();
		}

		final String reportFileName = getReportFileName(crashReportData);
		saveCrashReportFile(reportFileName, crashReportData);

		SendWorker sender = null;

		mToastWaitEnded = false;
		new Thread() {
			public void run() {
				Time beforeWait = new Time();
				Time currentTime = new Time();
				beforeWait.setToNow();
				long beforeWaitInMillis = beforeWait.toMillis(false);
				long elapsedTimeInMillis = 0L;
				while (elapsedTimeInMillis < 3000L) {
					try {
						Thread.sleep(3000L);
					} catch (InterruptedException e1) {
						TLog.d(TCrash.TAG,
								"Interrupted while waiting for Toast to end."
										+ e1.getMessage());
					}
					currentTime.setToNow();
					elapsedTimeInMillis = currentTime.toMillis(false)
							- beforeWaitInMillis;
				}
				TCrashErrorReporter.mToastWaitEnded = true;
			}
		}.start();

		final SendWorker worker = sender;

		new Thread() {
			public void run() {
				TLog.d(TCrash.TAG, "Waiting for Toast + worker...");
				while ((!TCrashErrorReporter.mToastWaitEnded)
						|| ((worker != null) && (worker.isAlive()))) {
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e1) {
						TLog.e(TCrash.TAG, "Error : " + e1.getMessage());
					}
				}

				if (TCrash.getInstance().getICarshListener() != null) {
					TCrash.getInstance().getICarshListener()
							.onAppCrash(reportFileName);
				}

				if (endApplication)
					TCrashErrorReporter.this.endApplication();
			}
		}.start();
	}

	private String getReportFileName(CrashReportData crashData) {
		Time now = new Time();
		now.setToNow();
		long timestamp = now.toMillis(false);
		String isSilent = crashData.getProperty(ReportField.IS_SILENT);
		return timestamp + ".stacktrace";
	}

	private void saveCrashReportFile(String fileName, CrashReportData crashData) {
		try {
			TLog.d(TCrash.TAG, "Writing crash report file " + fileName + ".");
			TCrashReportPersister persister = new TCrashReportPersister();
			persister.store(crashData, fileName);
		} catch (Exception e) {
			TLog.e(TCrash.TAG,
					"An error occurred while writing the report file..."
							+ e.getMessage());
		}
	}

	private String getLatestNonSilentReport(String[] filesList) {
		if ((filesList != null) && (filesList.length > 0)) {
			for (int i = filesList.length - 1; i >= 0; i--) {
				if (!this.mFileNameParser.isSilent(filesList[i])) {
					return filesList[i];
				}
			}

			return filesList[(filesList.length - 1)];
		}
		return null;
	}

	private void deletePendingReports(boolean deleteApprovedReports,
			boolean deleteNonApprovedReports, int nbOfLatestToKeep) {
		CrashReportFinder reportFinder = new CrashReportFinder();
		String[] filesList = reportFinder.getCrashReportFiles();
		Arrays.sort(filesList);
		if (filesList != null)
			for (int iFile = 0; iFile < filesList.length - nbOfLatestToKeep; iFile++) {
				String fileName = filesList[iFile];
				boolean isReportApproved = this.mFileNameParser
						.isApproved(fileName);
				if (((isReportApproved) && (deleteApprovedReports))
						|| ((!isReportApproved) && (deleteNonApprovedReports))) {
					File fileToDelete = new File(TCrash.getInstance()
							.getFilePath(), fileName);
					if (!fileToDelete.delete())
						TLog.e(TCrash.TAG, "Could not delete report : "
								+ fileToDelete);
				}
			}
	}

	private boolean containsOnlySilentOrApprovedReports(String[] reportFileNames) {
		for (String reportFileName : reportFileNames) {
			if (!this.mFileNameParser.isApproved(reportFileName)) {
				return false;
			}
		}
		return true;
	}

	@Deprecated
	public void addCustomData(String key, String value) {
		this.mCrashReportDataFactory.putCustomData(key, value);
	}

	public String putCustomData(String key, String value) {
		return this.mCrashReportDataFactory.putCustomData(key, value);
	}

	public String removeCustomData(String key) {
		return this.mCrashReportDataFactory.removeCustomData(key);
	}

	public String getCustomData(String key) {
		return this.mCrashReportDataFactory.getCustomData(key);
	}
}