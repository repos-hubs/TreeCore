package com.treecore.crash.collector;

import com.treecore.crash.TCrash;
import com.treecore.utils.log.TLog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

final class DumpSysCollector {
	public static String collectMemInfo() {
		StringBuilder meminfo = new StringBuilder();
		try {
			List commandLine = new ArrayList();
			commandLine.add("dumpsys");
			commandLine.add("meminfo");
			commandLine.add(Integer.toString(android.os.Process.myPid()));

			java.lang.Process process = Runtime.getRuntime().exec(
					(String[]) commandLine.toArray(new String[commandLine
							.size()]));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()), 8192);
			while (true) {
				String line = bufferedReader.readLine();
				if (line == null) {
					break;
				}
				meminfo.append(line);
				meminfo.append("\n");
			}
		} catch (IOException e) {
			TLog.e(TCrash.TAG,
					"DumpSysCollector.meminfo could not retrieve data"
							+ e.getMessage());
		}

		return meminfo.toString();
	}
}