package com.treecore.utils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TDigitalUtils {
	public static String TAG = TDigitalUtils.class.getSimpleName();
	private static final String HEX = "0123456789ABCDEF";

	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append("0123456789ABCDEF".charAt(b >> 4 & 0xF)).append(
				"0123456789ABCDEF".charAt(b & 0xF));
	}

	public static String hexMD5(byte[] data) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");

			md5.reset();
			md5.update(data);
			byte[] digest = md5.digest();

			return toHex(digest);
		} catch (NoSuchAlgorithmException e) {
		}
		throw new RuntimeException(
				"Error - this implementation of Java doesn't support MD5.");
	}

	public static String hexMD5(ByteBuffer buf, int offset, int len) {
		byte[] b = new byte[len];
		for (int i = 0; i < len; i++) {
			b[i] = buf.get(offset + i);
		}
		return hexMD5(b);
	}
}