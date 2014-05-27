package com.treecore.utils;

import android.os.Build;
import android.os.Build.VERSION;
import java.lang.reflect.Field;

public class TAndroidVersionUtils {
	public static int getAPILevel() {
		int apiLevel;
		try {
			Field SDK_INT = Build.VERSION.class.getField("SDK_INT");
			apiLevel = SDK_INT.getInt(null);
		} catch (SecurityException e) {
			apiLevel = Integer.parseInt(Build.VERSION.SDK);
		} catch (NoSuchFieldException e) {
			apiLevel = Integer.parseInt(Build.VERSION.SDK);
		} catch (IllegalArgumentException e) {
			apiLevel = Integer.parseInt(Build.VERSION.SDK);
		} catch (IllegalAccessException e) {
			apiLevel = Integer.parseInt(Build.VERSION.SDK);
		}

		return apiLevel;
	}

	public static boolean hasDonut() {
		return Build.VERSION.SDK_INT >= 4;
	}

	public static boolean hasEclair() {
		return Build.VERSION.SDK_INT >= 5;
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= 8;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= 9;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= 11;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= 12;
	}

	public static boolean hasIcecreamsandwich() {
		return Build.VERSION.SDK_INT >= 14;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= 16;
	}

	public static boolean hasJellyBeanMr1() {
		return Build.VERSION.SDK_INT >= 17;
	}
}