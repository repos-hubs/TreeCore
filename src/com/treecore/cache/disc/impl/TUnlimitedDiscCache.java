package com.treecore.cache.disc.impl;

import com.treecore.cache.TCacheManager;
import com.treecore.cache.disc.TBaseDiscCache;
import com.treecore.cache.disc.naming.TIFileNameGenerator;
import java.io.File;

public class TUnlimitedDiscCache extends TBaseDiscCache {
	public TUnlimitedDiscCache(File cacheDir) {
		this(cacheDir, TCacheManager.createFileNameGenerator());
	}

	public TUnlimitedDiscCache(File cacheDir,
			TIFileNameGenerator fileNameGenerator) {
		super(cacheDir, fileNameGenerator);
	}

	public void put(String key, File file) {
	}
}