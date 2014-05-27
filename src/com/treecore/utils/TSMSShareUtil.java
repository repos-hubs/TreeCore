package com.treecore.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class TSMSShareUtil {
	public static Boolean sendSms(Context mContext, String smstext) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent mIntent = new Intent("android.intent.action.SENDTO", smsToUri);
		mIntent.putExtra("sms_body", smstext);
		mContext.startActivity(mIntent);
		return null;
	}
}