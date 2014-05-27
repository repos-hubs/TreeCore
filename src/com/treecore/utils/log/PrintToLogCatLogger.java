package com.treecore.utils.log;

import android.util.Log;

public class PrintToLogCatLogger implements TILogger {
	public void d(String tag, String message) {
		Log.d(tag, message);
	}

	public void e(String tag, String message) {
		Log.e(tag, message);
	}

	public void i(String tag, String message) {
		Log.i(tag, message);
	}

	public void v(String tag, String message) {
		Log.v(tag, message);
	}

	public void w(String tag, String message) {
		Log.w(tag, message);
	}

	public void println(int priority, String tag, String message) {
		Log.println(priority, tag, message);
	}

	public void open() {
	}

	public void close() {
	}
}