package com.treecore.activity.layoutloader;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.treecore.TApplication;
import com.treecore.exception.TNoSuchNameLayoutException;
import com.treecore.utils.log.TLog;

public class TLayoutLoader implements TILayoutLoader {
	private static String TAG = TLayoutLoader.class.getSimpleName();
	private static TLayoutLoader instance;
	private Context mContext;

	private TLayoutLoader(Context context) {
		this.mContext = context;
	}

	public static TLayoutLoader getInstance() {
		if (instance == null) {
			instance = new TLayoutLoader(TApplication.getInstance().getApplicationContext());
		}
		return instance;
	}

	public int getLayoutID(String resIDName)
			throws PackageManager.NameNotFoundException,
			ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, TNoSuchNameLayoutException {
		int resID = readResID("layout", resIDName);
		if (resID == 0) {
			throw new TNoSuchNameLayoutException();
		}
		return resID;
	}

	public int readResID(String type, String resIDName)
			throws PackageManager.NameNotFoundException,
			ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException {
		PackageManager pm = this.mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(this.mContext.getPackageName(), 0);
		String packageName = pi.packageName;
		if ((packageName == null) || (packageName.equalsIgnoreCase(""))) {
			throw new PackageManager.NameNotFoundException("没有获取到系统包名！");
		}
		packageName = packageName + ".R";
		Class clazz = Class.forName(packageName);
		Class cls = readResClass(clazz, packageName + "$" + type);
		if (cls == null) {
			throw new PackageManager.NameNotFoundException("没发现资源包名！");
		}
		return readResID(cls, resIDName);
	}

	public Class<?> readResClass(Class<?> cls, String respackageName) {
		Class[] classes = cls.getDeclaredClasses();
		for (int i = 0; i < classes.length; i++) {
			Class tempClass = classes[i];
			TLog.v(TAG, tempClass.getName());
			if (tempClass.getName().equalsIgnoreCase(respackageName)) {
				return tempClass;
			}
		}
		return null;
	}

	public int readResID(Class<?> cls, String resIDName)
			throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = cls.getDeclaredFields();
		for (int j = 0; j < fields.length; j++) {
			if (fields[j].getName().equalsIgnoreCase(resIDName)) {
				return fields[j].getInt(cls);
			}
		}
		return 0;
	}
}