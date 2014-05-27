package com.treecore.utils;

import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TFileUtils {
	public static final int S_IRWXU = 448;
	public static final int S_IRUSR = 256;
	public static final int S_IWUSR = 128;
	public static final int S_IXUSR = 64;
	public static final int S_IRWXG = 56;
	public static final int S_IRGRP = 32;
	public static final int S_IWGRP = 16;
	public static final int S_IXGRP = 8;
	public static final int S_IRWXO = 7;
	public static final int S_IROTH = 4;
	public static final int S_IWOTH = 2;
	public static final int S_IXOTH = 1;
	private static final Pattern SAFE_FILENAME_PATTERN = Pattern
			.compile("[\\w%+,./=_-]+");

	public static native boolean getFileStatus(String paramString,
			FileStatus paramFileStatus);

	public static native int setPermissions(String paramString, int paramInt1,
			int paramInt2, int paramInt3);

	public static native int getPermissions(String paramString,
			int[] paramArrayOfInt);

	public static native int getFatVolumeId(String paramString);

	public static boolean sync(FileOutputStream stream) {
		try {
			if (stream != null) {
				stream.getFD().sync();
			}
			return true;
		} catch (IOException localIOException) {
		}
		return false;
	}

	public static boolean copyFile(File srcFile, File destFile) {
		boolean result = false;
		try {
			InputStream in = new FileInputStream(srcFile);
			try {
				result = copyToFile(in, destFile);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

	public static boolean copyToFile(InputStream inputStream, File destFile) {
		try {
			if (destFile.exists()) {
				destFile.delete();
			}
			FileOutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				out.flush();
				try {
					out.getFD().sync();
				} catch (IOException localIOException1) {
				}
				out.close();
			}
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	public static boolean isFilenameSafe(File file) {
		return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
	}

	public static String readTextFile(File file, int max, String ellipsis)
			throws IOException {
		InputStream input = new FileInputStream(file);
		try {
			long size = file.length();
			String str;
			if ((max > 0) || ((size > 0L) && (max == 0))) {
				if ((size > 0L) && ((max == 0) || (size < max)))
					max = (int) size;
				byte[] data = new byte[max + 1];
				int length = input.read(data);
				if (length <= 0)
					return "";
				if (length <= max)
					return new String(data, 0, length);
				if (ellipsis == null)
					return new String(data, 0, max);
				return new String(data, 0, max) + ellipsis;
			}
			if (max < 0) {
				boolean rolled = false;
				byte[] last = null;
				byte[] data = null;
				int len;
				do {
					if (last != null)
						rolled = true;
					byte[] tmp = last;
					last = data;
					data = tmp;
					if (data == null)
						data = new byte[-max];
					len = input.read(data);
				} while (len == data.length);

				if ((last == null) && (len <= 0))
					return "";
				if (last == null)
					return new String(data, 0, len);
				if (len > 0) {
					rolled = true;
					System.arraycopy(last, len, last, 0, last.length - len);
					System.arraycopy(data, 0, last, last.length - len, len);
				}
				if ((ellipsis == null) || (!rolled))
					return new String(last);
				return ellipsis + new String(last);
			}
			ByteArrayOutputStream contents = new ByteArrayOutputStream();

			byte[] data = new byte[1024];
			int len;
			do {
				len = input.read(data);
				if (len > 0)
					contents.write(data, 0, len);
			} while (len == data.length);
			return contents.toString();
		} finally {
			input.close();
		}
	}

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	public static String getStringFromFile(File file) throws Exception {
		FileInputStream fin = new FileInputStream(file);
		String ret = convertStreamToString(fin);

		fin.close();
		return ret;
	}

	public static boolean isFileExit(String path) {
		if (path == null) {
			return false;
		}
		try {
			File f = new File(path);
			if (f.exists())
				return true;
		} catch (Exception localException) {
		}
		return false;
	}

	public static boolean copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (!oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];

				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}

			return true;
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}

		return false;
	}

	public static boolean copyFolder(String oldPath, String newPath) {
		try {
			new File(newPath).mkdirs();
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator))
					temp = new File(oldPath + file[i]);
				else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + temp.getName().toString());
					byte[] b = new byte[5120];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static void saveFile(byte[] databyte, String fileName)
			throws Exception {
		if ((databyte == null) || (databyte.length <= 0)) {
			return;
		}
		File dirFile = new File(fileName);
		int len = databyte.length;
		Log.i("saveFile ", fileName + len);
		FileOutputStream fstream = new FileOutputStream(dirFile);
		BufferedOutputStream stream = new BufferedOutputStream(fstream, len);
		stream.write(databyte);
		stream.flush();
		stream.close();
		fstream.close();
		dirFile = null;
	}

	public static final class FileStatus {
		public int dev;
		public int ino;
		public int mode;
		public int nlink;
		public int uid;
		public int gid;
		public int rdev;
		public long size;
		public int blksize;
		public long blocks;
		public long atime;
		public long mtime;
		public long ctime;
	}
}