package com.treecore.utils.encryption;

import java.io.IOException;
import java.io.PrintStream;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class TDes {
	private static final String DES = "DES";

	public static void main(String[] args) throws Exception {
		String data = "123 456";
		String key = "wang!@#$%";
		System.err.println(encrypt(data, key));
		System.err.println(decrypt(encrypt(data, key), key));
	}

	public static String encrypt(String key, String content) throws Exception {
		byte[] bt = encrypt(content.getBytes(), key.getBytes());
		String strs = TBase64.encode(bt);
		return strs;
	}

	public static String decrypt(String key, String content)
			throws IOException, Exception {
		if (content == null)
			return null;
		byte[] buf = TBase64.decode(content);
		byte[] bt = decrypt(buf, key.getBytes());
		return new String(bt);
	}

	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();

		DESKeySpec dks = new DESKeySpec(key);

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance("DES");

		cipher.init(1, securekey, sr);

		return cipher.doFinal(data);
	}

	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();

		DESKeySpec dks = new DESKeySpec(key);

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance("DES");

		cipher.init(2, securekey, sr);

		return cipher.doFinal(data);
	}
}