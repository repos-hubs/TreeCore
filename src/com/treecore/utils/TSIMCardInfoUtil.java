package com.treecore.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import java.io.PrintStream;

public class TSIMCardInfoUtil {
	public static String getNativePhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService("phone");
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		return NativePhoneNumber;
	}

	public static String getProvidersName(Context context) {
		String ProvidersName = null;

		String IMSI = getIMSI(context);

		System.out.println(IMSI);
		if ((IMSI.startsWith("46000")) || (IMSI.startsWith("46002")))
			ProvidersName = "中国移动";
		else if (IMSI.startsWith("46001"))
			ProvidersName = "中国联通";
		else if (IMSI.startsWith("46003"))
			ProvidersName = "中国电信";
		else {
			ProvidersName = "其他服务商";
		}
		return ProvidersName;
	}

	public static String getIMSI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService("phone");

		return telephonyManager.getSubscriberId();
	}
}