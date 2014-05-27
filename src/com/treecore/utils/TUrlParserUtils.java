package com.treecore.utils;

import android.text.TextUtils;
import java.io.File;
import java.net.URL;
import java.util.UUID;

public class TUrlParserUtils {
	public static String urlParse(String baseUrl, String url) {
		String returnUrl = "";
		try {
			URL absoluteUrl = new URL(baseUrl);
			URL parseUrl = new URL(absoluteUrl, url);
			returnUrl = parseUrl.toString();
		} catch (Exception e) {
			e.getStackTrace();
		}
		return returnUrl;
	}

	public static String getFileNameFromUrl(String url) {
		int index = url.lastIndexOf('?');
		String filename;
		String filename;
		if (index > 1)
			filename = url.substring(url.lastIndexOf('/') + 1, index);
		else {
			filename = url.substring(url.lastIndexOf('/') + 1);
		}

		if (TextUtils.isEmpty(filename)) {
			filename = UUID.randomUUID();
		}
		return filename;
	}

	public static String getFileNameByUrl(String url) {
		String filename = "";
		try {
			URL url2 = new URL(url);
			filename = new File(url2.getFile()).getName();
		} catch (Exception localException) {
		}
		if (TextUtils.isEmpty(filename)) {
			filename = UUID.randomUUID();
		}
		return filename;
	}
}