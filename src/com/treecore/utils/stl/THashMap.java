package com.treecore.utils.stl;

import java.util.Date;
import java.util.HashMap;

public class THashMap<T> extends HashMap<String, T> {
	private static final long serialVersionUID = 1L;

	public T put(String key, T value) {
		if (hasValue(value)) {
			return super.put(key, value);
		}
		return null;
	}

	public boolean hasValue(Object value) {
		if (value != null) {
			return true;
		}
		return false;
	}

	public T get(Object key) {
		return super.get(key);
	}

	public String getString(String key) {
		return String.valueOf(get(key));
	}

	public int getInt(String key) {
		return Integer.valueOf(getString(key)).intValue();
	}

	public boolean getBoolean(String key) {
		return Boolean.valueOf(getString(key)).booleanValue();
	}

	public double getDouble(String key) {
		return Double.valueOf(getString(key)).doubleValue();
	}

	public float getFloat(String key) {
		return Float.valueOf(getString(key)).floatValue();
	}

	public long getLong(String key) {
		return Long.valueOf(getString(key)).longValue();
	}

	public Date getDate(String key) {
		return new Date(getString(key));
	}

	public char getChar(String key) {
		return getString(key).trim().toCharArray()[0];
	}

	public byte[] getBlob(String key) {
		return getString(key).getBytes();
	}

	public short getShort(String key) {
		return Short.valueOf(getString(key)).shortValue();
	}
}