package com.treecore.utils;

import android.database.Cursor;

public class TCursorUtils {
	public static String getString(Cursor cursor, String columnName) {
		try {
			int index = cursor.getColumnIndex(columnName);
			if (index == -1)
				return "";
			String result = cursor.getString(index);
			if (result == null)
				return "";
			return result;
		} catch (Exception localException) {
		}
		return "";
	}

	public static int getInt(Cursor cursor, String columnName) {
		try {
			int index = cursor.getColumnIndex(columnName);
			if (index == -1)
				return -1;
			return cursor.getInt(index);
		} catch (Exception localException) {
		}
		return -1;
	}

	public static long getLong(Cursor cursor, String columnName) {
		try {
			int index = cursor.getColumnIndex(columnName);
			if (index == -1)
				return 0L;
			return cursor.getLong(index);
		} catch (Exception localException) {
		}
		return 0L;
	}

	public static float getFloat(Cursor cursor, String columnName) {
		try {
			int index = cursor.getColumnIndex(columnName);
			if (index == -1)
				return -1.0F;
			return cursor.getFloat(index);
		} catch (Exception localException) {
		}
		return -1.0F;
	}
}