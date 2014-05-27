package com.treecore.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.treecore.utils.log.TLog;

public class TScreenUtils {
	public static String TAG = TPackageUtils.class.getSimpleName();

	public static String getDisplayDetails(Context context) {
		try {
			WindowManager windowManager = (WindowManager) context
					.getSystemService("window");

			Display display = windowManager.getDefaultDisplay();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);

			StringBuilder result = new StringBuilder();
			result.append("width=").append(display.getWidth()).append('\n');
			result.append("height=").append(display.getHeight()).append('\n');
			result.append("pixelFormat=").append(display.getPixelFormat())
					.append('\n');
			result.append("refreshRate=").append(display.getRefreshRate())
					.append("fps").append('\n');
			result.append("metrics.density=x").append(metrics.density)
					.append('\n');
			result.append("metrics.scaledDensity=x")
					.append(metrics.scaledDensity).append('\n');
			result.append("metrics.widthPixels=").append(metrics.widthPixels)
					.append('\n');
			result.append("metrics.heightPixels=").append(metrics.heightPixels)
					.append('\n');
			result.append("metrics.xdpi=").append(metrics.xdpi).append('\n');
			result.append("metrics.ydpi=").append(metrics.ydpi);
			return result.toString();
		} catch (RuntimeException e) {
			TLog.w(TAG,
					"Couldn't retrieve DisplayDetails for : "
							+ context.getPackageName() + e.getMessage());
		}
		return "Couldn't retrieve Display Details";
	}
}