package com.treecore.crash.collector;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.SparseArray;
import com.treecore.crash.TCrash;
import com.treecore.utils.log.TLog;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

final class ConfigurationCollector {
	private static final String SUFFIX_MASK = "_MASK";
	private static final String FIELD_SCREENLAYOUT = "screenLayout";
	private static final String FIELD_UIMODE = "uiMode";
	private static final String FIELD_MNC = "mnc";
	private static final String FIELD_MCC = "mcc";
	private static final String PREFIX_UI_MODE = "UI_MODE_";
	private static final String PREFIX_TOUCHSCREEN = "TOUCHSCREEN_";
	private static final String PREFIX_SCREENLAYOUT = "SCREENLAYOUT_";
	private static final String PREFIX_ORIENTATION = "ORIENTATION_";
	private static final String PREFIX_NAVIGATIONHIDDEN = "NAVIGATIONHIDDEN_";
	private static final String PREFIX_NAVIGATION = "NAVIGATION_";
	private static final String PREFIX_KEYBOARDHIDDEN = "KEYBOARDHIDDEN_";
	private static final String PREFIX_KEYBOARD = "KEYBOARD_";
	private static final String PREFIX_HARDKEYBOARDHIDDEN = "HARDKEYBOARDHIDDEN_";
	private static SparseArray<String> mHardKeyboardHiddenValues = new SparseArray();
	private static SparseArray<String> mKeyboardValues = new SparseArray();
	private static SparseArray<String> mKeyboardHiddenValues = new SparseArray();
	private static SparseArray<String> mNavigationValues = new SparseArray();
	private static SparseArray<String> mNavigationHiddenValues = new SparseArray();
	private static SparseArray<String> mOrientationValues = new SparseArray();
	private static SparseArray<String> mScreenLayoutValues = new SparseArray();
	private static SparseArray<String> mTouchScreenValues = new SparseArray();
	private static SparseArray<String> mUiModeValues = new SparseArray();

	private static final HashMap<String, SparseArray<String>> mValueArrays = new HashMap();

	static {
		mValueArrays.put("HARDKEYBOARDHIDDEN_", mHardKeyboardHiddenValues);
		mValueArrays.put("KEYBOARD_", mKeyboardValues);
		mValueArrays.put("KEYBOARDHIDDEN_", mKeyboardHiddenValues);
		mValueArrays.put("NAVIGATION_", mNavigationValues);
		mValueArrays.put("NAVIGATIONHIDDEN_", mNavigationHiddenValues);
		mValueArrays.put("ORIENTATION_", mOrientationValues);
		mValueArrays.put("SCREENLAYOUT_", mScreenLayoutValues);
		mValueArrays.put("TOUCHSCREEN_", mTouchScreenValues);
		mValueArrays.put("UI_MODE_", mUiModeValues);

		for (Field f : Configuration.class.getFields())
			if ((Modifier.isStatic(f.getModifiers()))
					&& (Modifier.isFinal(f.getModifiers()))) {
				String fieldName = f.getName();
				try {
					if (fieldName.startsWith("HARDKEYBOARDHIDDEN_"))
						mHardKeyboardHiddenValues
								.put(f.getInt(null), fieldName);
					else if (fieldName.startsWith("KEYBOARD_"))
						mKeyboardValues.put(f.getInt(null), fieldName);
					else if (fieldName.startsWith("KEYBOARDHIDDEN_"))
						mKeyboardHiddenValues.put(f.getInt(null), fieldName);
					else if (fieldName.startsWith("NAVIGATION_"))
						mNavigationValues.put(f.getInt(null), fieldName);
					else if (fieldName.startsWith("NAVIGATIONHIDDEN_"))
						mNavigationHiddenValues.put(f.getInt(null), fieldName);
					else if (fieldName.startsWith("ORIENTATION_"))
						mOrientationValues.put(f.getInt(null), fieldName);
					else if (fieldName.startsWith("SCREENLAYOUT_"))
						mScreenLayoutValues.put(f.getInt(null), fieldName);
					else if (fieldName.startsWith("TOUCHSCREEN_"))
						mTouchScreenValues.put(f.getInt(null), fieldName);
					else if (fieldName.startsWith("UI_MODE_"))
						mUiModeValues.put(f.getInt(null), fieldName);
				} catch (IllegalArgumentException e) {
					TLog.w(TCrash.TAG,
							"Error while inspecting device configuration: "
									+ e.getMessage());
				} catch (IllegalAccessException e) {
					TLog.w(TCrash.TAG,
							"Error while inspecting device configuration: "
									+ e.getMessage());
				}
			}
	}

	public static String toString(Configuration conf) {
		StringBuilder result = new StringBuilder();
		for (Field f : conf.getClass().getFields()) {
			try {
				if (!Modifier.isStatic(f.getModifiers())) {
					String fieldName = f.getName();
					result.append(fieldName).append('=');
					if (f.getType().equals(Integer.TYPE))
						result.append(getFieldValueName(conf, f));
					else {
						result.append(f.get(conf).toString());
					}
					result.append('\n');
				}
			} catch (IllegalArgumentException e) {
				TLog.e(TCrash.TAG,
						"Error while inspecting device configuration: "
								+ e.getMessage());
			} catch (IllegalAccessException e) {
				TLog.e(TCrash.TAG,
						"Error while inspecting device configuration: "
								+ e.getMessage());
			}
		}
		return result.toString();
	}

	private static String getFieldValueName(Configuration conf, Field f)
			throws IllegalAccessException {
		String fieldName = f.getName();
		if ((fieldName.equals("mcc")) || (fieldName.equals("mnc")))
			return Integer.toString(f.getInt(conf));
		if (fieldName.equals("uiMode"))
			return activeFlags((SparseArray) mValueArrays.get("UI_MODE_"),
					f.getInt(conf));
		if (fieldName.equals("screenLayout")) {
			return activeFlags((SparseArray) mValueArrays.get("SCREENLAYOUT_"),
					f.getInt(conf));
		}
		SparseArray values = (SparseArray) mValueArrays.get(fieldName
				.toUpperCase() + '_');
		if (values == null) {
			return Integer.toString(f.getInt(conf));
		}

		String value = (String) values.get(f.getInt(conf));
		if (value == null) {
			return Integer.toString(f.getInt(conf));
		}
		return value;
	}

	private static String activeFlags(SparseArray<String> valueNames,
			int bitfield) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < valueNames.size(); i++) {
			int maskValue = valueNames.keyAt(i);
			if (((String) valueNames.get(maskValue)).endsWith("_MASK")) {
				int value = bitfield & maskValue;
				if (value > 0) {
					if (result.length() > 0) {
						result.append('+');
					}
					result.append((String) valueNames.get(value));
				}
			}
		}
		return result.toString();
	}

	public static String collectConfiguration(Context context) {
		try {
			Configuration crashConf = context.getResources().getConfiguration();
			return toString(crashConf);
		} catch (RuntimeException e) {
			TLog.w(TCrash.TAG, "Couldn't retrieve CrashConfiguration for : "
					+ context.getPackageName() + e.getMessage());
		}
		return "Couldn't retrieve crash config";
	}
}