package com.treecore.utils.encryption;

import com.treecore.utils.TAndroidVersionUtils;
import com.treecore.utils.TDigitalUtils;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TAes {
	public static String encrypt(String key, String content) throws Exception {
		byte[] rawKey = getRawKey(key.getBytes());
		byte[] result = encrypt(rawKey, content.getBytes());
		return TDigitalUtils.toHex(result);
	}

	public static String decrypt(String key, String encrypted) throws Exception {
		byte[] rawKey = getRawKey(key.getBytes());
		byte[] enc = TDigitalUtils.toByte(encrypted);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");

		SecureRandom sr = null;
		if (TAndroidVersionUtils.hasJellyBeanMr1())
			sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
		else {
			sr = SecureRandom.getInstance("SHA1PRNG");
		}
		sr.setSeed(seed);
		kgen.init(256, sr);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	private static byte[] encrypt(byte[] key, byte[] content) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(1, skeySpec);
		byte[] encrypted = cipher.doFinal(content);
		return encrypted;
	}

	private static byte[] decrypt(byte[] key, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(2, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
}