package com.treecore.download;

import com.treecore.utils.TStorageUtils;
import com.treecore.utils.log.TLog;
import com.treecore.utils.task.TITaskListener;
import com.treecore.utils.task.TTask;
import com.treecore.utils.task.TTask.Task;
import com.treecore.utils.task.TTask.TaskEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

public class DownloadInfo implements TITaskListener {
	private String TAG = DownloadInfo.class.getSimpleName();
	private String mUrl = "";
	private String mFilePath = "";
	private String mFileName = "";

	private long mId = 0L;
	private long mPreviousFileSize = 0L;
	private long mTotalSize = 100L;
	private long mDownloadSize = 0L;
	private long mSpeed = 0L;
	private long mStartTime = 0L;
	private long mTotalTime = 0L;
	private int mErrorCode = 0;
	private TTask mDownloadTask;
	private Throwable mException;
	private AndroidHttpClient mAndroidHttpClient = null;
	private RandomAccessFile mRandomAccessFile;
	private byte[] mBuffer;
	private static int ICount = 1;

	public DownloadInfo(String url, String filePath, String fileName) {
		this.mUrl = url;
		this.mFilePath = filePath;
		this.mFileName = fileName;
		this.mId = ICount++;
	}

	public void setManager(TMDownloadManager manager) {
	}

	public long getId() {
		return this.mId;
	}

	public String getUrl() {
		return this.mUrl;
	}

	public String getFileName() {
		return this.mFileName;
	}

	public String getFilePath() {
		return this.mFilePath;
	}

	public long getTotalSize() {
		return this.mTotalSize;
	}

	public long getDownloadSize() {
		return this.mDownloadSize;
	}

	public long getSpeed() {
		return this.mSpeed;
	}

	public long getStartTime() {
		return this.mStartTime;
	}

	public long getPercent() {
		return (this.mDownloadSize + this.mPreviousFileSize) * 100L
				/ this.mTotalSize;
	}

	public long getTotalTime() {
		return this.mTotalTime;
	}

	public void setErrorCode(int error) {
		this.mErrorCode = error;
	}

	public int getErrorCode() {
		return this.mErrorCode;
	}

	public Throwable getError() {
		return this.mException;
	}

	public boolean isRuning() {
		return (this.mDownloadTask != null)
				&& (!this.mDownloadTask.getTask().isCancel());
	}

	public void startTask() {
		if (this.mDownloadTask == null) {
			this.mDownloadTask = new TTask();
			this.mDownloadTask.setIXTaskListener(this);
		}

		this.mErrorCode = 0;
		this.mDownloadTask.startTask(0);
		this.mStartTime = System.currentTimeMillis();
	}

	public void stopTask() {
		if (this.mDownloadTask != null) {
			this.mDownloadTask.stopTask();
		}
		this.mDownloadTask = null;

		if (this.mAndroidHttpClient != null) {
			this.mAndroidHttpClient.close();
		}
		try {
			if (this.mRandomAccessFile != null) {
				this.mRandomAccessFile.close();
				this.mRandomAccessFile = null;
			}
		} catch (Exception localException) {
		}
		if (this.mBuffer != null) {
			this.mBuffer = null;
		}

		if (TMDownloadManager.getInstance().getTIDownloadTaskListener() != null) {
			TMDownloadManager.getInstance().getTIDownloadTaskListener()
					.onDownloadTaskUpdate(this);
		}
		if (TMDownloadManager.getInstance().getTIDownloadTaskListener() != null)
			TMDownloadManager.getInstance().getTIDownloadTaskListener()
					.onDownloadTaskCancel(this);
	}

	public void onTask(TTask.Task task, TTask.TaskEvent event, Object[] params) {
		if ((this.mDownloadTask != null)
				&& (this.mDownloadTask.getTask() == task)
				&& (event != TTask.TaskEvent.Before))
			if (event == TTask.TaskEvent.Cancel) {
				TMDownloadManager.getInstance().delDownloadTask(this);
			} else if ((event != TTask.TaskEvent.Update)
					&& (event == TTask.TaskEvent.Work)) {
				try {
					downloadFile(task);
				} catch (Exception e) {
					this.mException = e;
					setErrorCode(3);
				}
				if (this.mAndroidHttpClient != null) {
					this.mAndroidHttpClient.close();
				}
				try {
					if (this.mRandomAccessFile != null) {
						this.mRandomAccessFile.close();
						this.mRandomAccessFile = null;
					}
				} catch (Exception localException1) {
				}
			}
	}

