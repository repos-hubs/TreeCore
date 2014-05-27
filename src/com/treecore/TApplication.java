package com.treecore;

import android.app.Application;
import android.content.Context;
import com.treecore.crash.TCrash;
import com.treecore.crash.TICrashListener;

public class TApplication extends Application implements TICrashListener {
	private static TApplication mThis = null;
	public static boolean mDebug = true;

	public void onCreate() {
		super.onCreate();
		mThis = this;

		TCrash.getInstance().setICrashListener(this);
		TCrash.getInstance().initConfig(this);
	}

	public void onAppCrash(String crashFile) {
	}

	public static TApplication getInstance() {
		return mThis;
	}

	protected void onExitApplication() {
	}

	public void AppExit(Context context, Boolean isBackground) {
	}

	public static boolean isRelease() {
		return !mDebug;
	}
}