package com.treecore.utils.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TMD5 {
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String getMD5(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();
		return getString(m);
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(byteToHex(b[i]));
		}
		return sb.toString();
	}

	public static String byteToHex(byte bt) {
		return "" + HEX_DIGITS[((bt & 0xF0) >> 4)] + HEX_DIGITS[(bt & 0xF)];
	}

	public static String getA1Md5(String userName, String token, String realm)
			throws NoSuchAlgorithmException {
		return getMD5(userName + ":" + realm + ":" + token);
	}

	private static String getA2Md5(String method, String url)
			throws NoSuchAlgorithmException {
		return getMD5(method + ":" + url);
	}

	public static String getResponseMd5(String userName, String token,
			String method, String url, String realm, String other) {
		try {
			String A1 = getA1Md5(userName, token, realm);
			String A2 = getA2Md5(method, url);

			String string = A1 + other + A2;

			return getMD5(string);
		} catch (NoSuchAlgorithmException e) {
		}

		return "";
	}
}