package com.treecore.utils;

import android.content.Context;
import android.content.Intent;

public class TMailShareUtil {
	public static Boolean sendMail(Context mContext, String title, String text) {
		Intent emailIntent = new Intent("android.intent.action.SEND");

		emailIntent.setType("text/plain");

		emailIntent.putExtra("android.intent.extra.EMAIL", "");

		emailIntent.putExtra("android.intent.extra.SUBJECT", title);

		emailIntent.putExtra("android.intent.extra.TEXT", text);
		mContext.startActivity(Intent.createChooser(emailIntent,
				"Choose Email Client"));
		return null;
	}
}