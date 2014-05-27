package com.treecore.activity.layoutloader;

import android.content.pm.PackageManager;

import com.treecore.exception.TNoSuchNameLayoutException;

public abstract interface TILayoutLoader {
	public abstract int getLayoutID(String paramString)
			throws ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, PackageManager.NameNotFoundException,
			TNoSuchNameLayoutException;
}