package com.treecore.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class TFileInfoUtils {
	private static String kB_UNIT_NAME = "KB";
	private static String B_UNIT_NAME = "B";
	private static String MB_UNIT_NAME = "MB";

	public static long getFileSizes(File f) throws Exception {
		long s = 0L;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		} else {
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}

	public static long getFileSize(File f) throws Exception {
		long size = 0L;
		File[] flist = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory())
				size += getFileSize(flist[i]);
			else {
				size += flist[i].length();
			}
		}
		return size;
	}

	public static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#0.00");
		String fileSizeString = "";
		if (fileS < 1024L)
			fileSizeString = df.format(fileS) + "B";
		else if (fileS < 1048576L)
			fileSizeString = df.format(fileS / 1024.0D) + "K";
		else if (fileS < 1073741824L)
			fileSizeString = df.format(fileS / 1048576.0D) + "M";
		else {
			fileSizeString = df.format(fileS / 1073741824.0D) + "G";
		}
		return fileSizeString;
	}

	public static long getlist(File f) {
		long size = 0L;
		File[] flist = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size += getlist(flist[i]);
				size -= 1L;
			}
		}
		return size;
	}

	public static String getSizeString(long size) {
		if (size < 1024L) {
			return String.valueOf(size) + B_UNIT_NAME;
		}
		size /= 1024L;

		if (size < 1024L) {
			return String.valueOf(size) + kB_UNIT_NAME;
		}
		size = size * 100L / 1024L;

		return String.valueOf(size / 100L) + "."
				+ (size % 100L < 10L ? "0" : "") + String.valueOf(size % 100L)
				+ MB_UNIT_NAME;
	}

	public static String getMbSize(long dirSize) {
		double size = 0.0D;
		size = (dirSize + 0.0D) / 1048576.0D;
		DecimalFormat df = new DecimalFormat("0.00");
		String filesize = df.format(size);
		return filesize;
	}

	public static String getKBSize(long dirSize) {
		double size = 0.0D;
		size = (dirSize + 0.0D) / 1024.0D;
		DecimalFormat df = new DecimalFormat("0.00");
		String filesize = df.format(size);
		return filesize;
	}
}