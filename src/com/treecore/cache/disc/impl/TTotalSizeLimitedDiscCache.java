package com.treecore.cache.disc.impl;

import com.treecore.cache.TCacheManager;
import com.treecore.cache.disc.TLimitedDiscCache;
import com.treecore.cache.disc.naming.TIFileNameGenerator;
import com.treecore.utils.log.TLog;
import java.io.File;

public class TTotalSizeLimitedDiscCache extends TLimitedDiscCache {
	private static final int MIN_NORMAL_CACHE_SIZE_IN_MB = 2;
	private static final int MIN_NORMAL_CACHE_SIZE = 2097152;

	public TTotalSizeLimitedDiscCache(File cacheDir, int maxCacheSize) {
		this(cacheDir, TCacheManager.createFileNameGenerator(), maxCacheSize);
	}

	public TTotalSizeLimitedDiscCache(File cacheDir,
			TIFileNameGenerator fileNameGenerator, int maxCacheSize) {
		super(cacheDir, fileNameGenerator, maxCacheSize);
		if (maxCacheSize < 2097152)
			TLog.w(this, String.format(
					"You set too small disc cache size (less than %1$d Mb)",
					new Object[] { Integer.valueOf(2) }));
	}

	protected int getSize(File file) {
		return (int) file.length();
	}
}