package com.treecore.filepath;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import com.treecore.TApplication;
import com.treecore.utils.TAndroidVersionUtils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TFilePathManager implements IXFilePath {
	private String mAppPathString = "";
	private static String PATH_IMAGE = "image";
	private static String PATH_AUDIO = "audio";
	private static String PATH_VIDEO = "video";
	private static String PATH_DOWNLOAD = "download";
	private static String PATH_CACHE = "cache";
	public static String PATH_SPLIT = File.separator;
	private static TFilePathManager mThis = null;

	public static TFilePathManager getInstance() {
		if (mThis == null)
			mThis = new TFilePathManager();
		return mThis;
	}

	public void release() {
		mThis = null;
	}

	public void initConfig(String appPath) {
		if (TextUtils.isEmpty(appPath)) {
			String name = TApplication.getInstance().getApplicationContext()
					.getPackageName();
			if ((hasExternalStorage()) || (!isExternalStorageRemovable()))
				appPath = Environment.getExternalStorageDirectory().getPath()
						+ PATH_SPLIT + name;
			else {
				appPath = TApplication.getInstance().getApplicationContext()
						.getFilesDir().getAbsolutePath();
			}
		}

		this.mAppPathString = appPath;
		initDir(getAppPath());
		initDir(getAudioPath());
		initDir(getCachePath());
		initDir(getDownloadPath());
		initDir(getImagePath());
	}

	public String getAudioPath() {
		return this.mAppPathString + PATH_SPLIT + PATH_AUDIO;
	}

	public String getVideoPath() {
		return this.mAppPathString + PATH_SPLIT + PATH_VIDEO;
	}

	public String getImagePath() {
		return this.mAppPathString + PATH_SPLIT + PATH_IMAGE;
	}

	public String getDownloadPath() {
		return this.mAppPathString + PATH_SPLIT + PATH_DOWNLOAD;
	}

	public String getCachePath() {
		return this.mAppPathString + PATH_SPLIT + PATH_CACHE;
	}

	public String getAppPath() {
		return this.mAppPathString;
	}

	private void initDir(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists())
				file.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		file = null;
	}

	public static boolean hasExternalStorage() {
		Boolean externalStorage = Boolean.valueOf(Environment
				.getExternalStorageState().equals("mounted"));
		return externalStorage.booleanValue();
	}

	public static long getUsableSpace(File path) {
		if (TAndroidVersionUtils.hasGingerbread()) {
			return path.length();
		}

		StatFs stats = new StatFs(path.getPath());
		return stats.getBlockSize() * stats.getAvailableBlocks();
	}

	public static boolean isExternalStorageRemovable() {
		if (TAndroidVersionUtils.hasGingerbread()) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}