package com.treecore;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Process;

public class UIBroadcast {
	public static String TAG = UIBroadcast.class.getCanonicalName();

	public static final String INTENT_ACTION_EVENT = TAG
			+ ".INTENT_ACTION_EVENT" + "." + Process.myPid();
	public static final String MAINEVENT = "mainevent";
	public static final String EVENT = "event";
	public static final String MESSAGE = "message";
	public static final String MESSAGE0 = "message0";
	public static final String MESSAGE1 = "message1";
	public static final String MESSAGE2 = "message2";
	public static final String MESSAGE3 = "message3";
	public static final String MESSAGE4 = "message4";
	public static final String MESSAGE5 = "message5";

	public static void sentEvent(Context context, int mainEvent,
			String[] message) {
		sentEvent(context, mainEvent, 0, message);
	}

	public static void sentEvent(Context context, int mainEvent, int event,
			String[] message) {
		Intent intent = new Intent(INTENT_ACTION_EVENT);
		intent.putExtra("mainevent", mainEvent);
		intent.putExtra("event", event);

		if (message != null) {
			for (int i = 0; i < message.length; i++) {
				intent.putExtra(
						String.format("message%d",
								new Object[] { Integer.valueOf(i) }),
						message[i]);
			}
		}
		context.sendBroadcast(intent);
	}

	public static void sentPostEvent(final Context context,
			final int mainEvent, final int event, final int second,
			final String[] message) {
		new AsyncTask() {
			protected Boolean doInBackground(String[] params) {
				try {
					Thread.sleep(second * 1000);
				} catch (Exception localException) {
				}
				return Boolean.valueOf(true);
			}

			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				UIBroadcast.sentEvent(context, mainEvent, event, message);
			}

			@Override
			protected Object doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		}.execute(new String[] { "" });
	}
}