package com.treecore.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.treecore.utils.TAndroidVersionUtils;
import com.treecore.utils.log.TLog;

public class TActivityUtils {
	private static final String TAG = TActivityUtils.class.getSimpleName();

	public static String FIELD_DATA0 = "data0";
	public static String FIELD_DATA1 = "data1";
	public static String FIELD_DATA2 = "data2";
	public static String FIELD_DATA3 = "data3";
	public static String FIELD_DATA4 = "data4";
	public static String FIELD_DATA5 = "data5";

	public static void jumpToActivity(Context context,
			Class<? extends Activity> targetClass) {
		Intent datatIntent = new Intent(context, targetClass);
		context.startActivity(datatIntent);
	}

	public static void jumpPostToActivity(final Context context,
			final Class<? extends Activity> targetClass, int second) {
		new AsyncTask() {
			protected Boolean doInBackground(String[] params) {
				try {
					Thread.sleep(this.val$second * 1000);
				} catch (Exception localException) {
				}
				return null;
			}

			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				Intent datatIntent = new Intent(context, targetClass);
				context.startActivity(datatIntent);
			}
		}.execute(new String[] { "" });
	}

	public static void jumpToNewActivity(Context context,
			Class<? extends Activity> targetClass) {
		Intent datatIntent = new Intent(context, targetClass);
		datatIntent.setFlags(268435456);
		context.startActivity(datatIntent);
	}

	public static void jumpToNewTopActivity(Context context,
			Class<? extends Activity> targetClass) {
		Intent datatIntent = new Intent(context, targetClass);
		datatIntent.setFlags(335544320);

		context.startActivity(datatIntent);
	}

	public static void jumpPostToNewActivity(final Context context,
			final Class<? extends Activity> targetClass, int second) {
		new AsyncTask() {
			protected Boolean doInBackground(String[] params) {
				try {
					Thread.sleep(this.val$second * 1000);
				} catch (Exception localException) {
				}
				return null;
			}

			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				Intent datatIntent = new Intent(context, targetClass);
				datatIntent.setFlags(268435456);
				context.startActivity(datatIntent);
			}
		}.execute(new String[] { "" });
	}

	public static void jumpPostToNewTopActivity(final Context context,
			final Class<? extends Activity> targetClass, int second) {
		new AsyncTask() {
			protected Boolean doInBackground(String[] params) {
				try {
					Thread.sleep(this.val$second * 1000);
				} catch (Exception localException) {
				}
				return null;
			}

			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				Intent datatIntent = new Intent(context, targetClass);
				datatIntent.setFlags(335544320);

				context.startActivity(datatIntent);
			}
		}.execute(new String[] { "" });
	}

	public static void jumpToActivity(Context context,
			Class<? extends Activity> targetClass, String[] datas) {
		Intent datatIntent = new Intent(context, targetClass);

		if (datas != null) {
			for (int i = 0; i < datas.length; i++) {
				datatIntent.putExtra("data" + i, datas[i]);
			}
		}

		context.startActivity(datatIntent);
	}

	public static void jumpPostToActivity(final Context context,
			final Class<? extends Activity> targetClass, int second,
			final String[] datas) {
		new AsyncTask() {
			protected Boolean doInBackground(String[] params) {
				try {
					Thread.sleep(this.val$second * 1000);
				} catch (Exception localException) {
				}
				return null;
			}

			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				Intent datatIntent = new Intent(context, targetClass);
				if (datas != null) {
					for (int i = 0; i < datas.length; i++) {
						datatIntent.putExtra("data" + i, datas[i]);
					}
				}
				context.startActivity(datatIntent);
			}
		}.execute(new String[] { "" });
	}

	public static void jumpToNewActivity(Context context,
			Class<? extends Activity> targetClass, String[] datas) {
		Intent datatIntent = new Intent(context, targetClass);
		datatIntent.setFlags(268435456);
		if (datas != null) {
			for (int i = 0; i < datas.length; i++) {
				datatIntent.putExtra("data" + i, datas[i]);
			}
		}
		context.startActivity(datatIntent);
	}

	public static void jumpPostToNewActivity(final Context context,
			final Class<? extends Activity> targetClass, int second,
			final String[] datas) {
		new AsyncTask() {
			protected Boolean doInBackground(String[] params) {
				try {
					Thread.sleep(this.val$second * 1000);
				} catch (Exception localException) {
				}
				return null;
			}

			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				Intent datatIntent = new Intent(context, targetClass);
				datatIntent.setFlags(268435456);
				if (datas != null) {
					for (int i = 0; i < datas.length; i++) {
						datatIntent.putExtra("data" + i, datas[i]);
					}
				}
				context.startActivity(datatIntent);
			}
		}.execute(new String[] { "" });
	}

	public static void jumpPostToNewTopActivity(final Context context,
			final Class<? extends Activity> targetClass, int second,
			final String[] datas) {
		new AsyncTask() {
			protected Boolean doInBackground(String[] params) {
				try {
					Thread.sleep(this.val$second * 1000);
				} catch (Exception localException) {
				}
				return null;
			}

			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				Intent datatIntent = new Intent(context, targetClass);
				datatIntent.setFlags(335544320);

				if (datas != null) {
					for (int i = 0; i < datas.length; i++) {
						datatIntent.putExtra("data" + i, datas[i]);
					}
				}
				context.startActivity(datatIntent);
			}
		}.execute(new String[] { "" });
	}

	public static void jumpToActivityForResult(Context context,
			Class<? extends Activity> targetClass, int resultId, String[] datas) {
		Intent datatIntent = new Intent(context, targetClass);
		if (datas != null) {
			for (int i = 0; i < datas.length; i++) {
				datatIntent.putExtra("data" + i, datas[i]);
			}
		}
		((Activity) context).startActivityForResult(datatIntent, resultId);
	}

	public static void jumpToActivityForResult(Context context,
			Class<? extends Activity> targetClass, int resultId) {
		Intent datatIntent = new Intent(context, targetClass);
		((Activity) context).startActivityForResult(datatIntent, resultId);
	}

	public static void jumpToSystemSMSActivity(Context context, String number) {
		Intent mIntent = new Intent("android.intent.action.VIEW");
		mIntent.putExtra("address", number);
		mIntent.setType("vnd.android-dir/mms-sms");
		context.startActivity(mIntent);
	}

	public static void jumpToActivity(Context context,
			ComponentName componentName) {
		Intent mIntent = new Intent();
		mIntent.addFlags(268435456);
		mIntent.setComponent(componentName);
		mIntent.setAction("android.intent.action.VIEW");
		context.startActivity(mIntent);
	}

	public static void jumpToActivity(Context context,
			ComponentName componentName, String[] datas) {
		Intent mIntent = new Intent();
		mIntent.addFlags(268435456);
		mIntent.setComponent(componentName);
		mIntent.setAction("android.intent.action.VIEW");
		if (datas != null) {
			for (int i = 0; i < datas.length; i++) {
				mIntent.putExtra("data" + i, datas[i]);
			}
		}
		context.startActivity(mIntent);
	}

	public static void jumpToNetworkSettingActivity(Context context) {
		Intent intent = null;
		try {
			if (TAndroidVersionUtils.hasHoneycomb()) {
				intent = new Intent("android.settings.WIRELESS_SETTINGS");
			} else {
				intent = new Intent();
				ComponentName comp = new ComponentName("com.android.settings",
						"com.android.settings.WirelessSettings");
				intent.setComponent(comp);
				intent.setAction("android.intent.action.VIEW");
			}
			context.startActivity(intent);
		} catch (Exception e) {
			TLog.w(TAG, "open network settings failed, please check...");
			e.printStackTrace();
		}
	}

	public static void jumpToSystemLocPickImageActivity(Activity activity,
			int requestCode) {
		Intent intent = null;
		intent = new Intent();
		intent.setType("image/*");
		intent.setAction("android.intent.action.GET_CONTENT");
		activity.startActivityForResult(intent, requestCode);
	}

	public static void jumpToSystemCameraPickImageActivity(Activity activity,
			int requestCode) {
		Intent intent = null;
		intent = new Intent("android.media.action.IMAGE_CAPTURE");
		activity.startActivityForResult(intent, requestCode);
	}
}