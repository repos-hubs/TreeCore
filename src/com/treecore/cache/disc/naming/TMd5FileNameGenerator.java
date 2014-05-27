package com.treecore.cache.disc.naming;

import com.treecore.utils.log.TLog;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TMd5FileNameGenerator implements TIFileNameGenerator {
	private static final String HASH_ALGORITHM = "MD5";
	private static final int RADIX = 36;

	public String generate(String imageUri) {
		byte[] md5 = getMD5(imageUri.getBytes());
		BigInteger bi = new BigInteger(md5).abs();
		return bi.toString(36);
	}

	private byte[] getMD5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			TLog.i(this, e.getMessage());
		}
		return hash;
	}
}