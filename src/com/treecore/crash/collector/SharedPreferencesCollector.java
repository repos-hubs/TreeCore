package com.treecore.crash.collector;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

final class SharedPreferencesCollector {
	public static String collect(Context context) {
		StringBuilder result = new StringBuilder();
		Map<String, Object> shrdPrefs = new TreeMap();
		shrdPrefs.put("default",
				PreferenceManager.getDefaultSharedPreferences(context));

		for (String prefsId : shrdPrefs.keySet()) {
			result.append(prefsId).append("\n");
			SharedPreferences prefs = (SharedPreferences) shrdPrefs
					.get(prefsId);
			if (prefs != null) {
				Map<String, ?> kv = prefs.getAll();
				if ((kv != null) && (kv.size() > 0)) {
					for (String key : kv.keySet()) {
						if (!filteredKey(key)) {
							if (kv.get(key) != null)
								result.append(key).append("=")
										.append(kv.get(key).toString())
										.append("\n");
							else
								result.append(key).append("=").append("null\n");
						}
					}
				} else
					result.append("empty\n");
			} else {
				result.append("null\n");
			}
			result.append("\n");
		}

		return result.toString();
	}

	private static boolean filteredKey(String key) {
		return false;
	}
}