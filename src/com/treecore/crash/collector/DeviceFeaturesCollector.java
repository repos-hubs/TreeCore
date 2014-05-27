package com.treecore.crash.collector;

import android.content.Context;
import android.content.pm.PackageManager;
import com.treecore.crash.TCrash;
import com.treecore.utils.TAndroidVersionUtils;
import com.treecore.utils.log.TLog;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class DeviceFeaturesCollector {
	public static String getFeatures(Context ctx) {
		if (TAndroidVersionUtils.getAPILevel() < 5) {
			return "Data available only with API Level >= 5";
		}

		StringBuilder result = new StringBuilder();
		try {
			PackageManager pm = ctx.getPackageManager();
			Method getSystemAvailableFeatures = PackageManager.class.getMethod(
					"getSystemAvailableFeatures", null);
			Object[] features = (Object[]) getSystemAvailableFeatures.invoke(
					pm, new Object[0]);
			for (Object feature : features) {
				String featureName = (String) feature.getClass()
						.getField("name").get(feature);
				if (featureName != null) {
					result.append(featureName);
				} else {
					Method getGlEsVersion = feature.getClass().getMethod(
							"getGlEsVersion", null);
					String glEsVersion = (String) getGlEsVersion.invoke(
							feature, new Object[0]);
					result.append("glEsVersion = ");
					result.append(glEsVersion);
				}
				result.append("\n");
			}
		} catch (Throwable e) {
			TLog.w(TCrash.TAG,
					"Couldn't retrieve DeviceFeatures for "
							+ ctx.getPackageName() + e.getMessage());
			result.append("Could not retrieve data: ");
			result.append(e.getMessage());
		}

		return result.toString();
	}
}