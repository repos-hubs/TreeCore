package com.treecore.crash.data;

import com.treecore.crash.TCrash;
import com.treecore.utils.log.TLog;
import java.io.File;
import java.io.FilenameFilter;

public class CrashReportFinder {
	public String[] getCrashReportFiles() {
		File dir = new File(TCrash.getInstance().getFilePath());
		if (dir == null) {
			TLog.w(TCrash.TAG,
					"Application files directory does not exist! The application may not be installed correctly. Please try reinstalling.");
			return new String[0];
		}

		TLog.d(TCrash.TAG,
				"Looking for error files in " + dir.getAbsolutePath());

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".stacktrace");
			}
		};
		String[] result = dir.list(filter);
		return result == null ? new String[0] : result;
	}
}