package com.treecore.crash.collector;

import android.content.Context;
import android.text.format.Time;
import com.treecore.crash.TCrash;
import com.treecore.utils.TReflectUtils;
import com.treecore.utils.log.TLog;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class DropBoxCollector {
	private static final String[] SYSTEM_TAGS = { "system_app_anr",
			"system_app_wtf", "system_app_crash", "system_server_anr",
			"system_server_wtf", "system_server_crash",
			"BATTERY_DISCHARGE_INFO", "SYSTEM_RECOVERY_LOG", "SYSTEM_BOOT",
			"SYSTEM_LAST_KMSG", "APANIC_CONSOLE", "APANIC_THREADS",
			"SYSTEM_RESTART", "SYSTEM_TOMBSTONE", "data_app_strictmode" };
	private static final String NO_RESULT = "N/A";

	public static String read(Context context, String[] additionalTags) {
		try {
			String serviceName = TReflectUtils.getDropBoxServiceName();
			if (serviceName == null) {
				return "N/A";
			}

			Object dropbox = context.getSystemService(serviceName);
			Method getNextEntry = dropbox.getClass().getMethod("getNextEntry",
					new Class[] { String.class, Long.TYPE });
			if (getNextEntry == null) {
				return "";
			}

			Time timer = new Time();
			timer.setToNow();
			timer.minute -= 5;
			timer.normalize(false);
			long time = timer.toMillis(false);

			List<String> tags = new ArrayList();

			tags.addAll(Arrays.asList(SYSTEM_TAGS));

			if ((additionalTags != null) && (additionalTags.length > 0)) {
				tags.addAll(Arrays.asList(additionalTags));
			}

			if (tags.isEmpty()) {
				return "No tag configured for collection.";
			}

			StringBuilder dropboxContent = new StringBuilder();
			for (String tag : tags) {
				dropboxContent.append("Tag: ").append(tag).append('\n');
				Object entry = getNextEntry.invoke(dropbox, new Object[] { tag,
						Long.valueOf(time) });
				if (entry == null) {
					dropboxContent.append("Nothing.").append('\n');
				} else {
					Method getText = entry.getClass().getMethod("getText",
							new Class[] { Integer.TYPE });
					Method getTimeMillis = entry.getClass().getMethod(
							"getTimeMillis", null);
					Method close = entry.getClass().getMethod("close", null);
					while (entry != null) {
						long msec = ((Long) getTimeMillis.invoke(entry, null))
								.longValue();

						timer.set(msec);
						dropboxContent.append("@").append(timer.format2445())
								.append('\n');
						String text = (String) getText.invoke(entry,
								new Object[] { Integer.valueOf(500) });
						if (text != null)
							dropboxContent.append("Text: ").append(text)
									.append('\n');
						else {
							dropboxContent.append("Not Text!").append('\n');
						}
						close.invoke(entry, null);
						entry = getNextEntry.invoke(dropbox, new Object[] {
								tag, Long.valueOf(msec) });
					}
				}
			}
			return dropboxContent.toString();
		} catch (SecurityException e) {
			TLog.i(TCrash.TAG, "DropBoxManager not available.");
		} catch (NoSuchMethodException e) {
			TLog.i(TCrash.TAG, "DropBoxManager not available.");
		} catch (IllegalArgumentException e) {
			TLog.i(TCrash.TAG, "DropBoxManager not available.");
		} catch (IllegalAccessException e) {
			TLog.i(TCrash.TAG, "DropBoxManager not available.");
		} catch (InvocationTargetException e) {
			TLog.i(TCrash.TAG, "DropBoxManager not available.");
		} catch (NoSuchFieldException e) {
			TLog.i(TCrash.TAG, "DropBoxManager not available.");
		}

		return "N/A";
	}
}