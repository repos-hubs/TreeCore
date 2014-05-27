package com.treecore.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.treecore.TApplication;

public final class TToastUtils {
	public static void makeText(Context context, int toastResourceId,
			int toastLength) {
		try {
			Toast.makeText(context, toastResourceId, toastLength).show();
		} catch (RuntimeException e) {
			Log.e(TToastUtils.class.getSimpleName(),
					"Could not send crash Toast", e);
		}
	}

	public static void makeText(int toastResourceId, int toastLength) {
		try {
			Toast.makeText(TApplication.getInstance().getApplicationContext(),
					toastResourceId, toastLength).show();
		} catch (RuntimeException e) {
			Log.e(TToastUtils.class.getSimpleName(),
					"Could not send crash Toast", e);
		}
	}

	public static void makeText(String content, int toastLength) {
		try {
			Toast.makeText(TApplication.getInstance().getApplicationContext(),
					content, toastLength).show();
		} catch (RuntimeException e) {
			Log.e(TToastUtils.class.getSimpleName(),
					"Could not send crash Toast", e);
		}
	}

	public static void makeText(String content) {
		try {
			Toast.makeText(TApplication.getInstance().getApplicationContext(),
					content, 0).show();
		} catch (RuntimeException e) {
			Log.e(TToastUtils.class.getSimpleName(),
					"Could not send crash Toast", e);
		}
	}
}