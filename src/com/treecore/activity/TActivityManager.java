package com.treecore.activity;

import android.app.Activity;
import com.treecore.activity.layoutloader.TILayoutLoader;
import com.treecore.activity.layoutloader.TLayoutLoader;
import java.util.Stack;

public class TActivityManager {
	private static Stack<Activity> mActivityStack;
	private static TActivityManager mThis;
	protected TActivity mCurrentActivity;
	protected TILayoutLoader mLayoutLoader;
	protected TInjector mInjector;

	public static TActivityManager getInstance() {
		if (mThis == null) {
			mThis = new TActivityManager();
		}
		return mThis;
	}

	public void addActivity(Activity activity) {
		if (mActivityStack == null) {
			mActivityStack = new Stack();
		}
		mActivityStack.add(activity);
	}

	public Activity currentActivity() {
		Activity activity = (Activity) mActivityStack.lastElement();
		return activity;
	}

	public void finishActivity() {
		Activity activity = (Activity) mActivityStack.lastElement();
		finishActivity(activity);
	}

	public void finishActivity(Activity activity) {
		if (activity != null) {
			mActivityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	public void removeActivity(Activity activity) {
		if (activity != null) {
			mActivityStack.remove(activity);
			activity = null;
		}
	}

	public void finishActivity(Class<?> cls) {
		for (Activity activity : mActivityStack)
			if (activity.getClass().equals(cls))
				finishActivity(activity);
	}

	public void finishAllActivity() {
		int i = 0;
		for (int size = mActivityStack.size(); i < size; i++) {
			if (mActivityStack.get(i) != null) {
				((Activity) mActivityStack.get(i)).finish();
			}
		}
		mActivityStack.clear();
	}

	public void back() {
	}

	public TILayoutLoader getLayoutLoader() {
		if (this.mLayoutLoader == null) {
			this.mLayoutLoader = TLayoutLoader.getInstance();
		}
		return this.mLayoutLoader;
	}

	public void setLayoutLoader(TILayoutLoader layoutLoader) {
		this.mLayoutLoader = layoutLoader;
	}

	public TInjector getInjector() {
		if (this.mInjector == null) {
			this.mInjector = TInjector.getInstance();
		}
		return this.mInjector;
	}

	public void setInjector(TInjector injector) {
		this.mInjector = injector;
	}
}