package com.treecore.cache.disc;

import com.treecore.cache.TCacheManager;
import com.treecore.cache.disc.naming.TIFileNameGenerator;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TLimitedDiscCache extends TBaseDiscCache {
	private final AtomicInteger cacheSize;
	private final int sizeLimit;
	private final Map<File, Long> lastUsageDates = Collections
			.synchronizedMap(new HashMap());

	public TLimitedDiscCache(File cacheDir, int sizeLimit) {
		this(cacheDir, TCacheManager.createFileNameGenerator(), sizeLimit);
	}

	public TLimitedDiscCache(File cacheDir,
			TIFileNameGenerator fileNameGenerator, int sizeLimit) {
		super(cacheDir, fileNameGenerator);
		this.sizeLimit = sizeLimit;
		this.cacheSize = new AtomicInteger();
		calculateCacheSizeAndFillUsageMap();
	}

	private void calculateCacheSizeAndFillUsageMap() {
		new Thread(new Runnable() {
			public void run() {
				int size = 0;
				File[] cachedFiles = TLimitedDiscCache.this.cacheDir
						.listFiles();
				if (cachedFiles != null) {
					for (File cachedFile : cachedFiles) {
						size += TLimitedDiscCache.this.getSize(cachedFile);
						TLimitedDiscCache.this.lastUsageDates.put(cachedFile,
								Long.valueOf(cachedFile.lastModified()));
					}
					TLimitedDiscCache.this.cacheSize.set(size);
				}
			}
		}).start();
	}

	public void put(String key, File file) {
		int valueSize = getSize(file);
		int curCacheSize = this.cacheSize.get();
		while (curCacheSize + valueSize > this.sizeLimit) {
			int freedSize = removeNext();
			if (freedSize == 0)
				break;
			curCacheSize = this.cacheSize.addAndGet(-freedSize);
		}
		this.cacheSize.addAndGet(valueSize);

		Long currentTime = Long.valueOf(System.currentTimeMillis());
		file.setLastModified(currentTime.longValue());
		this.lastUsageDates.put(file, currentTime);
	}

	public File get(String key) {
		File file = super.get(key);

		Long currentTime = Long.valueOf(System.currentTimeMillis());
		file.setLastModified(currentTime.longValue());
		this.lastUsageDates.put(file, currentTime);

		return file;
	}

	public void clear() {
		this.lastUsageDates.clear();
		this.cacheSize.set(0);
		super.clear();
	}

	private int removeNext() {
		if (this.lastUsageDates.isEmpty()) {
			return 0;
		}

		Long oldestUsage = null;
		File mostLongUsedFile = null;
		Set entries = this.lastUsageDates.entrySet();
		synchronized (this.lastUsageDates) {
			for (Map.Entry entry : entries) {
				if (mostLongUsedFile == null) {
					mostLongUsedFile = (File) entry.getKey();
					oldestUsage = (Long) entry.getValue();
				} else {
					Long lastValueUsage = (Long) entry.getValue();
					if (lastValueUsage.longValue() < oldestUsage.longValue()) {
						oldestUsage = lastValueUsage;
						mostLongUsedFile = (File) entry.getKey();
					}
				}
			}
		}

		int fileSize = getSize(mostLongUsedFile);
		if (mostLongUsedFile.delete()) {
			this.lastUsageDates.remove(mostLongUsedFile);
		}
		return fileSize;
	}

	protected abstract int getSize(File paramFile);
}