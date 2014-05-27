package com.treecore.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class TDensityUtils {
	public static int dipTopx(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5F);
	}

	public static int pxTodip(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5F);
	}
}