package com.treecore.cache.disc.impl;

import com.treecore.cache.TCacheManager;
import com.treecore.cache.disc.TLimitedDiscCache;
import com.treecore.cache.disc.naming.TIFileNameGenerator;
import java.io.File;

public class TFileCountLimitedDiscCache extends TLimitedDiscCache {
	public TFileCountLimitedDiscCache(File cacheDir, int maxFileCount) {
		this(cacheDir, TCacheManager.createFileNameGenerator(), maxFileCount);
	}

	public TFileCountLimitedDiscCache(File cacheDir,
			TIFileNameGenerator fileNameGenerator, int maxFileCount) {
		super(cacheDir, fileNameGenerator, maxFileCount);
	}

	protected int getSize(File file) {
		return 1;
	}
}