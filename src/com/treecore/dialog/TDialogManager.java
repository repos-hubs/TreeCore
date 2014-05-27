package com.treecore.dialog;

import android.content.Context;
import java.util.LinkedList;

public class TDialogManager {
	private static LinkedList<TDialog> mProgressDialogs = new LinkedList();

	public static void hideProgressDialog(Context context) {
		TDialog progressDialog = hasProgressDialog(context);
		if (progressDialog != null) {
			progressDialog.dismiss();
			mProgressDialogs.remove(progressDialog);
			progressDialog = null;
		}
	}

	public static void showProgressDialog(Context context, CharSequence title,
			CharSequence message) {
		showProgressDialog(context, title, message, true);
	}

	public static void showProgressDialog(Context context, CharSequence title,
			CharSequence message, boolean cancelAble) {
		TDialog progressDialog = hasProgressDialog(context);

		if (progressDialog == null) {
			progressDialog = new TDialog(context);
			progressDialog.setProgressStyle(0);
			progressDialog.setIndeterminate(false);
			mProgressDialogs.add(progressDialog);
		}

		progressDialog.setCancelable(cancelAble);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	private static TDialog hasProgressDialog(Context context) {
		for (TDialog progressDialog : mProgressDialogs) {
			if (progressDialog.getContextByActivity() == context) {
				return progressDialog;
			}
		}
		return null;
	}
}