	private long downloadFile(TTask.Task task) throws Exception {
		this.mAndroidHttpClient = AndroidHttpClient.newInstance(this.TAG);
		HttpGet httpGet = new HttpGet(this.mUrl);
		HttpResponse response = this.mAndroidHttpClient.execute(httpGet);
		this.mTotalSize = response.getEntity().getContentLength();

		File file = new File(this.mFilePath, this.mFileName);

		if ((file.length() > 0L) && (this.mTotalSize > 0L)
				&& (this.mTotalSize > file.length())) {
			httpGet.addHeader("Range", "bytes=" + file.length() + "-");
			this.mPreviousFileSize = file.length();

			this.mAndroidHttpClient.close();
			this.mAndroidHttpClient = AndroidHttpClient
					.newInstance("DownloadTask");
			response = this.mAndroidHttpClient.execute(httpGet);
			TLog.v(this.TAG, "File is not complete, .");
			TLog.v(this.TAG, "download now,File length:" + file.length()
					+ " totalSize:" + this.mTotalSize);
		} else if ((file.exists()) && (this.mTotalSize == file.length())) {
			TLog.v(this.TAG, "Output file already exists. Skipping download.");
			return 0L;
		}

		long storage = TStorageUtils.getAvailableStorage();
		TLog.i(this.TAG, "storage:" + storage + " totalSize:" + this.mTotalSize);
		if (this.mTotalSize - file.length() > storage) {
			setErrorCode(1);
			task.stopTask();
			this.mAndroidHttpClient.close();
			return 0L;
		}
		try {
			this.mRandomAccessFile = new ProgressReportingRandomAccessFile(
					file, "rw");
		} catch (FileNotFoundException e) {
			TLog.v(this.TAG, "OutputStream Error");
		}

		InputStream input = null;
		try {
			input = response.getEntity().getContent();
		} catch (IOException ex) {
			setErrorCode(3);
			this.mAndroidHttpClient.close();
			TLog.v(this.TAG, "InputStream Error" + ex.getMessage());
			return 0L;
		}

		int bytesCopied = copy(input, this.mRandomAccessFile, task);

		if ((this.mPreviousFileSize + bytesCopied != this.mTotalSize)
				&& (this.mTotalSize != -1L) && (!task.isCancel())) {
			throw new IOException("Download incomplete: " + bytesCopied
					+ " != " + this.mTotalSize);
		}

		this.mRandomAccessFile.close();
		this.mAndroidHttpClient.close();
		this.mAndroidHttpClient = null;
		this.mRandomAccessFile = null;
		TLog.v(this.TAG, "Download completed successfully.");
		return bytesCopied;
	}

	public int copy(InputStream input, RandomAccessFile out, TTask.Task task)
			throws Exception, IOException {
		this.mBuffer = new byte[8192];
		BufferedInputStream in = new BufferedInputStream(input, 8192);
		TLog.v(this.TAG, "length" + out.length());
		out.seek(out.length());

		int count = 0;
		int byteCount = 0;
		long errorBlockTimePreviousTime = -1L;
		long expireTime = 0L;
		try {
			while (!task.isCancel()) {
				byteCount = in.read(this.mBuffer, 0, 8192);
				if (byteCount == -1) {
					break;
				}
				out.write(this.mBuffer, 0, byteCount);
				count += byteCount;

				if (!TMDownloadManager.getInstance().isOnline()) {
					task.stopTask();
					setErrorCode(2);
					break;
				}

				if (this.mSpeed == 0L) {
					if (errorBlockTimePreviousTime > 0L) {
						expireTime = System.currentTimeMillis()
								- errorBlockTimePreviousTime;
						if (expireTime > 30000L) {
							setErrorCode(2);
							task.stopTask();
						}
					} else {
						errorBlockTimePreviousTime = System.currentTimeMillis();
					}
				} else {
					expireTime = 0L;
					errorBlockTimePreviousTime = -1L;
				}
			}
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				setErrorCode(3);
				TLog.e(this.TAG, e.getMessage());
			}
			try {
				in.close();
			} catch (IOException e) {
				setErrorCode(3);
				TLog.e(this.TAG, e.getMessage());
			}
		}

		this.mBuffer = null;
		return count;
	}

	private final class ProgressReportingRandomAccessFile extends RandomAccessFile {
		private int progress = 0;

		public ProgressReportingRandomAccessFile(File file, String mode)
				throws FileNotFoundException {
			super(file, mode);
		}

		public void write(byte[] buffer, int offset, int count)
				throws IOException {
			DownloadInfo.this.mTotalTime = (System.currentTimeMillis() - DownloadInfo.this.mStartTime);
			super.write(buffer, offset, count);
			this.progress += count;
			DownloadInfo.this.mDownloadSize = this.progress;
			DownloadInfo.this.mSpeed = (DownloadInfo.this.mDownloadSize / DownloadInfo.this.mTotalTime);
		}
	}
}