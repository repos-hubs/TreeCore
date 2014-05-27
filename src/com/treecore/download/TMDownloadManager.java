package com.treecore.download;

import android.os.Handler;
import android.os.Message;
import com.treecore.TApplication;
import com.treecore.TIGlobalInterface;
import com.treecore.utils.TStorageUtils;
import com.treecore.utils.network.TNetWorkUtil;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class TMDownloadManager implements TIGlobalInterface {
	public static final int ERROR_NONE = 0;
	public static final int ERROR_SD_NO_MEMORY = 1;
	public static final int ERROR_BLOCK_INTERNET = 2;
	public static final int ERROR_UNKONW = 3;
	public static final int TIME_OUT = 30000;
	public static final int BUFFER_SIZE = 8192;
	private Queue<DownloadInfo> mTaskQueue = new LinkedList();
	private TIDownloadTaskListener mIDownloadTaskListener;
	private static TMDownloadManager mThis = null;
	private Timer mTaskTimer;
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			TMDownloadManager.this.updateDownloadTask();
			super.handleMessage(msg);
		}
	};

	public static TMDownloadManager getInstance() {
		if (mThis == null)
			mThis = new TMDownloadManager();
		return mThis;
	}

	public void initConfig() {
	}

	public void release() {
		this.mTaskQueue.clear();
		this.mIDownloadTaskListener = null;

		if (this.mTaskTimer != null) {
			this.mTaskTimer.cancel();
			this.mTaskTimer = null;
		}
	}

	public long addDownloadTask(DownloadInfo downloadInfo) throws Exception {
		if (!TStorageUtils.isSDCardPresent())
			throw new Exception("未发现SD卡");
		if (!TStorageUtils.isSdCardWrittenable()) {
			throw new Exception("SD卡不能读写");
		}
		if ((downloadInfo.getId() != 0L)
				&& (hasDownloadTask(downloadInfo.getId()))) {
			downloadInfo.setManager(this);
			downloadInfo.startTask();
			return downloadInfo.getId();
		}

		this.mTaskQueue.add(downloadInfo);
		downloadInfo.setManager(this);
		downloadInfo.startTask();

		if (this.mTaskTimer == null) {
			this.mTaskTimer = new Timer();
			this.mTaskTimer.schedule(new TimerTask() {
				public void run() {
					TMDownloadManager.this.mHandler.sendEmptyMessage(0);

					if (TMDownloadManager.this.mTaskQueue.isEmpty()) {
						TMDownloadManager.this.mTaskTimer.cancel();
						TMDownloadManager.this.mTaskTimer = null;
					}
				}
			}, 1000L, 1000L);
		}
		return downloadInfo.getId();
	}

	public boolean delDownloadTask(long id) throws Exception {
		if (id == 0L)
			throw new Exception("id = 0");
		for (DownloadInfo info : this.mTaskQueue) {
			if (info.getId() == id) {
				return this.mTaskQueue.remove(info);
			}
		}
		return false;
	}

	public boolean delDownloadTask(DownloadInfo downloadInfo) {
		if (downloadInfo == null) {
			return false;
		}
		downloadInfo.stopTask();
		return this.mTaskQueue.remove(downloadInfo);
	}

	public void stopDownloadTask(DownloadInfo downloadInfo) throws Exception {
		if ((downloadInfo == null) || (downloadInfo.getId() == 0L))
			throw new Exception("null");
		downloadInfo.stopTask();
	}

	public void stopDownloadTask(long id) throws Exception {
		if (id == 0L)
			throw new Exception("id = 0");
		for (DownloadInfo info : this.mTaskQueue)
			if (info.getId() == id) {
				stopDownloadTask(info);
				return;
			}
	}

	public void updateDownloadTask() {
		if (this.mIDownloadTaskListener == null)
			return;
		for (DownloadInfo info : this.mTaskQueue)
			this.mIDownloadTaskListener.onDownloadTaskUpdate(info);
	}

	public boolean hasDownloadTask(long id) {
		for (DownloadInfo info : this.mTaskQueue) {
			if (info.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public TIDownloadTaskListener getTIDownloadTaskListener() {
		return this.mIDownloadTaskListener;
	}

	public void setTIDownloadTaskListener(TIDownloadTaskListener listener) {
		this.mIDownloadTaskListener = listener;
	}

	public boolean isOnline() {
		return TNetWorkUtil.isNetworkAvailable(TApplication.getInstance()
				.getApplicationContext());
	}
}