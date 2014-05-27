package com.treecore.crash.collector;

import com.treecore.crash.TCrash;
import com.treecore.utils.TAndroidVersionUtils;
import com.treecore.utils.log.TLog;
import com.treecore.utils.stl.BoundedLinkedList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

final class LogCatCollector {
	private static final int DEFAULT_TAIL_COUNT = 100;

	public static String collectLogCat(String bufferName) {
		int myPid = android.os.Process.myPid();
		String myPidStr = null;

		myPidStr = Integer.toString(myPid) + "):";

		List commandLine = new ArrayList();
		commandLine.add("logcat");
		if (bufferName != null) {
			commandLine.add("-b");
			commandLine.add(bufferName);
		}

		List logcatArgumentsList = new ArrayList();

		int tailIndex = logcatArgumentsList.indexOf("-t");
		int tailCount;
		if ((tailIndex > -1) && (tailIndex < logcatArgumentsList.size())) {
			tailCount = Integer.parseInt((String) logcatArgumentsList.get(tailIndex + 1));
			if (TAndroidVersionUtils.getAPILevel() < 8) {
				logcatArgumentsList.remove(tailIndex + 1);
				logcatArgumentsList.remove(tailIndex);
				logcatArgumentsList.add("-d");
			}
		} else {
			tailCount = -1;
		}

		LinkedList logcatBuf = new BoundedLinkedList(tailCount > 0 ? tailCount : 100);
		commandLine.addAll(logcatArgumentsList);
		try {
			java.lang.Process process = Runtime.getRuntime().exec(
					(String[]) commandLine.toArray(new String[commandLine
							.size()]));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()), 8192);

			TLog.d(TCrash.TAG, "Retrieving logcat output...");
			while (true) {
				String line = bufferedReader.readLine();
				if (line == null) {
					break;
				}
				if ((myPidStr == null) || (line.contains(myPidStr)))
					logcatBuf.add(line + "\n");
			}
		} catch (IOException e) {
			TLog.e(TCrash.TAG,
					"LogCatCollector.collectLogCat could not retrieve data."
							+ e.getMessage());
		}

		return logcatBuf.toString();
	}
}