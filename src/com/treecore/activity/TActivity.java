package com.treecore.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;
import com.treecore.TIProcessEvent;
import com.treecore.UIBroadcast;
import com.treecore.activity.layoutloader.TILayoutLoader;
import com.treecore.dialog.TDialogManager;
import com.treecore.utils.TStringUtils;
import java.util.ArrayList;

public abstract class TActivity extends Activity implements TIProcessEvent {
	private String TAG = TActivity.class.getCanonicalName();

	private String mModuleName = "";

	private String mLayouName = "";
	protected Context mContext;
	protected IntentFilter filter = new IntentFilter();
	private Status mStatus;
	protected ArrayList<String> mActivityParameters = new ArrayList();
	protected ArrayList<String> mBroadcastParameters = new ArrayList();
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UIBroadcast.INTENT_ACTION_EVENT.equals(action)) {
				TActivity.this.initBroadcastParameter(intent);
				TActivity.this.processEvent(intent);
			}
		}
	};

	public void processEvent(Intent intent) {
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		this.mStatus = Status.CREATED;

		this.filter.addAction(UIBroadcast.INTENT_ACTION_EVENT);
		this.filter.setPriority(1000);
		registerReceiver(this.mBroadcastReceiver, this.filter);

		initActivityParameter(getIntent());

		TActivityManager.getInstance().addActivity(this);
		getModuleName();
		if (TStringUtils.isEmpty(this.mLayouName))
			this.mLayouName = this.mContext.getPackageName();
		initInjector();
		loadDefautLayout();
	}

	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		TActivityManager.getInstance().getInjector().injectView(this);
	}

	public void finish() {
		TActivityManager.getInstance().removeActivity(this);
		super.finish();
	}

	protected void onStart() {
		this.mStatus = Status.STARTED;
		super.onStart();
	}

	protected void onResume() {
		this.mStatus = Status.RESUMED;
		super.onResume();
	}

	protected void onPause() {
		this.mStatus = Status.PAUSED;
		super.onPause();
	}

	protected void onStop() {
		this.mStatus = Status.STOPPED;
		super.onStop();
	}

	protected void onDestroy() {
		TDialogManager.hideProgressDialog(this);
		unregisterReceiver(this.mBroadcastReceiver);
		this.mBroadcastReceiver = null;

		if (this.mActivityParameters != null)
			this.mActivityParameters.clear();
		this.mActivityParameters = null;

		if (this.mBroadcastParameters != null)
			this.mBroadcastParameters.clear();
		this.mBroadcastParameters = null;
		this.mStatus = Status.DESTORYED;

		this.mContext = null;
		super.onDestroy();
	}

	private void initInjector() {
		TActivityManager.getInstance().getInjector().injectResource(this);
		TActivityManager.getInstance().getInjector().inject(this);
	}

	private void loadDefautLayout() {
		try {
			int layoutResID = TActivityManager.getInstance().getLayoutLoader()
					.getLayoutID(this.mLayouName);
			setContentView(layoutResID);
		} catch (Exception localException) {
		}
	}

	public void setContentView(View view, ViewGroup.LayoutParams params) {
		super.setContentView(view, params);

		TActivityManager.getInstance().getInjector().injectView(this);
	}

	public void setContentView(View view) {
		super.setContentView(view);

		TActivityManager.getInstance().getInjector().injectView(this);
	}

	public String getModuleName() {
		if ((this.mModuleName == null)
				|| (this.mModuleName.equalsIgnoreCase(""))) {
			this.mModuleName = getClass().getName().substring(0,
					getClass().getName().length() - 8);
			String[] arrays = this.mModuleName.split("\\.");
			this.mModuleName = (this.mModuleName = arrays[(arrays.length - 1)]
					.toLowerCase());
		}
		return this.mModuleName;
	}

	public void setModuleName(String moduleName) {
		this.mModuleName = moduleName;
	}

	public void initActivityParameter(Intent intent) {
		if (this.mActivityParameters == null)
			return;
		this.mActivityParameters.clear();
		this.mActivityParameters.add(intent
				.getStringExtra(TActivityUtils.FIELD_DATA0));
		this.mActivityParameters.add(intent
				.getStringExtra(TActivityUtils.FIELD_DATA1));
		this.mActivityParameters.add(intent
				.getStringExtra(TActivityUtils.FIELD_DATA2));
		this.mActivityParameters.add(intent
				.getStringExtra(TActivityUtils.FIELD_DATA3));
		this.mActivityParameters.add(intent
				.getStringExtra(TActivityUtils.FIELD_DATA4));
		this.mActivityParameters.add(intent
				.getStringExtra(TActivityUtils.FIELD_DATA5));
	}

	public void initBroadcastParameter(Intent intent) {
		if (this.mBroadcastParameters == null)
			return;
		this.mBroadcastParameters.clear();
		this.mBroadcastParameters.add(intent.getStringExtra("message0"));
		this.mBroadcastParameters.add(intent.getStringExtra("message1"));
		this.mBroadcastParameters.add(intent.getStringExtra("message2"));
		this.mBroadcastParameters.add(intent.getStringExtra("message3"));
		this.mBroadcastParameters.add(intent.getStringExtra("message4"));
		this.mBroadcastParameters.add(intent.getStringExtra("message5"));
	}

	public ArrayList<String> getActivityParameter() {
		return this.mActivityParameters;
	}

	public ArrayList<String> getBroadcastParameter() {
		return this.mBroadcastParameters;
	}

	public Status get_status() {
		return this.mStatus;
	}

	public boolean isActivity() {
		return (this.mStatus != Status.DESTORYED)
				&& (this.mStatus != Status.PAUSED)
				&& (this.mStatus != Status.STOPPED);
	}

	protected void makeText(String content) {
		Toast.makeText(this.mContext, content, 1).show();
	}

	public static enum Status {
		NONE, CREATED, STARTED, RESUMED, PAUSED, STOPPED, DESTORYED;
	}
}