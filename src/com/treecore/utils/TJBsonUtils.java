package com.treecore.utils;

import com.treecore.http2.bson.BasicBSONObject;
import org.json.JSONException;
import org.json.JSONObject;

public class TJBsonUtils {
	public static String getDataString(String key, JSONObject jsonObject) {
		String ret = "";
		if ((jsonObject.has(key)) && (!jsonObject.isNull(key))) {
			try {
				ret = jsonObject.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static String getDataString(String key,
			BasicBSONObject basicBSONObject) {
		String ret = "";
		if ((basicBSONObject.containsField(key))
				&& (basicBSONObject.get(key) != null))
			try {
				ret = basicBSONObject.getString(key);
			} catch (Exception localException) {
			}
		return ret;
	}

	public static int getDataInt(String key, JSONObject jsonObject) {
		int ret = 0;
		if ((jsonObject.has(key)) && (!jsonObject.isNull(key)))
			try {
				ret = jsonObject.getInt(key);
			} catch (JSONException localJSONException) {
			}
		return ret;
	}

	public static int getDataInt(String key, BasicBSONObject basicBSONObject) {
		int ret = 0;
		if ((basicBSONObject.containsField(key))
				&& (basicBSONObject.get(key) != null))
			try {
				ret = basicBSONObject.getInt(key);
			} catch (Exception localException) {
			}
		return ret;
	}

	public static long getDataLong(String key, JSONObject jsonObject) {
		long ret = 0L;
		if ((jsonObject.has(key)) && (!jsonObject.isNull(key)))
			try {
				ret = jsonObject.getLong(key);
			} catch (JSONException localJSONException) {
			}
		return ret;
	}

	public static long getDataLong(String key, BasicBSONObject basicBSONObject) {
		long ret = 0L;
		if ((basicBSONObject.containsField(key))
				&& (basicBSONObject.get(key) != null))
			try {
				ret = basicBSONObject.getLong(key);
			} catch (Exception localException) {
			}
		return ret;
	}

	public static double getDataDouble(String key, JSONObject jsonObject) {
		double ret = 0.0D;
		if ((jsonObject.has(key)) && (!jsonObject.isNull(key)))
			try {
				ret = jsonObject.getDouble(key);
			} catch (JSONException localJSONException) {
			}
		return ret;
	}

	public static double getDataDouble(String key,
			BasicBSONObject basicBSONObject) {
		double ret = 0.0D;
		if ((basicBSONObject.containsField(key))
				&& (basicBSONObject.get(key) != null))
			try {
				ret = basicBSONObject.getDouble(key);
			} catch (Exception localException) {
			}
		return ret;
	}
}