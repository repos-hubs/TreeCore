package com.treecore.utils.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.treecore.TIGlobalInterface;
import com.treecore.utils.TReflectUtils;
import java.lang.reflect.Field;

public class TPreferenceConfig implements TIConfig, TIGlobalInterface {
	private Context mContext;
	private SharedPreferences.Editor edit = null;
	private SharedPreferences mSharedPreferences;
	private String filename = TPreferenceConfig.class.getSimpleName();
	private Boolean isLoad = Boolean.valueOf(false);
	private static TPreferenceConfig mThisConfig;

	public void initConfig(Context context) {
		this.mContext = context;
		loadConfig();
	}

	public void initConfig() {
	}

	public void release() {
	}

	public static TPreferenceConfig getInstance() {
		if (mThisConfig == null) {
			mThisConfig = new TPreferenceConfig();
		}
		return mThisConfig;
	}

	private void loadConfig() {
		try {
			this.mSharedPreferences = this.mContext.getSharedPreferences(
					this.filename, 2);
			this.edit = this.mSharedPreferences.edit();
			this.isLoad = Boolean.valueOf(true);
		} catch (Exception e) {
			this.isLoad = Boolean.valueOf(false);
		}
	}

	public Boolean isLoadConfig() {
		return this.isLoad;
	}

	public void close() {
	}

	public void setString(String key, String value) {
		this.edit.putString(key, value);
		this.edit.commit();
	}

	public void setInt(String key, int value) {
		this.edit.putInt(key, value);
		this.edit.commit();
	}

	public void setBoolean(String key, Boolean value) {
		this.edit.putBoolean(key, value.booleanValue());
		this.edit.commit();
	}

	public void setByte(String key, byte[] value) {
		setString(key, String.valueOf(value));
	}

	public void setShort(String key, short value) {
		setString(key, String.valueOf(value));
	}

	public void setLong(String key, long value) {
		this.edit.putLong(key, value);
		this.edit.commit();
	}

	public void setFloat(String key, float value) {
		this.edit.putFloat(key, value);
		this.edit.commit();
	}

	public void setDouble(String key, double value) {
		setString(key, String.valueOf(value));
	}

	public void setString(int resID, String value) {
		setString(this.mContext.getString(resID), value);
	}

	public void setInt(int resID, int value) {
		setInt(this.mContext.getString(resID), value);
	}

	public void setBoolean(int resID, Boolean value) {
		setBoolean(this.mContext.getString(resID), value);
	}

	public void setByte(int resID, byte[] value) {
		setByte(this.mContext.getString(resID), value);
	}

	public void setShort(int resID, short value) {
		setShort(this.mContext.getString(resID), value);
	}

	public void setLong(int resID, long value) {
		setLong(this.mContext.getString(resID), value);
	}

	public void setFloat(int resID, float value) {
		setFloat(this.mContext.getString(resID), value);
	}

	public void setDouble(int resID, double value) {
		setDouble(this.mContext.getString(resID), value);
	}

	public String getString(String key, String defaultValue) {
		return this.mSharedPreferences.getString(key, defaultValue);
	}

	public int getInt(String key, int defaultValue) {
		return this.mSharedPreferences.getInt(key, defaultValue);
	}

	public boolean getBoolean(String key, Boolean defaultValue) {
		return this.mSharedPreferences.getBoolean(key,
				defaultValue.booleanValue());
	}

	public byte[] getByte(String key, byte[] defaultValue) {
		try {
			return getString(key, "").getBytes();
		} catch (Exception localException) {
		}
		return defaultValue;
	}

	public short getShort(String key, Short defaultValue) {
		try {
			return Short.valueOf(getString(key, "")).shortValue();
		} catch (Exception localException) {
		}
		return defaultValue.shortValue();
	}

	public long getLong(String key, Long defaultValue) {
		return this.mSharedPreferences.getLong(key, defaultValue.longValue());
	}

	public float getFloat(String key, Float defaultValue) {
		return this.mSharedPreferences.getFloat(key, defaultValue.floatValue());
	}

	public double getDouble(String key, Double defaultValue) {
		try {
			return Double.valueOf(getString(key, "")).doubleValue();
		} catch (Exception localException) {
		}
		return defaultValue.doubleValue();
	}

	public String getString(int resID, String defaultValue) {
		return getString(this.mContext.getString(resID), defaultValue);
	}

	public int getInt(int resID, int defaultValue) {
		return getInt(this.mContext.getString(resID), defaultValue);
	}

