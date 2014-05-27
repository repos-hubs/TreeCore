package com.treecore.utils.log;

import android.content.Context;
import com.treecore.TApplication;
import com.treecore.filepath.TFilePathManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintToFileLogger implements TILogger {
	public static final int VERBOSE = 2;
	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;
	public static final int ASSERT = 7;
	private Writer mWriter;
	private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat(
			"[yyyy-MM-dd HH:mm:ss] ");

	public PrintToFileLogger() {
		open();
	}

	public void open() {
		String filePath = TFilePathManager.getInstance().getAppPath();
		try {
			File file = new File(filePath, getCurrentTimeString() + ".log");
			String path = file.getAbsolutePath();
			file = null;
			this.mWriter = new BufferedWriter(new FileWriter(path), 2048);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getCurrentTimeString() {
		Date now = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String result = simpleDateFormat.format(now);
		now = null;
		simpleDateFormat = null;
		return result.replaceAll(":", "-");
	}

	public void d(String tag, String message) {
		println(3, tag, message);
	}

	public void e(String tag, String message) {
		println(6, tag, message);
	}

	public void i(String tag, String message) {
		println(4, tag, message);
	}

	public void v(String tag, String message) {
		println(2, tag, message);
	}

	public void w(String tag, String message) {
		println(5, tag, message);
	}

	public void println(int priority, String tag, String message) {
		String printMessage = "";
		switch (priority) {
		case 2:
			printMessage = "[V]|"
					+ tag
					+ "|"
					+ TApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		case 3:
			printMessage = "[D]|"
					+ tag
					+ "|"
					+ TApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		case 4:
			printMessage = "[I]|"
					+ tag
					+ "|"
					+ TApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		case 5:
			printMessage = "[W]|"
					+ tag
					+ "|"
					+ TApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		case 6:
			printMessage = "[E]|"
					+ tag
					+ "|"
					+ TApplication.getInstance().getApplicationContext()
							.getPackageName() + "|" + message;
			break;
		}

		println(printMessage);
	}

	public void println(String message) {
		try {
			if (this.mWriter == null)
				return;
			this.mWriter.write(TIMESTAMP_FMT.format(new Date()));
			this.mWriter.write(message);
			this.mWriter.write(10);
			this.mWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (this.mWriter == null)
				return;
			this.mWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}