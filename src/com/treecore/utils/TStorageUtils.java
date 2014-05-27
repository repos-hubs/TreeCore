package com.treecore.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.treecore.utils.log.TLog;
import java.io.File;

public class TStorageUtils {
	public static String TAG = TStorageUtils.class.getSimpleName();

	public static boolean isSdCardWrittenable() {
		if (Environment.getExternalStorageState().equals("mounted")) {
			return true;
		}
		return false;
	}

	public static boolean checkAvailableStorage() {
		Log.d(TAG, "checkAvailableStorage E");

		if (getAvailableStorage() < 10485760L) {
			return false;
		}

		return true;
	}

	public static boolean isSDCardPresent() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	public static long getAvailableStorage() {
		String storageDirectory = null;
		storageDirectory = Environment.getExternalStorageDirectory().toString();

		Log.v(TAG, "getAvailableStorage. storageDirectory : "
				+ storageDirectory);
		try {
			StatFs stat = new StatFs(storageDirectory);
			long avaliableSize = stat.getAvailableBlocks()
					* stat.getBlockSize();
			Log.v(TAG, "getAvailableStorage. avaliableSize : " + avaliableSize);
			return avaliableSize;
		} catch (RuntimeException ex) {
			Log.e(TAG, "getAvailableStorage - exception. return 0");
		}
		return 0L;
	}

	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	public static String getDeviceId(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService("phone");
			return tm.getDeviceId();
		} catch (RuntimeException e) {
			TLog.w(TAG,
					"Couldn't retrieve DeviceId for : "
							+ context.getPackageName() + e.getMessage());
		}
		return null;
	}
}