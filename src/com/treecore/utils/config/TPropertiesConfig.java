package com.treecore.utils.config;

import android.content.Context;
import com.treecore.TIGlobalInterface;
import com.treecore.utils.TReflectUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;

public class TPropertiesConfig implements TIConfig, TIGlobalInterface {
	private String mAssetsPath = "/assets/config.properties";

	private String mFilesPath = "config.properties";
	private static TPropertiesConfig mPropertiesConfig;
	private static final String LOADFLAG = "assetsload";
	private Context mContext;
	private Properties mProperties;

	public void initConfig(Context context) {
		this.mContext = context;
		loadConfig();
	}

	public void initConfig() {
	}

	public void release() {
	}

	public static TPropertiesConfig getInstance() {
		if (mPropertiesConfig == null) {
			mPropertiesConfig = new TPropertiesConfig();
		}
		return mPropertiesConfig;
	}

	private void loadConfig() {
		Properties props = new Properties();
		InputStream in = TPropertiesConfig.class
				.getResourceAsStream(this.mAssetsPath);
		try {
			if (in != null) {
				props.load(in);
				Enumeration e = props.propertyNames();
				if (e.hasMoreElements()) {
					while (e.hasMoreElements()) {
						String s = (String) e.nextElement();
						props.setProperty(s, props.getProperty(s));
					}
				}
			}
			setBoolean("assetsload", Boolean.valueOf(true));
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isLoadConfig() {
		return Boolean
				.valueOf(getBoolean("assetsload", Boolean.valueOf(false)));
	}

	public void setConfig(String key, String value) {
		if (value != null) {
			Properties props = getProperties();
			props.setProperty(key, value);
			setProperties(props);
		}
	}

	public String getAssetsPath() {
		return this.mAssetsPath;
	}

	public void setAssetsPath(String assetsPath) {
		this.mAssetsPath = assetsPath;
	}

	public String getFilesPath() {
		return this.mFilesPath;
	}

	public void setFilesPath(String filesPath) {
		this.mFilesPath = filesPath;
	}

	private Properties getProperties() {
		if (this.mProperties == null) {
			this.mProperties = getPro();
		}
		return this.mProperties;
	}

	private Properties getPro() {
		Properties props = new Properties();
		try {
			InputStream in = this.mContext.openFileInput(this.mFilesPath);
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	private void setProperties(Properties p) {
		try {
			OutputStream out = this.mContext.openFileOutput(this.mFilesPath, 0);
			p.store(out, null);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
	}

	public void setString(String key, String value) {
		setConfig(key, value);
	}

	public void setInt(String key, int value) {
		setString(key, String.valueOf(value));
	}

	public void setBoolean(String key, Boolean value) {
		setString(key, String.valueOf(value));
	}

	public void setByte(String key, byte[] value) {
		setString(key, String.valueOf(value));
	}

	public void setShort(String key, short value) {
		setString(key, String.valueOf(value));
	}

	public void setLong(String key, long value) {
		setString(key, String.valueOf(value));
	}

	public void setFloat(String key, float value) {
		setString(key, String.valueOf(value));
	}

	public void setDouble(String key, double value) {
		setString(key, String.valueOf(value));
	}

	public String getConfig(String key, String defaultValue) {
		return getProperties().getProperty(key, defaultValue);
	}

	public String getString(String key, String defaultValue) {
		return getConfig(key, defaultValue);
	}

	public int getInt(String key, int defaultValue) {
		try {
			return Integer.valueOf(getString(key, "")).intValue();
		} catch (Exception localException) {
		}
		return defaultValue;
	}

	public boolean getBoolean(String key, Boolean defaultValue) {
		try {
			return Boolean.valueOf(getString(key, "")).booleanValue();
		} catch (Exception localException) {
		}
		return defaultValue.booleanValue();
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
		try {
			return Long.valueOf(getString(key, "")).longValue();
		} catch (Exception localException) {
		}
		return defaultValue.longValue();
	}

	public float getFloat(String key, Float defaultValue) {
		try {
			return Float.valueOf(getString(key, "")).floatValue();
		} catch (Exception localException) {
		}
		return defaultValue.floatValue();
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
		return entity;
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
		Properties props = getProperties();
		props.remove(key);
		setProperties(props);
	}

	public void remove(String[] keys) {
		Properties props = getProperties();
		for (String key : keys) {
			props.remove(key);
		}
		setProperties(props);
	}

	public void clear() {
		Properties props = getProperties();
		props.clear();
		setProperties(props);
	}

	public void open() {
	}
}