package com.treecore.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.treecore.utils.log.TLog;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TAssetsFileUtils {
	private static final String TAG = TAssetsFileUtils.class.getSimpleName();

	public static InputStream getInputStreamForName(Context context,
			String fileName) {
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(fileName);
		} catch (IOException e) {
			TLog.d(TAG, e.getMessage());
		}
		return inputStream;
	}

	public static String getStringForName(Context context, String fileName) {
		InputStream inputStream = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try {
			inputStream = getInputStreamForName(context, fileName);
			int len;
			while ((len = inputStream.read(buf)) != -1) {
				int len;
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			TLog.d(TAG, e.getMessage());
		}
		return outputStream.toString();
	}

	public static Bitmap getBitmapForName(Context context, String fileName) {
		Bitmap bitmap = null;
		InputStream inputStream = null;
		try {
			inputStream = getInputStreamForName(context, fileName);
			bitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
		} catch (IOException e) {
			TLog.d(TAG, e.getMessage());
		}
		return bitmap;
	}
}