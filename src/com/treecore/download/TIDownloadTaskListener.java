package com.treecore.download;

public abstract interface TIDownloadTaskListener {
	public abstract void onDownloadTaskUpdate(DownloadInfo paramDownloadInfo);

	public abstract void onDownloadTaskCancel(DownloadInfo paramDownloadInfo);
}