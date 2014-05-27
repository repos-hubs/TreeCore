package com.treecore.crash;

import android.content.Context;
import android.text.TextUtils;
import com.treecore.TIGlobalInterface;
import com.treecore.crash.data.ReportField;
import com.treecore.utils.log.TLog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TCrash implements TIGlobalInterface {
	public static final String TAG = TCrash.class.getSimpleName();
	public static final String REPORTFILE_EXTENSION = ".stacktrace";
	public static final String APPROVED_SUFFIX = "-approved";
	public static final String EXTRA_REPORT_FILE_NAME = "REPORT_FILE_NAME";
	public static final int DEFAULT_BUFFER_SIZE_IN_BYTES = 8192;
	public static final String SILENT_SUFFIX = "-" + ReportField.IS_SILENT;
	private Context mContext;
	private boolean mEnabled = true;
	private TCrashErrorReporter mTAcraErrorReporter;
	private String mFilePath = "";
	private final List<TIReportSender> mReportSenders = new ArrayList();
	private static TCrash mThis;
	private TICrashListener mCrashListener;
	private String mAppCrashContentString = "";

	ReportField[] mReportFields = { ReportField.REPORT_ID,
			ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
			ReportField.PACKAGE_NAME, ReportField.FILE_PATH,
			ReportField.PHONE_MODEL, ReportField.ANDROID_VERSION,
			ReportField.BUILD, ReportField.BRAND, ReportField.PRODUCT,
			ReportField.TOTAL_MEM_SIZE, ReportField.AVAILABLE_MEM_SIZE,
			ReportField.CUSTOM_DATA, ReportField.STACK_TRACE,
			ReportField.INITIAL_CONFIGURATION, ReportField.CRASH_CONFIGURATION,
			ReportField.DISPLAY, ReportField.USER_COMMENT,
			ReportField.USER_APP_START_DATE, ReportField.USER_CRASH_DATE,
			ReportField.DUMPSYS_MEMINFO, ReportField.DROPBOX,
			ReportField.LOGCAT, ReportField.EVENTSLOG, ReportField.RADIOLOG,
			ReportField.IS_SILENT, ReportField.DEVICE_ID,
			ReportField.INSTALLATION_ID, ReportField.USER_EMAIL,
			ReportField.DEVICE_FEATURES, ReportField.ENVIRONMENT,
			ReportField.SETTINGS_SYSTEM, ReportField.SETTINGS_SECURE,
			ReportField.SHARED_PREFERENCES, ReportField.APPLICATION_LOG,
			ReportField.MEDIA_CODEC_LIST, ReportField.THREAD_DETAILS };

	public static TCrash getInstance() {
		if (mThis == null)
			mThis = new TCrash();
		return mThis;
	}

	public void initConfig(Context context) {
		this.mContext = context;
		initConfig();
	}

	public void initConfig() {
		try {
			this.mTAcraErrorReporter = new TCrashErrorReporter();
		} catch (Exception e) {
			TLog.w(TAG, "Error : " + e.getMessage());
		}
	}

	public void release() {
	}

	public TCrashErrorReporter getErrorReporter() {
		if (this.mTAcraErrorReporter == null) {
			throw new IllegalStateException(
					"Cannot access ErrorReporter before ACRA#init");
		}
		return this.mTAcraErrorReporter;
	}

	public void setFilePath(String path) {
		this.mFilePath = path;
	}

	public String getFilePath() {
		if (TextUtils.isEmpty(this.mFilePath))
			return this.mContext.getFilesDir().getAbsolutePath();
		return this.mFilePath;
	}

	public Context getContext() {
		return this.mContext;
	}

	public List<TIReportSender> getReportSenders() {
		return this.mReportSenders;
	}

	public void addReportSender(TIReportSender sender) {
		this.mReportSenders.add(sender);
	}

	public void removeReportSender(TIReportSender sender) {
		this.mReportSenders.remove(sender);
	}

	public void removeReportSenders(Class<?> senderClass) {
		if (TIReportSender.class.isAssignableFrom(senderClass))
			for (TIReportSender sender : this.mReportSenders)
				if (senderClass.isInstance(sender))
					this.mReportSenders.remove(sender);
	}

	public void removeAllReportSenders() {
		this.mReportSenders.clear();
	}

	public void setReportSender(TIReportSender sender) {
		removeAllReportSenders();
		addReportSender(sender);
	}

	public void setEnable(boolean enable) {
		this.mEnabled = enable;
	}

	public boolean isEnable() {
		return this.mEnabled;
	}

	public void setICrashListener(TICrashListener listener) {
		this.mCrashListener = listener;
	}

	public TICrashListener getICarshListener() {
		return this.mCrashListener;
	}

	public void setCrashContentShow(String content) {
		this.mAppCrashContentString = content;
	}

	public String getCrashContentShow() {
		return this.mAppCrashContentString;
	}

	public ReportField[] getReportFields() {
		return this.mReportFields;
	}
}