	public boolean getBoolean(int resID, Boolean defaultValue) {
		return getBoolean(this.mContext.getString(resID), defaultValue);
	}

	public byte[] getByte(int resID, byte[] defaultValue) {
		return getByte(this.mContext.getString(resID), defaultValue);
	}

	public short getShort(int resID, Short defaultValue) {
		return getShort(this.mContext.getString(resID), defaultValue);
	}

	public long getLong(int resID, Long defaultValue) {
		return getLong(this.mContext.getString(resID), defaultValue);
	}

	public float getFloat(int resID, Float defaultValue) {
		return getFloat(this.mContext.getString(resID), defaultValue);
	}

	public double getDouble(int resID, Double defaultValue) {
		return getDouble(this.mContext.getString(resID), defaultValue);
	}

	public void setConfig(Object entity) {
		Class clazz = entity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if ((!TReflectUtils.isTransient(field))
					&& (TReflectUtils.isBaseDateType(field))) {
				String columnName = TReflectUtils.getFieldName(field);
				field.setAccessible(true);
				setValue(field, columnName, entity);
			}
		}
	}

	private void setValue(Field field, String columnName, Object entity) {
		try {
			Class clazz = field.getType();
			if (clazz.equals(String.class))
				setString(columnName, (String) field.get(entity));
			else if ((clazz.equals(Integer.class))
					|| (clazz.equals(Integer.TYPE)))
				setInt(columnName, ((Integer) field.get(entity)).intValue());
			else if ((clazz.equals(Float.class)) || (clazz.equals(Float.TYPE)))
				setFloat(columnName, ((Float) field.get(entity)).floatValue());
			else if ((clazz.equals(Double.class))
					|| (clazz.equals(Double.TYPE)))
				setDouble(columnName,
						((Double) field.get(entity)).doubleValue());
			else if ((clazz.equals(Short.class)) || (clazz.equals(Short.class)))
				setShort(columnName, ((Short) field.get(entity)).shortValue());
			else if ((clazz.equals(Long.class)) || (clazz.equals(Long.TYPE)))
				setLong(columnName, ((Long) field.get(entity)).longValue());
			else if (clazz.equals(Boolean.class))
				setBoolean(columnName, (Boolean) field.get(entity));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public <T> T getConfig(Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		Object entity = null;
		try {
			entity = clazz.newInstance();
			for (Field field : fields) {
				field.setAccessible(true);
				if ((!TReflectUtils.isTransient(field))
						&& (TReflectUtils.isBaseDateType(field))) {
					String columnName = TReflectUtils.getFieldName(field);
					field.setAccessible(true);
					getValue(field, columnName, entity);
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return (T) entity;
	}

	private <T> void getValue(Field field, String columnName, T entity) {
		try {
			Class clazz = field.getType();
			if (clazz.equals(String.class))
				field.set(entity, getString(columnName, ""));
			else if ((clazz.equals(Integer.class))
					|| (clazz.equals(Integer.TYPE)))
				field.set(entity, Integer.valueOf(getInt(columnName, 0)));
			else if ((clazz.equals(Float.class)) || (clazz.equals(Float.TYPE)))
				field.set(entity, Float.valueOf(getFloat(columnName,
						Float.valueOf(0.0F))));
			else if ((clazz.equals(Double.class))
					|| (clazz.equals(Double.TYPE)))
				field.set(
						entity,
						Double.valueOf(getDouble(columnName,
								Double.valueOf(0.0D))));
			else if ((clazz.equals(Short.class)) || (clazz.equals(Short.class)))
				field.set(
						entity,
						Short.valueOf(getShort(columnName,
								Short.valueOf((short) 0))));
			else if ((clazz.equals(Long.class)) || (clazz.equals(Long.TYPE)))
				field.set(entity,
						Long.valueOf(getLong(columnName, Long.valueOf(0L))));
			else if ((clazz.equals(Byte.class)) || (clazz.equals(Byte.TYPE)))
				field.set(entity, getByte(columnName, new byte[8]));
			else if (clazz.equals(Boolean.class))
				field.set(
						entity,
						Boolean.valueOf(getBoolean(columnName,
								Boolean.valueOf(false))));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void remove(String key) {
		this.edit.remove(key);
		this.edit.commit();
	}

	public void remove(String[] keys) {
		for (String key : keys)
			remove(key);
	}

	public void clear() {
		this.edit.clear();
		this.edit.commit();
	}

	public void open() {
	}
}