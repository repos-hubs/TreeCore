package com.treecore.crash.collector;

import java.lang.reflect.Field;

import android.content.Context;
import android.provider.Settings;

import com.treecore.crash.TCrash;
import com.treecore.utils.log.TLog;

final class SettingsCollector {
	public static String collectSystemSettings(Context ctx) {
		StringBuilder result = new StringBuilder();
		Field[] keys = Settings.System.class.getFields();
		for (Field key : keys) {
			if ((!key.isAnnotationPresent(Deprecated.class))
					&& (key.getType() == String.class)) {
				try {
					Object value = Settings.System.getString(
							ctx.getContentResolver(), (String) key.get(null));
					if (value != null)
						result.append(key.getName()).append("=").append(value)
								.append("\n");
				} catch (IllegalArgumentException e) {
					TLog.w(TCrash.TAG, "Error : " + e.getMessage());
				} catch (IllegalAccessException e) {
					TLog.w(TCrash.TAG, "Error : " + e.getMessage());
				}
			}
		}

		return result.toString();
	}

	public static String collectSecureSettings(Context ctx) {
		StringBuilder result = new StringBuilder();
		Field[] keys = Settings.Secure.class.getFields();
		for (Field key : keys) {
			if ((!key.isAnnotationPresent(Deprecated.class))
					&& (key.getType() == String.class) && (isAuthorized(key))) {
				try {
					Object value = Settings.Secure.getString(
							ctx.getContentResolver(), (String) key.get(null));
					if (value != null)
						result.append(key.getName()).append("=").append(value)
								.append("\n");
				} catch (IllegalArgumentException e) {
					TLog.w(TCrash.TAG, "Error : " + e.getMessage());
				} catch (IllegalAccessException e) {
					TLog.w(TCrash.TAG, "Error : " + e.getMessage());
				}
			}
		}

		return result.toString();
	}

	private static boolean isAuthorized(Field key) {
		if ((key == null) || (key.getName().startsWith("WIFI_AP"))) {
			return false;
		}
		return true;
	}
}