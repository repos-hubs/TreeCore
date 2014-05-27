package com.treecore.crash.collector;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.text.format.Time;
import com.treecore.crash.TCrash;
import com.treecore.crash.data.CrashReportData;
import com.treecore.crash.data.ReportField;
import com.treecore.filepath.TFilePathManager;
import com.treecore.utils.TAndroidVersionUtils;
import com.treecore.utils.TFileInstallationUtils;
import com.treecore.utils.TPackageUtils;
import com.treecore.utils.TScreenUtils;
import com.treecore.utils.TStorageUtils;
import com.treecore.utils.log.TLog;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrashReportDataFactory {
	private final Context context;
	private final Map<String, String> customParameters = new HashMap();
	private final Time appStartDate = new Time();
	private final String initialConfiguration;

	public CrashReportDataFactory(Context context) {
		this.context = context;
		this.appStartDate.setToNow();
		this.initialConfiguration = ConfigurationCollector
				.collectConfiguration(context);
	}

	public String putCustomData(String key, String value) {
		return (String) this.customParameters.put(key, value);
	}

	public String removeCustomData(String key) {
		return (String) this.customParameters.remove(key);
	}

	public String getCustomData(String key) {
		return (String) this.customParameters.get(key);
	}

	public CrashReportData createCrashData(Throwable th, Thread brokenThread) {
		CrashReportData crashReportData = new CrashReportData();
		try {
			crashReportData.put(ReportField.STACK_TRACE, getStackTrace(th));
			crashReportData.put(ReportField.USER_APP_START_DATE,
					this.appStartDate.format3339(false));

			crashReportData.put(ReportField.REPORT_ID, UUID.randomUUID()
					.toString());

			crashReportData.put(ReportField.INSTALLATION_ID,
					TFileInstallationUtils.id(this.context));

			crashReportData.put(ReportField.INITIAL_CONFIGURATION,
					this.initialConfiguration);

			crashReportData.put(ReportField.CRASH_CONFIGURATION,
					ConfigurationCollector.collectConfiguration(this.context));

			crashReportData.put(ReportField.DUMPSYS_MEMINFO,
					DumpSysCollector.collectMemInfo());

			crashReportData.put(ReportField.PACKAGE_NAME,
					this.context.getPackageName());

			crashReportData.put(ReportField.BUILD,
					ReflectionCollector.collectConstants(Build.class));

			crashReportData.put(ReportField.PHONE_MODEL, Build.MODEL);

			crashReportData.put(ReportField.ANDROID_VERSION,
					Build.VERSION.RELEASE);

			crashReportData.put(ReportField.BRAND, Build.BRAND);

			crashReportData.put(ReportField.PRODUCT, Build.PRODUCT);

			crashReportData.put(ReportField.TOTAL_MEM_SIZE,
					Long.toString(TStorageUtils.getTotalInternalMemorySize()));
			crashReportData.put(ReportField.AVAILABLE_MEM_SIZE, Long
					.toString(TStorageUtils.getAvailableInternalMemorySize()));

			crashReportData.put(ReportField.FILE_PATH, TFilePathManager
					.getInstance().getAppPath());

			crashReportData.put(ReportField.DISPLAY,
					TScreenUtils.getDisplayDetails(this.context));

			Time curDate = new Time();
			curDate.setToNow();
			crashReportData.put(ReportField.USER_CRASH_DATE,
					curDate.format3339(false));

			crashReportData.put(ReportField.CUSTOM_DATA,
					createCustomInfoString());

			crashReportData.put(ReportField.DEVICE_FEATURES,
					DeviceFeaturesCollector.getFeatures(this.context));

			crashReportData.put(ReportField.ENVIRONMENT, ReflectionCollector
					.collectStaticGettersResults(Environment.class));

			crashReportData.put(ReportField.SETTINGS_SYSTEM,
					SettingsCollector.collectSystemSettings(this.context));

			crashReportData.put(ReportField.SETTINGS_SECURE,
					SettingsCollector.collectSecureSettings(this.context));

			crashReportData.put(ReportField.SHARED_PREFERENCES,
					SharedPreferencesCollector.collect(this.context));

			TPackageUtils pm = new TPackageUtils(this.context);

			PackageInfo pi = pm.getPackageInfo();
			if (pi != null) {
				crashReportData.put(ReportField.APP_VERSION_CODE,
						Integer.toString(pi.versionCode));
				crashReportData.put(ReportField.APP_VERSION_NAME,
						pi.versionName != null ? pi.versionName : "not set");
			} else {
				crashReportData.put(ReportField.APP_VERSION_NAME,
						"Package info unavailable");
			}

			if (pm.hasPermission("android.permission.READ_PHONE_STATE")) {
				String deviceId = TStorageUtils.getDeviceId(this.context);
				if (deviceId != null) {
					crashReportData.put(ReportField.DEVICE_ID, deviceId);
				}

			}

			if ((pm.hasPermission("android.permission.READ_LOGS"))
					|| (TAndroidVersionUtils.getAPILevel() >= 16)) {
				TLog.i(TCrash.TAG,
						"READ_LOGS granted! ACRA can include LogCat and DropBox data.");
			} else {
				TLog.i(TCrash.TAG,
						"READ_LOGS not allowed. ACRA will not include LogCat and DropBox data.");
			}

			crashReportData.put(ReportField.THREAD_DETAILS,
					ThreadCollector.collect(brokenThread));
		} catch (RuntimeException e) {
			TLog.e(TCrash.TAG,
					"Error while retrieving crash data" + e.getMessage());
		}

		return crashReportData;
	}

	private String createCustomInfoString() {
		StringBuilder customInfo = new StringBuilder();
		for (String currentKey : this.customParameters.keySet()) {
			String currentVal = (String) this.customParameters.get(currentKey);
			customInfo.append(currentKey);
			customInfo.append(" = ");
			customInfo.append(currentVal);
			customInfo.append("\n");
		}
		return customInfo.toString();
	}

	private String getStackTrace(Throwable th) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);

		Throwable cause = th;
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		String stacktraceAsString = result.toString();
		printWriter.close();

		return stacktraceAsString;
	}
}