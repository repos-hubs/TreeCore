package com.treecore.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.treecore.utils.log.TLog;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class TIpUtil {
	public static String getWifiIp(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService("wifi");

		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return intToIp(ipAddress);
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + (i >> 8 & 0xFF) + "." + (i >> 16 & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	public static String getGPRSIp() {
		try {
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			Enumeration enumIpAddr;
			for (; en.hasMoreElements(); enumIpAddr.hasMoreElements()) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				enumIpAddr = intf.getInetAddresses();
				continue;
				InetAddress inetAddress = (InetAddress) enumIpAddr
						.nextElement();
				if (!inetAddress.isLoopbackAddress())
					return inetAddress.getHostAddress().toString();
			}
		} catch (SocketException ex) {
			TLog.d("IpUtil", ex.getMessage());
		}
		return "";
	}
}