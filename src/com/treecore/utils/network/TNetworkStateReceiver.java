package com.treecore.utils.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.treecore.utils.log.TLog;
import java.util.ArrayList;

public class TNetworkStateReceiver extends BroadcastReceiver {
	private static Boolean networkAvailable = Boolean.valueOf(false);
	private static TNetWorkUtil.netType netType;
	private static ArrayList<TNetChangeObserver> netChangeObserverArrayList = new ArrayList();
	private static final String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String TA_ANDROID_NET_CHANGE_ACTION = "think.android.net.conn.CONNECTIVITY_CHANGE";
	private static BroadcastReceiver receiver;

	private static BroadcastReceiver getReceiver() {
		if (receiver == null) {
			receiver = new TNetworkStateReceiver();
		}
		return receiver;
	}

	public void onReceive(Context context, Intent intent) {
		receiver = this;
		if ((intent.getAction()
				.equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE"))
				|| (intent.getAction()
						.equalsIgnoreCase("think.android.net.conn.CONNECTIVITY_CHANGE"))) {
			TLog.i(this, "网络状态改变.");
			if (!TNetWorkUtil.isNetworkAvailable(context)) {
				TLog.i(this, "没有网络连接.");
				networkAvailable = Boolean.valueOf(false);
			} else {
				TLog.i(this, "网络连接成功.");
				netType = TNetWorkUtil.getAPNType(context);
				networkAvailable = Boolean.valueOf(true);
			}
			notifyObserver();
		}
	}

	public static void registerNetworkStateReceiver(Context mContext) {
		IntentFilter filter = new IntentFilter();
		filter.addAction("think.android.net.conn.CONNECTIVITY_CHANGE");
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		mContext.getApplicationContext()
				.registerReceiver(getReceiver(), filter);
	}

	public static void checkNetworkState(Context mContext) {
		Intent intent = new Intent();
		intent.setAction("think.android.net.conn.CONNECTIVITY_CHANGE");
		mContext.sendBroadcast(intent);
	}

	public static void unRegisterNetworkStateReceiver(Context mContext) {
		if (receiver != null)
			try {
				mContext.getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e) {
				TLog.d("TANetworkStateReceiver", e.getMessage());
			}
	}

	public static Boolean isNetworkAvailable() {
		return networkAvailable;
	}

	public static TNetWorkUtil.netType getAPNType() {
		return netType;
	}

	private void notifyObserver() {
		for (int i = 0; i < netChangeObserverArrayList.size(); i++) {
			TNetChangeObserver observer = (TNetChangeObserver) netChangeObserverArrayList
					.get(i);
			if (observer != null)
				if (isNetworkAvailable().booleanValue())
					observer.onConnect(netType);
				else
					observer.onDisConnect();
		}
	}

	public static void registerObserver(TNetChangeObserver observer) {
		if (netChangeObserverArrayList == null) {
			netChangeObserverArrayList = new ArrayList();
		}
		netChangeObserverArrayList.add(observer);
	}

	public static void removeRegisterObserver(TNetChangeObserver observer) {
		if (netChangeObserverArrayList != null)
			netChangeObserverArrayList.remove(observer);
	}
}