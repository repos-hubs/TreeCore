package com.treecore.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.treecore.utils.log.TLog;

public final class TPackageUtils {
	public static String TAG = TPackageUtils.class.getSimpleName();
	private final Context context;

	public TPackageUtils(Context context) {
		this.context = context;
	}

	public boolean hasPermission(String permission) {
		PackageManager pm = this.context.getPackageManager();
		if (pm == null) {
			return false;
		}
		try {
			return pm
					.checkPermission(permission, this.context.getPackageName()) == 0;
		} catch (RuntimeException e) {
		}

		return false;
	}

	public PackageInfo getPackageInfo() {
		PackageManager pm = this.context.getPackageManager();
		if (pm == null) {
			return null;
		}
		try {
			return pm.getPackageInfo(this.context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			TLog.v(TAG, "Failed to find PackageInfo for current App : "
					+ this.context.getPackageName());
			return null;
		} catch (RuntimeException e) {
		}

		return null;
	}
}