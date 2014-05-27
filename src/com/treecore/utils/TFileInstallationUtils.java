package com.treecore.utils;

import android.content.Context;
import com.treecore.utils.log.TLog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class TFileInstallationUtils {
	public static String TAG = TFileInstallationUtils.class.getSimpleName();
	private static String sID;
	private static final String INSTALLATION = "ACRA-INSTALLATION";

	public static synchronized String id(Context context) {
		if (sID == null) {
			File installation = new File(context.getFilesDir(),
					"ACRA-INSTALLATION");
			try {
				if (!installation.exists()) {
					writeInstallationFile(installation);
				}
				sID = readInstallationFile(installation);
			} catch (IOException e) {
				TLog.w(TAG,
						"Couldn't retrieve InstallationId for "
								+ context.getPackageName() + e.getMessage());
				return "Couldn't retrieve InstallationId";
			} catch (RuntimeException e) {
				TLog.w(TAG,
						"Couldn't retrieve InstallationId for "
								+ context.getPackageName() + e.getMessage());
				return "Couldn't retrieve InstallationId";
			}
		}
		return sID;
	}

	private static String readInstallationFile(File installation)
			throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		try {
			f.readFully(bytes);
		} finally {
			f.close();
		}
		return new String(bytes);
	}

	private static void writeInstallationFile(File installation)
			throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		try {
			String id = UUID.randomUUID().toString();
			out.write(id.getBytes());
		} finally {
			out.close();
		}
	}
}