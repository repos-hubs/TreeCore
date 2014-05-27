package com.treecore.utils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class TNetWorkUtil {
	public static boolean isNetworkAvailable(Context context) {
		if (context == null)
			return false;
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isNetworkConnected(Context context) {
		if (context == null)
			return false;
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}

	public static boolean isWifiConnected(Context context) {
		if (context == null)
			return false;
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(1);
		if (mWiFiNetworkInfo != null) {
			return mWiFiNetworkInfo.isAvailable();
		}
		return false;
	}

	public static boolean isMobileConnected(Context context) {
		if (context == null)
			return false;
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(0);
		if (mMobileNetworkInfo != null) {
			return mMobileNetworkInfo.isAvailable();
		}
		return false;
	}

	public static int getConnectedType(Context context) {
		if (context == null)
			return -1;
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if ((mNetworkInfo != null) && (mNetworkInfo.isAvailable())) {
			return mNetworkInfo.getType();
		}
		return -1;
	}

	public static netType getAPNType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType.noneNet;
		}
		int nType = networkInfo.getType();

		if (nType == 0) {
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				return netType.CMNET;
			}

			return netType.CMWAP;
		}
		if (nType == 1) {
			return netType.wifi;
		}
		return netType.noneNet;
	}

	public static enum netType {
		wifi, CMNET, CMWAP, noneNet;
	}
}