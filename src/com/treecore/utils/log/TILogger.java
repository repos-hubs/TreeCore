package com.treecore.utils.log;

public abstract interface TILogger {
	public abstract void v(String paramString1, String paramString2);

	public abstract void d(String paramString1, String paramString2);

	public abstract void i(String paramString1, String paramString2);

	public abstract void w(String paramString1, String paramString2);

	public abstract void e(String paramString1, String paramString2);

	public abstract void open();

	public abstract void close();

	public abstract void println(int paramInt, String paramString1,
			String paramString2);
}