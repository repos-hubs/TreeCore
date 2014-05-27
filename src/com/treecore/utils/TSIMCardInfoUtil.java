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
			ProvidersName = "�й��ƶ�";
		else if (IMSI.startsWith("46001"))
			ProvidersName = "�й���ͨ";
		else if (IMSI.startsWith("46003"))
			ProvidersName = "�й�����";
		else {
			ProvidersName = "����������";
		}
		return ProvidersName;
	}

	public static String getIMSI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService("phone");

		return telephonyManager.getSubscriberId();
	}
}