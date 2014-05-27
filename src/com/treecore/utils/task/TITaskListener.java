package com.treecore.utils.task;

public abstract interface TITaskListener {
	public abstract void onTask(TTask.Task paramTask,
			TTask.TaskEvent paramTaskEvent, Object[] paramArrayOfObject);
}