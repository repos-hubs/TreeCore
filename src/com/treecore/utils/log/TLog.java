package com.treecore.utils.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TLog {
	private static boolean mIgnoreAll = false;
	private static boolean mIgnoreInfo = false;
	private static boolean mIgnoreDebug = false;
	private static boolean mIgnoreWarn = false;
	private static boolean mIgnoreError = false;
	public static final int VERBOSE = 2;
	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;
	public static final int ASSERT = 7;
	private static HashMap<String, TILogger> mLoggerHashMap = new HashMap();
	private static TILogger mDefaultLogger = new PrintToLogCatLogger();
	private static HashMap<String, Boolean> mIgnoreTagHashMap = new HashMap();

	public static void enablePrintToFileLogger(boolean enable) {
		mDefaultLogger = null;
		if (enable)
			mDefaultLogger = new PrintToFileLogger();
		else
			mDefaultLogger = new PrintToLogCatLogger();
	}

	public static void release() {
		mDefaultLogger.close();
	}

	public static void addLogger(TILogger logger) {
		String loggerName = logger.getClass().getName();
		String defaultLoggerName = mDefaultLogger.getClass().getName();
		if ((!mLoggerHashMap.containsKey(loggerName))
				&& (!defaultLoggerName.equalsIgnoreCase(loggerName))) {
			logger.open();
			mLoggerHashMap.put(loggerName, logger);
		}
	}

	public static void removeLogger(TILogger logger) {
		String loggerName = logger.getClass().getName();
		if (mLoggerHashMap.containsKey(loggerName)) {
			logger.close();
			mLoggerHashMap.remove(loggerName);
		}
	}

	public static void d(Object object, String message) {
		printLoger(3, object, message);
	}

	public static void e(Object object, String message) {
		printLoger(6, object, message);
	}

	public static void i(Object object, String message) {
		printLoger(4, object, message);
	}

	public static void v(Object object, String message) {
		printLoger(2, object, message);
	}

	public static void w(Object object, String message) {
		printLoger(5, object, message);
	}

	public static void d(String tag, String message) {
		printLoger(3, tag, message);
	}

	public static void e(String tag, String message) {
		printLoger(6, tag, message);
	}

	public static void i(String tag, String message) {
		printLoger(4, tag, message);
	}

	public static void v(String tag, String message) {
		printLoger(2, tag, message);
	}

	public static void w(String tag, String message) {
		printLoger(5, tag, message);
	}

	public static void println(int priority, String tag, String message) {
		printLoger(priority, tag, message);
	}

	private static void printLoger(int priority, Object object, String message) {
		Class cls = object.getClass();
		String tag = cls.getName();
		String[] arrays = tag.split("\\.");
		tag = arrays[(arrays.length - 1)];
		printLoger(priority, tag, message);
	}

	private static void printLoger(int priority, String tag, String message) {
		if (!TApplication.isRelease()) {
			printLoger(mDefaultLogger, priority, tag, message);
			Iterator iter = mLoggerHashMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				TILogger logger = (TILogger) entry.getValue();
				if (logger != null)
					printLoger(logger, priority, tag, message);
			}
		}
	}

	private static void printLoger(TILogger logger, int priority, String tag,
			String message) {
		if ((!mIgnoreTagHashMap.isEmpty())
				&& (mIgnoreTagHashMap.containsKey(tag))) {
			return;
		}

		switch (priority) {
		case 2:
			if (!mIgnoreAll)
				logger.v(tag, message);
			break;
		case 3:
			if (!mIgnoreDebug)
				logger.d(tag, message);
			break;
		case 4:
			if (!mIgnoreInfo)
				logger.i(tag, message);
			break;
		case 5:
			if (!mIgnoreWarn)
				logger.w(tag, message);
			break;
		case 6:
			if (!mIgnoreError)
				logger.e(tag, message);
			break;
		}
	}

	public static void ignoreAll(boolean enable) {
		mIgnoreAll = enable;
	}

	public static void ignoreInfo(boolean enable) {
		mIgnoreInfo = enable;
	}

	public static void ignoreDebug(boolean enable) {
		mIgnoreDebug = enable;
	}

	public static void ignoreWarn(boolean enable) {
		mIgnoreWarn = enable;
	}

	public static void ignoreError(boolean enable) {
		mIgnoreError = enable;
	}

	public static void clearIgnoreTag() {
		mIgnoreTagHashMap.clear();
	}

	public static void addIgnoreTag(String[] tags) {
		String[] arrayOfString = tags;
		int j = tags.length;
		for (int i = 0; i < j; i++) {
			String tag = arrayOfString[i];
			mIgnoreTagHashMap.put(tag, Boolean.valueOf(true));
		}
	}
}