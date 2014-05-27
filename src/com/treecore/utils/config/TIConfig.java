package com.treecore.utils.config;

public abstract interface TIConfig {
	public abstract Boolean isLoadConfig();

	public abstract void open();

	public abstract void close();

	public abstract void setString(String paramString1, String paramString2);

	public abstract void setInt(String paramString, int paramInt);

	public abstract void setBoolean(String paramString, Boolean paramBoolean);

	public abstract void setByte(String paramString, byte[] paramArrayOfByte);

	public abstract void setShort(String paramString, short paramShort);

	public abstract void setLong(String paramString, long paramLong);

	public abstract void setFloat(String paramString, float paramFloat);

	public abstract void setDouble(String paramString, double paramDouble);

	public abstract void setString(int paramInt, String paramString);

	public abstract void setInt(int paramInt1, int paramInt2);

	public abstract void setBoolean(int paramInt, Boolean paramBoolean);

	public abstract void setByte(int paramInt, byte[] paramArrayOfByte);

	public abstract void setShort(int paramInt, short paramShort);

	public abstract void setLong(int paramInt, long paramLong);

	public abstract void setFloat(int paramInt, float paramFloat);

	public abstract void setDouble(int paramInt, double paramDouble);

	public abstract void setConfig(Object paramObject);

	public abstract String getString(String paramString1, String paramString2);

	public abstract int getInt(String paramString, int paramInt);

	public abstract boolean getBoolean(String paramString, Boolean paramBoolean);

	public abstract byte[] getByte(String paramString, byte[] paramArrayOfByte);

	public abstract short getShort(String paramString, Short paramShort);

	public abstract long getLong(String paramString, Long paramLong);

	public abstract float getFloat(String paramString, Float paramFloat);

	public abstract double getDouble(String paramString, Double paramDouble);

	public abstract String getString(int paramInt, String paramString);

	public abstract int getInt(int paramInt1, int paramInt2);

	public abstract boolean getBoolean(int paramInt, Boolean paramBoolean);

	public abstract byte[] getByte(int paramInt, byte[] paramArrayOfByte);

	public abstract short getShort(int paramInt, Short paramShort);

	public abstract long getLong(int paramInt, Long paramLong);

	public abstract float getFloat(int paramInt, Float paramFloat);

	public abstract double getDouble(int paramInt, Double paramDouble);

	public abstract <T> T getConfig(Class<T> paramClass);

	public abstract void remove(String paramString);

	public abstract void remove(String[] paramArrayOfString);

	public abstract void clear();
}