package com.treecore.dialog;

import android.app.ProgressDialog;
import android.content.Context;

public class TDialog extends ProgressDialog {
	private Context mContext;

	public TDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	public TDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	public Context getContextByActivity() {
		return this.mContext;
	}
}