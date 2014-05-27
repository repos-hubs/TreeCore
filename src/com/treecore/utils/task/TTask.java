package com.treecore.utils.task;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;

public class TTask {
	private static final String TAG = TTask.class.getCanonicalName();
	protected Task mTask;
	protected TITaskListener mIListener;
	protected Handler mHandler;
	private static int Update = 1;

	public boolean equalTask(Task task) {
		return this.mTask == task;
	}

	public Task getTask() {
		return this.mTask;
	}

	public void startTask(int TaskId) {
		stopTask();

		this.mTask = null;
		this.mTask = new Task(TaskId, new String[0]);
		this.mTask.execute(new String[] { "" });
	}

	public void startTask(int TaskId, String[] params) {
		stopTask();

		this.mTask = new Task(TaskId, params);
		this.mTask.execute(params);
	}

	public void startTask(String[] params) {
		stopTask();

		this.mTask = new Task(0, params);
		this.mTask.execute(new String[] { "" });
	}

	public void stopTask() {
		if (this.mTask != null)
			this.mTask.stopTask();
	}

	public boolean isTasking() {
		if ((this.mTask != null)
				&& (this.mTask.getStatus() == AsyncTask.Status.RUNNING)) {
			return true;
		}
		return false;
	}

	public void setIXTaskListener(TITaskListener listener) {
		this.mIListener = listener;
	}

	public class Task extends AsyncTask<String, Integer, Boolean> {
		protected String mErrorString = "";
		protected Task mThis;
		private int mTaskId = 0;
		private boolean mBCancel = false;
		private ArrayList<String> mParameters = new ArrayList();
		private Object mResultObject;

		public Task(int taskId, String[] params) {
			this.mTaskId = taskId;
			this.mThis = this;
			int i;
			if (params != null)
				for (i = 0; i < params.length; i++)
					this.mParameters.add(params[i]);
		}

		protected Boolean doInBackground(String[] params) {
			boolean result = false;
			if (this.mBCancel)
				return Boolean.valueOf(false);
			if (TTask.this.mIListener != null) {
				TTask.this.mIListener
						.onTask(this, TTask.TaskEvent.Work, params);
			}
			return Boolean.valueOf(true);
		}

		protected void onPreExecute() {
			if (this.mBCancel)
				return;
			super.onPreExecute();
			this.mResultObject = null;
			if (TTask.this.mIListener != null)
				TTask.this.mIListener.onTask(this, TTask.TaskEvent.Before,
						new Object[0]);
		}

		protected void onPostExecute(Boolean result) {
			if (this.mBCancel)
				return;
			super.onPostExecute(result);
			if (TTask.this.mIListener != null) {
				TTask.this.mIListener.onTask(this, TTask.TaskEvent.Cancel,
						new Object[] { result });
			}
			if ((this.mErrorString != null) && (!this.mErrorString.equals("")))
				Log.i(TTask.TAG, getTaskId() + this.mErrorString);
		}

		protected void onCancelled() {
			super.onCancelled();
			this.mBCancel = true;
			if (TTask.this.mIListener != null)
				TTask.this.mIListener.onTask(this, TTask.TaskEvent.Cancel,
						new Object[] { Boolean.valueOf(false) });
		}

		public void publishTProgress(int values) {
			TTask.this.mTask.publishProgress(new Integer[] { Integer
					.valueOf(values) });
		}

		public ArrayList<String> getParameter() {
			return this.mParameters;
		}

		public void stopTask() {
			this.mParameters.clear();
			this.mBCancel = true;
			cancel(true);
		}

		public void setResultObject(Object object) {
			this.mResultObject = object;
		}

		public Object getResultObject() {
			return this.mResultObject;
		}

		public void setError(String error) {
			this.mErrorString = error;
		}

		public String getError() {
			return this.mErrorString;
		}

		public void setTaskId(int taskId) {
			this.mTaskId = taskId;
		}

		public int getTaskId() {
			return this.mTaskId;
		}

		public boolean isCancel() {
			return this.mBCancel;
		}
	}

	public static enum TaskEvent {
		Before, Update, Cancel, Work;
	}
}